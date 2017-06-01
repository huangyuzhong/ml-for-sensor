package com.device.inspect.controller;

import com.device.inspect.Application;
import com.device.inspect.common.model.charater.User;
import com.device.inspect.common.model.device.*;
import com.device.inspect.common.model.firm.Room;
import com.device.inspect.common.repository.charater.UserRepository;
import com.device.inspect.common.repository.device.*;
import com.device.inspect.common.repository.firm.RoomRepository;
import com.device.inspect.common.repository.record.MessageSendRepository;
import com.device.inspect.common.restful.RestResponse;
import com.device.inspect.common.restful.device.RestInspectData;
import com.device.inspect.common.restful.tsdata.RestDeviceMonitoringTSData;
import com.device.inspect.common.restful.tsdata.RestTelemetryTSData;
import com.device.inspect.common.util.transefer.ByteAndHex;
import com.device.inspect.common.util.transefer.InspectTypeTool;
import com.device.inspect.common.util.transefer.StringDate;
import com.sun.jersey.core.impl.provider.entity.XMLJAXBElementProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.influxdb.InfluxDB;
import org.influxdb.impl.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.text.ParseException;
import java.util.*;
import java.text.SimpleDateFormat;
/**
 * Created by Administrator on 2016/7/25.
 */
@RestController
@RequestMapping(value = "/api/rest")
public class SocketMessageApi {
    private static final Logger LOGGER = LogManager.getLogger(SocketMessageApi.class);

    @Autowired
    private MessageController messageController;

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

    String unit = "s";

    private User judgeByPrincipal(Principal principal){
        if (null == principal||null==principal.getName())
            throw new UsernameNotFoundException("You are not login!");
        User user = userRepository.findByName(principal.getName());
        if (null==user)
            throw new UsernameNotFoundException("user not found!");
        return user;
    }

