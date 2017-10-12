package com.device.inspect.controller;

import com.alibaba.fastjson.JSON;
import com.device.inspect.Application;
import com.device.inspect.common.ai.KMeansEmulate;
import com.device.inspect.common.ai.KMeansUse;
import com.device.inspect.common.managers.MessageController;
import com.device.inspect.common.model.charater.User;
import com.device.inspect.common.model.device.*;
import com.device.inspect.common.model.record.DealAlertRecord;
import com.device.inspect.common.model.record.DealRecord;
import com.device.inspect.common.model.record.Models;
import com.device.inspect.common.repository.charater.UserRepository;
import com.device.inspect.common.repository.device.*;
import com.device.inspect.common.repository.firm.RoomRepository;
import com.device.inspect.common.repository.record.*;
import com.device.inspect.common.restful.RestResponse;
import com.device.inspect.common.restful.record.BlockChainDealDetail;
import com.device.inspect.common.restful.record.BlockChainDealRecord;
import com.device.inspect.common.restful.tsdata.RestDeviceMonitoringTSData;
import com.device.inspect.common.restful.tsdata.RestTelemetryTSData;
import com.device.inspect.common.service.MemoryCacheDevice;
import com.device.inspect.common.service.OnchainService;
import com.device.inspect.common.setting.Constants;
import com.device.inspect.common.util.transefer.ByteAndHex;
import com.device.inspect.common.util.transefer.InspectMessage;
import com.device.inspect.common.util.transefer.InspectProcessTool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.influxdb.impl.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.Principal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

import static com.device.inspect.common.setting.Constants.DEAL_STATUS_TRANSFER_MAP;
import static com.device.inspect.common.setting.Constants.ONCHAIN_DEAL_STATUS_EXECUTING_WITH_ALERT;


/**
 * Created by Administrator on 2016/7/25.
 */
@RestController
@RequestMapping(value = "/api/rest")
public class SocketMessageApi {
    private static final Logger LOGGER = LogManager.getLogger(SocketMessageApi.class);
    private static final Random random = new Random();

    @Autowired
    private MessageController messageController;

    @Autowired
    private  DeviceRepository deviceRepository;

    @Autowired
    private  InspectTypeRepository inspectTypeRepository;

    @Autowired
    private  DeviceInspectRepository deviceInspectRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private AlertCountRepository alertCountRepository;

    @Autowired
    private DeviceVersionRepository deviceVersionRepository;

    @Autowired
    private MonitorDeviceRepository monitorDeviceRepository;

    @Autowired
    private Pt100Repository pt100Repository;

    @Autowired
    private Pt100ZeroRepository pt100ZeroRepository;

    @Autowired
    private DeviceRunningStatusRepository deviceRunningStatusRepository;

    @Autowired
    private DeviceTypeInspectRunningStatusRepository deviceTypeInspectRunningStatusRepository;

    @Autowired
    private DeviceInspectRunningStatusRepository deviceInspectRunningStatusRepository;

    @Autowired
    private MessageSendRepository messageSendRepository;

    @Autowired
    private DeviceFloorRepository deviceFloorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MemoryCacheDevice memoryCacheDevice;

    @Autowired
    private DeviceRunningStatusHistoryRepository deviceRunningStatusHistoryRepository;

    @Autowired
    private DealRecordRepository dealRecordRepository;

    @Autowired
    private OnchainService onchainService;

    @Autowired
    private DealAlertRecordRepository dealAlertRecordRepository;

    @Autowired
    private OpeModelsLevelRepository opeModelsLevelRepository;

    @Autowired
    private KMeansUse kMeansUse;

    String unit = "s";

    private User judgeByPrincipal(Principal principal){
        if (null == principal||null==principal.getName())
            throw new UsernameNotFoundException("You are not login!");
        User user = userRepository.findByName(principal.getName());
        if (null==user)
            throw new UsernameNotFoundException("user not found!");
        return user;
    }

    /**
     * generate alert description string
     * @param deviceInspect
     * @param inspectMessage
     * @param alertType
     * @return
     */
    private String getAlertMsg(DeviceInspect deviceInspect, InspectMessage inspectMessage, int alertType){
        String alertMsg = "";
        if (alertType == Constants.ALERT_CODE_RED) {
            if (inspectMessage.getCorrectedValue() > deviceInspect.getHighUp()) {
                alertMsg = String.format("inspect %s found value %f exceeded threshold %f at %s.",
                        deviceInspect.getName(), inspectMessage.getCorrectedValue(), deviceInspect.getHighUp(), inspectMessage.getSamplingTime());
            } else {
                alertMsg = String.format("inspect %s found value %f exceeded threshold %f at %s.",
                        deviceInspect.getName(), inspectMessage.getCorrectedValue(), deviceInspect.getHighDown(), inspectMessage.getSamplingTime());
            }

        } else if (alertType == Constants.ALERT_CODE_YELLOW) {

            // push notification if necessary
            if (inspectMessage.getCorrectedValue() > deviceInspect.getLowUp()) {
                alertMsg = String.format("inspect %s found value %f exceeded threshold %f at %s.",
                        deviceInspect.getName(), inspectMessage.getCorrectedValue(), deviceInspect.getLowUp(), inspectMessage.getSamplingTime());
            } else {
                alertMsg = String.format("inspect %s found value %f exceeded threshold %f at %s.",
                        deviceInspect.getName(), inspectMessage.getCorrectedValue(), deviceInspect.getLowDown(), inspectMessage.getSamplingTime());
            }
        }

        return alertMsg;
    }

