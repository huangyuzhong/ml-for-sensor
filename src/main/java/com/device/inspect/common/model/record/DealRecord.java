package com.device.inspect.common.model.record;

import com.device.inspect.common.model.charater.User;
import com.device.inspect.common.model.device.Device;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zyclincoln on 7/15/17.
 */
@Entity
@Table(name = "deal_record")
public class DealRecord {
    @Id
    @GeneratedValue()
    private Integer id;

    @ManyToOne(targetEntity = Device.class)
    @JoinColumn(name = "device_id")
    private Device device;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id", insertable=false, updatable=false)
    private User lessor;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id", insertable=false, updatable=false)
    private User lessee;

    @Column(name = "price")
    private Double price;

    @Column(name = "begin_time")
    private Date beginTime;

    @Column(name = "end_time")
    private Date endTime;

    @Column(name = "device_serial_number")
    private String deviceSerialNumber;

    @Column(name = "aggrement")
    private String aggrement;

    @Column(name = "status")
    private Integer status;

    public void setId(Integer id){
        this.id = id;
    }

    public Integer getId(){
        return this.id;
    }

    public Device getDevice(){
        return this.device;
    }

    public void setDevice(Device device){
        this.device = device;
    }

    public User getLessor(){
        return this.lessor;
    }

    public void setLessor(User lessor){
        this.lessor = lessor;
    }

    public User getLessee(){
        return this.lessee;

    }

    public void setLessee(User lessee){
        this.lessee = lessee;
    }

    public Double getPrice(){
        return this.price;
    }

    public void setPrice(Double price){
        this.price = price;
    }

    public Date getBeginTime(){
        return this.beginTime;
    }

    public void setBeginTime(Date beginTime){
        this.beginTime = beginTime;
    }

    public Date getEndTime(){
        return this.endTime;
    }

    public void setEndTime(Date endTime){
        this.endTime = endTime;
    }

    public String getDeviceSerialNumber(){
        return this.deviceSerialNumber;
    }

    public void setDeviceSerialNumber(String deviceSerialNumber){
        this.deviceSerialNumber = deviceSerialNumber;
    }

    public String getAggrement(){
        return this.aggrement;
    }

    public void setAggrement(String aggrement){
        this.aggrement = aggrement;
    }

    public Integer getStatus(){
        return this.status;
    }

    public void setStatus(Integer status){
        this.status = status;
    }
}
