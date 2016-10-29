//package com.device.inspect.config.schedule;
//
//import com.device.inspect.common.model.device.AlertCount;
//import com.device.inspect.common.model.device.Device;
//import com.device.inspect.common.model.device.DeviceFloor;
//import com.device.inspect.common.model.record.MessageSend;
//import com.device.inspect.common.repository.charater.UserRepository;
//import com.device.inspect.common.repository.device.AlertCountRepository;
//import com.device.inspect.common.repository.device.DeviceFloorRepository;
//import com.device.inspect.common.repository.device.DeviceRepository;
//import com.device.inspect.common.repository.record.MessageSendRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//
//import java.util.Date;
//import java.util.List;
//
///**
// * Created by Administrator on 2016/10/29.
// */
//public class AlertMessageSendScheduleImp implements MySchedule {
//
//    private static final int LOW_ALERT_NUMBER = 8;
//    private static final int MESSAGE_SEND_TIME = 30;
//
//    @Autowired
//    private DeviceRepository deviceRepository;
//
//    @Autowired
//    private AlertCountRepository alertCountRepository;
//
//    @Autowired
//    private MessageSendRepository messageSendRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private DeviceFloorRepository deviceFloorRepository;
//
//    @Scheduled(cron = "0 5/10 * * * ? ")
//    @Override
//    public void scheduleTask() {
//        List<Device> deviceList = deviceRepository.findByEnable(1);
//        for (Device device :deviceList){
//            Date lastTime = new Date(new Date().getTime()-10*60*1000 );
//            List<AlertCount> list = alertCountRepository.findByDeviceIdAndCreateDateAfter(device.getId(),lastTime);
//            if (null!=list&&list.size()>0){
//
//                Integer sendType = 0;
//                String message = "设备编号为"+device.getCode();
//                String highMessage = "";
//                String lowMessage = "";
//                int highNum = 0;
//                int lowNum = 0;
//                for (AlertCount alertCount : list){
//                    if (alertCount.getType()==2){
//                        highNum+=1;
//                    }
//                    if (alertCount.getType()==1) {
//                        if (alertCount.getNum()>5)
//                            lowNum += 1;
//                    }
//                }
//                if (highNum>0||lowNum>0){
//                    MessageSend messageSendManager = messageSendRepository.
//                            findTopByUserIdAndDeviceIdAndEnableOrderByCreateDesc(device.getManager().getId(),device.getId(),1) ;
//                    if (null!=message&&(new Date().getTime()-messageSendManager.getCreate().getTime())/(60*1000)<30){
//
//                    }else {
//                        //添加发送
//                    }
//                    List<DeviceFloor> deviceFloorList = deviceFloorRepository.findByDeviceId(device.getId());
//                    if (null!=deviceFloorList&&deviceFloorList.size()>0){
//                        for (DeviceFloor deviceFloor : deviceFloorList){
//                            if (null!=deviceFloor.getScientist()){
//                                MessageSend messageSendScientist = messageSendRepository.
//                                        findTopByUserIdAndDeviceIdAndEnableOrderByCreateDesc(device.getManager().getId(),device.getId(),1) ;
//                                if (null!=message&&(new Date().getTime()-messageSendScientist.getCreate().getTime())/(60*1000)<30){
//
//                                }else {
//                                    //添加发送
//                                }
//                            }
//                        }
//                    }
//                }
//
//            }
//        }
//
//
//
//    }
//}
