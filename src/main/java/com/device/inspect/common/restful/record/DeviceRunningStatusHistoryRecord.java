package com.device.inspect.common.restful.record;


/**
 * Created by zyclincoln on 7/17/17.
 */
public class DeviceRunningStatusHistoryRecord {
    private Integer id;
    private Integer deviceId;
    private Long changeTime;
    private Integer changeToStatus;

    public DeviceRunningStatusHistoryRecord(){

    }

    public DeviceRunningStatusHistoryRecord(Integer id, Integer deviceId, Long changeTime, Integer changeToStatus){
        this.id = id; this.deviceId = deviceId; this.changeTime = changeTime; this.changeToStatus = changeToStatus;
    }

    public Integer getId(){
        return this.id;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public Integer getDeviceId(){
        return this.deviceId;
    }

    public void setDeviceId(Integer deviceId){
        this.deviceId = deviceId;
    }

    public Long getChangeTime(){
        return this.changeTime;
    }

    public void setChangeTime(Long changeTime){
        this.changeTime = changeTime;
    }

    public Integer getChangeToStatus(){
        return this.changeToStatus;
    }

    public void setChangeToStatus(Integer changeToStatus){
        this.changeToStatus = changeToStatus;
    }
}
