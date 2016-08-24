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

    public RestRoom(@NotNull Room room){
        this.id = room.getId();
        this.name = room.getName();
        this.xPoint = room.getxPoint();
        this.yPoint = room.getyPoint();
        this.deviceNum = room.getDeviceNum();
        this.createDate = room.getCreateDate();
        this.background = room.getBackground();
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
}
