package com.device.inspect.config.schedule;

import com.alibaba.fastjson.JSON;
import com.device.inspect.common.model.charater.User;
import com.device.inspect.common.model.device.MonitorDevice;
import com.device.inspect.common.model.device.ScientistDevice;
import com.device.inspect.common.model.record.DealRecord;
import com.device.inspect.common.model.record.DeviceOrderList;
import com.device.inspect.common.repository.charater.UserRepository;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import static com.device.inspect.common.setting.Defination.*;

import java.util.Date;
import java.util.List;

/**
 * Created by zyclincoln on 7/17/17.
 */
@Component
public class CheckFinishDeal {
    private static final Logger LOGGER = LogManager.getLogger(CheckFinishDeal.class);

    @Autowired
    private OnchainService onchainService;

    @Autowired
    private DealRecordRepository dealRecordRepository;

    @Autowired
    private MonitorDeviceRepository monitorDeviceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScientistDeviceRepository scientistDeviceRepository;

    @Autowired
    private DeviceOrderListRepository deviceOrderListRepository;

    @Scheduled(cron = "30 */1 * * * ? ")
    public void scheduleTask() {
        LOGGER.info(String.format("Check Execut Deal: begin checking deal record which meets rent start time at %s", new Date()));
        List<DealRecord> beginRecords = dealRecordRepository.findByStatusAndBeginTimeBefore(ONCHAIN_DEAL_STATUS_DEAL, new Date());
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

        List<DealRecord> records = dealRecordRepository.findByStatusAndEndTimeBefore(ONCHAIN_DEAL_STATUS_EXECUTING, new Date());
        for(DealRecord record : records){
            try {
                record.setStatus(ONCHAIN_DEAL_STATUS_WAITING_MUTUAL_CONFIRM);

                ScientistDevice scientistDevice = scientistDeviceRepository.findByScientistIdAndDeviceId(record.getLessee(), record.getDevice().getId());
                scientistDeviceRepository.delete(scientistDevice);

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
                    String order = "power off";
                    DeviceOrderList monitorOrder = new DeviceOrderList(new Date(), monitorSerialNo, order, DEVICE_ACTION_NOACT);
                    deviceOrderListRepository.save(monitorOrder);
                }
            }
            catch(Exception e){
                LOGGER.error("Check Finish Deal Error: " + e.getMessage());
            }
        }

        LOGGER.info("Device alerting ,and begin checking deal record which meets rent end time");
        List<DealRecord> alertRecords = dealRecordRepository.findByStatusAndEndTimeBefore(ONCHAIN_DEAL_STATUS_EXECUTING_WITH_ALERT, new Date());
        for (DealRecord alertRecord : alertRecords) {
            try {
                alertRecord.setStatus(ONCHAIN_DEAL_STATUS_WAITING_MUTUAL_CONFIRM_WITH_ALERT);

                ScientistDevice scientistDevice = scientistDeviceRepository.findByScientistIdAndDeviceId(alertRecord.getLessee(), alertRecord.getDevice().getId());
                scientistDeviceRepository.delete(scientistDevice);

                BlockChainDealDetail data = new BlockChainDealDetail(alertRecord.getId(), alertRecord.getDevice().getId(), alertRecord.getLessor(),
                        alertRecord.getLessee(), alertRecord.getPrice(), alertRecord.getBeginTime().getTime(), alertRecord.getEndTime().getTime(),
                        alertRecord.getDeviceSerialNumber(), alertRecord.getAggrement(), alertRecord.getStatus());
                BlockChainDealRecord value = new BlockChainDealRecord(DEAL_STATUS_TRANSFER_MAP.get(alertRecord.getStatus()), data);

                onchainService.sendStateUpdateTx("deal", String.valueOf(alertRecord.getId()),
                        "", JSON.toJSONString(value));

                dealRecordRepository.save(alertRecord);

                // add power on order
                MonitorDevice monitor = monitorDeviceRepository.findByDeviceId(alertRecord.getDevice().getId());
                if(monitor != null && monitor.getNumber() != null && !monitor.getNumber().isEmpty()){
                    String monitorSerialNo = monitor.getNumber();
                    String order = "power off";
                    DeviceOrderList monitorOrder = new DeviceOrderList(new Date(), monitorSerialNo, order, DEVICE_ACTION_NOACT);
                    deviceOrderListRepository.save(monitorOrder);
                }
            }
            catch(Exception e){
                LOGGER.error("Device alerting ,And Check Finish Deal Error: " + e.getMessage());
            }
        }
    }
}
