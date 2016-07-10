package com.device.inspect.common.model.device;

import javax.persistence.*;

/**
 * Created by Administrator on 2016/7/8.
 */
@Entity
@Table(name = "monitor_device")
public class MonitorDevice {

    private Integer id;
    private Integer number;
    private String Battery;
    private String Online;

    @Id
    @GeneratedValue()
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    @Column(name = "battery_status")
    public String getBattery() {
        return Battery;
    }

    public void setBattery(String battery) {
        Battery = battery;
    }

    @Column(name = "online_status")
    public String getOnline() {
        return Online;
    }

    public void setOnline(String online) {
        Online = online;
    }
}
