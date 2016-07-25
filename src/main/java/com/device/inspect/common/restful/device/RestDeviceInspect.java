package com.device.inspect.common.restful.device;

import com.device.inspect.common.model.device.Device;
import com.device.inspect.common.model.device.DeviceInspect;
import com.device.inspect.common.model.device.InspectType;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;

/**
 * Created by Administrator on 2016/7/20.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestDeviceInspect {

    private Integer id;
    private RestInspectType inspectType;
    private Float standard;
    private Float lowUp;
    private Float lowDown;
    private Float highUp;
    private Float highDown;
    private Integer lowAlter;
    private String name;

    public RestDeviceInspect() {
    }

    public RestDeviceInspect(@NotNull DeviceInspect deviceInspect) {
        this.id = deviceInspect.getId();
        this.inspectType = null==deviceInspect.getInspectType()?null:new RestInspectType(deviceInspect.getInspectType());
        this.standard = deviceInspect.getStandard();
        this.lowUp = deviceInspect.getLowUp();
        this.lowDown = deviceInspect.getLowDown();
        this.highUp = deviceInspect.getHighUp();
        this.highDown = deviceInspect.getHighDown();
        this.lowAlter = deviceInspect.getLowAlter();
        this.name = deviceInspect.getName();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RestInspectType getInspectType() {
        return inspectType;
    }

    public void setInspectType(RestInspectType inspectType) {
        this.inspectType = inspectType;
    }

    public Float getStandard() {
        return standard;
    }

    public void setStandard(Float standard) {
        this.standard = standard;
    }

    public Float getLowUp() {
        return lowUp;
    }

    public void setLowUp(Float lowUp) {
        this.lowUp = lowUp;
    }

    public Float getLowDown() {
        return lowDown;
    }

    public void setLowDown(Float lowDown) {
        this.lowDown = lowDown;
    }

    public Float getHighUp() {
        return highUp;
    }

    public void setHighUp(Float highUp) {
        this.highUp = highUp;
    }

    public Float getHighDown() {
        return highDown;
    }

    public void setHighDown(Float highDown) {
        this.highDown = highDown;
    }

    public Integer getLowAlter() {
        return lowAlter;
    }

    public void setLowAlter(Integer lowAlter) {
        this.lowAlter = lowAlter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
