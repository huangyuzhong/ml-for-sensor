package com.device.inspect.common.restful.data;

import java.util.List;

/**
 * Created by zyclincoln on 5/13/17.
 */
public class RestInspectDataArray {
    private List<Long> timeSeries;
    private List<TelemetryData> telemetries;

    public List<Long> getTimeSeries(){
        return this.timeSeries;
    }

    public void setTimeSeries(List<Long> timeSeries){
        this.timeSeries = timeSeries;
    }

    public List<TelemetryData> getTelemetries(){
        return this.telemetries;
    }

    public void setTelemetries(List<TelemetryData> telemetries){
        this.telemetries = telemetries;
    }
}
