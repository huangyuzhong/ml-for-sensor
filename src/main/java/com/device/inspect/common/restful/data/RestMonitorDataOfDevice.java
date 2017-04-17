package com.device.inspect.common.restful.data;

import java.util.List;

/**
 * Created by zyclincoln on 4/17/17.
 */
public class RestMonitorDataOfDevice {
    private String deviceName;
    private String deviceId;
    private String deviceLocation;
    private String deviceManager;
    private String startTime;
    private String endTime;
    private String deviceLogo;
    private List<String> name;
    private List<List<String>> data;

    public String getDeviceName(){
        return this.deviceName;
    }

    public void setDeviceName(String deviceName){
        this.deviceName = deviceName;
    }

    public String getDeviceId(){
        return this.deviceId;
    }

    public void setDeviceId(String deviceId){
        this.deviceId = deviceId;
    }

    public String getDeviceLocation(){
        return this.deviceLocation;
    }

    public void setDeviceLocation(String deviceLocation){
        this.deviceLocation = deviceLocation;
    }

    public String getDeviceManager(){
        return this.deviceManager;
    }

    public void setDeviceManager(String deviceManager){
        this.deviceManager = deviceManager;
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

    public String getDeviceLogo(){
        return this.deviceLogo;
    }

    public void setDeviceLogo(String deviceLogo){
        this.deviceLogo = deviceLogo;
    }

    public List<String> getName(){
        return this.name;
    }

    public void setName(List<String> name){
        this.name = name;
    }

    public List<List<String>> getData(){
        return this.data;
    }

    public void setData(List<List<String>> data){
        this.data = data;
    }
}
