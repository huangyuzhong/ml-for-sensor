package com.device.inspect.config.schedule;

import com.alibaba.fastjson.JSON;
import com.device.inspect.Application;
import com.device.inspect.common.model.charater.User;
import com.device.inspect.common.model.device.*;
import com.device.inspect.common.model.record.DealRecord;
import com.device.inspect.common.model.record.DeviceOrderList;
import com.device.inspect.common.repository.charater.UserRepository;
import com.device.inspect.common.repository.device.DeviceInspectRepository;
import com.device.inspect.common.repository.device.InspectTypeRepository;
import com.device.inspect.common.repository.device.MonitorDeviceRepository;
import com.device.inspect.common.repository.device.ScientistDeviceRepository;
import com.device.inspect.common.repository.record.DealRecordRepository;
import com.device.inspect.common.repository.record.DeviceOrderListRepository;
import com.device.inspect.common.restful.record.BlockChainDealDetail;
import com.device.inspect.common.restful.record.BlockChainDealRecord;
import com.device.inspect.common.service.OnchainService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import static com.device.inspect.common.setting.Defination.*;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;
import java.util.List;

/**
 * Created by zyclincoln on 7/17/17.
 */
@Component
public class CheckFinishDeal{
    private static final Logger LOGGER = LogManager.getLogger(CheckFinishDeal.class);

    @Autowired
    private OnchainService onchainService;

    public void setOnchainService(OnchainService onchainService){
        this.onchainService = onchainService;
    }

    @Autowired
    private DealRecordRepository dealRecordRepository;

    public void setDealRecordRepository(DealRecordRepository dealRecordRepository){
        this.dealRecordRepository = dealRecordRepository;
    }

    @Autowired
    private MonitorDeviceRepository monitorDeviceRepository;

    public void setMonitorDeviceRepository(MonitorDeviceRepository monitorDeviceRepository){
        this.monitorDeviceRepository = monitorDeviceRepository;
    }

    @Autowired
    private UserRepository userRepository;

