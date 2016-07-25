//package com.device.inspect.common.service;
//
//import com.device.inspect.common.model.device.Device;
//import com.device.inspect.common.model.device.DeviceInspect;
//import com.device.inspect.common.model.device.InspectData;
//import com.device.inspect.common.model.device.InspectType;
//import com.device.inspect.common.repository.device.DeviceInspectRepository;
//import com.device.inspect.common.repository.device.DeviceRepository;
//import com.device.inspect.common.repository.device.InspectDataRepository;
//import com.device.inspect.common.repository.device.InspectTypeRepository;
//import com.device.inspect.common.util.transefer.ByteAndHex;
//import com.device.inspect.common.util.transefer.StringDate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
//
//import java.text.ParseException;
//import java.util.Date;
//
///**
// * Created by Administrator on 2016/7/25.
// */
//@Component
//public class InsertInspectData {
//    @Autowired
//    public static InspectDataRepository inspectDataRepository;
//
//    @Autowired
//    public static DeviceRepository deviceRepository;
//
//    @Autowired
//    public static InspectTypeRepository inspectTypeRepository;
//
//    @Autowired
//    public static DeviceInspectRepository deviceInspectRepository;
//
//
//    public void excuteData(String result){
//        String monitorTypeCode = result.substring(4,5);
//        InspectType inspectType = inspectTypeRepository.findByCode(monitorTypeCode);
//        Date date = null;
//        if (null != inspectType){
//            String year = result.substring(12,13);
//            String month = result.substring(14,15);
//            String day = result.substring(16,17);
//            String hour = result.substring(18,19);
//            String min = result.substring(20,21);
//            String sec = result.substring(22,23);
//            String stringDate = "20"+year+"-"+month+"-"+day+" "+hour+":"+min+":"+sec;
//            try {
//                date = StringDate.stringToDate(stringDate, "yyyy-MM-dd HH:mm:ss");
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//
//            String fisrtData = result.substring(26,33);
//            String secondData = result.substring(34, 41);
//
//            int first = ByteAndHex.byteArrayToInt(ByteAndHex.hexStringToBytes(fisrtData), 0, 4);
//            int second = ByteAndHex.byteArrayToInt(ByteAndHex.hexStringToBytes(secondData), 0, 4);
//
//            Device device = deviceRepository.findOne(101);
//            DeviceInspect deviceInspect = deviceInspectRepository.
//                    findByInspectTypeIdAndDeviceId(inspectType.getId(), device.getId());
//            InspectData inspectData = new InspectData();
//            inspectData.setCreateDate(date);
//            inspectData.setDevice(device);
//            inspectData.setDeviceInspect(deviceInspect);
//            inspectData.setResult(Float.valueOf(first / 1000).toString());
//
//            inspectDataRepository.save(inspectData);
//        }
//    }
//
//}
