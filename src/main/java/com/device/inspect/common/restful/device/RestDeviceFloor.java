package com.device.inspect.common.restful.device;

import com.device.inspect.common.model.device.Device;
import com.device.inspect.common.model.device.DeviceFloor;
import com.device.inspect.common.restful.charater.RestUser;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;

/**
 * Created by Administrator on 2016/7/20.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestDeviceFloor {
    private Integer id;
    private Integer floorNum;
    private String name;
    private Integer num;
    private String mobile;
    private String email;
    private Integer productNum;
    private RestUser scientist;

    public RestDeviceFloor(@NotNull DeviceFloor deviceFloor) {
        this.id = deviceFloor.getId();
        this.floorNum = deviceFloor.getFloorNum();
        this.name = deviceFloor.getName();
        this.num = deviceFloor.getNum();
        this.productNum = deviceFloor.getProductNum();
        this.scientist = null==deviceFloor.getScientist()?null:new RestUser(deviceFloor.getScientist());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFloorNum() {
        return floorNum;
    }

    public void setFloorNum(Integer floorNum) {
        this.floorNum = floorNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getProductNum() {
        return productNum;
    }

    public void setProductNum(Integer productNum) {
        this.productNum = productNum;
    }

    public RestUser getScientist() {
        return scientist;
    }

    public void setScientist(RestUser scientist) {
        this.scientist = scientist;
    }
}
