package com.device.inspect.common.restful.firm;

import com.device.inspect.common.model.firm.Room;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by Administrator on 2016/7/12.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestRoom {

    private Integer id;
    private String name;
//    private Floor floor;
    private Float xPoint;
    private Float yPoint;
    private Integer deviceNum;
    private Date createDate;
    private String background;

    private Integer lowAlert;
    private Integer highAlert;
    private Integer online;
    private Integer offline;
    private Integer total;
    private Float score;
    private Integer enable;

    public RestRoom(@NotNull Room room){
        this.id = room.getId();
        this.name = room.getName();
        this.xPoint = room.getxPoint();
        this.yPoint = room.getyPoint();
        this.deviceNum = room.getDeviceNum();
        this.createDate = room.getCreateDate();
        this.background = room.getBackground();
        this.lowAlert = room.getLowAlert();
        this.highAlert = room.getHighAlert();
        this.online = room.getOnline();
        this.offline = room.getOffline();
        this.total = room.getTotal();
        this.score = room.getScore();
        this.enable = room.getEnable();
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

    public Float getxPoint() {
        return xPoint;
    }

    public void setxPoint(Float xPoint) {
        this.xPoint = xPoint;
    }

    public Float getyPoint() {
        return yPoint;
    }

    public void setyPoint(Float yPoint) {
        this.yPoint = yPoint;
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

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
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
}
