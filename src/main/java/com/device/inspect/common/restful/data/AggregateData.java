package com.device.inspect.common.restful.data;

import org.springframework.security.access.method.P;

/**
 * Created by zyclincoln on 5/12/17.
 */
public class AggregateData {
    private String monitorId;
    private String yellowAlertCount;
    private String redAlertCount;
    private String yellowAlertTotalTime;
    private String redAlertTotalTime;
    private String maxValue;
    private String maxValueTime;
    private String minValue;
    private String minValueTime;
    private String avgValue;

    public String getMonitorId(){
        return this.monitorId;
    }

    public void setMonitorId(String monitorId){
        this.monitorId = monitorId;
    }

    public String getYellowAlertCount(){
        return this.yellowAlertCount;
    }

    public void setYellowAlertCount(String yellowAlertCount){
        this.yellowAlertCount = yellowAlertCount;
    }

    public String getRedAlertCount(){
        return this.redAlertCount;
    }

    public void setRedAlertCount(String redAlertCount){
        this.redAlertCount = redAlertCount;
    }

    public String getYellowAlertTotalTime(){
        return this.yellowAlertTotalTime;
    }

    public void setYellowAlertTotalTime(String yellowAlertTotalTime){
        this.yellowAlertTotalTime = yellowAlertTotalTime;
    }

    public String getRedAlertTotalTime(){
        return this.redAlertTotalTime;
    }

    public void setRedAlertTotalTime(String redAlertTotalTime){
        this.redAlertTotalTime = redAlertTotalTime;
    }

    public String getMaxValue(){
        return this.maxValue;
    }

    public void setMaxValue(String maxValue){
        this.maxValue = maxValue;
    }

    public String getMaxValueTime(){
        return this.maxValueTime;
    }

    public void setMaxValueTime(String maxValueTime){
        this.maxValueTime = maxValueTime;
    }

    public String getMinValue(){
        return this.minValue;
    }

    public void setMinValue(String minValue){
        this.minValue = minValue;
    }

    public String getMinValueTime(){
        return this.minValueTime;
    }

    public void setMinValueTime(String minValueTime){
        this.minValueTime = minValueTime;
    }

    public String getAvgValue(){
        return this.avgValue;
    }

    public void setAvgValue(String avgValue){
        this.avgValue = avgValue;
    }
}
