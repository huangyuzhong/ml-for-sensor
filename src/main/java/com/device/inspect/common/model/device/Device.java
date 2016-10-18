package com.device.inspect.common.model.device;

import com.device.inspect.common.model.charater.User;
import com.device.inspect.common.model.firm.Building;
import com.device.inspect.common.model.firm.Room;
import com.device.inspect.common.model.firm.Storey;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/7/8.
 */
@Entity
@Table(name = "device")
public class Device {

    private Integer id;
    private String code;
    private String name;
    private DeviceType deviceType;
    private Date createDate;
    private String creator;
    private Date purchase;
    private String photo;
    private User manager;
    private Integer alterNum;
    private String maintain;
    private Date maintainDate;
    private Integer maintainAlterDays;
    private Room room;
    private String model;
    private MonitorDevice monitorDevice;
    private Float xPoint;
    private Float yPoint;
    private List<DeviceFloor> deviceFloorList;
    private List<DeviceFile> deviceFileList;
    private List<DeviceInspect> deviceInspectList;
    private String pushType;
    private Integer pushInterval;
    private Storey floor;
    private Building building;
    private String score;
    private Integer enable;

    @Id
    @GeneratedValue()
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

    @ManyToOne()
    @JoinColumn(name = "type_id")
    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    @Column(name = "create_date")
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

    @Column(name = "purchase_date")
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

    @ManyToOne()
    @JoinColumn(name = "manager_user_id")
    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }

    @Column(name = "alert_num")
    public Integer getAlterNum() {
        return alterNum;
    }

    public void setAlterNum(Integer alterNum) {
        this.alterNum = alterNum;
    }

    @Column(name = "maintain_rule")
    public String getMaintain() {
        return maintain;
    }

    public void setMaintain(String maintain) {
        this.maintain = maintain;
    }

    @Column(name = "maintain_date")
    public Date getMaintainDate() {
        return maintainDate;
    }

    public void setMaintainDate(Date maintainDate) {
        this.maintainDate = maintainDate;
    }

    @Column(name = "maintain_alert_days")
    public Integer getMaintainAlterDays() {
        return maintainAlterDays;
    }

    public void setMaintainAlterDays(Integer maintainAlterDays) {
        this.maintainAlterDays = maintainAlterDays;
    }

    @ManyToOne()
    @JoinColumn(name = "room_id")
    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    @OneToOne(mappedBy = "device")
    public MonitorDevice getMonitorDevice() {
        return monitorDevice;
    }

    public void setMonitorDevice(MonitorDevice monitorDevice) {
        this.monitorDevice = monitorDevice;
    }

    @OneToMany(mappedBy = "device")
    public List<DeviceFloor> getDeviceFloorList() {
        return deviceFloorList;
    }

    public void setDeviceFloorList(List<DeviceFloor> deviceFloorList) {
        this.deviceFloorList = deviceFloorList;
    }

    @OneToMany(mappedBy = "device")
    public List<DeviceFile> getDeviceFileList() {
        return deviceFileList;
    }

    public void setDeviceFileList(List<DeviceFile> deviceFileList) {
        this.deviceFileList = deviceFileList;
    }

    @OneToMany(mappedBy = "device")
    public List<DeviceInspect> getDeviceInspectList() {
        return deviceInspectList;
    }

    public void setDeviceInspectList(List<DeviceInspect> deviceInspectList) {
        this.deviceInspectList = deviceInspectList;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Column(name = "x_location")
    public Float getxPoint() {
        return xPoint;
    }

    public void setxPoint(Float xPoint) {
        this.xPoint = xPoint;
    }

    @Column(name = "y_location")
    public Float getyPoint() {
        return yPoint;
    }

    public void setyPoint(Float yPoint) {
        this.yPoint = yPoint;
    }

    @Column(name = "push_type")
    public String getPushType() {
        return pushType;
    }

    public void setPushType(String pushType) {
        this.pushType = pushType;
    }

    @Column(name = "push_interval")
    public Integer getPushInterval() {
        return pushInterval;
    }

    public void setPushInterval(Integer pushInterval) {
        this.pushInterval = pushInterval;
    }

    @ManyToOne
    @JoinColumn(name = "floor_id")
    public Storey getFloor() {
        return floor;
    }

    public void setFloor(Storey floor) {
        this.floor = floor;
    }

    @ManyToOne
    @JoinColumn(name = "build_id")
    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    @Column(name = "health_score")
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
}
