package com.device.inspect.common.restful.device;

import com.device.inspect.common.model.device.Device;
import com.device.inspect.common.model.device.DeviceInspect;
import com.device.inspect.common.model.device.DeviceInspectRunningStatus;
import com.device.inspect.common.model.device.InspectType;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
import java.util.List;

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
    private String start;
    private String value;
    private String end;
    private Float zero;
    private Float originalValue;
    private Float correctionValue;
    private List<RestDeviceInspectRunningStatus> runningStatus;

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
        if (null!=deviceInspect.getDevice()&&null!=deviceInspect.getHighDown()){
            this.value = String.valueOf(deviceInspect.getHighUp() - deviceInspect.getHighDown());
            this.start = String.valueOf(deviceInspect.getStandard() - 3*(deviceInspect.getHighUp() - deviceInspect.getHighDown()));
            this.end = String.valueOf(deviceInspect.getStandard() + 3*(deviceInspect.getHighUp() - deviceInspect.getHighDown()));
        }else {
            this.start = "0";
            this.end = "1";
            this.value = "1";
        }
        this.zero=deviceInspect.getZero();
        this.originalValue=deviceInspect.getOriginalValue();
        this.correctionValue=deviceInspect.getCorrectionValue();
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

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public Float getZero() {
        return zero;
    }

    public void setZero(Float zero) {
        this.zero = zero;
    }

    public Float getOriginalValue() {
        return originalValue;
    }

    public void setOriginalValue(Float originalValue) {
        this.originalValue = originalValue;
    }

    public Float getCorrectionValue() {
        return correctionValue;
    }

    public void setCorrectionValue(Float correctionValue) {
        this.correctionValue = correctionValue;
    }

    public List<RestDeviceInspectRunningStatus> getRunningStatus(){
        return runningStatus;
    }

    public void setRunningStatus(List<RestDeviceInspectRunningStatus> statuses){
        this.runningStatus = statuses;
    }
}
