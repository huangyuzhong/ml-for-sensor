package com.device.inspect.common.restful.data;

import java.util.List;

/**
 * Created by zyclincoln on 5/13/17.
 */
public class TelemetryData {
    private String name;
    private Integer deviceInspectId;
    private List<Float> tsData;
    private AggregateData aggregateData;

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public Integer getDeviceInspectId(){
        return this.deviceInspectId;
    }

    public void setDeviceInspectId(Integer deviceInspectId){
        this.deviceInspectId = deviceInspectId;
    }

    public List<Float> getTsData(){
        return this.tsData;
    }

    public void setTsData(List<Float> tsData){
        this.tsData = tsData;
    }

    public AggregateData getAggregateData(){
        return this.aggregateData;
    }

    public void setAggregateData(AggregateData aggregateData){
        this.aggregateData = aggregateData;
    }
}
