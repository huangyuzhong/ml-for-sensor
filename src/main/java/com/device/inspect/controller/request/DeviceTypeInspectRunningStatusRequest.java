package com.device.inspect.controller.request;

import com.device.inspect.common.model.device.DeviceTypeInspectRunningStatus;

/**
 * Created by zyclincoln on 3/16/17.
 */
public class DeviceTypeInspectRunningStatusRequest {
    private Integer id;
    private Integer runningStatusId;
    private Integer deviceTypeInspectId;
    private Float threshold;

    public DeviceTypeInspectRunningStatusRequest(){

    }

    public DeviceTypeInspectRunningStatusRequest(DeviceTypeInspectRunningStatus status){
        this.id = status.getId();
        this.runningStatusId = status.getDeviceRunningStatus().getId();
        this.deviceTypeInspectId = status.getDeviceTypeInspect().getId();
        this.threshold = status.getThreshold();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRunningStatusId() {
        return runningStatusId;
    }

    public void setRunningStatusId(Integer runningStatusId) {
        this.runningStatusId = runningStatusId;
    }

    public Integer getDeviceTypeInspectId() {
        return deviceTypeInspectId;
    }

    public void setDeviceTypeInspectId(Integer deviceTypeInspectId) {
        this.deviceTypeInspectId = deviceTypeInspectId;
    }

    public Float getThreshold() {
        return threshold;
    }

    public void setThreshold(Float threshold) {
        this.threshold = threshold;
    }
}