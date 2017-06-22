package com.device.inspect.common.util.transefer;

/**
 * Created by gxu on 5/30/17.
 */

import com.device.inspect.common.model.device.Pt100;
import com.device.inspect.common.repository.device.Pt100Repository;
import com.device.inspect.common.repository.device.Pt100ZeroRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.*;

public class InspectProcessTool {

    private static final Logger LOGGER = LogManager.getLogger(InspectProcessTool.class);


    private static final Map<String, List<String>> inspectTypes = createMap();

    private static Map<String, List<String>> createMap() {
        Map<String, List<String>> map = new HashMap<String, List<String>>();

        map.put("00", Arrays.asList("temperature_PT100", "degree"));
        map.put("01", Arrays.asList("humidity", "%"));
        map.put("02", Arrays.asList("CO2", "ppm"));
        map.put("04", Arrays.asList("temperature", "degree"));
        map.put("06", Arrays.asList("PSID", "pa"));
        map.put("07", Arrays.asList("methane", "LEL%"));
        map.put("05", Arrays.asList("door", "on_off"));
        map.put("03", Arrays.asList("battery", "%"));
        map.put("08", Arrays.asList("active_energy", "KWH"));
        map.put("09", Arrays.asList("passive_energy", "KWH"));
        map.put("0a", Arrays.asList("voltage", "V"));
        map.put("0b", Arrays.asList("current", "A"));
        map.put("0c", Arrays.asList("active_power", "W"));
        map.put("0d", Arrays.asList("passive_power", "Q"));
        map.put("0e", Arrays.asList("temperature_PT100", "degree"));
        map.put("0f", Arrays.asList("humidity", "%"));
        map.put("10", Arrays.asList("TVOC", "mg_m3"));
        map.put("11", Arrays.asList("Smoke", "normal_abnormal"));
        map.put("18", Arrays.asList("temperature_PT100", "degree"));
        map.put("19", Arrays.asList("temperature_PT100", "degree"));
        map.put("1a", Arrays.asList("temperature_PT100", "degree"));
        map.put("1b", Arrays.asList("current", "A"));

        map.put("22", Arrays.asList("phase_voltage_a", "V"));
        map.put("23", Arrays.asList("phase_voltage_b", "V"));
        map.put("24", Arrays.asList("phase_voltage_c", "V"));
        map.put("25", Arrays.asList("phase_current_a", "A"));
        map.put("26", Arrays.asList("phase_current_b", "A"));
        map.put("27", Arrays.asList("phase_current_c", "A"));
        map.put("28", Arrays.asList("three_phase_power", "KW"));
        map.put("29", Arrays.asList("three_phase_energy", "KWH"));

        return map;
    }

    public static String getMeasurementByCode(String code) {
        code = code.toLowerCase();
        if (inspectTypes.containsKey(code)) {
            return inspectTypes.get(code).get(0);
        } else {
            return code;
        }
    }

    public static String getMeasurementUnitByCode(String code) {
        code = code.toLowerCase();
        if (inspectTypes.containsKey(code)) {

            return inspectTypes.get(code).get(1);
        } else {
            return code;
        }
    }