    public void setUserRepository(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Autowired
    private ScientistDeviceRepository scientistDeviceRepository;

    public void setScientistDeviceRepository(ScientistDeviceRepository scientistDeviceRepository){
        this.scientistDeviceRepository = scientistDeviceRepository;
    }

    @Autowired
    private DeviceOrderListRepository deviceOrderListRepository;

    public void setDeviceOrderListRepository(DeviceOrderListRepository deviceOrderListRepository){
        this.deviceOrderListRepository = deviceOrderListRepository;
    }

    @Autowired
    private DeviceInspectRepository deviceInspectRepository;

    public void setDeviceInspectRepository(DeviceInspectRepository deviceInspectRepository){
        this.deviceInspectRepository = deviceInspectRepository;
    }

    @Autowired
    private InspectTypeRepository inspectTypeRepository;

    public void setInspectTypeRepository(InspectTypeRepository inspectTypeRepository){
        this.inspectTypeRepository = inspectTypeRepository;
    }

//    @Scheduled(cron = "30 * * * * ?")
    public void executeInternal(){
        if(onchainService.isValid()!= true){
            return;
        }
        LOGGER.info(String.format("Check Execute Deal: begin checking deal record which meets rent start time at %s", new Date()));
        List<DealRecord> beginRecords = dealRecordRepository.findByStatusAndBeginTimeBefore(ONCHAIN_DEAL_STATUS_DEAL, new Date(new Date().getTime() + 1000*10));
        for(DealRecord record : beginRecords){
            try{
                LOGGER.info(String.format("Check Execute Deal: found deal to execute: %d", record.getId()));
                record.setStatus(ONCHAIN_DEAL_STATUS_EXECUTING);

                ScientistDevice scientistDevice = new ScientistDevice();
                User lessee = userRepository.findOne(record.getLessee());
                scientistDevice.setScientist(lessee);
                scientistDevice.setDevice(record.getDevice());
                scientistDeviceRepository.save(scientistDevice);

                BlockChainDealDetail data = new BlockChainDealDetail(record.getId(), record.getDevice().getId(), record.getLessor(),
                        record.getLessee(), record.getPrice(), record.getBeginTime().getTime(), record.getEndTime().getTime(),
                        record.getDeviceSerialNumber(), record.getAggrement(), record.getStatus());
                BlockChainDealRecord value = new BlockChainDealRecord(DEAL_STATUS_TRANSFER_MAP.get(record.getStatus()), data);
                onchainService.sendStateUpdateTx("deal", String.valueOf(record.getId()),
                        "", JSON.toJSONString(value));
                dealRecordRepository.save(record);

                // add power on order
                MonitorDevice monitor = monitorDeviceRepository.findByDeviceId(record.getDevice().getId());
                if(monitor != null && monitor.getNumber() != null && !monitor.getNumber().isEmpty()){
                    String monitorSerialNo = monitor.getNumber();
                    String order = "power on";
                    DeviceOrderList monitorOrder = new DeviceOrderList(new Date(), monitorSerialNo, order, DEVICE_ACTION_NOACT);
                    deviceOrderListRepository.save(monitorOrder);
                }

            }
            catch(Exception e){
                LOGGER.error("Check Finish Deal Error: " + e.getMessage());
            }
        }

        LOGGER.info("Check Finish Deal: begin checking deal record which meets rent end time");

        List<DealRecord> records = dealRecordRepository.findByStatusAndEndTimeBefore(ONCHAIN_DEAL_STATUS_EXECUTING, new Date(new Date().getTime() + 1000*10));
        List<DealRecord> records_with_alert = dealRecordRepository.findByStatusAndEndTimeBefore(ONCHAIN_DEAL_STATUS_EXECUTING_WITH_ALERT, new Date(new Date().getTime() + 1000*10));
        if(records_with_alert != null && !records_with_alert.isEmpty()){
            records.addAll(records_with_alert);
        }
        for(DealRecord record : records){
            try {
                // check whether device have currency inspect, if yes, check whether device has been shutdown. In fact, I feel quite ashamed about this push, which
                // added so many hard-code logic and ugly schema, just for a demo. I am quite sure these code shall cause some extremely annoying trouble in the future.

                InspectType currentType = inspectTypeRepository.findByCode("16");
                DeviceInspect deviceInspect = deviceInspectRepository.findByInspectTypeIdAndDeviceId(currentType.getId(), record.getDevice().getId());

                boolean canFinish = false;
                if(deviceInspect == null){
                    LOGGER.info(String.format("device %d has no currency type %d.", record.getDevice().getId(), currentType.getId()));
                    canFinish = true;
                }
                else{
                    List<Object> latestCurrentRecord = Application.influxDBManager.readLatestTelemetry(deviceInspect.getInspectType().getMeasurement(), record.getDevice().getId(), deviceInspect.getId());
                    if(((Double)latestCurrentRecord.get(1)).floatValue() < 0.01){
                        LOGGER.info(String.format("device %d is closed, cut off power.", record.getDevice().getId()));
                        canFinish = true;
                    }
                    else{
                        LOGGER.info(String.format("device %d is using, don't cut off power.", record.getDevice().getId()));
                        canFinish = false;
                    }
                }

                if(canFinish){
                    LOGGER.info(String.format("finish deal %d, current status %d", record.getId(), record.getStatus()));
                    if(record.getStatus() == ONCHAIN_DEAL_STATUS_EXECUTING){
                        record.setStatus(ONCHAIN_DEAL_STATUS_WAITING_MUTUAL_CONFIRM);
                    }
                    else{
                        record.setStatus(ONCHAIN_DEAL_STATUS_WAITING_MUTUAL_CONFIRM_WITH_ALERT);
                    }
                    LOGGER.info(String.format("deal finished %d, current status %d", record.getId(), record.getStatus()));
                    ScientistDevice scientistDevice = scientistDeviceRepository.findTopByScientistIdAndDeviceId(record.getLessee(), record.getDevice().getId());
                    scientistDeviceRepository.delete(scientistDevice);

                    BlockChainDealDetail data = new BlockChainDealDetail(record.getId(), record.getDevice().getId(), record.getLessor(),
                            record.getLessee(), record.getPrice(), record.getBeginTime().getTime(), record.getEndTime().getTime(),
                            record.getDeviceSerialNumber(), record.getAggrement(), record.getStatus());
                    BlockChainDealRecord value = new BlockChainDealRecord(DEAL_STATUS_TRANSFER_MAP.get(record.getStatus()), data);
                    onchainService.sendStateUpdateTx("deal", String.valueOf(record.getId()),
                            "", JSON.toJSONString(value));
                    record.setRealEndTime(new Date());
                    dealRecordRepository.save(record);

                    // send power off order
                    MonitorDevice monitor = monitorDeviceRepository.findByDeviceId(record.getDevice().getId());
                    if(monitor != null && monitor.getNumber() != null && !monitor.getNumber().isEmpty()){
                        String monitorSerialNo = monitor.getNumber();
                        String order = "power off";
                        DeviceOrderList monitorOrder = new DeviceOrderList(new Date(), monitorSerialNo, order, DEVICE_ACTION_NOACT);
                        deviceOrderListRepository.save(monitorOrder);
                    }

                }

            }
            catch(Exception e){
                LOGGER.error("Check Finish Deal Error: " + e.getMessage());
            }
        }
    }
}
