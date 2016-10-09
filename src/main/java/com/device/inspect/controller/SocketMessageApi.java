package com.device.inspect.controller;

import com.device.inspect.common.model.device.*;
import com.device.inspect.common.model.firm.Room;
import com.device.inspect.common.repository.device.*;
import com.device.inspect.common.repository.firm.RoomRepository;
import com.device.inspect.common.restful.RestResponse;
import com.device.inspect.common.restful.device.RestInspectData;
import com.device.inspect.common.util.transefer.ByteAndHex;
import com.device.inspect.common.util.transefer.StringDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private AlertCountRepository alertCountRepository;

    String unit = "s";

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
//            int second = ByteAndHex.byteArrayToInt(ByteAndHex.hexStringToBytes(secondData), 0, 4);

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
//            if (null!=deviceInspect.getStandard()){
//                Float judge = Float.valueOf(first / 1000)/deviceInspect.getStandard();
//                if ((judge>0&&judge>100)||(judge<0&&judge<-100)){
//                    return new RestResponse("超出范围！",1005,null);
//                }
//            }else {
//                if (first/1000!=1||first/1000!=0)
//                    return new RestResponse("数据有误！",1005,null);
//            }


//            inspectData.setCreateDate(date);
            inspectData.setCreateDate(new Date());
            inspectData.setDevice(device);
            inspectData.setDeviceInspect(deviceInspect);
            Float record = Float.valueOf(first / 1000);
            inspectData.setResult(record.toString());
//            if(monitorTypeCode.equals("02")&&record>100000){
//                return new RestResponse("二氧化碳数据错误！",1005,null);
//            }

            inspectDataRepository.save(inspectData);
            if (null==deviceInspect.getStandard()||null==deviceInspect.getHighUp()||null==deviceInspect.getLowDown()){
                return new RestResponse(new RestInspectData(inspectData));
            }
            AlertCount high = alertCountRepository.
                    findTopByDeviceIdAndInspectTypeIdAndTypeOrderByCreateDateDesc(device.getId(), deviceInspect.getInspectType().getId(), 2);
            AlertCount low = alertCountRepository.
                    findTopByDeviceIdAndInspectTypeIdAndTypeOrderByCreateDateDesc(device.getId(), deviceInspect.getInspectType().getId(), 1);

            if (deviceInspect.getHighUp()<record&&record<deviceInspect.getHighDown()){
                if (null!=low){
                    AlertCount newLow = new AlertCount();
                    newLow.setDevice(device);
                    newLow.setInspectType(deviceInspect.getInspectType());
                    newLow.setNum(0);
                    newLow.setType(1);
                    newLow.setUnit(unit);
                    newLow.setCreateDate(new Date());
                    alertCountRepository.save(newLow);
                }

                if (null == high){
                    high.setDevice(device);
                    high.setInspectType(deviceInspect.getInspectType());
                    high.setNum(0);
                    high.setType(1);
                    high.setUnit(unit);
                }
                if (high.getNum()==0){
                    high.setCreateDate(new Date());
                }
                high.setNum(high.getNum() + 1);
                alertCountRepository.save(high);
            }else if ((record<=deviceInspect.getHighUp()&&record>deviceInspect.getLowUp())||
                    (record>=deviceInspect.getHighDown()&&record<deviceInspect.getLowDown())){
                if (null!=high){
                    AlertCount newHigh = new AlertCount();
                    newHigh.setDevice(device);
                    newHigh.setInspectType(deviceInspect.getInspectType());
                    newHigh.setNum(0);
                    newHigh.setType(1);
                    newHigh.setUnit(unit);
                    newHigh.setCreateDate(new Date());
                    alertCountRepository.save(newHigh);
                }
                if (null == low){
                    low.setDevice(device);
                    low.setInspectType(deviceInspect.getInspectType());
                    low.setNum(0);
                    low.setType(1);
                    low.setUnit(unit);
                }
                if (low.getNum()==0){
                    low.setCreateDate(new Date());
                }
                low.setNum(low.getNum()+1);
                alertCountRepository.save(low);
            }else {
                AlertCount newLow = new AlertCount();
                newLow.setDevice(device);
                newLow.setInspectType(deviceInspect.getInspectType());
                newLow.setNum(0);
                newLow.setType(1);
                newLow.setUnit(unit);
                newLow.setCreateDate(new Date());
                alertCountRepository.save(newLow);

                AlertCount newHigh = new AlertCount();
                newHigh.setDevice(device);
                newHigh.setInspectType(deviceInspect.getInspectType());
                newHigh.setNum(0);
                newHigh.setType(1);
                newHigh.setUnit(unit);
                newHigh.setCreateDate(new Date());
                alertCountRepository.save(newHigh);
            }

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

    @RequestMapping(value = "/device/chart/{deviceId}")
    public RestResponse getTopTwentyData(@PathVariable Integer deviceId){
        Device device = deviceRepository.findOne(deviceId);
        List<DeviceInspect> deviceInspectList = deviceInspectRepository.findByDeviceId(deviceId);
        Map map = new HashMap();
        List<List> list = new ArrayList<List>();
        Float  score = Float.valueOf(100);
        if (null!=deviceInspectList&&deviceInspectList.size()>0){
            for (DeviceInspect deviceInspect : deviceInspectList){
                List<InspectData> inspectDatas = inspectDataRepository.
                        findTop20ByDeviceIdAndDeviceInspectIdOrderByCreateDateDesc(deviceId, deviceInspect.getId());
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