    /**
     * 通过报警类型编码得到报警状态描述字符串
     * @param alertType
     * @return
     */
    private String getInspectStatusFromAlertType(int alertType){

        if(Constants.ALERT_CODE_STATUS_MAP.containsKey(alertType)){
            return Constants.ALERT_CODE_STATUS_MAP.get(alertType);
        }else{
            return Constants.UNDEFINED;
        }
    }


    /**
     * 判断报文是否为报警
     * @param deviceInspect
     * @param device
     * @param inspectMessage
     * @return
     */
    private int checkAlertOutofMessage(DeviceInspect deviceInspect, Device device, InspectMessage inspectMessage){
        int alert_type = Constants.ALERT_CODE_NO_ALERT;

        // alert inspect
        LOGGER.info("check data against alert");
        if (deviceInspect.getHighUp() < inspectMessage.getCorrectedValue() || inspectMessage.getCorrectedValue() < deviceInspect.getHighDown()) {
            alert_type = Constants.ALERT_CODE_RED;


        } else if (deviceInspect.getLowUp() < inspectMessage.getCorrectedValue() || inspectMessage.getCorrectedValue() < deviceInspect.getLowDown()) {
            alert_type = Constants.ALERT_CODE_YELLOW;
        }


        return alert_type;
    }

    /**
     * 将报警更新到设备在区块链上的状态
     * @param alertType
     * @param inspectMessage
     * @param device
     * @param deviceInspect
     */
    public void updateAlertToOnchainDevice(int alertType, InspectMessage inspectMessage, Device device, DeviceInspect deviceInspect){
        if (device.getDeviceChainKey() != null){
            List<DealRecord> dealRecords = dealRecordRepository.findByDeviceIdAndStatus(device.getId(), Constants.ONCHAIN_DEAL_STATUS_EXECUTING);

            if(dealRecords != null) {
                String alertMsg = getAlertMsg(deviceInspect, inspectMessage, alertType);
                for (DealRecord dealRecord : dealRecords) {
                    LOGGER.debug(String.format("found alert [%s] during deal %d.", alertMsg, dealRecord.getId()));
                    try {
                        dealRecord.setStatus(ONCHAIN_DEAL_STATUS_EXECUTING_WITH_ALERT);
                        BlockChainDealDetail data = new BlockChainDealDetail(dealRecord.getId(), dealRecord.getDevice().getId(), dealRecord.getLessor(),
                                dealRecord.getLessee(), dealRecord.getPrice(), dealRecord.getBeginTime().getTime(), dealRecord.getEndTime().getTime(),
                                dealRecord.getDeviceSerialNumber(), dealRecord.getAggrement(), dealRecord.getStatus());
                        BlockChainDealRecord value = new BlockChainDealRecord(DEAL_STATUS_TRANSFER_MAP.get(dealRecord.getStatus()), data);
                        LOGGER.info(String.format("Alert happens in device lease. Change transfer status to alert. lease record %d, alert time %s, alert -- %s",
                                dealRecord.getId(), inspectMessage.getSamplingTime(), alertMsg));
                        onchainService.sendStateUpdateTx("deal", String.valueOf(dealRecord.getId()) + String.valueOf(dealRecord.getDevice().getId()),
                                "", JSON.toJSONString(value));
                        dealRecordRepository.save(dealRecord);
                        dealAlertRecordRepository.save(new DealAlertRecord(inspectMessage.getSamplingTime(), dealRecord.getId(), alertMsg));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            else{
                LOGGER.debug("no transfer is ongoing when alert happened");
            }
        }

        // add a new type of alert, just for demo, and hard code the inspect type and threshold value directly into code, which I strongly don't recommend
        // 以下的code是demo的时候用来模拟设备事故触发报警, 正常运行时候不需要. 报警应该有报警参数的阈值来判断.
            /*
            if(inspectMessage.getInspectTypeCode().equals("16") && inspectMessage.getCorrectedValue() < 0.01 && device.getDeviceChainKey() != null){
                LOGGER.info("found currency down to zero, check whether it's in deal and without alert");
                List<DealRecord> dealRecords = dealRecordRepository.findByDeviceIdAndStatus(device.getId(), 2);
                if(dealRecords != null) {
                    for (DealRecord dealRecord : dealRecords) {
                        LOGGER.info(String.format("found device in deal %s when power failure problem happened.", dealRecord.getId()));
                        if(dealRecord.getEndTime().getTime() < inspectMessage.getSamplingTime().getTime()){
                            // if alert time has exceeded deal end time, pass
                            continue;
                        }
//                        Date currentTime = new Date();
                        List<List<Object>> deviceRunningStatusHistories = Application.influxDBManager.readDeviceOperatingStatusInTimeRange(device.getId(), dealRecord.getBeginTime(), dealRecord.getEndTime());
                        boolean isRun = false;
                        if(deviceRunningStatusHistories != null && deviceRunningStatusHistories.size() > 0){
                            for(List<Object> deviceRunningStatusHistory : deviceRunningStatusHistories){
                                LOGGER.info(String.format("device %d change status to %d at %s", device.getId(), ((Double)deviceRunningStatusHistory.get(1)).intValue(), new Date(TimeUtil.fromInfluxDBTimeFormat((String)deviceRunningStatusHistory.get(0)))));
                                if(((Double)deviceRunningStatusHistory.get(1)).intValue() == 20){
                                // device has run
                                    LOGGER.info(String.format("detect power failure problem, device has run at %s between deal id %d.", new Date(TimeUtil.fromInfluxDBTimeFormat((String)deviceRunningStatusHistory.get(0))), dealRecord.getId()));
                                    isRun = true;
                                    break;
                                }
                            }
                        }
                        else{
                            LOGGER.info(String.format("no device running status change log during %s, %s.", dealRecord.getBeginTime(), dealRecord.getEndTime()));
                        }
                        if(isRun) {
                            inspectStatus = "power failure";
                            alert_type = 3;
                            LOGGER.info(String.format("found alert %s during deal %d.", alertMsg, dealRecord.getId()));
                            messageController.sendPowerMsg(device, deviceInspect, inspectMessage.getSamplingTime());
                            alertMsg = String.format("power failure occured at %s.", inspectMessage.getSamplingTime());
                            try {
                                dealRecord.setStatus(ONCHAIN_DEAL_STATUS_EXECUTING_WITH_ALERT);
                                BlockChainDealDetail data = new BlockChainDealDetail(dealRecord.getId(), dealRecord.getDevice().getId(), dealRecord.getLessor(),
                                        dealRecord.getLessee(), dealRecord.getPrice(), dealRecord.getBeginTime().getTime(), dealRecord.getEndTime().getTime(),
                                        dealRecord.getDeviceSerialNumber(), dealRecord.getAggrement(), dealRecord.getStatus());
                                BlockChainDealRecord value = new BlockChainDealRecord(DEAL_STATUS_TRANSFER_MAP.get(dealRecord.getStatus()), data);
                                LOGGER.info(String.format("Change transfer status to alert. %d, %s, %s", dealRecord.getId(), inspectMessage.getSamplingTime(), alertMsg));
                                onchainService.sendStateUpdateTx("deal", String.valueOf(dealRecord.getId()) + String.valueOf(dealRecord.getDevice().getId()),
                                        "", JSON.toJSONString(value));
                                dealRecordRepository.save(dealRecord);
                                dealAlertRecordRepository.save(new DealAlertRecord(inspectMessage.getSamplingTime(), dealRecord.getId(), alertMsg));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                else{
                    LOGGER.info("no transfer is ongoing when alert happened");
                }

            }
            */
    }


    /**
     * 将报文写入 influxdb
     * @param device
     * @param deviceInspect
     * @param inspectMessage
     * @param alertType
     */
    private void writeInspectMessageToInflux(Device device, DeviceInspect deviceInspect, InspectMessage inspectMessage, int alertType){
        if(Application.influxDBManager != null){

            String inspectStatus = getInspectStatusFromAlertType(alertType);

            try {
                int retry = 0;
                int max_try = 3;

                while(retry < max_try) {
                    boolean writeSuccess = Application.influxDBManager.writeTelemetry(inspectMessage.getSamplingTime(), device.getId(),
                            device.getName(), device.getDeviceType().getName(),
                            inspectStatus,  deviceInspect.getId(),
                            deviceInspect.getInspectType().getMeasurement(),
                            inspectMessage.getCorrectedValue(), inspectMessage.getOriginalValue());

                    if(!writeSuccess){
                        Thread.sleep(200);
                        retry ++;
                    }
                    else{
                        LOGGER.debug(String.format("Successfully write Device [%d] telemetry %s (%s) to influxdb",
                                device.getId(),
                                //InspectProcessTool.getMeasurementByCode(inspectMessage.getInspectTypeCode()),
                                deviceInspect.getInspectType().getMeasurement(),
                                inspectMessage.getCorrectedValue()));

                        break;
                    }
                }

                if(retry >= max_try){
                    LOGGER.error(String.format("Abort writing telemetry %s (%s) after %d approach",
                            deviceInspect.getInspectType().getMeasurement(),
                            //InspectProcessTool.getMeasurementByCode(inspectMessage.getInspectTypeCode()),
                            inspectMessage.getCorrectedValue(), max_try));
                }

            }catch (Exception e){
                LOGGER.error(String.format("Failed to parse %s telemetry data %s, %s",
                        deviceInspect.getInspectType().getMeasurement(),
                        //InspectProcessTool.getMeasurementByCode(inspectMessage.getInspectTypeCode()),
                        inspectMessage.getCorrectedValue(), inspectMessage.getOriginalValue()));

            }
        }

    }

    /**
     * 当报文为报警时, 更新报警信息表 alert_count
     * @param device
     * @param deviceInspect
     * @param inspectMessage
     * @param alertType
     * @return
     */
    private AlertCount updateAlertCount(Device device, DeviceInspect deviceInspect, InspectMessage inspectMessage, int alertType){
        AlertCount liveAlert = null;

        // 判断 是否是新的报警
        boolean isNewAlert = false;

        // 获取该参数的上一条信息
        List<Object> lastInspectRecord = Application.influxDBManager.readLatestTelemetry(
                deviceInspect.getInspectType().getMeasurement(),
                device.getId(),
                deviceInspect.getId());

        long lastInspectTime = 0;
        String lastInspectStatus = null;

        // 如果这是该设备该参数第一条报文, 判断为新报警
        if(lastInspectRecord==null || lastInspectRecord.size()==0){
            isNewAlert = true;
        }else{
            //如果上一条报文和当前报文的时间戳相隔超过5分钟, 判断为新报警
            lastInspectTime = TimeUtil.fromInfluxDBTimeFormat((String)lastInspectRecord.get(0));
            lastInspectStatus = (String)lastInspectRecord.get(2);

            if(inspectMessage.getSamplingTime().getTime() - lastInspectTime > 5 * 60 * 1000){
                isNewAlert = true;
            }

            //如果上一条报文不是报警, 当前报文必定为新报警
            else if(lastInspectStatus.equals("normal")){
                isNewAlert = true;
            }
            else{
                // 上一条报文是报警

                // 从table alert_count 获取该设备改参数的上一条黄色报警和红色报警, 并通过时间戳和上一条报文的时间戳的比较来确定哪个是正在进行中的报警
                AlertCount last_yellow_alert = alertCountRepository.findTopByDeviceIdAndInspectTypeIdAndTypeAndFinishBeforeOrderByFinishDesc(
                        deviceInspect.getDevice().getId(), deviceInspect.getInspectType().getId(), 1, inspectMessage.getSamplingTime());
                AlertCount last_red_alert = alertCountRepository.findTopByDeviceIdAndInspectTypeIdAndTypeAndFinishBeforeOrderByFinishDesc(
                        deviceInspect.getDevice().getId(), deviceInspect.getInspectType().getId(), 2, inspectMessage.getSamplingTime());


                if (last_yellow_alert != null && last_yellow_alert.getFinish() != null && lastInspectTime == last_yellow_alert.getFinish().getTime()) {
                    liveAlert = last_yellow_alert;
                }
                else if (last_red_alert != null && last_red_alert .getFinish() != null && lastInspectTime == last_red_alert.getFinish().getTime()) {
                    liveAlert = last_red_alert;
                }

                if (liveAlert == null) {
                    // 上一条报文的时间戳和alert_count里报警的时间戳不匹配. 运行到这里, 必然是因为有错, 可能是因为上一条报文没有写入到influxdb
                    LOGGER.error(String.format("Device id: %d, Inspect type: %s, last alert timestamp does not match last message. There must be something wrong, maybe "
                            + " last message not written to influxdb. Sample Time %s. Now, try to find the latest alert from table alert_count and check if it can be live alert",
                            device.getId(), deviceInspect.getInspectType().getName(), inspectMessage.getSamplingTime().toString()));

                    // 在上一条红色/黄色报警中找出'最近更新时间'距离当前时间最近的一条
                    AlertCount latestAlert = null;
                    boolean red_alert_available = (last_red_alert != null) && (last_red_alert.getFinish() != null);
                    boolean yellow_alert_available = (last_yellow_alert != null) && (last_yellow_alert.getFinish() != null);
                    if(red_alert_available && yellow_alert_available){
                        latestAlert = last_red_alert.getFinish().getTime() >= last_yellow_alert.getFinish().getTime() ?
                                last_red_alert : last_yellow_alert;
                    }
                    else if(red_alert_available){
                        latestAlert = last_red_alert;
                    }
                    else if(yellow_alert_available){
                        latestAlert = last_yellow_alert;
                    }

                    //如果alert_count中没有找到任何一条报警信息, 或者都是5分钟之前的, 判断当前报文是新报警. 否则设定该条报警为当前正在活跃的报警
                    if (latestAlert == null || inspectMessage.getSamplingTime().getTime() - latestAlert.getFinish().getTime() > 5 * 60 * 1000) {
                        isNewAlert = true;
                    } else {
                        // set newer alert as live alert
                        liveAlert = latestAlert;
                    }
                }

                // 如果与上一条报警是不同的类型， 处理为新报警
                if (liveAlert != null) {
                    if (liveAlert.getType() != alertType) {
                        isNewAlert = true;
                    } else {
                        // 否则, 判断为旧报警, 更新报警条目的'最近更新时间'
                        liveAlert.setNum(liveAlert.getNum() + 1);
                        liveAlert.setFinish(inspectMessage.getSamplingTime());
                        alertCountRepository.save(liveAlert);
                        LOGGER.info(String.format("this is existing %s alert %d, updating finish time to db", getInspectStatusFromAlertType(alertType), liveAlert.getId()));
                    }
                }
            }

        }

        if(isNewAlert){
            // 如果目前判断为新报警, 在alert_count table里新增一条
            String alertMsg = getAlertMsg(deviceInspect, inspectMessage, alertType);
            liveAlert = AlertCount.createNewAlertAndSave(alertCountRepository, device, deviceInspect.getInspectType(), alertType, unit, inspectMessage.getSamplingTime());

            // 如果该设备在区块链上
            if (device.getDeviceChainKey() != null){
                List<DealRecord> dealRecords = dealRecordRepository.findByDeviceIdAndStatus(device.getId(), ONCHAIN_DEAL_STATUS_EXECUTING_WITH_ALERT);
                if(dealRecords != null) {
                    for (DealRecord dealRecord : dealRecords) {
                        LOGGER.info(String.format("found alert %s during deal %d.", alertMsg, dealRecord.getId()));
                        try {
                            dealAlertRecordRepository.save(new DealAlertRecord(inspectMessage.getSamplingTime(), dealRecord.getId(), alertMsg));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        }
        return liveAlert;
    }

    private void pushAlertNotification(AlertCount liveAlert, Device device, DeviceInspect deviceInspect, InspectMessage inspectMessage){
        if(liveAlert == null){
            LOGGER.warn("Alert object is null. Something wrong!!!");
        }
        else {
            if (liveAlert.getType() == Constants.ALERT_CODE_RED) {
                if (inspectMessage.getCorrectedValue() > deviceInspect.getHighUp()) {
                    messageController.sendAlertMsg(device, liveAlert, deviceInspect.getHighUp(), inspectMessage.getCorrectedValue(), inspectMessage.getSamplingTime());

                } else {
                    messageController.sendAlertMsg(device, liveAlert, deviceInspect.getHighDown(), inspectMessage.getCorrectedValue(), inspectMessage.getSamplingTime());

                }
            }

            else if(liveAlert.getType() == Constants.ALERT_CODE_YELLOW) {
                if (inspectMessage.getCorrectedValue() > deviceInspect.getLowUp()) {
                    messageController.sendAlertMsg(device, liveAlert, deviceInspect.getLowUp(), inspectMessage.getCorrectedValue(), inspectMessage.getSamplingTime());

                } else {
                    messageController.sendAlertMsg(device, liveAlert, deviceInspect.getLowDown(), inspectMessage.getCorrectedValue(), inspectMessage.getSamplingTime());

                }
            }
        }
    }

    /**
     * 处理报警参数的报文信息
     * @param device
     * @param deviceInspect
     * @param inspectMessage
     * @param onlineData
     * @return
     */
    private int processInspectMessageAboutAlert(Device device, DeviceInspect deviceInspect, InspectMessage inspectMessage, boolean onlineData){
        // 判断监控值是否触发报警
        if (null == deviceInspect.getStandard() || null == deviceInspect.getHighUp() || null == deviceInspect.getLowDown()) {
            LOGGER.warn(String.format("This alert inspect %d has no alert parameter, save inspect data and return", deviceInspect.getId()));
            return Constants.ALERT_CODE_NO_ALERT;
        }

        int alertType = checkAlertOutofMessage(deviceInspect, device, inspectMessage);

        if(alertType > Constants.ALERT_CODE_NO_ALERT) {

            // alert inspect
            // update device alert time and alert status
            if(onlineData){
                memoryCacheDevice.updateDeviceAlertTimeAndType(device.getId(), inspectMessage.getSamplingTime(), alertType);
            }

            updateAlertToOnchainDevice(alertType, inspectMessage, device, deviceInspect);
            LOGGER.info("updateAlertToOnchainDevice");

            // 更新 alert_count 表, 逻辑描述参看 LAB-194
            AlertCount liveAlert = updateAlertCount(device, deviceInspect, inspectMessage, alertType);

            // 发送报警信息到用户
            pushAlertNotification(liveAlert, device, deviceInspect, inspectMessage);
        }

        return alertType;

    }

    /**
     * 处理运行状态参数的报文信息
     * @param device
     * @param deviceInspect
     * @param inspectMessage
     */
    private void processInspectMessageAboutOperatingStatus(Device device, DeviceInspect deviceInspect, InspectMessage inspectMessage) {
        int runningStatus = 0;

        // 通过阈值查看设备当前运行状态
        if (deviceInspect.getInspectPurpose() == Constants.INSPECT_PURPOSE_OPERATING_STATUS_BY_THRESHOLDS) {
            LOGGER.debug("check operating status against threshold");
            List<DeviceInspectRunningStatus> runningStatuses = deviceInspectRunningStatusRepository.findByDeviceInspectIdOrderByThresholdAsc(deviceInspect.getId());
            if (runningStatuses != null && runningStatuses.size() > 0) {
                for (DeviceInspectRunningStatus deviceRunningStatus : runningStatuses) {
                    if (inspectMessage.getCorrectedValue() > deviceRunningStatus.getThreshold()) {
                        runningStatus = deviceRunningStatus.getDeviceRunningStatus().getLevel();
                    }
                }
            }
        }

        // 通过机器学习模型来判断当前运行状态
        else if (deviceInspect.getInspectPurpose() == Constants.INSPECT_PURPOSE_OPERATING_STATUS_BY_LEARNING_MODEL) {
            LOGGER.debug("check operating status against model");
            if (deviceInspect.getModels() != null) {
                Models models = deviceInspect.getModels();
                // 检验UseAML模型里面的数据是否需要更新
                long beginTimeTest = new Date().getTime();
                if (deviceInspect.getUseModelTime() == null || ((new Date().getTime() - deviceInspect.getUseModelTime().getTime()) / (60 * 60 * 1000) >= deviceInspect.getLevel().getInterval())) { // 代表之前没用使用过模型去学习数据，判断设备运行状态。
                    // 设置使用模型的时间，使用模型去学习一次，并更新相应的时间间隔等级。
                    // step 1:
                    deviceInspect.setUseModelTime(new Date());

                    // step 2:
                    Double result = KMeansEmulate.doTask(models.getUrl(), models.getApi(), deviceInspect.getInspectType().getMeasurement(), deviceInspect.getDevice().getId().toString(), null, new Date().getTime());

                    // step 3:
                    DecimalFormat df = new DecimalFormat("######0");
                    int resultInt = Integer.parseInt(df.format(result * 100));
                    List<OpeModelsLevel> opeModelsLevels = opeModelsLevelRepository.findAll();
                    for (OpeModelsLevel opeModelsLevel : opeModelsLevels) {
                        if (resultInt >= opeModelsLevel.getLevel()) {
                            deviceInspect.setLevel(opeModelsLevel);
                        }
                    }
                    // TODO: should avoid writing if there is no change
                    deviceInspectRepository.save(deviceInspect);
                }

                // 实施数据与UseAML模型比对，生成最新状态。
                int result = kMeansUse.doTask(deviceInspect.getDevice().getId().toString(), deviceInspect.getInspectType().getMeasurement(), inspectMessage.getCorrectedValue().toString());
                long endTimeTest = new Date().getTime();
                LOGGER.debug("************运行模型耗费的时间，单位秒**********：" + (endTimeTest - beginTimeTest) / 1000);
                // TODO: why there is no idling
                if (result == 0)
                    runningStatus = Constants.DEVICE_OPERATING_STATUS_STOPPED;
                else
                    runningStatus = Constants.DEVICE_OPERATING_STATUS_RUNNING;
            }
        }

        // 判断运行状态是否改变
        if (device.getLatestRunningStatus() == null || device.getLatestRunningStatus() != runningStatus) {
            device.setLatestRunningStatus(runningStatus);
            LOGGER.info(String.format("Device %d change running status to %d", device.getId(), runningStatus));
            try {
                deviceRepository.save(device);
                if (!Application.influxDBManager.writeDeviceOperatingStatus(inspectMessage.getSamplingTime(), device.getId(),
                        device.getName(), device.getDeviceType().getName(), runningStatus)) {
                    LOGGER.error(String.format("Writing operating status change %d of device %d to influxdb failed", runningStatus, device.getId()));
                }
            } catch (Exception e) {
                LOGGER.error(String.format("Failed to write device running status change into database at %s, %s",
                        new Date().toString(),
                        e.toString()));
            }
        }
    }

    /**
     * 解析报文并处理
     * @param inspectMessageString
     * @param onlineData
     * @return
     */
    public DeviceInspect parseAndProcessInspectMessage(String inspectMessageString, boolean onlineData) {

        // 解析报文
        InspectMessage inspectMessage = null;
        try{
            inspectMessage = new InspectMessage(inspectMessageString);
            LOGGER.info("Parsing inspect message.");
        }catch (Exception parseException){
            LOGGER.error(String.format("Failed to parse inspect message string %s. Err: %s", inspectMessageString, parseException.getMessage()));
            return null;
        }


        // 根据解析的报文从数据库读取设备，监控参数信息
        Device device = null;
        MonitorDevice monitorDevice = null;
        InspectType inspectType = null;

        DeviceInspect deviceInspect = null;

        try{
            monitorDevice = monitorDeviceRepository.findByNumber(inspectMessage.getMonitorSN());
            if (null == monitorDevice) {
                return null;
            }
            LOGGER.info("monitorDevice");

            device = monitorDevice.getDevice();
            if (device.getEnable() == 0) {
                LOGGER.warn(String.format("This device %d is disabled. Should not receive data from disabled device.", device.getId()));
                return null;
            }
            LOGGER.info("Device");

            inspectType = inspectTypeRepository.findByCode(inspectMessage.getInspectTypeCode());
            if(inspectType == null){
                LOGGER.warn("Failed to get inspectType using type code " + inspectMessage.getInspectTypeCode());
                return null;
            }
            LOGGER.info("InspectType");

            deviceInspect = deviceInspectRepository.
                    findByInspectTypeIdAndDeviceId(inspectType.getId(), device.getId());
            if(deviceInspect == null && !inspectMessage.getInspectTypeCode().equals("03")){
                LOGGER.warn(String.format("Failed to get device inspect by type %s, device %d", inspectType.getName(), device.getId()));
                return null;
            }
            LOGGER.info("DeviceInspect");

            // 报文是电池电量, 特殊处理
            if (inspectMessage.getInspectTypeCode().equals("03")) {
                monitorDevice.setBattery(String.valueOf(Float.valueOf(inspectMessage.getiData()) / 10));
                // add abstract device inspect for all battery message, used in following alert parse
                deviceInspect = new DeviceInspect();
                deviceInspect.setZero(0F);
                deviceInspect.setId(-1);
                deviceInspect.setStandard(100F);
                deviceInspect.setHighDown(0.2F);
                deviceInspect.setLowDown(0.2F);
                deviceInspect.setHighUp(110F);
                deviceInspect.setLowUp(110F);
                deviceInspect.setInspectPurpose(0);
                deviceInspect.setName("Remain Battery Percentage");
                deviceInspect.setInspectType(inspectType);
                monitorDeviceRepository.save(monitorDevice);
                LOGGER.info("InspectMessage equals battery.");
            }
        }catch (Exception e){
            LOGGER.error("Failed to get device/monitor/inspectType from database. Err: " + e.toString());
            return null;
        }
        // 如果是在线数据，更新内存缓存设备的最新活动信息
        if(onlineData){
            memoryCacheDevice.updateDeviceActivityTime(device.getId(), inspectMessage.getSamplingTime());
            LOGGER.info("OnlineData");
        }

        // 把原始监控数值转换为直观数值

        try{
            InspectProcessTool.calculateInspectValue(inspectMessage, deviceInspect.getZero(), pt100Repository);
            LOGGER.info("Calculate inspect value.");
        }catch (Exception e){
            LOGGER.error("Failed to calculate inspect value. Err: " + e.toString());
            e.printStackTrace();
            return null;
        }

        // 判断监控数值是否非法
        if (inspectMessage.getOriginalValue() == -200f && inspectMessage.getCorrectedValue() == -300f){
            //这个判断如果为true的话，表示上面的r值为非法值，则不需要进行一下处理，直接返回。
            LOGGER.warn("Monitoring illegal value");
            return null;
        }


        ////////////////// 报警参数的处理 ////////////////////
        int alertType = Constants.ALERT_CODE_NO_ALERT;
        if(deviceInspect.getInspectPurpose() == 0){
            alertType = processInspectMessageAboutAlert(device, deviceInspect, inspectMessage, onlineData);
        }


        /////////////////////// 写入监测数据到 influx DB ////////////////
        writeInspectMessageToInflux(device, deviceInspect, inspectMessage, alertType);

        //////////////////////// 运行状态参数的处理 /////////////////////

        if (deviceInspect.getInspectPurpose() == Constants.INSPECT_PURPOSE_OPERATING_STATUS_BY_THRESHOLDS
                || deviceInspect.getInspectPurpose() == Constants.INSPECT_PURPOSE_OPERATING_STATUS_BY_LEARNING_MODEL){

            processInspectMessageAboutOperatingStatus(device, deviceInspect, inspectMessage);

        }

        return deviceInspect;

    }

    void writeDeviceRunningStatus(Device device, InspectMessage inspectMessage, Integer runningStatus){
        int retry = 0;
        int max_try = 3;

        try {
            while (retry < max_try) {
                boolean writeSuccess = Application.influxDBManager.writeDeviceOperatingStatus(inspectMessage.getSamplingTime(), device.getId(), device.getName(), device.getDeviceType().getName(), runningStatus);

                if (!writeSuccess) {
                    Thread.sleep(200);
                    retry++;
                } else {
                    LOGGER.debug(String.format("Successfully write Device [%d] running status %s to influxdb",
                            device.getId(), runningStatus));

                    break;
                }
            }

            if (retry >= max_try) {
                LOGGER.error(String.format("Abort writing device %s running status (%s) after %d approach",
                        device.getId(), runningStatus,  max_try));
            }
        }catch (Exception e){
            LOGGER.error("Failed to write running status. Err:" + e.toString());
        }
    }


    /**
     * This is just used for testing the reply sms to cancel alert push
     * @param params
     * @return
     */
    @RequestMapping(value = "/socket/cancel/push", method = RequestMethod.GET)
    public RestResponse cancelAlertPush(@RequestParam Map<String, String> params){
        if(!Application.isTesting){
            return null;
        }

        String deviceId = params.get("deviceId");
        String mobile = params.get("mobile");

        messageController.processReceivedReplyMessage(mobile, deviceId);

        return new RestResponse();
    }


    /**
     * 更新数据
     * @param result
     * @return
    */
    @RequestMapping(value = "/socket/insert/data",method = RequestMethod.GET)
    public RestResponse excuteInspectData(@RequestParam String result) {
        DeviceInspect deviceInspect = parseAndProcessInspectMessage(result, true);
        if(deviceInspect == null) {
            return new RestResponse(null);
        }

        LOGGER.info("add response datagram head");
        String response = null;
        List<Byte> responseByte = new ArrayList<Byte>();
        responseByte.add((byte)0xEF);
        responseByte.add((byte)0x02);
        responseByte.add((byte)0x05);

        responseByte.add((byte) 0x00);
        try {
            if (null!=deviceInspect.getStandard()&&null!=deviceInspect.getLowDown()&&null!=deviceInspect.getLowUp()&&
                    null!=deviceInspect.getHighDown()&&null!=deviceInspect.getHighUp()){
                //默认不存在没有报警值得情况

                float lowUp = deviceInspect.getLowUp();
                float lowDown = deviceInspect.getLowDown();
                float highUp = deviceInspect.getHighUp();
                float highDown = deviceInspect.getHighDown();

                for (byte one : ByteAndHex.intToByteArray((int) lowUp*1000))
                    responseByte.add(one);
                for (byte two : ByteAndHex.intToByteArray((int) lowDown*1000) )
                    responseByte.add(two);
                for (byte three : ByteAndHex.intToByteArray((int) highUp*1000))
                    responseByte.add(three);
                for (byte four : ByteAndHex.intToByteArray((int)highDown*1000))
                    responseByte.add(four);
            }else responseByte.add((byte)0x00);

            responseByte.add((byte)0xFF);
            responseByte.add((byte)0x02);
            byte[] message = new byte[responseByte.size()];
            for (int i = 0; i < responseByte.size(); i++) {
                message[i] = responseByte.get(i);
            }
            response = ByteAndHex.bytesToHexString(message);
        } catch (Exception e) {
            LOGGER.error("Failed to generate response message. " + e.getLocalizedMessage());
            LOGGER.error("exception stack: ", e);
        }
        return new RestResponse(response);
    }

    /**
     * 设备绑定数据内容
     */
    @RequestMapping(value = "/device/current/data", method = RequestMethod.GET)
    public RestResponse getCurrentData(Principal principal, @RequestParam Map<String,String> requestParam){
        User user = judgeByPrincipal(principal);
        if (null == user.getCompany()){
            return new RestResponse("user's information incorrect!",1005,null);
        }

        Integer deviceId = Integer.parseInt(requestParam.get("deviceId"));
        Device device = deviceRepository.findOne(deviceId);
        List<DeviceInspect> deviceInspectList = deviceInspectRepository.findByDeviceId(deviceId);
        Map map = new HashMap();
        List<List> list = new ArrayList<List>();
        Float  score = Float.valueOf(100);
        Date currentTime = new Date();

        RestDeviceMonitoringTSData restDeviceMonitoringTSData = new RestDeviceMonitoringTSData();
        restDeviceMonitoringTSData.setDeviceId(deviceId);
        restDeviceMonitoringTSData.setDeviceName(device.getName());
        restDeviceMonitoringTSData.setEndTime(String.valueOf(currentTime.getTime()));

        if(requestParam.get("timeVal") != null){
            Long startTime = Long.parseLong(requestParam.get("timeVal"));
            // if timeVal is less than 10 sec before current server time, return 10 sec before current server time as begin time
            if(startTime > new Date().getTime() - 1000*10 ){
                requestParam.put("timeVal", String.valueOf(new Date().getTime() - 1000*10));
            }
            restDeviceMonitoringTSData.setStartTime(requestParam.get("timeVal"));

        }else{
            Long time5minBefore = currentTime.getTime() - 5 * 60 * 1000;
            restDeviceMonitoringTSData.setStartTime(time5minBefore.toString());
        }
        List<RestTelemetryTSData> restTelemetryTSDataList = new ArrayList<RestTelemetryTSData>();
        restDeviceMonitoringTSData.setTelemetries(restTelemetryTSDataList);

        Integer runningLevel = -1;
        if (null!=deviceInspectList&&deviceInspectList.size()>0){
            for (DeviceInspect deviceInspect : deviceInspectList){
                boolean isStatusInspect = false;
                boolean isNotRequiredData = false;
                if(deviceInspect.getInspectPurpose() == 1 || deviceInspect.getInspectPurpose() == 2){
                    // when device has status inspect, lowest running level is 0 (shut down)
                    isStatusInspect = true;
                    if(runningLevel == -1){
                        runningLevel = 0;
                    }
                }

                //String measurementName = InspectProcessTool.getMeasurementByCode(deviceInspect.getInspectType().getCode());
                String measurementName = deviceInspect.getInspectType().getMeasurement();
                String measurementUnit = deviceInspect.getInspectType().getUnit();
                        //InspectProcessTool.getMeasurementUnitByCode(deviceInspect.getInspectType().getCode());
                List<List<Object>> inspectSeries = null;
                if(requestParam.get("timeVal") != null){
                    Date startTime = new Date();
                    startTime.setTime(Long.parseLong(requestParam.get("timeVal")));

                    // each List<Object> is [time, value]
                    inspectSeries = Application.influxDBManager.readTelemetryInTimeRange(measurementName,
                            deviceId, deviceInspect.getId(), startTime, new Date(), Calendar.SECOND);
                    // for status inspect, if there is no data in given timeVal, try to get recent 5 minutes data, and set flag for not sending these data back
                    if(inspectSeries == null && isStatusInspect){
                        startTime = new Date (currentTime.getTime() - 5 * 60 * 1000);
                        inspectSeries = Application.influxDBManager.readTelemetryInTimeRange(measurementName,
                                deviceId, deviceInspect.getId(), startTime, new Date(), Calendar.SECOND);
                        isNotRequiredData = true;
                    }
                }
                else{
                    // default to get data in latest 5 minutes
                    Date startTime = new Date (currentTime.getTime() - 5 * 60 * 1000);
                    inspectSeries = Application.influxDBManager.readTelemetryInTimeRange(measurementName,
                            deviceId, deviceInspect.getId(), startTime, new Date(), Calendar.SECOND);
                }

                List<Long> timeSeries = new ArrayList<Long>();
                List<Float> valueSeries = new ArrayList<Float>();

                if(inspectSeries != null && inspectSeries.size() > 0) {

                    //device running status
                    if(deviceInspect.getInspectPurpose() == 1){ // this inspect is used to guide running status
                        List<DeviceInspectRunningStatus> runningStatuses = deviceInspectRunningStatusRepository.findByDeviceInspectId(deviceInspect.getId());
                        for(DeviceInspectRunningStatus status : runningStatuses){
                            if(Float.parseFloat(inspectSeries.get(0).get(1).toString()) > status.getThreshold()){
                                if(status.getDeviceRunningStatus().getLevel() > runningLevel){
                                    runningLevel = status.getDeviceRunningStatus().getLevel();
                                }
                            }
                        }
                    }

                    // copy data to REST response, if the data is not required (only used for get running status), do not send them back
                    if(!isNotRequiredData) {
                        for (List<Object> telemetryEntry : inspectSeries) {
                            String timeRFC3999 = telemetryEntry.get(0).toString();

                            long timeStamp = TimeUtil.fromInfluxDBTimeFormat(timeRFC3999);
                            timeSeries.add(timeStamp);
                            valueSeries.add(Float.parseFloat(telemetryEntry.get(1).toString()));
                        }
                    }

                    RestTelemetryTSData telemetryTSData = new RestTelemetryTSData();
                    telemetryTSData.setName(measurementName.toUpperCase());
                    telemetryTSData.setCode(deviceInspect.getInspectType().getCode());
                    telemetryTSData.setUnit(measurementUnit.toUpperCase());
                    telemetryTSData.setDeviceInspectId(deviceInspect.getId());
                    telemetryTSData.setTimeSeries(timeSeries);
                    telemetryTSData.setValueSeries(valueSeries);

                    restTelemetryTSDataList.add(telemetryTSData);
                }

            }
        }
        map.put("runningStatus", runningLevel);
        map.put("batteryStatus", device.getMonitorDevice().getBattery());
        map.put("tsdata", restDeviceMonitoringTSData);
        return new RestResponse(map);

    }


}
