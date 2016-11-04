package com.device.inspect.controller;

import com.device.inspect.common.model.device.*;
import com.device.inspect.common.model.firm.Room;
import com.device.inspect.common.repository.device.*;
import com.device.inspect.common.repository.firm.RoomRepository;
import com.device.inspect.common.restful.RestResponse;
import com.device.inspect.common.restful.device.RestInspectData;
import com.device.inspect.common.util.transefer.ByteAndHex;
import com.device.inspect.common.util.transefer.StringDate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private static final Logger LOGGER = LogManager.getLogger(SocketMessageApi.class);
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

    @Autowired
    private DeviceVersionRepository deviceVersionRepository;

    @Autowired
    private MonitorDeviceRepository monitorDeviceRepository;

    String unit = "s";

    @RequestMapping(value = "/socket/insert/data",method = RequestMethod.GET)
    public RestResponse excuteInspectData(@RequestParam String result){
        LOGGER.info(result);
        String monitorTypeCode = result.substring(6,8);
        String fisrtData = result.substring(48,56);
        int first = ByteAndHex.byteArrayToInt(ByteAndHex.hexStringToBytes(fisrtData), 0, 4);
        Device device = new Device();
        String mointorCode = result.substring(8,26);
        MonitorDevice monitorDevice = monitorDeviceRepository.findByNumber(mointorCode);

        if (null==monitorDevice)
            return new RestResponse(null);
        if(monitorTypeCode.equals("03")) {
            monitorDevice.setBattery(String.valueOf(Float.valueOf(first)/1000));
            return new RestResponse(null);
        }

        device = monitorDevice.getDevice();
        if (device.getEnable()==0)
            return new RestResponse(null);

        InspectType inspectType = inspectTypeRepository.findByCode(monitorTypeCode);

        String response = null;

        InspectData inspectData = new InspectData();

        if (null != inspectType){

            DeviceInspect deviceInspect = deviceInspectRepository.
                    findByInspectTypeIdAndDeviceId(inspectType.getId(), device.getId());

            inspectData.setCreateDate(new Date());
            inspectData.setDevice(device);
            inspectData.setDeviceInspect(deviceInspect);
            float record = Float.valueOf(first)/1000;
            inspectData.setResult(String.valueOf(record));

            inspectDataRepository.save(inspectData);
            if (null==deviceInspect.getStandard()||null==deviceInspect.getHighUp()||null==deviceInspect.getLowDown()){
                return new RestResponse(null);
            }
            AlertCount high = alertCountRepository.
                    findTopByDeviceIdAndInspectTypeIdAndTypeOrderByCreateDateDesc(device.getId(), deviceInspect.getInspectType().getId(), 2);
            AlertCount low = alertCountRepository.
                    findTopByDeviceIdAndInspectTypeIdAndTypeOrderByCreateDateDesc(device.getId(), deviceInspect.getInspectType().getId(), 1);

            if (deviceInspect.getHighUp()<record||record<deviceInspect.getHighDown()){
                if (null!=low&&low.getNum()>0){
                    low.setFinish(new Date());
                    alertCountRepository.save(low);
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
                    high = new AlertCount();
                    high.setDevice(device);
                    high.setInspectType(deviceInspect.getInspectType());
                    high.setNum(0);
                    high.setType(2);
                    high.setCreateDate(new Date());
                    high.setUnit(unit);
                }
                if (high.getNum()==0){
                    high.setCreateDate(new Date());
                }
                high.setNum(high.getNum() + 1);
                alertCountRepository.save(high);
                inspectData.setType("high");
            }else if ((record<=deviceInspect.getHighUp()&&record>deviceInspect.getLowUp())||
                    (record>=deviceInspect.getHighDown()&&record<deviceInspect.getLowDown())){
                if (null!=high&&high.getNum()>0){
                    high.setFinish(new Date());
                    alertCountRepository.save(high);
                    AlertCount newHigh = new AlertCount();
                    newHigh.setDevice(device);
                    newHigh.setInspectType(deviceInspect.getInspectType());
                    newHigh.setNum(0);
                    newHigh.setType(2);
                    newHigh.setUnit(unit);
                    newHigh.setCreateDate(new Date());
                    alertCountRepository.save(newHigh);
                }
                if (null == low){
                    low = new AlertCount();
                    low.setDevice(device);
                    low.setInspectType(deviceInspect.getInspectType());
                    low.setCreateDate(new Date());
                    low.setNum(0);
                    low.setType(1);
                    low.setUnit(unit);
                }
                if (low.getNum()==0){
                    low.setCreateDate(new Date());
                }
                low.setNum(low.getNum()+1);
                alertCountRepository.save(low);
                inspectData.setType("low");
            }else {
                if (null==low||low.getNum()>0){
                    if (null!=low){
                        low.setFinish(new Date());
                        alertCountRepository.save(low);
                    }

                    AlertCount newLow = new AlertCount();
                    newLow.setDevice(device);
                    newLow.setInspectType(deviceInspect.getInspectType());
                    newLow.setNum(0);
                    newLow.setType(1);
                    newLow.setUnit(unit);
                    newLow.setCreateDate(new Date());
                    alertCountRepository.save(newLow);
                }

                if (null==high||high.getNum()>0){
                    if (null!=high){
                        high.setFinish(new Date());
                        alertCountRepository.save(high);
                    }


                    AlertCount newHigh = new AlertCount();
                    newHigh.setDevice(device);
                    newHigh.setInspectType(deviceInspect.getInspectType());
                    newHigh.setNum(0);
                    newHigh.setType(2);
                    newHigh.setUnit(unit);
                    newHigh.setCreateDate(new Date());
                    alertCountRepository.save(newHigh);
                }
                inspectData.setType("normal");
            }
            inspectDataRepository.save(inspectData);

            DeviceVersion deviceVersion = deviceVersionRepository.findTopOrderByCreateDateDesc();
            String firstCode = result.substring(26,28);
            String secondCode = result.substring(28,30);
            String thirdCode = result.substring(30,32);
            String forthCode = result.substring(32,34);

            List<Byte> responseByte = new ArrayList<Byte>();
            responseByte.add((byte)0xEF);
            responseByte.add((byte)0x02);
            responseByte.add((byte)0x05);
            boolean updateFlag = false;
            if (!firstCode.equals(deviceVersion.getFirstCode())||!secondCode.equals(deviceVersion.equals(deviceVersion.getSecondCode()))||
                    !thirdCode.equals(deviceVersion.getThirdCode())||!forthCode.equals(deviceVersion.getForthCode())){
                updateFlag = true;
                if (deviceVersion.getType().equals("1")){
                    responseByte.add((byte)0x01);
                }else {
                    responseByte.add((byte)0x02);
                }
            }else responseByte.add((byte)0x00);       //版本号更新确定
            try {
                if (null!=deviceInspect.getStandard()&&null!=deviceInspect.getLowDown()&&null!=deviceInspect.getLowUp()&&
                        null!=deviceInspect.getHighDown()&&null!=deviceInspect.getHighUp()){
                    //默认不存在没有报警值得情况
//                    responseByte.add((byte)0x01);
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


                if (updateFlag){
                    int length = deviceVersion.getUrl().length();
                    String confirm = "";
                    if (String.valueOf(length).length()<4){
                        for (int i = 0; i < 4-String.valueOf(length).length(); i++) {
                            confirm ="0"+confirm;
                        }
                    }
                    confirm = confirm+length;
                    for (byte bb : ByteAndHex.hexStringToBytes(confirm))
                        responseByte.add(bb);
                    for (char cc : deviceVersion.getUrl().toCharArray())
                        responseByte.add((byte)cc);
                }

                responseByte.add((byte)0xFF);
                responseByte.add((byte)0x02);
                byte[] message = new byte[responseByte.size()];
                for (int i = 0; i < responseByte.size(); i++) {
                    message[i] = responseByte.get(i);
                }
                response = ByteAndHex.bytesToHexString(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return new RestResponse(response);
    }

    /**
     * 房间绑定设备数据内容
     * @param roomId
     * @return
     */
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
