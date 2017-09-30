package com.device.inspect.common.restful.data;

import java.util.List;

/**
 * Created by gxu on 9/29/17.
 */
public class DailyMonitoringTS {
    int inspectTypeId;
    int deviceTypeId;
    int deviceId;
    String measurement;
    String unit;
    String inspectType;
    String deviceType;
    String deviceModel;
    List<List<Object>> dailyAverage;
    Double overalMax;
    Double overalMin;
    Double overalAverage;

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

    public void setUnit(String unit){ this.unit = unit;}

    public String getUnit() {return this.unit;}

    public void setDeviceModel(String model){
        this.deviceModel = model;
    }

    public String getDeviceModel(){
        return this.deviceModel;
    }

    public void setDailyAverage(List<List<Object>> counts){
        this.dailyAverage = counts;
    }

    public List<List<Object>> getDailyAverage(){
        return this.dailyAverage;
    }

    public void setOveralMax(Double max){
        this.overalMax = max;
    }

    public Double getOveralMax(){ return this.overalMax;}

    public void setOveralMin(Double min){
        this.overalMin = min;
    }
    public Double getOveralMin(){ return this.overalMin;}

    public void setOveralAverage(Double avg){
        this.overalAverage = avg;
    }

    public Double getOveralAverage(){return this.overalAverage;}
}
