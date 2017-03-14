package com.device.inspect.common.model.device;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by zyclincoln on 3/14/17.
 */
@Entity
@Table(name="device_type_inspect_running_status")
public class DeviceTypeInspectRunningStatus {
    private Integer id;
    private DeviceTypeInspect deviceTypeInspect;
    private DeviceRunningStatus deviceRunningStatus;
    private Float threshold;

    @Id
    @GeneratedValue()
    public Integer getId(){
        return this.id;
    }
    public void setId(Integer id){
        this.id = id;
    }

    @ManyToOne
    @NotNull
    @JoinColumn(name="device_type_inspect_id")
    public DeviceTypeInspect getDeviceTypeInspect(){
        return deviceTypeInspect;
    }
    public void setDeviceTypeInspect(DeviceTypeInspect inspect){
        this.deviceTypeInspect = inspect;
    }

    @ManyToOne
    @NotNull
    @JoinColumn(name = "device_running_status_id")
    public DeviceRunningStatus getDeviceRunningStatus(){
        return deviceRunningStatus;
    }
    public void setDeviceRunningStatus(DeviceRunningStatus status){
        this.deviceRunningStatus = status;
    }

    @Column(name = "threshold")
    public Float getThreshold(){
        return this.threshold;
    }
    public void setThreshold(Float threshold){
        this.threshold = threshold;
    }

}