    public static void calculateInspectValue(InspectMessage inspectMessage, Float zero, Pt100Repository pt100Repository) {
        //测量原值
        Float originalInspectValue;
        //添加矫正值
        Float correctedInspectValue;
        //判断是否是PT100

        // parsing raw data from remote device


        LOGGER.info(String.format("Calculating inspect value for type code %s, %s", inspectMessage.getInspectTypeCode(), getMeasurementByCode(inspectMessage.getInspectTypeCode())));

        if (inspectMessage.getInspectTypeCode().equals("00")) {

            //将int类型转换成两个doube类型的电压
            double AD0 = ((inspectMessage.getiData() >> 16) & 0xffff) * 1.024 / 32768;//前两个字节转换成doube类型的电压
            double AD1 = (inspectMessage.getiData() & 0xffff) * 1.024 / 32768;//后两个字节转换成doube类型的电压

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

                originalInspectValue = Float.valueOf(temperature);
                //添加测量原值

                //矫正值
                correctedInspectValue = originalInspectValue - zero;


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

                //将温度存入测量原值
                originalInspectValue = k * r + b;
                //添加矫正值
                correctedInspectValue = originalInspectValue - zero;

            }
            //设置检测结果

        } else if (inspectMessage.getInspectTypeCode().equals("07")) {
            //判断是不是甲烷
            //根据上传的值算出电压
            Float v = (Float.valueOf(inspectMessage.getiData()) * Float.valueOf(2.018f)) / Float.valueOf(32768);

            //算出的电压值如果小于0.4   record 都为百分之零
            if (v < 0.4) {
                originalInspectValue = 0f;
                correctedInspectValue = originalInspectValue;


            } else if (v < 2) {
                float b = 0.4f;
                float k = 1.6f;
                originalInspectValue = (v - b) / k;

                //甲烷添加测量原值
                correctedInspectValue = originalInspectValue - zero;

            } else {
                originalInspectValue = 10f;
                correctedInspectValue = originalInspectValue;
            }

        } else if (inspectMessage.getInspectTypeCode().equals("06")) {
            int value = ByteAndHex.byteArrayToInt(ByteAndHex.hexStringToBytes(inspectMessage.getsData().substring(4)), 0, 2);
            System.out.println("part value: " + value);
            if (value > 32767) {
                value = value - 65536;
            }

            originalInspectValue = Float.valueOf(value);

            // 默认海拔750米， 对应压差调整系数为1.04
            correctedInspectValue = originalInspectValue / 60 * (float) 1.04;

        } else if (inspectMessage.getInspectTypeCode().equals("08") || inspectMessage.getInspectTypeCode().equals("09")) {
            //添加测量原值

            originalInspectValue = Float.valueOf(inspectMessage.getiData());
            correctedInspectValue = originalInspectValue * 250 * 20 / 18000000 - zero;


        } else if (inspectMessage.getInspectTypeCode().equals("0a")) {
            originalInspectValue = Float.valueOf(inspectMessage.getiData());
            correctedInspectValue = originalInspectValue * 250 / 10000 - zero;


        } else if (inspectMessage.getInspectTypeCode().equals("0b")) {
            originalInspectValue = Float.valueOf(inspectMessage.getiData());
            correctedInspectValue = originalInspectValue * 20 / 10000 - zero;


        } else if (inspectMessage.getInspectTypeCode().equals("0c") || inspectMessage.getInspectTypeCode().equals("0d")) {
            originalInspectValue = Float.valueOf(inspectMessage.getiData());
            correctedInspectValue = originalInspectValue * 20 * 250 / 10000 - zero;


        } else if (inspectMessage.getInspectTypeCode().equals("18") || inspectMessage.getInspectTypeCode().equals("19") || inspectMessage.getInspectTypeCode().equals("1a")) {

            double U = inspectMessage.getiData() * 1.024 / 32768;
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

                //添加测量原值

                originalInspectValue = Float.valueOf(temperature);
                //矫正值
                correctedInspectValue = originalInspectValue - zero;

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
                //添加测量原值
                originalInspectValue = k * r + b;
                //添加矫正值
                correctedInspectValue = originalInspectValue - zero;

            }
        } else if (inspectMessage.getInspectTypeCode().equals("1b")) {  // IRING
            Float vRecord = (Float.valueOf(inspectMessage.getiData()) * 512 / 10000) / 32768;
            originalInspectValue = vRecord * 5 * 1000 / 333;
            correctedInspectValue = originalInspectValue - zero;
        } else if (inspectMessage.getInspectTypeCode().equals("1c") || inspectMessage.getInspectTypeCode().equals("1d") || inspectMessage.getInspectTypeCode().equals("1e")) {  // MPU_AX, MPU_AY, MPU_AZ
            originalInspectValue = (Float.valueOf(inspectMessage.getiData()))/32768 * 2;
            correctedInspectValue = originalInspectValue - zero;
        } else if (inspectMessage.getInspectTypeCode().equals("1f") || inspectMessage.getInspectTypeCode().equals("20") || inspectMessage.getInspectTypeCode().equals("21")) {  // MPU_GX, MPU_GY, MPU_AZ
            originalInspectValue = (Float.valueOf(inspectMessage.getiData()))/32768 * 2000;
            correctedInspectValue = originalInspectValue - zero;
        } else if(inspectMessage.getInspectTypeCode().equals("22") || inspectMessage.getInspectTypeCode().equals("23") || inspectMessage.getInspectTypeCode().equals("24")){
            originalInspectValue = (Float.valueOf(inspectMessage.getiData()))/40;  // 值=DATA*U0*Ubb/10000; 单位：V;
            correctedInspectValue = originalInspectValue - zero;
        } else if(inspectMessage.getInspectTypeCode().equals("25") || inspectMessage.getInspectTypeCode().equals("26") || inspectMessage.getInspectTypeCode().equals("27")){
            originalInspectValue = (Float.valueOf(inspectMessage.getiData()))/10;  // 值=DATA*I0*Ibb/10000; 单位：A;
            correctedInspectValue = originalInspectValue - zero;
        } else if(inspectMessage.getInspectTypeCode().equals("28")){
            originalInspectValue = Float.valueOf((short)(inspectMessage.getiData()).intValue())*3/40;  // 值=VALUE*250*5*200*3/10000 /1000; 单位：kW;
            correctedInspectValue = originalInspectValue - zero;
        } else if(inspectMessage.getInspectTypeCode().equals("29")){
            originalInspectValue = (Float.valueOf(inspectMessage.getiData()))/72;  // 值=DATA*250*5*200/18000000; 单位：kWh;
            correctedInspectValue = originalInspectValue - zero;
        } else {
            //添加测量原值
            originalInspectValue = Float.valueOf(inspectMessage.getiData());
            correctedInspectValue = originalInspectValue / 1000 - zero;

        }

        inspectMessage.setOriginalValue(originalInspectValue);
        inspectMessage.setCorrectedValue(correctedInspectValue);

    }

}
