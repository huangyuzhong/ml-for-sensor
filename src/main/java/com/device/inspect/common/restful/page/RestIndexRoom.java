package com.device.inspect.common.restful.page;

import com.device.inspect.common.model.firm.Floor;
import com.device.inspect.common.model.firm.Room;
import com.device.inspect.common.restful.firm.RestRoom;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/12.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestIndexRoom {
    private Integer id;
    private Integer deviceNum;
    private Integer alertNum;
    private List<RestRoom> roomList;

    public RestIndexRoom(@NotNull Floor floor) {
        this.id = floor.getId();
        deviceNum = 0;
        alertNum = 0;
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
}
