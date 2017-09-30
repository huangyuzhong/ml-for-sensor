package com.device.inspect.common.restful.data;

import java.util.List;
/**
 * Created by gxu on 9/26/17.
 */
public class DailyAlertTS {
    int inspectTypeId;
    int deviceTypeId;
    int deviceId;
    String measurement;
    String inspectType;
    String deviceType;
    String deviceModel;
    List<List<Object>> yellowAlertCount;
    List<List<Object>> redAlertCount;

    public void setInspectTypeId(int id){
        this.inspectTypeId = id;
    }

    public int getInspectTypeId(){
        return this.inspectTypeId;
    }

    public void setInspectType(String name){
        this.inspectType = name;
    }

    public String getInspectType(){
        return this.inspectType;
    }

    public void setDeviceTypeId(int id){
        this.deviceTypeId = id;
    }

    public int getDeviceTypeId(){
        return this.deviceTypeId;
    }

    public void setDeviceId(int id){
        this.deviceId = id;
    }

    public int getDeviceId(){
        return this.deviceId;
    }

    public void setDeviceType(String name){
        this.deviceType = name;
    }

    public String getDeviceType(){
        return this.deviceType;
    }

    public void setMeasurement(String name){
        this.measurement = name;
    }

    public String getMeasurement(){
        return this.measurement;
    }

    public void setDeviceModel(String model){
        this.deviceModel = model;
    }

    public String getDeviceModel(){
        return this.deviceModel;
    }

    public void setYellowAlertCount(List<List<Object>> counts){
        this.yellowAlertCount = counts;
    }

    public List<List<Object>> getYellowAlertCount(){
        return this.yellowAlertCount;
    }

    public void setRedAlertCount(List<List<Object>> counts){
        this.redAlertCount = counts;
    }

    public List<List<Object>> getRedAlertCount(){
        return this.redAlertCount;
    }
}
