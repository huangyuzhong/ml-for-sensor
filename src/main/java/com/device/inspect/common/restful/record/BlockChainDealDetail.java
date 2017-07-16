package com.device.inspect.common.restful.record;

/**
 * Created by zyclincoln on 7/16/17.
 */
public class BlockChainDealDetail{
    private Integer id;
    private Integer deviceId;
    private Integer lessorId;
    private Integer lesseeId;
    private Double dealPrice;
    private Long beginTime;
    private Long endTime;
    private String deviceSerialNumber;
    private String rentClause;
    private Integer dealStatus;

    public BlockChainDealDetail(){

    }

    public BlockChainDealDetail(Integer id, Integer deviceId, Integer lessorId, Integer lesseeId, Double dealPrice,
                                Long beginTime, Long endTime, String deviceSerialNumber, String rentClause, Integer dealStatus){
        this.id = id; this.deviceId = deviceId; this.lessorId = lessorId; this.lesseeId = lesseeId; this.dealPrice = dealPrice;
        this.beginTime = beginTime; this.endTime = endTime; this.deviceSerialNumber = deviceSerialNumber;
        this.rentClause = rentClause; this.dealStatus = dealStatus;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public Integer getId(){
        return this.id;
    }

    public void setDeviceId(Integer deviceId){
        this.deviceId = deviceId;
    }

    public Integer getDeviceId(){
        return this.deviceId;
    }

    public void setLessorId(Integer lessorId){
        this.lessorId = lessorId;
    }

    public Integer getLessorId(){
        return this.lessorId;
    }

    public void setLesseeId(Integer lesseeId){
        this.lesseeId = lesseeId;
    }

    public Integer getLesseeId(){
        return this.lesseeId;
    }

    public void setDealPrice(Double dealPrice){
        this.dealPrice = dealPrice;
    }

    public Double getDealPrice(){
        return this.dealPrice;
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

    public void setDeviceSerialNumber(String deviceSerialNumber){
        this.deviceSerialNumber = deviceSerialNumber;
    }

    public String getDeviceSerialNumber(){
        return this.deviceSerialNumber;
    }

    public void setRentClause(String rentClause){
        this.rentClause = rentClause;
    }

    public String getRentClause(){
        return this.rentClause;
    }

    public void setDealStatus(Integer dealStatus){
        this.dealStatus = dealStatus;
    }

    public Integer getDealStatus(){
        return this.dealStatus;
    }
}
