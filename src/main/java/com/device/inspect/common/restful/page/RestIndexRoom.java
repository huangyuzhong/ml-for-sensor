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
    private Integer deviceNum;
    private Integer alertNum;
    private Integer days;
    private List<RestRoom> roomList;
    private Integer floorId;
    private String buildName;
    private Integer buildId;

    public RestIndexRoom(@NotNull Storey floor) {
        this.id = floor.getId();
        this.name = floor.getName();
        this.buildName = floor.getBuild().getName();
        this.buildId = floor.getBuild().getId();
        this.floorId = floor.getId();
        deviceNum = 0;
        alertNum = 0;
        if (null!=floor.getCreateDate())
            days = MyCalendar.getDateSpace(floor.getCreateDate(), new Date());
        if (null!=floor.getRoomList()&&floor.getRoomList().size()>0){
            roomList = new ArrayList<RestRoom>();
            for (Room room : floor.getRoomList()){
                deviceNum += room.getDeviceNum();
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

    public Integer getAlertNum() {

        return alertNum;
    }

    public void setAlertNum(Integer alertNum) {
        this.alertNum = alertNum;
    }

    public Integer getDeviceNum() {

        return deviceNum;
    }

    public void setDeviceNum(Integer deviceNum) {
        this.deviceNum = deviceNum;
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
}
