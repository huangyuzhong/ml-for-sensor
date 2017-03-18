package com.device.inspect.common.model.device;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by zyclincoln on 3/19/17.
 */

@Entity
@Table(name = "device_hourly_utilization",
        uniqueConstraints = { @UniqueConstraint( columnNames = { "device_id", "start_hour" } ) })
public class DeviceHourlyUtilization {
    private Integer id;
    private Device deviceId;
    private Date startHour;
    private Integer runningTime;
    private Integer idleTime;
    private Float powerLowerBound;
    private Float powerUpperBound;
    private Float consumedEnergy;

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
    @JoinColumn(name = "device_id")
    public Device getDeviceId(){
        return this.deviceId;
    }
    public void setDeviceId(Device device){
        this.deviceId = device;
    }

    @NotNull
    @Column(name = "start_hour")
    public Date getStartHour(){
        return this.startHour;
    }
    public void setStartHour(Date startHour){
        this.startHour = startHour;
    }

    @Column(name = "running_time")
    public Integer getRunningTime(){
        return this.runningTime;
    }
    public void setRunningTime(Integer runningTime){
        this.runningTime = runningTime;
    }

    @Column(name = "idle_time")
    public Integer getIdleTime(){
        return this.idleTime;
    }
    public void setIdleTime(Integer idleTime){
        this.idleTime = idleTime;
    }

    @Column(name = "power_lower_bound")
    public Float getPowerLowerBound(){
        return this.powerLowerBound;
    }
    public void setPowerLowerBound(Float powerLowerBound){
        this.powerLowerBound = powerLowerBound;
    }

    @Column(name = "power_upper_bound")
    public Float getPowerUpperBound(){
        return this.powerUpperBound;
    }
    public void setPowerUpperBound(Float powerUpperBound){
        this.powerUpperBound = powerUpperBound;
    }

    @Column(name = "consumed_energy")
    public Float getConsumedEnergy(){
        return this.consumedEnergy;
    }
    public void setConsumedEnergy(Float consumedEnergy){
        this.consumedEnergy = consumedEnergy;
    }
}
