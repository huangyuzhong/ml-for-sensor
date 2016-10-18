package com.device.inspect.common.model.device;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Administrator on 2016/10/18.
 */
@Entity
@Table(name = "device_offline")
public class DeviceOffline {
    private Integer id;
    private Device device;
    private Date offlineDate;

    @Id
    @GeneratedValue()
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToMany()
    @JoinColumn(name = "device_id")
    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    @Column(name = "offline_date")
    public Date getOfflineDate() {
        return offlineDate;
    }

    public void setOfflineDate(Date offlineDate) {
        this.offlineDate = offlineDate;
    }

}
