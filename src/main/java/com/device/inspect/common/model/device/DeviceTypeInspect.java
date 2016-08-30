package com.device.inspect.common.model.device;

import javax.persistence.*;

/**
 * Created by Administrator on 2016/7/8.
 */
@Entity
@Table(name = "device_type_inspect")
public class DeviceTypeInspect {

    private Integer id;
    private DeviceType deviceType;
    private InspectType inspectType;
    private Float standard;
    private Float lowUp;
    private Float lowDown;
    private Float highUp;
    private Float highDown;
    private Integer lowAlter;

    @Id
    @GeneratedValue()
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne()
    @JoinColumn(name = "device_type_id")
    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
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
}
