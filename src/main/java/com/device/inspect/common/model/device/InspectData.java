package com.device.inspect.common.model.device;

import javax.persistence.*;

/**
 * Created by Administrator on 2016/7/8.
 */
@Entity
@Table(name = "inspect_data")
public class InspectData {

    private Integer id;
    private Device device;
    private DeviceInspect deviceInspect;
    private String result;

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
    @JoinColumn(name = "device_inspect_id")
    public DeviceInspect getDeviceInspect() {
        return deviceInspect;
    }

    public void setDeviceInspect(DeviceInspect deviceInspect) {
        this.deviceInspect = deviceInspect;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
