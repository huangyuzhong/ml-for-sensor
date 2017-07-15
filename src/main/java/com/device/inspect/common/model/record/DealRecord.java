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

    private Integer id;
    private Device device;
    private User lessor;
    private User lessee;
    private Double price;
    private Date beginTime;
    private Date endTime;
    private String deviceSerialNumber;
    private String aggrement;
    private Integer status;

    @Id
    @GeneratedValue()
    public void setId(Integer id){
        this.id = id;
    }

    public Integer getId(){
        return this.id;
    }

    @ManyToOne
    @JoinColumn(name = "device_id")
    public Device getDevice(){
        return this.device;
    }

    public void setDevice(Device device){
        this.device = device;
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    public User getLessor(){
        return this.lessor;
    }

    public void setLessor(User lessor){
        this.lessor = lessor;
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    public User getLessee(){
        return this.lessee;

    }

    public void setLessee(User lessee){
        this.lessee = lessee;
    }

    @Column(name = "price")
    public Double getPrice(){
        return this.price;
    }

    public void setPrice(Double price){
        this.price = price;
    }

    @Column(name = "begin_time")
    public Date getBeginTime(){
        return this.beginTime;
    }

    public void setBeginTime(Date beginTime){
        this.beginTime = beginTime;
    }

    @Column(name = "end_time")
    public Date getEndTime(){
        return this.endTime;
    }

    public void setEndTime(Date endTime){
        this.endTime = endTime;
    }

    @Column(name = "device_serial_number")
    public String getDeviceSerialNumber(){
        return this.deviceSerialNumber;
    }

    public void setDeviceSerialNumber(String deviceSerialNumber){
        this.deviceSerialNumber = deviceSerialNumber;
    }

    @Column(name = "aggrement")
    public String getAggrement(){
        return this.aggrement;
    }

    public void setAggrement(String aggrement){
        this.aggrement = aggrement;
    }

    @Column(name = "status")
    public Integer getStatus(){
        return this.status;
    }

    public void setStatus(Integer status){
        this.status = status;
    }
}