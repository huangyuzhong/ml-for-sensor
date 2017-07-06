package com.device.inspect.controller;

import com.device.inspect.Application;
import com.device.inspect.common.model.charater.User;
import com.device.inspect.common.model.device.Device;
import com.device.inspect.common.model.device.DeviceFloor;
import com.device.inspect.common.model.device.DeviceInspect;
import com.device.inspect.common.model.device.InspectData;
import com.device.inspect.common.model.firm.Building;
import com.device.inspect.common.model.firm.Room;
import com.device.inspect.common.model.firm.Storey;
import com.device.inspect.common.model.record.MessageSend;
import com.device.inspect.common.repository.device.DeviceFloorRepository;
import com.device.inspect.common.repository.device.DeviceInspectRepository;
import com.device.inspect.common.repository.device.InspectDataRepository;
import com.device.inspect.common.repository.record.MessageSendRepository;
import com.device.inspect.common.service.MessageSendService;
import com.device.inspect.common.util.transefer.InspectProcessTool;
import com.device.inspect.common.util.transefer.StringDate;
import org.apache.commons.httpclient.util.DateUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.influxdb.impl.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by zyclincoln on 4/14/17.
 */
@Service
public class MessageController {

    private static final Logger LOGGER = LogManager.getLogger(MessageController.class);


    @Autowired
    private MessageSendRepository messageSendRepository;

    @Autowired
    private DeviceInspectRepository deviceInspectRepository;

    @Autowired
    private DeviceFloorRepository deviceFloorRepository;

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
    public void sendPowerMsg(Device device, DeviceInspect deviceInspect, Date sampleTime){
        DateFormat formatter= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss Z");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

        String message = String.format(powerMsgFormat, device.getCode(), device.getName(), formatter.format(sampleTime));
        message += getAddressOfDevice(device);
        sendMessage(device, deviceInspect, sampleTime, message);
    }

    /**
     * 掉线报警信息，发送掉线异常报警信息
     */
    public void sendOfflineMsg(Device device, DeviceInspect deviceInspect, Date sampleTime){
        DateFormat formatter= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss Z");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

        String message = String.format(OffLineMsgFormat, device.getCode(), device.getName(), formatter.format(sampleTime));
        message += getAddressOfDevice(device);
        sendMessage(device, deviceInspect, sampleTime, message);
    }

