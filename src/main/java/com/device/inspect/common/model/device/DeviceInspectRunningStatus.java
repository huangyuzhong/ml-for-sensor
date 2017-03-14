package com.device.inspect.common.model.device;

/**
 * Created by zyclincoln on 3/14/17.
 */
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "device_inspect_running_status")
public class DeviceInspectRunningStatus {
    private Integer id;
    private DeviceInspect deviceInspect;
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
    @JoinColumn(name = "device_inspect_id")
    public DeviceInspect getDeviceInspect(){
        return this.deviceInspect;
    }
    public void setDeviceInspect(DeviceInspect inspect){
        this.deviceInspect = inspect;
    }

    @ManyToOne
    @NotNull
    @JoinColumn(name = "device_running_status_id")
    public DeviceRunningStatus getDeviceRunningStatus(){
        return this.deviceRunningStatus;
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
