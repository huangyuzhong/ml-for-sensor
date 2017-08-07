package com.device.inspect.common.restful.record;

/**
 * Created by zyclincoln on 7/19/17.
 */
public class RestDealRecord {
    private Integer id;
    private Integer device;
    private Integer lessor;
    private Integer lessee;
    private Double price;
    private Long beginTime;
    private Long endTime;
    private String deviceSerialNumber;
    private String aggrement;
    private Integer status;
    private Long realEndTime;

    public RestDealRecord(){

    }

    public RestDealRecord(Integer id, Integer device, Integer lessor, Integer lessee, Double price, Long beginTime,
                          Long endTime, String deviceSerialNumber, String aggrement, Integer status, Long realEndTime){
        this.id = id;
        this.device = device;
        this.lessor = lessor;
        this.lessee = lessee;
        this.price = price;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.deviceSerialNumber = deviceSerialNumber;
        this.aggrement = aggrement;
        this.status = status;
        this.realEndTime = realEndTime;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public Integer getId(){
        return this.id;
    }

    public Integer getDevice(){
        return this.device;
    }

    public void setDevice(Integer device){
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

    public Long getBeginTime(){
        return this.beginTime;
    }

    public void setBeginTime(Long beginTime){
        this.beginTime = beginTime;
    }

    public Long getEndTime(){
        return this.endTime;
    }

    public void setEndTime(Long endTime){
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

    public void setRealEndTime(Long realEndTime){
        this.realEndTime = realEndTime;
    }

    public Long getRealEndTime(){
        return this.realEndTime;
    }
}