    public DeviceInspect parseInspectAndSave(String inspectMessage, boolean onlineData) {
        String monitorTypeCode = inspectMessage.substring(6, 8);


        //直接获取解析终端时间报文
        String deviceDateYear = inspectMessage.substring(34, 36);
        String deviceDateMonth = inspectMessage.substring(36, 38);
        String deviceDateDay = inspectMessage.substring(38, 40);
        String deviceDateHour = inspectMessage.substring(40, 42);
        String deviceDateMinute = inspectMessage.substring(42, 44);
        String deviceDateSecond = inspectMessage.substring(44, 46);
        String strDeviceDate = 20 + "" + deviceDateYear + deviceDateMonth + deviceDateDay + deviceDateHour + deviceDateMinute + deviceDateSecond;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date deviceSamplingTime = new Date();
        try {
            deviceSamplingTime = sdf.parse(strDeviceDate);
            deviceSamplingTime.setTime(deviceSamplingTime.getTime());

            LOGGER.info(deviceSamplingTime.toString());

        } catch (ParseException e) {
            LOGGER.error("parsing device time string error: " + e.getMessage());
        }

        String deviceSamplingData = inspectMessage.substring(48, 56);
        int iDeviceSamplingData = ByteAndHex.byteArrayToInt(ByteAndHex.hexStringToBytes(deviceSamplingData), 0, 4);
        Device device = new Device();
        String mointorCode = inspectMessage.substring(8, 26);
        //LOGGER.info("终端编号是：：：：："+mointorCode);
        MonitorDevice monitorDevice = monitorDeviceRepository.findByNumber(mointorCode);
        if (null == monitorDevice)
            return null;
        if (monitorTypeCode.equals("03")) {
            monitorDevice.setBattery(String.valueOf(Float.valueOf(iDeviceSamplingData) / 10));
            monitorDeviceRepository.save(monitorDevice);
        }
        device = monitorDevice.getDevice();
        if (device.getEnable() == 0)
            return null;

        InspectType inspectType = inspectTypeRepository.findByCode(monitorTypeCode);

        InspectData inspectData = new InspectData();
        if (null != inspectType) {
            DeviceInspect deviceInspect = deviceInspectRepository.
                    findByInspectTypeIdAndDeviceId(inspectType.getId(), device.getId());
            if (null == deviceInspect)
                return null;
            //测量原值
            Float record;
            //添加矫正值
            Float check;
            //判断是否是PT100

            // parsing raw data from remote device
            try {

                if (monitorTypeCode.equals("00")) {
                    LOGGER.info(String.format("Device %d, Monitor code 00, pt 100 temperature",
                            device.getId()));
                    inspectData.setCreateDate(deviceSamplingTime);
                    inspectData.setDevice(device);
                    inspectData.setDeviceInspect(deviceInspect);


                    //将int类型转换成两个doube类型的电压
                    double AD0 = ((iDeviceSamplingData >> 16) & 0xffff) * 1.024 / 32768;//前两个字节转换成doube类型的电压
                    double AD1 = (iDeviceSamplingData & 0xffff) * 1.024 / 32768;//后两个字节转换成doube类型的电压

                    double R = (1000 * AD0 - 1000 * AD1) / (3.38 - AD0 - AD1);//生成double类型的电阻

                    LOGGER.info("Resistance is :" + R);

                    //将double类型的电阻转换成float类型
                    //将电阻四舍五入到小数点两位
                    BigDecimal bigDecimal = new BigDecimal(Float.valueOf(String.valueOf(R)));
                    Float r = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                    //通过设备编号去查找相应的pt100,如果对应的电阻直接有相应的温度
                    if (pt100Repository.findByResistance(r) != null) {
                        Pt100 pt100 = pt100Repository.findByResistance(r);
                        //通过电阻找到对象的温度
                        String temperature = pt100.getTemperature();

                        record = Float.valueOf(temperature);
                        //添加测量原值
                        inspectData.setRealValue(String.valueOf(record));
                        deviceInspect.setOriginalValue(record);
                        //矫正值
                        check = record - (deviceInspect.getZero());
                        inspectData.setResult(String.valueOf(check));
                        deviceInspect.setCorrectionValue(check);
                    } else {
                        //通过电阻找到对应的温度
                        //从小到大
                        List<Pt100> list1 = new ArrayList<Pt100>();
                        //使用默认表查询
                        list1 = pt100Repository.findByResistanceAfterOrderByResistanceDESC(r);
                        //找到对应的Pt100
                        Pt100 one = list1.get(0);
                        String temperature1 = one.getTemperature();
                        Float resistance1 = one.getResistance();
                        //从大到小
                        List<Pt100> list2 = new ArrayList<Pt100>();
                        //使用默认表查询
                        list2 = pt100Repository.findByResistanceBeforeOrderByResistanceASC(r);
                        //找到对应的Pt100
                        Pt100 two = list2.get(0);
                        String temperature2 = two.getTemperature();
                        Float resistance2 = two.getResistance();
                        //进行线性公式计算出改r下面的温度
                        float k = (Float.valueOf(temperature2) - Float.valueOf(temperature1)) / (resistance2 - resistance1);

                        float b = Float.valueOf(temperature1) - (k * resistance1);

                        //将温度存入record
                        record = k * r + b;
                        //添加测量原值
                        inspectData.setRealValue(String.valueOf(record));
                        deviceInspect.setOriginalValue(record);
                        //添加矫正值
                        check = record - (deviceInspect.getZero());
                        inspectData.setResult(String.valueOf(check));
                        deviceInspect.setCorrectionValue(check);
                    }
                    //设置检测结果

                } else if (monitorTypeCode.equals("07")) {
                    LOGGER.info(String.format("Device %d, Monitor code 07, jia wan",
                            device.getId()));
                    //判断是不是甲烷
                    //根据上传的值算出电压
                    Float v = (Float.valueOf(iDeviceSamplingData) * Float.valueOf(2.018f)) / Float.valueOf(32768);

                    //算出的电压值如果小于0.4   record 都为百分之零
                    if (v < 0.4) {
                        inspectData.setCreateDate(deviceSamplingTime);
                        inspectData.setDevice(device);
                        inspectData.setDeviceInspect(deviceInspect);
                        record = 0f;
                        //添加校正值
                        inspectData.setResult(String.valueOf(record));
                        deviceInspect.setOriginalValue(record);
                        //甲烷添加测量原值
                        inspectData.setRealValue(String.valueOf(record));
                        deviceInspect.setCorrectionValue(record);
                    } else if (v < 2) {
                        float b = 0.4f;
                        float k = 1.6f;
                        record = (v - b) / k;

                        //甲烷添加测量原值
                        inspectData.setRealValue(String.valueOf(record));
                        deviceInspect.setOriginalValue(record);
                        inspectData.setCreateDate(deviceSamplingTime);
                        inspectData.setDevice(device);
                        inspectData.setDeviceInspect(deviceInspect);
                        check = record - (deviceInspect.getZero());
                        inspectData.setResult(String.valueOf(check));
                        deviceInspect.setCorrectionValue(check);
                    } else {
                        record = 10f;
                        inspectData.setCreateDate(deviceSamplingTime);
                        inspectData.setDevice(device);
                        inspectData.setDeviceInspect(deviceInspect);
                        inspectData.setResult(String.valueOf(record));
                        //甲烷添加测量原值
                        inspectData.setRealValue(String.valueOf(record));
                        deviceInspect.setOriginalValue(record);
                    }

                } else if (monitorTypeCode.equals("06")) {
                    LOGGER.info(String.format("Device %d, Monitor code 06, sdp610 pressure",
                            device.getId()));
                    inspectData.setCreateDate(deviceSamplingTime);
                    inspectData.setDevice(device);
                    inspectData.setDeviceInspect(deviceInspect);
                    System.out.println("first data: " + deviceSamplingData);
                    System.out.println(deviceSamplingData.length());
                    int value = ByteAndHex.byteArrayToInt(ByteAndHex.hexStringToBytes(deviceSamplingData.substring(4)), 0, 2);
                    System.out.println("part value: " + value);
                    if (value > 32767) {
                        value = value - 65536;
                    }
                    System.out.println("real value: " + value);
                    inspectData.setRealValue(String.valueOf(value));
                    record = Float.valueOf(value) / 60;
                    System.out.println("filted record: " + record);
                    deviceInspect.setOriginalValue(record);
                    check = record * (float) 1.04;
                    System.out.println("checked record: " + check);
                    inspectData.setResult(String.valueOf(check));
                    deviceInspect.setCorrectionValue(check);
                } else if (monitorTypeCode.equals("08") || monitorTypeCode.equals("09")) {
                    LOGGER.info(String.format("Device %d, Monitor code 08-09, energy",
                            device.getId()));
                    inspectData.setCreateDate(deviceSamplingTime);
                    inspectData.setDevice(device);
                    inspectData.setDeviceInspect(deviceInspect);
                    //添加测量原值
                    inspectData.setRealValue(String.valueOf(iDeviceSamplingData));
                    record = Float.valueOf(iDeviceSamplingData) * 250 * 20 / 18000000;
                    deviceInspect.setOriginalValue(record);
                    check = record - (deviceInspect.getZero());
                    inspectData.setResult(String.valueOf(check));
                    deviceInspect.setCorrectionValue(check);
                } else if (monitorTypeCode.equals("0a")) {
                    LOGGER.info(String.format("Device %d, Monitor code 0a, voltage",
                            device.getId()));
                    inspectData.setCreateDate(deviceSamplingTime);
                    inspectData.setDevice(device);
                    inspectData.setDeviceInspect(deviceInspect);
                    inspectData.setRealValue(String.valueOf(iDeviceSamplingData));
                    record = Float.valueOf(iDeviceSamplingData) * 250 / 10000;
                    deviceInspect.setOriginalValue(record);
                    check = record - (deviceInspect.getZero());
                    inspectData.setResult(String.valueOf(check));
                    deviceInspect.setCorrectionValue(check);
                } else if (monitorTypeCode.equals("0b")) {
                    LOGGER.info(String.format("Device %d, Monitor code 0b, currency",
                            device.getId()));
                    LOGGER.info("e-currency: " + deviceSamplingTime);
                    inspectData.setCreateDate(deviceSamplingTime);
                    inspectData.setDevice(device);
                    inspectData.setDeviceInspect(deviceInspect);
                    inspectData.setRealValue(String.valueOf(iDeviceSamplingData));
                    record = Float.valueOf(iDeviceSamplingData) * 20 / 10000;
                    deviceInspect.setOriginalValue(record);
                    check = record - (deviceInspect.getZero());
                    inspectData.setResult(String.valueOf(check));
                    deviceInspect.setCorrectionValue(check);

                } else if (monitorTypeCode.equals("0c") || monitorTypeCode.equals("0d")) {
                    LOGGER.info(String.format("Device %d, Monitor code 0c-0d, power",
                            device.getId()));
                    inspectData.setCreateDate(deviceSamplingTime);
                    inspectData.setRealValue(String.valueOf(iDeviceSamplingData));
                    record = Float.valueOf(iDeviceSamplingData) * 20 * 250 / 10000;
                    inspectData.setDevice(device);
                    inspectData.setDeviceInspect(deviceInspect);
                    deviceInspect.setOriginalValue(record);
                    check = record - (deviceInspect.getZero());
                    inspectData.setResult(String.valueOf(check));
                    deviceInspect.setCorrectionValue(check);

                } else if (monitorTypeCode.equals("18") || monitorTypeCode.equals("19") || monitorTypeCode.equals("1a")) {
                    LOGGER.info(String.format("Device %d, Monitor code %s, pt 100 temperature",
                            device.getId(), monitorTypeCode));
                    inspectData.setCreateDate(deviceSamplingTime);
                    inspectData.setDevice(device);
                    inspectData.setDeviceInspect(deviceInspect);

                    double U = iDeviceSamplingData * 1.024 / 32768;
                    double R = 1002 * U / (3.37 - U) - 0.5;//生成double类型的电阻

                    LOGGER.info("Resistance is :" + R);

                    //将double类型的电阻转换成float类型
                    //将电阻四舍五入到小数点两位
                    BigDecimal bigDecimal = new BigDecimal(Float.valueOf(String.valueOf(R)));
                    Float r = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                    //通过设备编号去查找相应的pt100,如果对应的电阻直接有相应的温度
                    if (pt100Repository.findByResistance(r) != null) {
                        Pt100 pt100 = pt100Repository.findByResistance(r);
                        //通过电阻找到对象的温度
                        String temperature = pt100.getTemperature();

                        record = Float.valueOf(temperature);
                        //添加测量原值
                        inspectData.setRealValue(String.valueOf(record));
                        deviceInspect.setOriginalValue(record);
                        //矫正值
                        check = record - (deviceInspect.getZero());
                        inspectData.setResult(String.valueOf(check));
                        deviceInspect.setCorrectionValue(check);
                    } else {
                        //通过电阻找到对应的温度
                        //从小到大
                        List<Pt100> list1 = new ArrayList<Pt100>();
                        //使用默认表查询
                        list1 = pt100Repository.findByResistanceAfterOrderByResistanceDESC(r);
                        //找到对应的Pt100
                        Pt100 one = list1.get(0);
                        String temperature1 = one.getTemperature();
                        Float resistance1 = one.getResistance();
                        //从大到小
                        List<Pt100> list2 = new ArrayList<Pt100>();
                        //使用默认表查询
                        list2 = pt100Repository.findByResistanceBeforeOrderByResistanceASC(r);
                        //找到对应的Pt100
                        Pt100 two = list2.get(0);
                        String temperature2 = two.getTemperature();
                        Float resistance2 = two.getResistance();
                        //进行线性公式计算出改r下面的温度
                        float k = (Float.valueOf(temperature2) - Float.valueOf(temperature1)) / (resistance2 - resistance1);

                        float b = Float.valueOf(temperature1) - (k * resistance1);

                        //将温度存入record
                        record = k * r + b;
                        //添加测量原值
                        inspectData.setRealValue(String.valueOf(record));
                        deviceInspect.setOriginalValue(record);
                        //添加矫正值
                        check = record - (deviceInspect.getZero());
                        inspectData.setResult(String.valueOf(check));
                        deviceInspect.setCorrectionValue(check);
                    }
                } else if(monitorTypeCode.equals("1b")){
                    LOGGER.info(String.format("Device %d, Monitor code 0a, voltage",
                            device.getId()));
                    inspectData.setCreateDate(deviceSamplingTime);
                    inspectData.setDevice(device);
                    inspectData.setDeviceInspect(deviceInspect);
                    inspectData.setRealValue(String.valueOf(iDeviceSamplingData));
                    Float vRecord = (Float.valueOf(iDeviceSamplingData) * 512 / 10000) / 32768;
                    record = vRecord * 5 * 1000 / 333;
                    LOGGER.info(String.format("数据转化的电压为：%d", vRecord));
                    LOGGER.info(String.format("根据电压转化的电流为：%d", record));
                    deviceInspect.setOriginalValue(record);
                    check = record - (deviceInspect.getZero());
                    inspectData.setResult(String.valueOf(check));
                    deviceInspect.setCorrectionValue(check);
                } else {
                    LOGGER.info(String.format("Device %d, Monitor code 0e-0f-10",
                            device.getId()));
                    inspectData.setCreateDate(deviceSamplingTime);
                    inspectData.setDevice(device);
                    inspectData.setDeviceInspect(deviceInspect);
                    //添加测量原值
                    inspectData.setRealValue(String.valueOf(iDeviceSamplingData));
                    record = Float.valueOf(iDeviceSamplingData) / 1000;
                    deviceInspect.setOriginalValue(record);
                    check = record - (deviceInspect.getZero());
                    inspectData.setResult(String.valueOf(check));
                    deviceInspect.setCorrectionValue(check);
                }
            } catch (Exception e) {
                LOGGER.error("failed to parse datagram from remote device: " + e.getLocalizedMessage());
                LOGGER.error("exception stack: ", e);
                return null;
            }

            LOGGER.info("successfully parsing datagram.");

            if(onlineData){
                device.setLastActivityTime(deviceSamplingTime);
            }

            try {
                deviceInspectRepository.save(deviceInspect);
                inspectDataRepository.save(inspectData);
            } catch (Exception e) {
                LOGGER.error("failed to save parsed datagram to database. " + e.getLocalizedMessage());
                LOGGER.error("exception stack: ", e);
                return null;
            }

            LOGGER.info("parsed datagram saved to database deviceInspect and inspectData");

            if (null == deviceInspect.getStandard() || null == deviceInspect.getHighUp() || null == deviceInspect.getLowDown()) {
                return null;
            }

            // set alert type, and send alert message if necessary
            int alert_type = 0;
            if(deviceInspect.getInspectPurpose() == 0){
                // alert inspect
                LOGGER.info("check data against alert");
                if(deviceInspect.getHighUp() < record || record < deviceInspect.getHighDown()){
                    alert_type = 2;
                    inspectData.setType("high");

                    // update device alert time and alert status
                    if(onlineData) {
                        device.setLastRedAlertTime(deviceSamplingTime);
                        device.setStatus(2);
                    }

                    // push notification if necessary
                    if (record > deviceInspect.getLowUp()) {
                        messageController.sendAlertMsg(device, deviceInspect, deviceInspect.getLowUp(), record, deviceSamplingTime);
                    } else {
                        messageController.sendAlertMsg(device, deviceInspect, deviceInspect.getLowDown(), record, deviceSamplingTime);
                    }

                    LOGGER.info("red alert");
                }
                else if(deviceInspect.getLowUp() < record || record < deviceInspect.getLowDown()){
                    alert_type = 1;

                    // set inspect_data column 'type'
                    inspectData.setType("low");

                    // update device alert time and alert status
                    if(onlineData) {
                        device.setLastYellowAlertTime(deviceSamplingTime);
                        device.setStatus(1);
                    }

                    // push notification if necessary
                    if (record > deviceInspect.getLowUp()) {
                        messageController.sendAlertMsg(device, deviceInspect, deviceInspect.getLowUp(), record, deviceSamplingTime);
                    } else {
                        messageController.sendAlertMsg(device, deviceInspect, deviceInspect.getLowDown(), record, deviceSamplingTime);
                    }

                    LOGGER.info("yellow alert");
                }
                else{
                    alert_type = 0;

                    inspectData.setType("normal");
                    LOGGER.info("normal");
                }
            }
            else{
                inspectData.setType("normal");
                LOGGER.info("status data, pass alert check");
            }

            // update alert_count table, record alert info
            if(alert_type != 0){
                InspectData last_inspect = inspectDataRepository.findTopByDeviceIdAndDeviceInspectIdAndCreateDateBeforeOrderByCreateDateDesc(
                        device.getId(),deviceInspect.getId(), deviceSamplingTime);
                AlertCount last_yellow_alert = alertCountRepository.findTopByDeviceIdAndInspectTypeIdAndTypeAndFinishBeforeOrderByFinishDesc(
                        deviceInspect.getDevice().getId(), deviceInspect.getInspectType().getId(), 1, deviceSamplingTime);
                AlertCount last_red_alert = alertCountRepository.findTopByDeviceIdAndInspectTypeIdAndTypeAndFinishBeforeOrderByFinishDesc(
                        deviceInspect.getDevice().getId(), deviceInspect.getInspectType().getId(), 2, deviceSamplingTime);
                if(last_inspect == null ||
                        last_inspect.getType().equals("normal") ||
                        deviceSamplingTime.getTime() - last_inspect.getCreateDate().getTime() > 5*60*1000){
                    // new alert count
                    createNewAlertAndSave(device, deviceInspect.getInspectType(), alert_type, unit, deviceSamplingTime);
                }

                else{
                    AlertCount liveAlert = null;
                    if(last_inspect.getCreateDate().getTime() == last_yellow_alert.getFinish().getTime()){
                        liveAlert = last_yellow_alert;
                    }
                    else if(last_inspect.getCreateDate().getTime() == last_red_alert.getFinish().getTime()){
                        liveAlert = last_red_alert;
                    }

                    if(liveAlert == null){
                        LOGGER.error(String.format("Device id: %d, Inspect id: %d, live alert count not match. Sample Time %s.",
                                device.getId(), deviceInspect.getId(), deviceSamplingTime.toString()));

                        AlertCount newerCount = last_red_alert.getFinish().getTime() >= last_yellow_alert.getFinish().getTime() ?
                                last_red_alert : last_yellow_alert;
                        if(deviceSamplingTime.getTime() - newerCount.getFinish().getTime() > 5*60*1000){
                            // create a new alert
                            createNewAlertAndSave(device, deviceInspect.getInspectType(), alert_type, unit, deviceSamplingTime);
                        }
                        else{
                            // set newer alert as live alert
                            liveAlert = newerCount;
                        }
                    }

                    if(liveAlert != null){
                        if(liveAlert.getType() != alert_type){
                            // alert type is not equal, create a new alert
                            createNewAlertAndSave(device, deviceInspect.getInspectType(), alert_type, unit, deviceSamplingTime);
                        }
                        else{
                            // extend live alert
                            liveAlert.setNum(liveAlert.getNum() + 1);
                            liveAlert.setFinish(deviceSamplingTime);
                            alertCountRepository.save(liveAlert);
                            LOGGER.info("datagram alert type set and updating to db");
                        }
                    }
                }

            }

            inspectDataRepository.save(inspectData);

            // write data to influx DB
            if(Application.influxDBManager != null){
                try {
                    int retry = 0;
                    int max_try = 3;

                    while(retry < max_try) {
                        boolean writeSuccess = Application.influxDBManager.writeTelemetry(deviceSamplingTime, device.getId(),
                                device.getName(), device.getDeviceType().getName(),
                                inspectData.getType(),  deviceInspect.getId(),
                                InspectTypeTool.getMeasurementByCode(monitorTypeCode),
                                Float.parseFloat(inspectData.getResult()), Float.parseFloat(inspectData.getRealValue()));

                        if(!writeSuccess){
                            Thread.sleep(200);
                            retry ++;
                        }
                        else{
                            LOGGER.info(String.format("Successfully write telemetry %s (%s) to influxdb",
                                    InspectTypeTool.getMeasurementByCode(monitorTypeCode), inspectData.getResult()));

                            break;
                        }
                    }

                    if(retry >= max_try){
                        LOGGER.error(String.format("Abort writing telemetry %s (%s) after %d approach",
                                InspectTypeTool.getMeasurementByCode(monitorTypeCode), inspectData.getResult(), max_try));
                    }


                }catch (Exception e){
                    LOGGER.error(String.format("Failed to parse %s telemetry data %s, %s",
                            InspectTypeTool.getMeasurementByCode(monitorTypeCode),
                            inspectData.getResult(), inspectData.getRealValue()));

                }
            }

            if(onlineData){
                //因为一个设备可能同时发送多个参数的数据， 所以有多个线程同时update device， 会造成deadlock。
                //这里加上retry来避免deadlock造成的data丢失
                int retry = 0;
                int max_retry = 5;
                while(retry < max_retry) {
                    try {
                        deviceRepository.save(device);
                        break;
                    } catch (Exception e) {
                        LOGGER.info(String.format("Failed to update device %d, Err: %s", device.getId(), e.toString()));

                        retry ++;
                        try {
                            Thread.sleep(100);
                        }catch (InterruptedException ie){
                            LOGGER.warn(String.format("Failed to sleep 0.1 sec. Err: %s", ie.toString()));
                        }
                    }
                }

                if(retry >= max_retry) {
                    LOGGER.error(String.format("Aborting update device %d after %d approaches", device.getId(), max_retry));
                }
            }

            return deviceInspect;
        }
        return null;
    }

