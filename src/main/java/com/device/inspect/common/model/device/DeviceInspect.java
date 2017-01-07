package com.device.inspect.common.model.device;

import javax.persistence.*;

/**
 * Created by Administrator on 2016/7/8.
 */
@Entity
@Table(name = "device_inspect")
public class DeviceInspect {

    private Integer id;
    private Device device;
    private InspectType inspectType;
    private Float standard;
    private Float lowUp;
    private Float lowDown;
    private Float highUp;
    private Float highDown;
    private Integer lowAlter;
    private String name;
    private Float zero;
    private Float originalValue;
    private Float correctionValue;

    @Id
    @GeneratedValue()
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne()
    @JoinColumn(name = "device_id")
    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    @ManyToOne()
    @JoinColumn(name = "inspect_type_id")
    public InspectType getInspectType() {
        return inspectType;
    }

    public void setInspectType(InspectType inspectType) {
        this.inspectType = inspectType;
    }

    public Float getStandard() {
        return standard;
    }

    public void setStandard(Float standard) {
        this.standard = standard;
    }


    @Column(name = "low_up_alert")
    public Float getLowUp() {
        return lowUp;
    }

    public void setLowUp(Float lowUp) {
        this.lowUp = lowUp;
    }

    @Column(name = "low_down_alert")
    public Float getLowDown() {
        return lowDown;
    }

    public void setLowDown(Float lowDown) {
        this.lowDown = lowDown;
    }

    @Column(name = "high_up_alert")
    public Float getHighUp() {
        return highUp;
    }

    public void setHighUp(Float highUp) {
        this.highUp = highUp;
    }

    @Column(name = "high_down_alert")
    public Float getHighDown() {
        return highDown;
    }

    public void setHighDown(Float highDown) {
        this.highDown = highDown;
    }

    @Column(name = "low_alert_minutes")
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

    @Column(name = "zero")
    public Float getZero() {
        return zero;
    }

    public void setZero(Float zero) {
        this.zero = zero;
    }

    @Column(name = "original_value")
    public Float getOriginalValue() {
        return originalValue;
    }

    public void setOriginalValue(Float originalValue) {
        this.originalValue = originalValue;
    }

    @Column(name = "correction_value")
    public Float getCorrectionValue() {
        return correctionValue;
    }

    public void setCorrectionValue(Float correctionValue) {
        this.correctionValue = correctionValue;
    }
}
