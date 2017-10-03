package com.device.inspect.common.managers;

import com.device.inspect.Application;
import com.device.inspect.common.model.charater.User;
import com.device.inspect.common.model.device.*;
import com.device.inspect.common.model.firm.Building;
import com.device.inspect.common.model.firm.Room;
import com.device.inspect.common.model.firm.Storey;
import com.device.inspect.common.repository.charater.UserRepository;
import com.device.inspect.common.repository.device.DeviceFloorRepository;
import com.device.inspect.common.repository.device.DeviceInspectRepository;
import com.device.inspect.common.repository.device.ScientistDeviceRepository;
import com.device.inspect.common.repository.record.MessageSendRepository;
import com.device.inspect.common.service.MessageSendService;
import com.device.inspect.common.setting.Constants;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.influxdb.impl.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zyclincoln on 4/14/17.
 */
@Service
public class MessageController {

    private static final Logger LOGGER = LogManager.getLogger(MessageController.class);

    public static final String PUSH_MESSAGE_ACTIVE = "active";
    public static final String PUSH_MESSAGE_CANCEL = "cancelled";
    public static final String MESSAGE_TYPE_ALERT = "alert";
    public static final String MESSAGE_TYPE_VERIFICATION = "verify";
    public static final String MESSAGE_MEDIA_SMS = "SMS";
    public static final String MESSAGE_MEDIA_EMAIL = "Email";
    public static final String MESSAGE_RESULT_SUCCESS = "OK";
    public static final String MESSAGE_RESULT_FAILURE = "ERROR";
    public static final String MESSAGE_ACTION_SEND = "send";
    public static final String MESSAGE_ACTION_RECV = "receive";



    @Autowired
    private MessageSendRepository messageSendRepository;

    @Autowired
    private DeviceInspectRepository deviceInspectRepository;

    @Autowired
    private DeviceFloorRepository deviceFloorRepository;

    @Autowired
    private ScientistDeviceRepository scientistDeviceRepository;

    @Autowired
    private UserRepository userRepository;

    final private String alertFormat = "INTELAB报警：【%s-%s】于【%s】检测到【%s】异常";
    final private String valueFormat = "（阈值【%.2f】，检测值【%.2f】）。";
    final private String doorInfoFormat = "检测到【门开关】参数异常。";
    final private String locationInfoFormat = "请尽快去现场【%s】检查。";
    final private String doorAlertFormat = ",门打开时间超过%d分钟。";
    final private String OffLineMsgFormat = "【%s-%s】于【%s】检测到网络异常，";
    final private String powerMsgFormat = "【%s-%s】于【%s】检测到供电异常，";
    final private Integer doorInspectId = 8;
    final private float doorOpen = 1;

    /**
     * 电量报警函数，发送供电异常报警信息
     */

    public void sendPowerMsg(Device device, AlertCount alert, Date sampleTime){
        DateFormat formatter= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss Z");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

        String message = String.format(powerMsgFormat, device.getCode(), device.getName(), formatter.format(sampleTime));
        message += getAddressOfDevice(device);
        sendMessage(device, alert, sampleTime, message);
    }


    /**
     * 掉线报警信息，发送掉线异常报警信息
     */
    public void sendOfflineMsg(Device device, AlertCount alert, Date sampleTime){
        DateFormat formatter= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss Z");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

        String message = String.format(OffLineMsgFormat, device.getCode(), device.getName(), formatter.format(sampleTime));
        message += getAddressOfDevice(device);
        sendMessage(device, alert, sampleTime, message);
    }

    /**
     * 监控参数报警函数，分析异常情况并发送报警信息
     */
    public void sendAlertMsg(Device device, AlertCount alert, Float standard, Float value, Date sampleTime){
        DateFormat formatter= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss Z");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

        String message = String.format(alertFormat, device.getCode(), device.getName(), formatter.format(sampleTime),
                alert.getInspectType().getName());

        DeviceInspect doorInspect = deviceInspectRepository.findByInspectTypeIdAndDeviceId(doorInspectId, device.getId());

        // if this alerting inspect is not door, get door status if door is an inspect of this device.
        if(alert.getInspectType().getId() != doorInspectId){
            if(doorInspect != null) {
                List<Object> doorInspectData = Application.influxDBManager.readLatestTelemetry(
                        alert.getInspectType().getMeasurement(),
                        device.getId(), doorInspect.getId());


                message += String.format(valueFormat, standard, value);

                if (doorInspectData != null && ((Double) doorInspectData.get(1)).floatValue() == doorOpen) {
                    LOGGER.info(String.format("alerting inspect %d of device %d is with door open.",
                            alert.getInspectType().getName(),
                            device.getId()));
                    message += doorInfoFormat;
                }
            }
        }
        else{
            // this alerting inspect is door inspect
            // get top 20 value of door inspect from db, and find how long has the door been open
            Date time3minBefore = DateUtils.addMinutes(sampleTime, -3);
            List<List<Object>> doorInspectData = Application.influxDBManager.readTelemetryInTimeRange(
                    alert.getInspectType().getMeasurement(),
                    device.getId(), doorInspect.getId(), time3minBefore, sampleTime, Calendar.SECOND);

            if(doorInspectData != null && doorInspectData.size() > 0){
                Long openMilisecond = new Long(0);

                for(int i=doorInspectData.size()-1; i>=0; i--){
                    if(((Double)doorInspectData.get(i).get(1)).floatValue() == doorOpen){
                        openMilisecond = sampleTime.getTime() - TimeUtil.fromInfluxDBTimeFormat((String)doorInspectData.get(i).get(0));
                    }
                    else{
                        break;
                    }

                }
                // if the continuous open time is less than one minutes, do nothing
                if(openMilisecond < 1*60*1000){
                    LOGGER.info(String.format("Device %d, door open for %d sec, less than 1 minutes, skip push notification",
                            device.getId(),
                            openMilisecond / 1000));
                    return;
                }
                else{
                    LOGGER.info(String.format("Device %d, door open for %d sec, more than 1 minutes, moving forward on push notification",
                            device.getId(),
                            openMilisecond / 1000));
                    message += String.format(doorAlertFormat, (int)(openMilisecond/1000/60));

                }
            }

        }

        // get location of the device
        message += getAddressOfDevice(device);

        // get most recent notification sent to the device manager
        sendMessage(device, alert, sampleTime, message);
    }

