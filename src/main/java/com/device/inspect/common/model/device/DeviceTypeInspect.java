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
}
