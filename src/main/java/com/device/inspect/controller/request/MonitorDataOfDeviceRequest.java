package com.device.inspect.controller.request;

import java.util.List;

/**
 * Created by zyclincoln on 4/17/17.
 */
public class MonitorDataOfDeviceRequest {
    private String deviceId;
    private String beginTime;
    private String endTime;
    private List<String> monitorId;

    public String getDeviceId(){
        return this.deviceId;
    }

    public void setDeviceId(String deviceId){
        this.deviceId = deviceId;
    }

    public String getBeginTime(){
        return this.beginTime;
    }

    public void setBeginTime(String beginTime){
        this.beginTime = beginTime;
    }

    public String getEndTime(){
        return this.endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public List<String> getMonitorId(){
        return this.monitorId;
    }

    public void setMonitorId(List<String> monitorId){
        this.monitorId = monitorId;
    }
}
