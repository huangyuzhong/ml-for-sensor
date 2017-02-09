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

import java.math.BigDecimal;
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

    @Autowired
    private Pt100Repository pt100Repository;

    @Autowired
    private Pt100ZeroRepository pt100ZeroRepository;


    String unit = "s";

    /**
     * 更新数据
     * @param result
     * @return
     */
    @RequestMapping(value = "/socket/insert/data",method = RequestMethod.GET)
    public RestResponse excuteInspectData(@RequestParam String result){
        LOGGER.info(result);

        String monitorTypeCode = result.substring(6,8);

        String fisrtData = result.substring(48,56);
        int first = ByteAndHex.byteArrayToInt(ByteAndHex.hexStringToBytes(fisrtData), 0, 4);
        Device device = new Device();
        String mointorCode = result.substring(8,26);
//        LOGGER.info("终端编号是：：：：："+mointorCode);
        MonitorDevice monitorDevice = monitorDeviceRepository.findByNumber(mointorCode);
        if (null==monitorDevice)
            return new RestResponse(null);
        if(monitorTypeCode.equals("03")) {
            monitorDevice.setBattery(String.valueOf(Float.valueOf(first)/10));
            monitorDeviceRepository.save(monitorDevice);
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
            if (null==deviceInspect)
                return new RestResponse(null);
            //测量原值
            Float record;
            //添加矫正值
            Float check;
            //判断是否是PT100
            if (monitorTypeCode.equals("00")){
                inspectData.setCreateDate(new Date());
                inspectData.setDevice(device);
                inspectData.setDeviceInspect(deviceInspect);


                //将int类型转换成两个doube类型的电压
                double AD0=((first>>16)&0xffff)*1.024/32768;//前两个字节转换成doube类型的电压
                double AD1=(first&0xffff)*1.024/32768;//后两个字节转换成doube类型的电压

                double R=(1000*AD0-1000*AD1)/(3.38-AD0-AD1);//生成double类型的电阻


                System.out.println("计算出来的电阻："+R);

                //将double类型的电阻转换成float类型
                //将电阻四舍五入到小数点两位
                BigDecimal bigDecimal=new BigDecimal(Float.valueOf(String.valueOf(R)));
                Float r=bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP).floatValue();
                //通过设备编号去查找相应的pt100,如果对应的电阻直接有相应的温度
                if (pt100Repository.findByResistance(r)!=null){
                    Pt100 pt100=pt100Repository.findByResistance(r);
//                    Pt100 pt100=pt100Repository.findByDeviceTypeIdAndResistance(device.getDeviceType().getId(),r);
                    //通过电阻找到对象的温度
                    String temperature=pt100.getTemperature();


                    record=Float.valueOf(temperature);
                    //添加测量原值
                    inspectData.setRealValue(String.valueOf(record));
                    deviceInspect.setOriginalValue(record);
                    //矫正值
                    check=record-(deviceInspect.getZero());
                    inspectData.setResult(String.valueOf(check));
                    deviceInspect.setCorrectionValue(check);
//                    Pt100Zero pt100Zero=new Pt100Zero();
//                    //查询飘零表
//                    pt100Zero=pt100ZeroRepository.findByCode(mointorCode);
//                    if (pt100Zero!=null){
//                        record=record+(Float.valueOf(String.valueOf(pt100Zero.getZeroValue())));
//                    }

                }else {
                    //通过电阻找到对应的温度
                    //从小到大
                    List<Pt100> list1=new ArrayList<Pt100>();
                    //使用默认表查询
                    list1=pt100Repository.findByResistanceAfterOrderByResistanceDESC(r);
//                    list1=pt100Repository.findByDeviceTypeIdAndResistanceAfterOrderByASC(device.getDeviceType().getId(),r);
                    //找到对应的Pt100
                    Pt100 one=list1.get(0);
                    String temperature1=one.getTemperature();
                    Float resistance1=one.getResistance();
                    //从大到小
                    List<Pt100> list2=new ArrayList<Pt100>();
                    //使用默认表查询
                    list2=pt100Repository.findByResistanceBeforeOrderByResistanceASC(r);
//                    list2=pt100Repository.findByDeviceTypeIdAndResistanceBeforeOrderByDESC(device.getDeviceType().getId(),r);
                    //找到对应的Pt100
                    Pt100 two=list2.get(0);
                    String temperature2=two.getTemperature();
                    Float resistance2=two.getResistance();
                    //进行线性公式计算出改r下面的温度
                    float k=(Float.valueOf(temperature2)-Float.valueOf(temperature1))/(resistance2-resistance1);

                    float b=Float.valueOf(temperature1)-(k*resistance1);

                    //将温度存入record
                    record = k*r+b;
                    //添加测量原值
                    inspectData.setRealValue(String.valueOf(record));
                    deviceInspect.setOriginalValue(record);
                    //添加矫正值
                    check=record-(deviceInspect.getZero());
                    inspectData.setResult(String.valueOf(check));
                    deviceInspect.setCorrectionValue(check);
//                    Pt100Zero pt100Zero=new Pt100Zero();
//                    //查询飘零表
//                    pt100Zero=pt100ZeroRepository.findByCode(mointorCode);
//                    if (pt100Zero!=null){
//                        record=record+(Float.valueOf(String.valueOf(pt100Zero.getZeroValue())));
//                    }
                }
                //设置检测结果
//                inspectData.setResult(String.valueOf(record));
            }else if (monitorTypeCode.equals("07")){
                //判断是不是甲烷
                //根据上传的值算出电压
                Float v=(Float.valueOf(first)*Float.valueOf(2.018f))/Float.valueOf(32768);

                //算出的电压值如果小于0.4   record 都为百分之零
                if (v<0.4){
                    inspectData.setCreateDate(new Date());
                    inspectData.setDevice(device);
                    inspectData.setDeviceInspect(deviceInspect);
                    record=0f;
                    //添加校正值
                    inspectData.setResult(String.valueOf(record));
                    deviceInspect.setOriginalValue(record);
                    //甲烷添加测量原值
                    inspectData.setRealValue(String.valueOf(record));
                    deviceInspect.setCorrectionValue(record);
                }else if (v<2){
                    float b=0.4f;
                    float k=1.6f;
                    record=(v-b)/k;

                    //甲烷添加测量原值
                    inspectData.setRealValue(String.valueOf(record));
                    deviceInspect.setOriginalValue(record);
                    inspectData.setCreateDate(new Date());
                    inspectData.setDevice(device);
                    inspectData.setDeviceInspect(deviceInspect);
                    check=record-(deviceInspect.getZero());
                    inspectData.setResult(String.valueOf(check));
                    deviceInspect.setCorrectionValue(check);
                } else {
                    record=10f;
                    inspectData.setCreateDate(new Date());
                    inspectData.setDevice(device);
                    inspectData.setDeviceInspect(deviceInspect);
                    inspectData.setResult(String.valueOf(record));
                    //甲烷添加测量原值
                    inspectData.setRealValue(String.valueOf(record));
                    deviceInspect.setOriginalValue(record);
                }

            }
            else if(monitorTypeCode.equals("06")){
                inspectData.setCreateDate(new Date());
                inspectData.setDevice(device);
                inspectData.setDeviceInspect(deviceInspect);
                int value = ByteAndHex.byteArrayToInt(ByteAndHex.hexStringToBytes(fisrtData), 2, 2);
                value = (~value)/60;
                inspectData.setRealValue(String.valueOf(value));
                record = Float.valueOf(value)/60;
                deviceInspect.setOriginalValue(record);
                check=record*(float)1.04;
                inspectData.setResult(String.valueOf(check));
                deviceInspect.setCorrectionValue(check);
            } else{
                inspectData.setCreateDate(new Date());
                inspectData.setDevice(device);
                inspectData.setDeviceInspect(deviceInspect);
                //添加测量原值
                inspectData.setRealValue(String.valueOf(first));
                record = Float.valueOf(first)/1000;
                deviceInspect.setOriginalValue(record);
                check=record-(deviceInspect.getZero());
                inspectData.setResult(String.valueOf(check));
                deviceInspect.setCorrectionValue(check);
            }
            deviceInspectRepository.save(deviceInspect);
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

//            DeviceVersion deviceVersion = deviceVersionRepository.findTopOrderByCreateDateDesc();
            DeviceVersion deviceVersion=device.getDeviceVersion();
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

    /**
     * 设备绑定数据内容
     * @param deviceId
     * @return
     */
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

    /**
     * 设备警报图表
     * @param deviceId
     * @return
     */
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