    private void sendMessage(Device device, AlertCount alert, Date sampleTime, String message){

        // get all candidate message receivers
        List<User> userList = new ArrayList<>();

        userList.add(device.getManager());

        List<ScientistDevice> scientistList = scientistDeviceRepository.findByDeviceId(device.getId());

        for(ScientistDevice scientist : scientistList){
            userList.add(scientist.getScientist());
        }

        // for each receiver, check if it is necessary to send message

        for(User user : userList){
            List<Object> latestMessage = Application.influxDBManager.readLatestMessageByUserIdInspectIdAction(user.getId(), alert.getInspectType().getId(), MESSAGE_ACTION_SEND);

            if(latestMessage != null && !latestMessage.isEmpty()){
                if(sampleTime.getTime() - TimeUtil.fromInfluxDBTimeFormat((String)latestMessage.get(0)) < 5*60*1000){
                    LOGGER.info(String.format("Device %d, alert message has been sent to manager at %s within 5 minutes skip this time.",
                            device.getId(),
                            latestMessage.get(0)));
                    continue;
                }
            }


            LOGGER.info(String.format("Device %d, last alert is more than 5 minutes away, sending alert to manager %s",
                    device.getId(),
                    device.getManager().getTelephone()));

            // 将所有的报警时间都抄送到test@ilabservice.com这个邮箱，不管用户是否选择报警。
            if(MessageSendService.sendEmailToIntelabTest(message)){
                LOGGER.info("Successfully sent alert to test@ilabservice.com. " + message);

            }else{
                LOGGER.warn("Failed to sent alert to test@ilabservice.com. "  + message);
            }

            // 在没有网的情况下，通过SIM800将报警信息以短信的方式发送给指定的号码
//            if (device.getManager().getMobile() != null) {
//                MessageSendService.sendMessageToInteLabManager(message, device.getManager().getMobile());  //因为虚拟机上不存在端口，会抛出相应的异常，所以暂时先将它屏蔽掉。
//            }else{
//                LOGGER.warn("The mobile of device owner is null, please complete the information as soon as possible.");
//            }

            // 查看用户是否已经cancel了这台设备的报警推送

            // 查看最近的3条信息, 看有没有cancel
            boolean alertCancelled = Application.influxDBManager.checkAlertPushStatusExistInLatestUpdates(alert.getId(), user.getId(), PUSH_MESSAGE_CANCEL, 3);

            if(!alertCancelled) {
                try {
                    sendAlertMessageToUser(alert, user, message);
                } catch (Exception e) {
                    LOGGER.error(String.format("Exception happens in sending alert for device %d to manager %s, %s",
                            device.getId(),
                            device.getManager().getTelephone(),
                            e.toString()));
                    e.printStackTrace();
                }
            }
        }

    }

