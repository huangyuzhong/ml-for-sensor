package com.device.inspect.common.restful.firm;

import com.device.inspect.common.model.firm.Storey;
import com.device.inspect.common.util.time.MyCalendar;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by Administrator on 2016/7/10.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestFloor {

    private Integer id;
    private Integer num;
    private String name;
//    private RestBuilding build;
    private Integer deviceNum;
    private Date createDate;
    private String background;
    private Float xpoint;
    private Float ypoint;

    private Integer lowAlert;
    private Integer highAlert;
    private Integer online;
    private Integer offline;
    private Integer total;
    private Float score;
    private Integer enable;
    private Integer days;

    public RestFloor(@NotNull Storey floor){
        this.id = floor.getId();
        this.num = floor.getNum();
        this.name = floor.getName();
//        this.build = null==floor.getBuild()?null:new RestBuilding(floor.getBuild());
        this.deviceNum = floor.getDeviceNum();
        this.createDate = floor.getCreateDate();
        this.background = floor.getBackground();
        this.xpoint = floor.getXpoint();
        this.ypoint = floor.getYpoint();
        this.lowAlert = floor.getLowAlert();
        this.highAlert = floor.getHighAlert();
        this.online = floor.getOnline();
        this.offline = floor.getOffline();
        this.total = floor.getTotal();
        this.score = floor.getScore();
        this.enable = floor.getEnable();
        if (null!=floor.getCreateDate())
            days = MyCalendar.getDateSpace(floor.getCreateDate(),new Date());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public RestBuilding getBuild() {
//        return build;
//    }
//
//    public void setBuild(RestBuilding build) {
//        this.build = build;
//    }

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
}
