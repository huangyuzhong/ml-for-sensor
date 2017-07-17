package com.device.inspect.common.model.record;

import com.device.inspect.common.model.device.Device;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by zyclincoln on 7/17/17.
 */
@Entity
@Table(name = "device_running_status_history")
public class DeviceRunningStatusHistory {
    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne(targetEntity = Device.class)
    @JoinColumn(name = "device_id")
    @NotNull
    private Device device;

    @NotNull
    @Column(name = "changeTime")
    private Date changeTime;

    @NotNull
    @Column(name = "changeToStatus")
    private Integer changeToStatus;

    public void setId(Integer id){
        this.id = id;
    }

    public Integer getId(){
        return this.id;
    }

    public void setDevice(Device device){
        this.device = device;
    }

    public Device getDevice(){
        return this.device;
    }

    public void setChangeTime(Date changeTime){
        this.changeTime = changeTime;
    }

    public Date getChangeTime(){
        return this.changeTime;
    }

    public void setChangeToStatus(Integer status){
        this.changeToStatus = status;
    }

    public Integer getChangeToStatus(){
        return this.changeToStatus;
    }
}
