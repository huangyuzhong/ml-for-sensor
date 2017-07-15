package com.device.inspect.common.model.record;

import com.device.inspect.common.model.device.Device;

import javax.persistence.*;

/**
 * Created by zyclincoln on 7/15/17.
 */

@Entity
@Table(name = "device_disable_time")
public class DeviceDisableTime {
    private Integer id;
    private Device device;
    private String strategyType;
    private String content;

    @Id
    @GeneratedValue()
    public Integer getId(){
        return this.id;
    }

    public void setId(Integer id){
        this.id = id;
    }

    @ManyToOne()
    @JoinColumn(name = "device_id")
    public Device getDevice(){
        return this.device;
    }

    public void setDevice(Device device){
        this.device = device;
    }

    @Column(name = "strategy_type")
    public String getStrategyType(){
        return this.strategyType;
    }

    public void setStrategyType(String strategyType){
        this.strategyType = strategyType;
    }

    @Column(name = "content")
    public String getContent(){
        return this.content;
    }

    public void setContent(String content){
        this.content = content;
    }
}

