package com.device.inspect.common.restful.firm;

import com.device.inspect.common.model.firm.Building;
import com.device.inspect.common.model.firm.Company;
import com.device.inspect.common.util.time.MyCalendar;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by Administrator on 2016/7/10.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestBuilding {
    private Integer id;
    private String name;
    private Float xpoint;
    private Float ypoint;
    private Integer deviceNum;
    private Date createDate;
    private Integer lowAlert;
    private Integer highAlert;
    private Integer online;
    private Integer offline;
    private Integer total;
    private Float score;
    private Integer enable;
    private Integer days;
    private String background;

    public RestBuilding(@NotNull Building building) {
        this.id = building.getId();
        this.name = building.getName();
        this.xpoint = building.getXpoint();
        this.ypoint = building.getYpoint();
        this.deviceNum = building.getDeviceNum();
        this.createDate = building.getCreateDate();
        this.lowAlert = building.getLowAlert();
        this.highAlert = building.getHighAlert();
        this.online = building.getOnline();
        this.offline = building.getOffline();
        this.total = building.getTotal();
        this.score = building.getScore();
        this.enable = building.getEnable();
        this.background = building.getBackground();
        if (null!=building.getCreateDate())
            days = MyCalendar.getDateSpace(building.getCreateDate(),new Date());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getXpoint() {
        return xpoint;
    }

    public void setXpoint(Float xpoint) {
        this.xpoint = xpoint;
    }

    public Float getYpoint() {
        return ypoint;
    }

    public void setYpoint(Float ypoint) {
        this.ypoint = ypoint;
    }

    public Integer getDeviceNum() {
        return deviceNum;
    }

    public void setDeviceNum(Integer deviceNum) {
        this.deviceNum = deviceNum;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Integer getLowAlert() {
        return lowAlert;
    }

    public void setLowAlert(Integer lowAlert) {
        this.lowAlert = lowAlert;
    }

    public Integer getHighAlert() {
        return highAlert;
    }

    public void setHighAlert(Integer highAlert) {
        this.highAlert = highAlert;
    }

    public Integer getOnline() {
        return online;
    }

    public void setOnline(Integer online) {
        this.online = online;
    }

    public Integer getOffline() {
        return offline;
    }

    public void setOffline(Integer offline) {
        this.offline = offline;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public String getBackground() {return background;}

    public void setBackground(String background) {this.background = background;}
}
