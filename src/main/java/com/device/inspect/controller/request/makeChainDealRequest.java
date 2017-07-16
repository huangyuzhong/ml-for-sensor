package com.device.inspect.controller.request;

/**
 * Created by zyclincoln on 7/16/17.
 */
public class makeChainDealRequest {
    private Integer lesseeId;
    private Integer deviceId;
    private Long beginTime;
    private Long endTime;

    public void setLesseeId(Integer lesseeId){
        this.lesseeId = lesseeId;
    }

    public Integer getLesseeId(){
        return this.lesseeId;
    }

    public void setDeviceId(Integer deviceId){
        this.deviceId = deviceId;
    }

    public Integer getDeviceId(){
        return this.deviceId;
    }

    public void setBeginTime(Long beginTime){
        this.beginTime = beginTime;
    }

    public Long getBeginTime(){
        return this.beginTime;
    }

    public void setEndTime(Long endTime){
        this.endTime = endTime;
    }

    public Long getEndTime(){
        return this.endTime;
    }
}
