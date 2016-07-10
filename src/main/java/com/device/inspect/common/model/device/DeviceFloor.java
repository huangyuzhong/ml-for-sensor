package com.device.inspect.common.model.device;

import com.device.inspect.common.model.charater.User;

import javax.persistence.*;

/**
 * Created by Administrator on 2016/7/8.
 */
@Entity
@Table(name = "device_floor")
public class DeviceFloor {
    private Integer id;
    private Device device;
    private Integer floorNum;
    private User scientist;
    private String name;
    private Integer num;

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

    @Column(name = "floor_num")
    public Integer getFloorNum() {
        return floorNum;
    }

    public void setFloorNum(Integer floorNum) {
        this.floorNum = floorNum;
    }

    @ManyToOne()
    @JoinColumn(name = "user_id")
    public User getScientist() {
        return scientist;
    }

    public void setScientist(User scientist) {
        this.scientist = scientist;
    }
    @Column(name = "subject_name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "subject_num")
    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
}
