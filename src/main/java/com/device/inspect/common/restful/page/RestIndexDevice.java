package com.device.inspect.common.restful.page;

import com.device.inspect.common.model.charater.User;
import com.device.inspect.common.model.device.Device;
import com.device.inspect.common.model.firm.Room;
import com.device.inspect.common.restful.device.RestDevice;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/18.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestIndexDevice {
    private Integer id;
    private Integer uid;
    private Integer alterNum;
    private List<RestDevice> deviceList;

    public  RestIndexDevice(@NotNull Room room){
        this.id = room.getId();
        alterNum = 0;
        if (null!=room.getDeviceList()&&room.getDeviceList().size()>0){
            deviceList = new ArrayList<RestDevice>();
            for (Device device : room.getDeviceList()){
                deviceList.add(new RestDevice(device));
            }
        }
    }

    public  RestIndexDevice(@NotNull User user){
        this.uid = user.getId();
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAlterNum() {
        return alterNum;
    }

    public void setAlterNum(Integer alterNum) {
        this.alterNum = alterNum;
    }

    public List<RestDevice> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<RestDevice> deviceList) {
        this.deviceList = deviceList;
    }
}
