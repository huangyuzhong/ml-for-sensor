package com.device.inspect.controller;

import com.device.inspect.common.model.device.Device;
import com.device.inspect.common.model.device.DeviceInspect;
import com.device.inspect.common.model.device.InspectData;
import com.device.inspect.common.model.device.InspectType;
import com.device.inspect.common.model.firm.Room;
import com.device.inspect.common.repository.device.DeviceInspectRepository;
import com.device.inspect.common.repository.device.DeviceRepository;
import com.device.inspect.common.repository.device.InspectDataRepository;
import com.device.inspect.common.repository.device.InspectTypeRepository;
import com.device.inspect.common.repository.firm.RoomRepository;
import com.device.inspect.common.restful.RestResponse;
import com.device.inspect.common.restful.device.RestInspectData;
import com.device.inspect.common.util.transefer.ByteAndHex;
import com.device.inspect.common.util.transefer.StringDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.*;

/**
 * Created by Administrator on 2016/7/25.
 */
@RestController
@RequestMapping(value = "/api/rest")
public class SocketMessageApi {
    @Autowired
    private  InspectDataRepository inspectDataRepository;

    @Autowired
    private  DeviceRepository deviceRepository;

    @Autowired
    private  InspectTypeRepository inspectTypeRepository;

    @Autowired
    private  DeviceInspectRepository deviceInspectRepository;

    @Autowired
    private RoomRepository roomRepository;

    @RequestMapping(value = "/socket/insert/data",method = RequestMethod.GET)
    public RestResponse excuteInspectData(@RequestParam String result){
        String monitorTypeCode = result.substring(4,6);
        InspectType inspectType = inspectTypeRepository.findByCode(monitorTypeCode);
        Date date = null;
        InspectData inspectData = new InspectData();
        if (null != inspectType){
//            String year = result.substring(12,14);
//            String month = result.substring(14,16);
//            String day = result.substring(16,18);
//            String hour = result.substring(18,20);
//            String min = result.substring(20,22);
//            String sec = result.substring(22,24);
//            String stringDate = "20"+year+"-"+month+"-"+day+" "+hour+":"+min+":"+sec;
//            try {
//                date = StringDate.stringToDate(stringDate, "yyyy-MM-dd HH:mm:ss");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            String fisrtData = result.substring(26,34);
            String secondData = result.substring(34, 42);

            int first = ByteAndHex.byteArrayToInt(ByteAndHex.hexStringToBytes(fisrtData), 0, 4);
            int second = ByteAndHex.byteArrayToInt(ByteAndHex.hexStringToBytes(secondData), 0, 4);

            Device device = new Device();
            if (monitorTypeCode.equals("00")||monitorTypeCode.equals("08")){
                device = deviceRepository.findOne(101);
            }else if (monitorTypeCode.equals("01")||monitorTypeCode.equals("04")||
                    monitorTypeCode.equals("05")||monitorTypeCode.equals("06")){
                device = deviceRepository.findOne(103);
            }else {
                device = deviceRepository.findOne(104);
            }

            DeviceInspect deviceInspect = deviceInspectRepository.
                    findByInspectTypeIdAndDeviceId(inspectType.getId(), device.getId());

//            inspectData.setCreateDate(date);
            inspectData.setCreateDate(new Date());
            inspectData.setDevice(device);
            inspectData.setDeviceInspect(deviceInspect);
            inspectData.setResult(Float.valueOf(first / 1000).toString());

            inspectDataRepository.save(inspectData);
        }
        return new RestResponse(new RestInspectData(inspectData));
    }

    @RequestMapping(value = "/room/current/data",method = RequestMethod.GET)
    public RestResponse getCurrentDataFromDevice(@RequestParam Integer roomId){
        Room room = roomRepository.findOne(roomId);
        if (null!=room.getDevice()){
            List<DeviceInspect> deviceInspectList = deviceInspectRepository.findByDeviceId(room.getDevice().getId());
//        List<InspectData> inspectDataList = new ArrayList<InspectData>();
            List<RestInspectData> list = new ArrayList<RestInspectData>();
            if (null!=deviceInspectList&&deviceInspectList.size()>0){
                for (DeviceInspect deviceInspect : deviceInspectList){
                    InspectData inspectData = inspectDataRepository.
                            findTopByDeviceIdAndDeviceInspectIdOrderByCreateDateDesc(room.getDevice().getId(), deviceInspect.getId());
                    if (null!=inspectData)
                        list.add(new RestInspectData(inspectData));
                }
            }
            return new RestResponse(list);
        }else {
            return new RestResponse();
        }



    }

    @RequestMapping(value = "/device/current/data",method = RequestMethod.GET)
    public RestResponse getCurrentData(@RequestParam Integer deviceId){
        Device device = deviceRepository.findOne(deviceId);
        List<DeviceInspect> deviceInspectList = deviceInspectRepository.findByDeviceId(deviceId);
        Map map = new HashMap();
        List<List> list = new ArrayList<List>();
        Float  score = Float.valueOf(100);
        if (null!=deviceInspectList&&deviceInspectList.size()>0){
            for (DeviceInspect deviceInspect : deviceInspectList){
                List<InspectData> inspectDatas = inspectDataRepository.
                        findTop7ByDeviceIdAndDeviceInspectIdOrderByCreateDateDesc(deviceId, deviceInspect.getId());
                if (null!=inspectDatas&&inspectDatas.size()>0) {
                    List<RestInspectData> insertDatas = new ArrayList<RestInspectData>();
                    for (InspectData inspectData : inspectDatas) {
                        insertDatas.add(new RestInspectData(inspectData));
                        if(null!=inspectData.getDeviceInspect().getHighDown()){
                            float m=0;
                            if (Float.valueOf(inspectData.getResult())>inspectData.getDeviceInspect().getStandard()){
                                m = Float.valueOf(inspectData.getResult())-inspectData.getDeviceInspect().getStandard();
                            }else {
                                m = inspectData.getDeviceInspect().getStandard()-Float.valueOf(inspectData.getResult());
                            }
                            score = score - m/(inspectData.getDeviceInspect().getHighUp()-inspectData.getDeviceInspect().getStandard());
                        }
                    }
                    list.add(insertDatas);
                }
            }
        }
        map.put("list",list);
        map.put("score",score);

        return new RestResponse(map);

    }

}
