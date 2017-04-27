package com.device.inspect.common.model.record;

import com.device.inspect.common.model.device.Device;

import java.util.Date;

/**
 * Created by zyclincoln on 4/27/17.
 */
public class OfflineHourUnit {
    private Date beginTime;
    private Date endTime;
    private Device device;

    public OfflineHourUnit(Date beginTime, Date endTime, Device device){
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.device = device;
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

    public Device getDevice(){
        return this.device;
    }

    public void setDevice(Device device){
        this.device = device;
    }
}
