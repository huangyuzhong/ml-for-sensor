package com.device.inspect.common.model.device;

import javax.persistence.*;
import java.util.Date;

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
    private Date createDate;
    private String type;

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

    @Column(name = "create_date")
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
