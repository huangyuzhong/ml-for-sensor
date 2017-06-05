package com.device.inspect.common.cache;

import java.util.Date;

/**
 * Created by zyclincoln on 6/5/17.
 */
public class MemoryDevice {
    private int deviceId;
    private Date lastActivityTime = null;
    private Date lastAlertTime = null;
    private int lastAlertType = 0;

    public MemoryDevice(int deviceId){
        this.deviceId = deviceId;
    }

    public void updateNewestLastActivityTime(Date lastActivityTime){
        if(this.lastActivityTime == null || lastActivityTime.getTime() > this.lastActivityTime.getTime()){
            this.lastActivityTime = lastActivityTime;
        }
    }

    public void updateNewestLastAlertTimeAndType(Date lastAlertTime, int lastAlertType){
        if(this.lastAlertTime == null || lastAlertTime.getTime() > this.lastAlertTime.getTime()){
            this.lastAlertTime = lastAlertTime;
            this.lastAlertType = lastAlertType;
        }
    }

    public Date getLastActivityTime(){
        return this.lastActivityTime;
    }

    public Date getLastAlertTime(){
        return this.lastAlertTime;
    }

    public int getDeviceId(){
        return this.deviceId;
    }

    public int getLastAlertType(){
        return this.lastAlertType;
    }

}
