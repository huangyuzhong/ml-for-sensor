package com.device.inspect.common.restful.device;

import com.device.inspect.common.model.device.DeviceRunningStatus;
import com.device.inspect.common.model.device.DeviceTypeInspectRunningStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;

/**
 * Created by zyclincoln on 3/15/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestDeviceTypeInspectRunningStatus {
    private Integer id;
    private Integer deviceTypeInspectId;
    private DeviceRunningStatus deviceRunningStatus;
    private Float threshold;
    public RestDeviceTypeInspectRunningStatus(@NotNull DeviceTypeInspectRunningStatus status){
        this.id = status.getId();
        this.deviceTypeInspectId = status.getDeviceTypeInspect().getId();
        this.deviceRunningStatus = status.getDeviceRunningStatus();
        this.threshold = status.getThreshold();
    }

    public Integer getId(){
        return this.id;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public Integer getDeviceTypeInspectId(){
        return this.deviceTypeInspectId;
    }

    public void setDeviceTypeInspectId(Integer id){
        this.deviceTypeInspectId = id;
    }

    public DeviceRunningStatus getDeviceRunningStatus(){
        return this.deviceRunningStatus;
    }

    public void setDeviceRunningStatus(DeviceRunningStatus status){
        this.deviceRunningStatus = status;
    }

    public Float getThreshold(){
        return this.threshold;
    }

    public void setThreshold(float threshold){
        this.threshold = threshold;
    }
}
