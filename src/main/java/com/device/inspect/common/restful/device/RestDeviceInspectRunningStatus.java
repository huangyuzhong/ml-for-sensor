package com.device.inspect.common.restful.device;

import com.device.inspect.common.model.device.DeviceInspectRunningStatus;
import com.device.inspect.common.model.device.DeviceRunningStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;

/**
 * Created by zyclincoln on 3/15/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestDeviceInspectRunningStatus {
    private Integer id;
    private Integer deviceInspectId;
    private DeviceRunningStatus deviceRunningStatus;
    private Float threshold;

    public RestDeviceInspectRunningStatus(@NotNull DeviceInspectRunningStatus status){
        this.id = status.getId();
        this.deviceInspectId = status.getDeviceInspect().getId();
        this.deviceRunningStatus = status.getDeviceRunningStatus();
        this.threshold = status.getThreshold();
    }

    public Integer getId(){
        return this.id;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public Integer getDeviceInspectId(){
        return this.deviceInspectId;
    }

    public void setDeviceInspectId(Integer id){
        this.deviceInspectId = id;
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