    public void sendAlertMessageToUser(AlertCount alert, User user, String message){
        boolean mailAvailable = false;
        boolean msgAvailable = false;
        // check if user set void alert
        if (user.getRemoveAlert()!=null&&
                !"".equals(user.getRemoveAlert())&&
                user.getRemoveAlert().equals("0")){
            msgAvailable = true;
            mailAvailable = true;
            LOGGER.info(String.format("User %s does not void alert, send both message and email",
                    user.getName()));
        }
        if(user.getRemoveAlert()!=null&&
                !"".equals(user.getRemoveAlert())&&
                user.getRemoveAlert().equals("1")){
            mailAvailable = true;
            LOGGER.info(String.format("User %s set void message, send only email",
                    user.getName()));
        }
        String description = new String();


        boolean smsSuccess = false;
        // 发送短信
        if(msgAvailable) {
            Date startMsgSendTime = new Date();
            String result;

            if (user.getMobile() == null) {
                LOGGER.error( String.format("无法为报警 %d , 设备 %d, 参数 %s 发送短信给用户 %d, 用户没有绑定手机",
                        alert.getId(), alert.getDevice().getId(), alert.getInspectType().getName(), user.getId()));

            } else {
                if (MessageSendService.pushAlertMsg(user, message)) {

                    description = "短信发送成功";
                    LOGGER.info("device alert: send message " + message);
                    result = MESSAGE_RESULT_SUCCESS;

                    smsSuccess = true;


                } else {
                    description = "短信发送失败";
                    result = MESSAGE_RESULT_FAILURE;

                }

                Date endMsgSendTime = new Date();

                // 记录短信到influxdb

                Application.influxDBManager.writeMessage(endMsgSendTime, user.getId(),
                        alert.getInspectType().getId(),
                        alert.getDevice().getId(),
                        MESSAGE_TYPE_ALERT, MESSAGE_MEDIA_SMS, MESSAGE_ACTION_SEND,
                        result,
                        message,
                        description,
                        Double.valueOf(endMsgSendTime.getTime() - startMsgSendTime.getTime()));


            }
        }


        // 发送邮件
        boolean mailSuccess = false;
        if(mailAvailable){
            if (user.getEmail()==null||"".equals(user.getEmail())){
                LOGGER.error( String.format("无法为报警 %d , 设备 %d, 参数 %s 发送邮件给用户 %d, 用户没有绑定邮箱",
                        alert.getId(), alert.getDevice().getId(), alert.getInspectType().getName(), user.getId()));
            }
            else {
                Date startMsgSendTime = new Date();
                String result;
                if(MessageSendService.pushAlertMail(user, message)){
                    LOGGER.info("device alert: send email " + message);
                    description = "邮件发送成功";
                    result = MESSAGE_RESULT_SUCCESS;
                    mailSuccess = true;
                }
                else{
                    description = "邮件发送失败";
                    result = MESSAGE_RESULT_FAILURE;
                }


                Date endMsgSendTime = new Date();

                // 记录到influxdb
                Application.influxDBManager.writeMessage(endMsgSendTime, user.getId(),
                        alert.getInspectType().getId(),
                        alert.getDevice().getId(),
                        MESSAGE_TYPE_ALERT, MESSAGE_MEDIA_EMAIL, MESSAGE_ACTION_SEND,
                        result,
                        message,
                        description,
                        Double.valueOf(endMsgSendTime.getTime() - startMsgSendTime.getTime()));
            }

        }

        if( (smsSuccess || mailSuccess) && Application.influxDBManager != null){
            Application.influxDBManager.writeAlertPushStatus(new Date(), alert.getId(), user.getId(),
                    alert.getDevice().getId(), PUSH_MESSAGE_ACTIVE, 1);
        }

        if(!mailAvailable && !msgAvailable){

            LOGGER.error( String.format("无法为报警 %d , 设备 %d, 参数 %s 推送给用户 %s, 用户关闭了所有推送",
                    alert.getId(), alert.getDevice().getId(), alert.getInspectType().getName(), user.getName()));
        }

    }

    private int getReplyMessageType(String message){
        // TODO : 目前回复消息只有取消推送一种情况, 以后可能有跟过可能, 比如重新激活推送, 等等
        try{
            Integer.parseInt(message);
            return Constants.REPLY_MESSAGE_TYPE_CANCEL_PUSH;
        }catch (Exception ex){

        }

        return Constants.REPLY_MESSAGE_TYPE_UNKNOWN;
    }


    public void processReceivedReplyMessage(String senderMobile, String messageContent){
        // 判断短信发送手机号是否合法
        User user = userRepository.findByMobile(senderMobile);
        if(user == null){
            LOGGER.info(String.format("Reply message's mobile number %s is not registered, ignore", senderMobile));
            return;
        }

        // 从消息内容获取消息的目的,
        int messageType  = getReplyMessageType(messageContent);

        // 处理 取消报警推送
        if(messageType == Constants.REPLY_MESSAGE_TYPE_CANCEL_PUSH){
            Integer deviceId  = Integer.valueOf(messageContent);


            if (Application.influxDBManager == null){
                LOGGER.warn("InfluxdbManager not initialized. Skipping processing reply message");
                return;
            }

            Date twentyMinutesBefore = DateUtils.addMinutes(new Date(), -20);
            List<Integer> alertIdList = Application.influxDBManager.readAlertIdFromPushStatusByUserIdDeviceIdStatusTimeRange(
                    twentyMinutesBefore, user.getId(), deviceId, PUSH_MESSAGE_ACTIVE);

            for(Integer alertId: alertIdList){
                Application.influxDBManager.writeAlertPushStatus(new Date(), alertId, user.getId(), deviceId, PUSH_MESSAGE_CANCEL, 1);
            }
        }
    }

    private String getAddressOfDevice(Device device){
        String message = new String();
        Room room = device.getRoom();
        Storey floor = room.getFloor();
        Building building = floor.getBuild();
        String location = new String();
        if(building != null){
            location += building.getName() + " ";
        }
        if(floor != null){
            location += floor.getName() + " ";
        }
        if(room != null){
            location += room.getName();
        }
        message = String.format(locationInfoFormat, location);
        return message;
    }


}
