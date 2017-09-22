package com.device.inspect.common.restful.device;

import com.device.inspect.common.model.device.AlertCount;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;

/**
 * Created by fgz on 2017/9/21.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestAlertCount {
    private Long startTime;
    private Long endTime;
    private String deviceName;
    private Integer deviceId;
    private String deviceTypeName;
    private Integer deviceTypeId;
    private String inspectMeasurement;
    private String inspectTypeName;
    private Integer alertType;
    private String monitorNum;

    public RestAlertCount(@NotNull AlertCount alertCount){
        this.startTime = alertCount.getCreateDate()==null?null:alertCount.getCreateDate().getTime();
        this.endTime = alertCount.getFinish()==null?null:alertCount.getFinish().getTime();
        this.deviceName = alertCount.getDevice()==null?null:alertCount.getDevice().getName();
        this.deviceId = alertCount.getDevice()==null?null:alertCount.getDevice().getId();
        this.deviceTypeName = alertCount.getDevice()==null?null:alertCount.getDevice().getDeviceType().getName();
        this.deviceTypeId = alertCount.getDevice()==null?null:alertCount.getDevice().getDeviceType().getId();
        this.inspectMeasurement = alertCount.getInspectType()==null?null:alertCount.getInspectType().getMeasurement();
        this.inspectTypeName = alertCount.getInspectType() == null ? null : alertCount.getInspectType().getName();
        this.alertType = alertCount.getType();
        this.monitorNum = alertCount.getDevice() == null ? null : alertCount.getDevice().getMonitorDevice().getNumber();
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceTypeName() {
        return deviceTypeName;
    }

    public void setDeviceTypeName(String deviceTypeName) {
        this.deviceTypeName = deviceTypeName;
    }

    public Integer getDeviceTypeId() {
        return deviceTypeId;
    }

    public void setDeviceTypeId(Integer deviceTypeId) {
        this.deviceTypeId = deviceTypeId;
    }

    public String getInspectMeasurement() {
        return inspectMeasurement;
    }

    public void setInspectMeasurement(String inspectMeasurement) {
        this.inspectMeasurement = inspectMeasurement;
    }

    public String getInspectTypeName() {
        return inspectTypeName;
    }

    public void setInspectTypeName(String inspectTypeName) {
        this.inspectTypeName = inspectTypeName;
    }

    public Integer getAlertType() {
        return alertType;
    }

    public void setAlertType(Integer alertType) {
        this.alertType = alertType;
    }

    public String getMonitorNum() {
        return monitorNum;
    }

    public void setMonitorNum(String monitorNum) {
        this.monitorNum = monitorNum;
    }
}
