package com.device.inspect.config.schedule;

import com.device.inspect.common.model.charater.User;
import com.device.inspect.common.model.device.AlertCount;
import com.device.inspect.common.model.device.Device;
import com.device.inspect.common.model.device.DeviceFloor;
import com.device.inspect.common.model.record.MessageSend;
import com.device.inspect.common.repository.charater.UserRepository;
import com.device.inspect.common.repository.device.AlertCountRepository;
import com.device.inspect.common.repository.device.DeviceFloorRepository;
import com.device.inspect.common.repository.device.DeviceRepository;
import com.device.inspect.common.repository.record.MessageSendRepository;
import com.device.inspect.common.service.MessageSendService;
import com.device.inspect.controller.OperateController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/10/29.
 */
@Component
public class AlertMessageSendScheduleImp implements MySchedule {

    private static final int LOW_ALERT_NUMBER = 8;
    private static final int MESSAGE_SEND_TIME = 30;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private AlertCountRepository alertCountRepository;

    @Autowired
    private MessageSendRepository messageSendRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeviceFloorRepository deviceFloorRepository;
    
    @Scheduled(cron = "0 5/10 * * * ? ")
    @Override
    public void scheduleTask() {
        List<Device> deviceList = deviceRepository.findByEnable(1);
        for (Device device :deviceList){
            Date lastTime = new Date(new Date().getTime()-10*60*1000 );
            List<AlertCount> list = alertCountRepository.findByDeviceIdAndCreateDateAfter(device.getId(),lastTime);
            if (null!=list&&list.size()>0){
                Integer sendType = 0;
                String message = "设备："+device.getName();
                String highMessage = "";
                String lowMessage = "";
                int highNum = 0;
                int lowNum = 0;
                for (AlertCount alertCount : list){
                    if (alertCount.getType()==2){
                        highNum+=1;
                    }
                    if (alertCount.getType()==1) {
                        if (alertCount.getNum()>5)
                            lowNum += 1;
                    }
                }
                System.out.println("highNum: "+highNum+"   "+"lowNum: "+lowNum);
                System.out.println("device.getManager().getId(): "+device.getManager().getId());
                System.out.println("device.getId(): "+device.getId());
                if (highNum>0||lowNum>0){
                    MessageSend messageSendManager = messageSendRepository.
                            findTopByUserIdAndDeviceIdAndEnableOrderByCreateDesc(device.getManager().getId(),device.getId(),1) ;
                    if (null!=messageSendManager&&(new Date().getTime()-messageSendManager.getCreate().getTime())/(60*1000)<30){

                    }else {
                        System.out.println("校验");
                        //0是允许报警  1，是允许邮箱报警
                        MessageSend messageSend = new MessageSend();
                        messageSend.setDevice(device);
                        messageSend.setCreate(new Date());
                        messageSend.setUser(device.getManager());
                        if (device.getManager().getRemoveAlert()!=null&&!"".equals(device.getManager().getRemoveAlert())&&device.getManager().getRemoveAlert().equals("0")){
                            //允许发送报警信息
                            //添加发送
                            String reason=MessageSendService.pushAlertMessge(device.getManager(),"",message);
                            messageSend.setType(reason);
                            messageSend.setError(device.getId()+"报警,发送给设备管理员"+device.getManager().getUserName());
                            messageSend.setReason("alert");
                            if (reason.equals("推送失败"))
                                messageSend.setEnable(0);
                            else
                                messageSend.setEnable(1);
                        }else if (device.getManager().getRemoveAlert()!=null&&!"".equals(device.getManager().getRemoveAlert())&&device.getManager().getRemoveAlert().equals("1")){
                            //允许邮箱推送报警信息
                            if (device.getManager().getEmail()!=null&&!"".equals(device.getManager().getEmail())){
                                Boolean reason=MessageSendService.sendEmai(device.getManager(),device.getManager().getEmail(),message,1);
                                messageSend.setType("email");
                                messageSend.setError(device.getId()+"报警,发送给设备管理员"+device.getManager().getUserName());
                                messageSend.setReason("alert");
                                if (reason)
                                    messageSend.setEnable(1);
                                else
                                    messageSend.setEnable(0);
                            }else {
                                messageSend.setReason("管理员没有绑定邮箱");
                                messageSend.setEnable(0);
                            }
                        }else {
                            //不推送报警信息
                            messageSend.setReason("管理员设置了不允许报警推送");
                            messageSend.setEnable(0);
                        }

                        messageSendRepository.save(messageSend);
                    }
                    List<DeviceFloor> deviceFloorList = deviceFloorRepository.findByDeviceId(device.getId());
                    if (null!=deviceFloorList&&deviceFloorList.size()>0){
                        for (DeviceFloor deviceFloor : deviceFloorList){
                            if (null!=deviceFloor.getScientist()){
                                MessageSend messageSendScientist = messageSendRepository.
                                        findTopByUserIdAndDeviceIdAndEnableOrderByCreateDesc(deviceFloor.getScientist().getId(),device.getId(),1) ;
                                if (null!=messageSendScientist&&(new Date().getTime()-messageSendScientist.getCreate().getTime())/(60*1000)<30){

                                }else {
                                    MessageSend messageSend = new MessageSend();
                                    messageSend.setDevice(device);
                                    messageSend.setCreate(new Date());
                                    messageSend.setUser(deviceFloor.getScientist());
                                    if (deviceFloor.getScientist().getRemoveAlert()!=null&&!"".equals(deviceFloor.getScientist().getRemoveAlert())&&deviceFloor.getScientist().getRemoveAlert().equals("0")){
                                        //允许发送报警信息
                                        //添加发送
                                        String reason=MessageSendService.pushAlertMessge(deviceFloor.getScientist(),"",message);
                                        messageSend.setReason("alert");
                                        messageSend.setType(reason);
                                        messageSend.setError(device.getId()+"报警,发送给实验品"+deviceFloor.getType()+
                                                "管理员"+deviceFloor.getScientist().getUserName());

                                        if (reason.equals("推送失败"))
                                            messageSend.setEnable(0);
                                        else
                                            messageSend.setEnable(1);
                                    }else if (deviceFloor.getScientist().getRemoveAlert()!=null&&!"".equals(deviceFloor.getScientist().getRemoveAlert())&&deviceFloor.getScientist().getRemoveAlert().equals("1")){
                                        //云巽邮箱推送报警信息
                                        if (deviceFloor.getScientist().getEmail()!=null&&!"".equals(deviceFloor.getScientist().getEmail())){
                                            Boolean reason=MessageSendService.sendEmai(deviceFloor.getScientist(),deviceFloor.getScientist().getEmail(),message,1);
                                            messageSend.setType("email");
                                            messageSend.setError(device.getId()+"报警,发送给给实验品管理员"+deviceFloor.getScientist().getUserName());
                                            messageSend.setReason("alert");
                                            if (reason)
                                                messageSend.setEnable(1);
                                            else
                                                messageSend.setEnable(0);
                                        }else {
                                            messageSend.setReason("实验品管理员没有绑定邮箱");
                                            messageSend.setEnable(0);
                                        }
                                    }else {
                                        //不推送报警信息
                                        messageSend.setReason("实验品管理员设置了不允许报警推送");
                                        messageSend.setEnable(0);
                                    }

                                    messageSendRepository.save(messageSend);
                                }
                            }
                        }
                    }
                }

            }
        }



    }
}
