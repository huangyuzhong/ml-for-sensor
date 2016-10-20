package com.device.inspect.common.restful.device;

import com.device.inspect.common.model.device.Device;
import com.device.inspect.common.model.device.MonitorDevice;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;

/**
 * Created by Administrator on 2016/7/20.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestMonitorDevice {
    private Integer id;
    private String number;
    private String battery;
    private Integer online;

    public RestMonitorDevice(@NotNull MonitorDevice monitorDevice) {
        this.id = monitorDevice.getId();
        this.number = monitorDevice.getNumber();
        this.battery =  monitorDevice.getBattery();
        this.online = monitorDevice.getOnline();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getBattery() {
        return battery;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }

    public Integer getOnline() {
        return online;
    }

    public void setOnline(Integer online) {
        this.online = online;
    }
}
