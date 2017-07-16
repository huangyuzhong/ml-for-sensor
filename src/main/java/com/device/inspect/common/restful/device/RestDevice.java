package com.device.inspect.common.restful.device;

import com.device.inspect.common.model.charater.User;
import com.device.inspect.common.model.device.*;
import com.device.inspect.common.restful.charater.RestScientistDevice;
import com.device.inspect.common.restful.charater.RestUser;
import com.device.inspect.common.util.time.MyCalendar;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.jws.soap.SOAPBinding;
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
    private String serialNo;
    private Date purchase;
    private String photo;
    private RestUser manager;
    private Integer alterNum;
    private String maintain;
    private Date maintainDate;
    private Integer maintainAlterDays;
    private String model;
    private Float xPoint;
    private Float yPoint;
    private RestMonitorDevice monitorDevice;
    private List<RestDeviceFloor> deviceFloors;
    private List<RestDeviceInspect> deviceInspects;
    private List<RestFile> files;
    private String pushType;
    private Integer pushInterval;
    private String roomName;
    private Integer roomId;
    private String roomBackground;
    private String score;
    private Integer enable;
    private Integer days;
    private RestDeviceVersion deviceVersion;
    private Long yellowAlertCountToday = new Long(0);
    private Long redAlertCountToday = new Long(0);
    private List<RestScientistDevice> scientists;  //DATE 2017/6/22; CREATOR @FGZ
    private Integer enableSharing;
    private String deviceChainKey;
    private String rentClause;
    private Double rentPrice;

    public RestDevice(@NotNull Device device) {
        this.id = device.getId();
        this.code = device.getCode();
        this.name = device.getName();
        this.deviceType = null==device.getDeviceType()?null:new RestDeviceType(device.getDeviceType());
        this.createDate = device.getCreateDate();
        this.serialNo = device.getSerialNo();
        this.purchase = device.getPurchase();
        this.manager = null==device.getManager()?null:new RestUser(device.getManager());
        this.photo = device.getPhoto();
        this.alterNum = device.getAlterNum();
        this.maintain = device.getMaintain();
        this.maintainDate = device.getMaintainDate();
        this.maintainAlterDays = device.getMaintainAlterDays();
        this.monitorDevice = null==device.getMonitorDevice()?null:new RestMonitorDevice(device.getMonitorDevice());
        this.model = device.getModel();
        this.xPoint = device.getxPoint();
        this.yPoint = device.getyPoint();
        this.pushType = device.getPushType();
        this.pushInterval = device.getPushInterval();
        this.roomName = device.getRoom().getFloor().getBuild().getName() + device.getRoom().getFloor().getName()+
                device.getRoom().getName();
        this.roomId = device.getRoom().getId();
        this.roomBackground = device.getRoom().getBackground();
        this.score = device.getScore();
        this.enable = device.getEnable();
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
        if (null!=device.getCreateDate())
            days = MyCalendar.getDateSpace(device.getCreateDate(),new Date());

        this.deviceVersion = null==device.getDeviceVersion()?null:new RestDeviceVersion(device.getDeviceVersion());

     // DATE 2017/6/22; CREATOR @FGZ; START
        if (null!=device.getScientistDeviceList() && device.getScientistDeviceList().size()>0){
            this.scientists = new ArrayList<RestScientistDevice>();
            for (ScientistDevice scientistDevice : device.getScientistDeviceList()){
                if (null != scientistDevice)
                    this.scientists.add(new RestScientistDevice(scientistDevice));
            }
        }
     // DATE 2017/6/22; CREATOR @FGZ; END
        if(null != device.getEnableSharing()){
            this.enableSharing = device.getEnableSharing();
        }
        if(null != device.getDeviceChainKey()){
            this.deviceChainKey = device.getDeviceChainKey();
        }
        if(null != device.getRentPrice()){
            this.rentPrice = device.getRentPrice();
        }
        if(null != device.getRentClause()){
            this.rentClause = device.getRentClause();
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

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
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

    public RestUser getManager() {
        return manager;
    }

    public void setManager(RestUser manager) {
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

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Float getxPoint() {
        return xPoint;
    }

    public void setxPoint(Float xPoint) {
        this.xPoint = xPoint;
    }

    public Float getyPoint() {
        return yPoint;
    }

    public void setyPoint(Float yPoint) {
        this.yPoint = yPoint;
    }

    public String getPushType() {
        return pushType;
    }

    public void setPushType(String pushType) {
        this.pushType = pushType;
    }

    public Integer getPushInterval() {
        return pushInterval;
    }

    public void setPushInterval(Integer pushInterval) {
        this.pushInterval = pushInterval;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public Integer getRoomId() { return roomId; }
    public void setRoomId(Integer roomId) { this.roomId = roomId; }

    public String getRoomBackground() { return this.roomBackground; }
    public void setRoomBackground(String roomBackground) {this.roomBackground = roomBackground; }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
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

    public RestDeviceVersion getDeviceVersion() {
        return deviceVersion;
    }

    public void setDeviceVersion(RestDeviceVersion deviceVersion) {
        this.deviceVersion = deviceVersion;
    }

    public void setYellowAlertCountToday(Long alertCount) { this.yellowAlertCountToday = alertCount; }

    public Long getYellowAlertCountToday() {return this.yellowAlertCountToday;}

    public void setRedAlertCountToday(Long alertCount) { this.redAlertCountToday = alertCount; }

    public Long getRedAlertCountToday() {return this.redAlertCountToday; }
// DATE 2017/6/22; CREATOR @FGZ; START
    public List<RestScientistDevice> getScientists() {
        return scientists;
    }

    public void setScientists(List<RestScientistDevice> scientists) {
        this.scientists = scientists;
    }
// DATE 2017/6/22; CREATOR @FGZ; END

    public Integer getEnableSharing() {return this.enableSharing;}
    public void setEnableSharing(Integer enableSharing) {this.enableSharing = enableSharing;}

    public Double getRentPrice() {return this.rentPrice; }
    public void setRentPrice(Double rentPrice) { this.rentPrice = rentPrice; }

    public String getDeviceChainKey() {return this.deviceChainKey; }
    public void setDeviceChainKey(String deviceChainKey) { this.deviceChainKey = deviceChainKey;}

    public String getRentClause() {return this.rentClause;}
    public void setRentClause(String rentClause) { this.rentClause = rentClause;}

}
