package com.device.inspect.common.util.transefer;

/**
 * Created by gxu on 6/3/17.
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InspectMessage {
    private Date samplingTime;
    private String inspectTypeCode;
    private String monitorSN;
    private String sData;
    private Integer iData;

    private Float originalValue;
    private Float correctedValue;

    public Date getSamplingTime(){return this.samplingTime;}
    public String getInspectTypeCode() { return this.inspectTypeCode; }
    public String getMonitorSN() {return this.monitorSN;}
    public String getsData() {return this.sData;}
    public Integer getiData() {return this.iData;}

    public Float getOriginalValue() {return this.originalValue;}
    public void setOriginalValue(Float originalValue){ this.originalValue = originalValue;}
    public Float getCorrectedValue() {return this.correctedValue;}
    public void setCorrectedValue(Float correctedValue) {this.correctedValue = correctedValue;}


    public InspectMessage(String rawMessageString) throws  ParseException{
        this.inspectTypeCode = rawMessageString.substring(6, 8);
        this.monitorSN = rawMessageString.substring(8, 26);

        //直接获取解析终端时间报文
        String deviceDateYear = rawMessageString.substring(34, 36);
        String deviceDateMonth = rawMessageString.substring(36, 38);
        String deviceDateDay = rawMessageString.substring(38, 40);
        String deviceDateHour = rawMessageString.substring(40, 42);
        String deviceDateMinute = rawMessageString.substring(42, 44);
        String deviceDateSecond = rawMessageString.substring(44, 46);
        String strDeviceDate = 20 + "" + deviceDateYear + deviceDateMonth + deviceDateDay + deviceDateHour + deviceDateMinute + deviceDateSecond;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date deviceSamplingTime = new Date();
        deviceSamplingTime = sdf.parse(strDeviceDate);
        deviceSamplingTime.setTime(deviceSamplingTime.getTime());

        this.samplingTime = deviceSamplingTime;
        this.sData = rawMessageString.substring(48, 56);
        this.iData = ByteAndHex.byteArrayToInt(ByteAndHex.hexStringToBytes(this.sData), 0, 4);


    }
}
