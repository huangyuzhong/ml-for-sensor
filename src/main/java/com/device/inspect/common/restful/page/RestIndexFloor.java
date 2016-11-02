package com.device.inspect.common.restful.page;

import com.device.inspect.common.model.firm.Building;
import com.device.inspect.common.model.firm.Storey;
import com.device.inspect.common.restful.firm.RestFloor;
import com.device.inspect.common.util.time.MyCalendar;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/7/11.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestIndexFloor {
    private  Integer id;
    private String name;
//    private Integer devcieNum;
    private Integer alertNum;
    private Integer days;
    private List<RestFloor> floors;
    private Integer buildId;
    private Integer lowAlert;
    private Integer highAlert;
    private Integer online;
    private Integer offline;
    private Integer total;
    private Float score;
    private String background;

    public RestIndexFloor(@NotNull Building building){
        this.id = building.getId();
        this.name = building.getName();
        this.buildId = building.getId();
        this.lowAlert = building.getLowAlert();
        this.highAlert = building.getHighAlert();
        this.online = building.getOnline();
        this.offline = building.getOffline();
        this.total = building.getTotal();
        this.score = building.getScore();
        this.background = building.getBackground();

        alertNum = 0 ;
        if (null!=building.getCreateDate())
            days = MyCalendar.getDateSpace(building.getCreateDate(),new Date());
        if(null!= building.getFloorList()&&building.getFloorList().size()>0){
            floors = new ArrayList<RestFloor>();
            for (Storey floor:building.getFloorList()){
                floors.add(new RestFloor(floor));
            }
        }
    }

    public Integer getAlertNum() {
        return alertNum;
    }

    public void setAlertNum(Integer alertNum) {
        this.alertNum = alertNum;
    }

    public List<RestFloor> getFloors() {
        return floors;
    }

    public void setFloors(List<RestFloor> floors) {
        this.floors = floors;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getBuildId() {
        return buildId;
    }

    public void setBuildId(Integer buildId) {
        this.buildId = buildId;
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

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }
}
