package com.device.inspect.common.model.device;

import com.device.inspect.common.model.charater.User;

import javax.persistence.*;

/**
 * Created by Administrator on 2016/10/28.
 */
@Entity
@Table(name = "scientist_device")
public class ScientistDevice {
    private Integer id;
    private User scientist;
    private Device device;

    @Id
    @GeneratedValue()
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne()
    @JoinColumn(name = "user_id")
    public User getScientist() {
        return scientist;
    }

    public void setScientist(User scientist) {
        this.scientist = scientist;
    }

    @ManyToOne()
    @JoinColumn(name = "device_id")
    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }
}
