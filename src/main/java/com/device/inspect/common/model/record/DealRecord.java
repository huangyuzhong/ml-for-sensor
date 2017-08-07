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

    @Column(name = "lessor_id")
    private Integer lessor;

    @Column(name = "lessee_id")
    private Integer lessee;

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

    @Column(name = "real_end_time")
    private Date realEndTime;

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

    public Integer getLessor(){
        return this.lessor;
    }

    public void setLessor(Integer lessor){
        this.lessor = lessor;
    }

    public Integer getLessee(){
        return this.lessee;

    }

    public void setLessee(Integer lessee){
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

    public Date getRealEndTime(){
        return this.realEndTime;
    }

    public void setRealEndTime(Date realEndTime){
        this.realEndTime = realEndTime;
    }
}
