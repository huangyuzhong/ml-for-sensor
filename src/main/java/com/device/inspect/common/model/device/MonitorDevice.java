package com.device.inspect.common.model.device;

import javax.persistence.*;

/**
 * Created by Administrator on 2016/7/8.
 */
@Entity
@Table(name = "monitor_device")
public class MonitorDevice {

    private Integer id;
    private String number;
    private String battery;
    private Integer online;
    private Device device;


    @Id
    @GeneratedValue()
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

    @Column(name = "battery_status")
    public String getBattery() {
        return battery;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }

    @Column(name = "online_status")
    public Integer getOnline() {
        return online;
    }

    public void setOnline(Integer online) {
        this.online = online;
    }

    @OneToOne()
    @JoinColumn(name = "device_id")
    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }
}
