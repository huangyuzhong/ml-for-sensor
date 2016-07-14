package com.device.inspect.common.restful.device;

import com.device.inspect.common.model.charater.User;
import com.device.inspect.common.model.device.Device;
import com.device.inspect.common.model.device.DeviceType;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by Administrator on 2016/7/12.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestDevice {
    private Integer id;
    private String code;
    private String name;
    private RestDeviceType deviceType;
    private Date createDate;
    private String creator;
    private Date purchase;
    private String photo;
//    private User manager;
    private Integer alterNum;
    private String maintain;
    private Date maintainDate;
    private Integer maintainAlterDays;

    public RestDevice(@NotNull Device device) {
        this.id = device.getId();
        this.code = device.getCode();
        this.deviceType = null==device.getDeviceType()?null:new RestDeviceType(device.getDeviceType());
        this.createDate = device.getCreateDate();
        this.creator = device.getCreator();
        this.purchase = device.getPurchase();
        this.photo = device.getPhoto();
        this.alterNum = device.getAlterNum();
        this.maintain = device.getMaintain();
        this.maintainDate = device.getMaintainDate();
        this.maintainAlterDays = device.getMaintainAlterDays();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RestDeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(RestDeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getPurchase() {
        return purchase;
    }

    public void setPurchase(Date purchase) {
        this.purchase = purchase;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Integer getAlterNum() {
        return alterNum;
    }

    public void setAlterNum(Integer alterNum) {
        this.alterNum = alterNum;
    }

    public String getMaintain() {
        return maintain;
    }

    public void setMaintain(String maintain) {
        this.maintain = maintain;
    }

    public Date getMaintainDate() {
        return maintainDate;
    }

    public void setMaintainDate(Date maintainDate) {
        this.maintainDate = maintainDate;
    }

    public Integer getMaintainAlterDays() {
        return maintainAlterDays;
    }

    public void setMaintainAlterDays(Integer maintainAlterDays) {
        this.maintainAlterDays = maintainAlterDays;
    }
}
