package com.device.inspect.common.restful.data;

import org.springframework.security.access.method.P;

/**
 * Created by zyclincoln on 5/12/17.
 */
public class AggregateData {
    private Integer yellowAlertCount;
    private Integer redAlertCount;
    private Long yellowAlertTotalTime;
    private Long redAlertTotalTime;
    private Float maxValue;
    private Long maxValueTime;
    private Float minValue;
    private Long minValueTime;
    private Float avgValue;
    private Float mktdata;


    public Integer getYellowAlertCount(){
        return this.yellowAlertCount;
    }

    public void setYellowAlertCount(Integer yellowAlertCount){
        this.yellowAlertCount = yellowAlertCount;
    }

    public Integer getRedAlertCount(){
        return this.redAlertCount;
    }

    public void setRedAlertCount(Integer redAlertCount){
        this.redAlertCount = redAlertCount;
    }

    public Long getYellowAlertTotalTime(){
        return this.yellowAlertTotalTime;
    }

    public void setYellowAlertTotalTime(Long yellowAlertTotalTime){
        this.yellowAlertTotalTime = yellowAlertTotalTime;
    }

    public Long getRedAlertTotalTime(){
        return this.redAlertTotalTime;
    }

    public void setRedAlertTotalTime(Long redAlertTotalTime){
        this.redAlertTotalTime = redAlertTotalTime;
    }

    public Float getMaxValue(){
        return this.maxValue;
    }

    public void setMaxValue(Float maxValue){
        this.maxValue = maxValue;
    }

    public Long getMaxValueTime(){
        return this.maxValueTime;
    }

    public void setMaxValueTime(Long maxValueTime){
        this.maxValueTime = maxValueTime;
    }

    public Float getMinValue(){
        return this.minValue;
    }

    public void setMinValue(Float minValue){
        this.minValue = minValue;
    }

    public Long getMinValueTime(){
        return this.minValueTime;
    }

    public void setMinValueTime(Long minValueTime){
        this.minValueTime = minValueTime;
    }

    public Float getAvgValue(){
        return this.avgValue;
    }

    public void setAvgValue(Float avgValue){
        this.avgValue = avgValue;
    }

    public Float getMktdata(){
        return this.mktdata;
    }

    public void setMktdata(Float mktdata){
        this.mktdata = mktdata;
    }
}
