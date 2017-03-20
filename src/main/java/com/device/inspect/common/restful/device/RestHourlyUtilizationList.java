package com.device.inspect.common.restful.device;

import com.device.inspect.config.schedule.HourlyUtilityCalculation;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Created by zyclincoln on 3/20/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestHourlyUtilizationList {
    private Integer deviceId;
    private List<RestHourlyUtilization> hourlyUtilizations;

    public RestHourlyUtilizationList(Integer deviceId, List<RestHourlyUtilization> hourlyUtilizations){
        this.hourlyUtilizations = hourlyUtilizations;
        this.deviceId = deviceId;
    }

    public Integer getDeviceId(){
        return this.deviceId;
    }

    public void setDeviceId(Integer deviceId){
        this.deviceId = deviceId;
    }

    public List<RestHourlyUtilization> getHourlyUtilizations(){
        return hourlyUtilizations;
    }

    public void setHourlyUtilizations(List<RestHourlyUtilization> restHourlyUtilizations){
        this.hourlyUtilizations = restHourlyUtilizations;
    }
}
