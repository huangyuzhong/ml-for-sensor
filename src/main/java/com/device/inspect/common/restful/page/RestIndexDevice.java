package com.device.inspect.common.restful.page;

import com.device.inspect.common.model.charater.User;
import com.device.inspect.common.model.device.Device;
import com.device.inspect.common.model.firm.Room;
import com.device.inspect.common.restful.device.RestDevice;
import com.device.inspect.common.util.time.MyCalendar;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/7/18.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestIndexDevice {
    private Integer id;
    private String name;
    private Integer days;
    private List<RestDevice> deviceList;
    private Integer roomId;
    private Integer floorId;
    private String floorName;
    private Integer buildId;
    private String buildName;
    private Integer lowAlert;
    private Integer highAlert;
    private Integer online;
    private Integer offline;
    private Integer total;
    private Float score;
    private String background;

    public  RestIndexDevice(@NotNull Room room){
        this.id = room.getId();
        this.name = room.getName();
        this.roomId = room.getId();
        this.floorId = room.getFloor().getId();
        this.floorName = room.getFloor().getName();
        this.buildId = room.getFloor().getBuild().getId();
        this.buildName = room.getFloor().getBuild().getName();
//        this.alterNum = 0;
        this.lowAlert = room.getLowAlert();
        this.highAlert = room.getHighAlert();
        this.online = room.getOnline();
        this.offline = room.getOffline();
        this.total = room.getTotal();
        this.score = room.getScore();
        this.background = room.getBackground();
        if (null!=room.getCreateDate())
            days = MyCalendar.getDateSpace(room.getCreateDate(), new Date());
        if (null!=room.getDeviceList()&&room.getDeviceList().size()>0){
            deviceList = new ArrayList<RestDevice>();
            for (Device device : room.getDeviceList()){
                deviceList.add(new RestDevice(device));
            }
        }

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<RestDevice> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<RestDevice> deviceList) {
        this.deviceList = deviceList;
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

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }


    public Integer getFloorId() {
        return floorId;
    }

    public void setFloorId(Integer floorId) {
        this.floorId = floorId;
    }

    public String getFloorName() {
        return floorName;
    }

    public void setFloorName(String floorName) {
        this.floorName = floorName;
    }

    public Integer getBuildId() {
        return buildId;
    }

    public void setBuildId(Integer buildId) {
        this.buildId = buildId;
    }

    public String getBuildName() {
        return buildName;
    }

    public void setBuildName(String buildName) {
        this.buildName = buildName;
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
