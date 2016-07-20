package com.device.inspect.common.restful.device;

import com.device.inspect.common.model.charater.User;
import com.device.inspect.common.model.device.*;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private User manager;
    private Integer alterNum;
    private String maintain;
    private Date maintainDate;
    private Integer maintainAlterDays;
    private RestMonitorDevice monitorDevice;
    private List<RestDeviceFloor> deviceFloors;
    private List<RestDeviceInspect> deviceInspects;
    private List<RestFile> files;

    public RestDevice(@NotNull Device device) {
        this.id = device.getId();
        this.code = device.getCode();
        this.deviceType = null==device.getDeviceType()?null:new RestDeviceType(device.getDeviceType());
        this.createDate = device.getCreateDate();
        this.creator = device.getCreator();
        this.purchase = device.getPurchase();
        this.manager = device.getManager();
        this.photo = device.getPhoto();
        this.alterNum = device.getAlterNum();
        this.maintain = device.getMaintain();
        this.maintainDate = device.getMaintainDate();
        this.maintainAlterDays = device.getMaintainAlterDays();
        this.monitorDevice = null==device.getMonitorDevice()?null:new RestMonitorDevice(device.getMonitorDevice());
        if (null!=device.getDeviceFloorList()&&device.getDeviceFloorList().size()>0){
            this.deviceFloors = new ArrayList<RestDeviceFloor>();
            for (DeviceFloor deviceFloor : device.getDeviceFloorList())
                deviceFloors.add(new RestDeviceFloor(deviceFloor));
        }

        if (null!=device.getDeviceInspectList()&&device.getDeviceInspectList().size()>0){
            this.deviceInspects = new ArrayList<RestDeviceInspect>();
            for (DeviceInspect deviceInspect : device.getDeviceInspectList())
                deviceInspects.add(new RestDeviceInspect(deviceInspect));
        }

        if (null!=device.getDeviceFileList()&&device.getDeviceFileList().size()>0){
            this.files = new ArrayList<RestFile>();
            for (DeviceFile deviceFile:device.getDeviceFileList()){
                if (null!=deviceFile.getFile())
                    files.add(new RestFile(deviceFile.getFile()));
            }
        }
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

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }

    public RestMonitorDevice getMonitorDevice() {
        return monitorDevice;
    }

    public void setMonitorDevice(RestMonitorDevice monitorDevice) {
        this.monitorDevice = monitorDevice;
    }

    public List<RestDeviceFloor> getDeviceFloors() {
        return deviceFloors;
    }

    public void setDeviceFloors(List<RestDeviceFloor> deviceFloors) {
        this.deviceFloors = deviceFloors;
    }

    public List<RestDeviceInspect> getDeviceInspects() {
        return deviceInspects;
    }

    public void setDeviceInspects(List<RestDeviceInspect> deviceInspects) {
        this.deviceInspects = deviceInspects;
    }

    public List<RestFile> getFiles() {
        return files;
    }

    public void setFiles(List<RestFile> files) {
        this.files = files;
    }
}
