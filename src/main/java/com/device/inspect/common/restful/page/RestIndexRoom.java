package com.device.inspect.common.restful.page;

import com.device.inspect.common.model.firm.Room;
import com.device.inspect.common.model.firm.Storey;
import com.device.inspect.common.restful.firm.RestRoom;
import com.device.inspect.common.util.time.MyCalendar;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/7/12.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestIndexRoom {
    private Integer id;
    private String name;
    private Integer days;
    private List<RestRoom> roomList;
    private Integer floorId;
    private String buildName;
    private Integer buildId;
    private Integer lowAlert;
    private Integer highAlert;
    private Integer online;
    private Integer offline;
    private Integer total;
    private Float score;
    private String background;

    public RestIndexRoom(@NotNull Storey floor) {
        this.id = floor.getId();
        this.name = floor.getName();
        this.buildName = floor.getBuild().getName();
        this.buildId = floor.getBuild().getId();
        this.floorId = floor.getId();
        this.lowAlert = floor.getLowAlert();
        this.highAlert = floor.getHighAlert();
        this.online = floor.getOnline();
        this.offline = floor.getOffline();
        this.total = floor.getTotal();
        this.score = floor.getScore();
        this.background = floor.getBackground();
        if (null!=floor.getCreateDate())
            days = MyCalendar.getDateSpace(floor.getCreateDate(), new Date());
        if (null!=floor.getRoomList()&&floor.getRoomList().size()>0){
            roomList = new ArrayList<RestRoom>();
            for (Room room : floor.getRoomList()){
                roomList.add(new RestRoom(room));
            }
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<RestRoom> getRoomList() {
        return roomList;
    }

    public void setRoomList(List<RestRoom> roomList) {
        this.roomList = roomList;
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

    public Integer getFloorId() {
        return floorId;
    }

    public void setFloorId(Integer floorId) {
        this.floorId = floorId;
    }

    public String getBuildName() {
        return buildName;
    }

    public void setBuildName(String buildName) {
        this.buildName = buildName;
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
