package com.device.inspect.common.restful.tsdata;

/**
 * Created by gxu on 5/30/17.
 */

import java.util.List;

public class RestDeviceMonitoringTSData {
    private String deviceName;
    private Integer deviceId;
    private String startTime;
    private String endTime;
    private List<RestTelemetryTSData> telemetries;

    public String getDeviceName(){
        return this.deviceName;
    }

    public void setDeviceName(String deviceName){
        this.deviceName = deviceName;
    }

    public Integer getDeviceId(){
        return this.deviceId;
    }

    public void setDeviceId(Integer deviceId){
        this.deviceId = deviceId;
    }

    public String getStartTime(){
        return this.startTime;
    }

    public void setStartTime(String startTime){
        this.startTime = startTime;
    }

    public String getEndTime(){
        return this.endTime;
    }

    public void setEndTime(String endTime){
        this.endTime = endTime;
    }

    public List<RestTelemetryTSData> getTelemetries() { return this.telemetries; }

    public void setTelemetries(List<RestTelemetryTSData> telemetries){ this.telemetries = telemetries; }
}
