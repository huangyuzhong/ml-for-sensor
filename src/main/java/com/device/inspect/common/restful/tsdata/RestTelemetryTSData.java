package com.device.inspect.common.restful.tsdata;

import com.device.inspect.common.restful.data.TelemetryData;

import java.util.List;

/**
 * Created by gxu on 5/30/17.
 */
public class RestTelemetryTSData {
    private String name;
    private String code;
    private String unit;
    private Integer deviceInspectId;
    private List<Long> timeSeries;
    private List<Float> valueSeries;

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getCode() { return this.code; }

    public void setCode(String code){ this.code = code; }

    public String getUnit() {return this.unit; }

    public void setUnit(String unit) { this.unit = unit; }

    public Integer getDeviceInspectId(){
        return this.deviceInspectId;
    }

    public void setDeviceInspectId(Integer deviceInspectId){
        this.deviceInspectId = deviceInspectId;
    }

    public List<Long> getTimeSeries(){
        return this.timeSeries;
    }

    public void setTimeSeries(List<Long> timeSeries){
        this.timeSeries = timeSeries;
    }

    public List<Float> getValueSeries(){ return this.valueSeries; }

    public void setValueSeries(List<Float> valueSeries){
        this.valueSeries = valueSeries;
    }


}