    // this function may need a better place
    void createNewAlertAndSave(Device device, InspectType inspectType, Integer alert_type, String unit, Date deviceSamplingTime){
        AlertCount newAlert = new AlertCount();
        newAlert.setDevice(device);
        newAlert.setInspectType(inspectType);
        newAlert.setNum(1);
        newAlert.setType(alert_type);
        newAlert.setUnit(unit);
        newAlert.setCreateDate(deviceSamplingTime);
        newAlert.setFinish(deviceSamplingTime);
        alertCountRepository.save(newAlert);
        LOGGER.info("datagram alert type set and updating to db");
    }

    /**
     * 更新数据
     * @param result
     * @return
    */
    @RequestMapping(value = "/socket/insert/data",method = RequestMethod.GET)
    public RestResponse excuteInspectData(@RequestParam String result) {
        LOGGER.info(result);
        DeviceInspect deviceInspect = parseInspectAndSave(result, true);
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
                if(deviceInspect.getInspectPurpose() == 1 && runningLevel == -1){
                    // when device has status inspect, lowest running level is 0 (shut down)
                    runningLevel = 0;
                }
                List<InspectData> inspectDatas;
                if(requestParam.get("timeVal") != null){
                    Date startTime = new Date();
                    startTime.setTime(Long.parseLong(requestParam.get("timeVal")));
                    inspectDatas = inspectDataRepository.
                            findTop100ByDeviceInspectIdAndCreateDateAfterOrderByCreateDateDesc(deviceInspect.getId(),
                                    startTime);
                    if(inspectDatas == null || inspectDatas.size() == 0){
                        inspectDatas = inspectDataRepository.
                                findTop20ByDeviceIdAndDeviceInspectIdOrderByCreateDateDesc(deviceId, deviceInspect.getId());
                    }
                }
                else{
                    inspectDatas = inspectDataRepository.
                            findTop20ByDeviceIdAndDeviceInspectIdOrderByCreateDateDesc(deviceId, deviceInspect.getId());
                }
                if (null!=inspectDatas&&inspectDatas.size()>0) {
                    List<RestInspectData> insertDatas = new ArrayList<RestInspectData>();
                    for (InspectData inspectData : inspectDatas) {
                        insertDatas.add(new RestInspectData(inspectData));
                        if(null!=inspectData.getDeviceInspect().getHighDown()){
                            float m = 0;
                            if (Float.valueOf(inspectData.getResult())>inspectData.getDeviceInspect().getStandard()){
                                m = Float.valueOf(inspectData.getResult())-inspectData.getDeviceInspect().getStandard();
                            }else {
                                m = inspectData.getDeviceInspect().getStandard()-Float.valueOf(inspectData.getResult());
                            }
                            score = score - m/(inspectData.getDeviceInspect().getHighUp()-inspectData.getDeviceInspect().getStandard());
                        }
                    }
                    //device running status
                    if(deviceInspect.getInspectPurpose() == 1){ // this inspect is used to guide running status
                        List<DeviceInspectRunningStatus> runningStatuses = deviceInspectRunningStatusRepository.findByDeviceInspectId(deviceInspect.getId());
                        for(DeviceInspectRunningStatus status : runningStatuses){
                            if(Float.parseFloat(inspectDatas.get(0).getResult()) > status.getThreshold()){
                                if(status.getDeviceRunningStatus().getLevel() > runningLevel){
                                    runningLevel = status.getDeviceRunningStatus().getLevel();
                                }
                            }
                        }
                    }

                    list.add(insertDatas);
                }

                if(Application.influxDBManager != null){

                    String measurementName = InspectTypeTool.getMeasurementByCode(deviceInspect.getInspectType().getCode());

                    String measurementUnit = InspectTypeTool.getMeasurementUnitByCode(deviceInspect.getInspectType().getCode());
                    List<List<Object>> inspectSeries = null;
                    if(requestParam.get("timeVal") != null){
                        Date startTime = new Date();
                        startTime.setTime(Long.parseLong(requestParam.get("timeVal")));


                        // each List<Object> is [time, value]
                        inspectSeries = Application.influxDBManager.readTelemetryInTimeRange(measurementName,
                                deviceId, deviceInspect.getId(), startTime, new Date());


                    }
                    else{
                        // default to get data in latest 5 minutes
                        Date startTime = new Date (currentTime.getTime() - 5 * 60 * 1000);
                        inspectSeries = Application.influxDBManager.readTelemetryInTimeRange(measurementName,
                                deviceId, deviceInspect.getId(), startTime, new Date());

                    }

                    List<Long> timeSeries = new ArrayList<Long>();
                    List<Float> valueSeries = new ArrayList<Float>();

                    if(inspectSeries != null && inspectSeries.size() > 0) {

                        for (List<Object> telemetryEntry : inspectSeries) {
                            String timeRFC3999 = telemetryEntry.get(0).toString();

                            long timeStamp = StringDate.rfc3339ToLong(timeRFC3999);
                            timeSeries.add(timeStamp);
                            valueSeries.add(Float.parseFloat(telemetryEntry.get(1).toString()));
                        }

                        RestTelemetryTSData telemetryTSData = new RestTelemetryTSData();
                        telemetryTSData.setName(measurementName);
                        telemetryTSData.setCode(deviceInspect.getInspectType().getCode());
                        telemetryTSData.setUnit(measurementUnit);
                        telemetryTSData.setDeviceInspectId(deviceInspect.getId());
                        telemetryTSData.setTimeSeries(timeSeries);
                        telemetryTSData.setValueSeries(valueSeries);

                        restTelemetryTSDataList.add(telemetryTSData);
                    }
                }
            }
        }
        map.put("list", list);
        map.put("score", score);
        map.put("runningStatus", runningLevel);

        if(Application.influxDBManager != null){
            map.put("tsdata", restDeviceMonitoringTSData);
        }
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