    /**
     * 监控参数报警函数，分析异常情况并发送报警信息
     */
    public void sendAlertMsg(Device device, DeviceInspect deviceInspect, Float standard, Float value, Date sampleTime){
        DateFormat formatter= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss Z");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

        String message = String.format(alertFormat, device.getCode(), device.getName(), formatter.format(sampleTime),
                deviceInspect.getName());

        DeviceInspect doorInspect = deviceInspectRepository.findByInspectTypeIdAndDeviceId(doorInspectId, device.getId());

        // if this alerting inspect is not door, get door status if door is an inspect of this device.
        if(deviceInspect.getInspectType().getId() != doorInspectId){
            if(doorInspect != null) {
                List<Object> doorInspectData = Application.influxDBManager.readLatestTelemetry(
                        deviceInspect.getInspectType().getMeasurement(),
                        device.getId(), doorInspect.getId());


                message += String.format(valueFormat, standard, value);

                if (doorInspectData != null && ((Double) doorInspectData.get(1)).floatValue() == doorOpen) {
                    LOGGER.info(String.format("alerting inspect %d of device %d is with door open.",
                            deviceInspect.getId(),
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
                    deviceInspect.getInspectType().getMeasurement(),
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
        sendMessage(device, deviceInspect, sampleTime, message);
    }

    private void sendMessage(Device device, DeviceInspect deviceInspect, Date sampleTime, String message){
        MessageSend messageSend = null;
        if(deviceInspect != null){
            messageSend =  messageSendRepository.
                    findTopByUserIdAndDeviceIdAndEnableAndDeviceInspectIdOrderByCreateDesc(device.getManager().getId(), device.getId(), 1, deviceInspect.getId());
        }

        // if there exists a notification sent in 5 minutes, skip
        if(messageSend != null && (sampleTime.getTime() - messageSend.getCreate().getTime()) < 5*60*1000){
            LOGGER.info(String.format("Device %d, alert has sent message to manager at %s within 5 minutes skip this time.",
                    device.getId(),
                    messageSend.getCreate()));
            return;
        }
        else{
            LOGGER.info(String.format("Device %d, last alert is more than 5 minutes away, sending alert to manager %s",
                    device.getId(),
                    device.getManager().getTelephone()));

            // 将所有的报警时间都抄送到test@ilabservice.com这个邮箱，不管用户是否选择报警。
            if(MessageSendService.sendEmailToIntelabTest(message)){
                LOGGER.info("Successfully sent alert to test@ilabservice.com. " + message);

            }else{
                LOGGER.warn("Failed to sent alert to test@ilabservice.com. "  + message);
            }

            try {
                MessageSend newMessageSend = new MessageSend();
                newMessageSend.setCreate(sampleTime);
                newMessageSend.setDevice(device);
                newMessageSend.setUser(device.getManager());
                newMessageSend.setDeviceInspect(deviceInspect);
                newMessageSend.setError(device.getId() + "报警,发送给设备管理员" + device.getManager().getUserName());
                sendAlertMsgToUsr(device.getManager(), message, newMessageSend);
            }catch (Exception e){
                LOGGER.error(String.format("Exception happens in sending alert for device %d to manager %s, %s",
                        device.getId(),
                        device.getManager().getTelephone(),
                        e.toString()));
                e.printStackTrace();
            }
        }

        List<DeviceFloor> deviceFloorList = deviceFloorRepository.findByDeviceId(device.getId());
        if (null!=deviceFloorList&&deviceFloorList.size()>0){
            for (DeviceFloor deviceFloor : deviceFloorList){
                if (null!=deviceFloor.getScientist()){
                    MessageSend messageSendScientist = messageSendRepository.
                            findTopByUserIdAndDeviceIdAndEnableOrderByCreateDesc(deviceFloor.getScientist().getId(),device.getId(),1) ;
                    if (null!=messageSendScientist && (sampleTime.getTime()-messageSendScientist.getCreate().getTime()) < 5*60*1000){
                        LOGGER.info("device alert: " + device.getId() + ", has sent message to scientist at " + messageSend.getCreate() + ", passed this time.");
                    }
                    else {
                        MessageSend newMessageSend = new MessageSend();
                        newMessageSend.setDevice(device);
                        newMessageSend.setCreate(sampleTime);
                        newMessageSend.setUser(deviceFloor.getScientist());
                        newMessageSend.setDeviceInspect(deviceInspect);
                        newMessageSend.setError(device.getId()+"报警,发送给实验品管理员"+deviceFloor.getScientist().getUserName());
                        sendAlertMsgToUsr(deviceFloor.getScientist(), message, newMessageSend);
                    }
                }
            }
        }
    }

    /**
     * 发送报警信息给特定用户
     */
    public void sendAlertMsgToUsr(User user, String message, MessageSend messageSend){
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
        String type = new String();
        String reason = "alert";

        messageSend.setEnable(0);

        if(msgAvailable){
            if(MessageSendService.pushAlertMsg(user, message)){
                type += "短信发送成功";
                LOGGER.info("device alert: send message " + message);
                messageSend.setEnable(1);
            }
            else{
                type += "短信发送失败";
            }
        }
        if(mailAvailable){
            if (user.getEmail()==null||"".equals(user.getEmail())){
                reason = "没有绑定邮箱";
            }
            else if(MessageSendService.pushAlertMail(user, message)){
                LOGGER.info("device alert: send email " + message);
                type += "邮件发送成功";
                messageSend.setEnable(1);
            }
            else{
                type += "邮件发送失败";
            }
        }
        if(!mailAvailable && !msgAvailable){
            reason = "停用通知";
        }
        messageSend.setType(type);
        messageSend.setReason(reason);
        messageSendRepository.save(messageSend);

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
