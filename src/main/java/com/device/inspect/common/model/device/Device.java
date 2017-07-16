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
    private String serialNo;
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
    private Integer status;
    private List<ScientistDevice> scientistDeviceList;
    private DeviceVersion deviceVersion;
    private Date lastActivityTime;
    private Date lastYellowAlertTime;
    private Date lastRedAlertTime;
    private Integer enableSharing;
    private String deviceChainKey;
    private String rentClause;
    private Double rentPrice;

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

    @Column(name = "serial_no")
    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
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

    @Column(name = "alert_status")
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @OneToMany(mappedBy = "device")
    public List<ScientistDevice> getScientistDeviceList() {
        return scientistDeviceList;
    }

    public void setScientistDeviceList(List<ScientistDevice> scientistDeviceList) {
        this.scientistDeviceList = scientistDeviceList;
    }

    @ManyToOne
    @JoinColumn(name = "device_version_id")
    public DeviceVersion getDeviceVersion() {
        return deviceVersion;
    }

    public void setDeviceVersion(DeviceVersion deviceVersion) {
        this.deviceVersion = deviceVersion;
    }

    @Column(name = "last_activity_time")
    public Date getLastActivityTime(){
        return this.lastActivityTime;
    }
    public void setLastActivityTime(Date lastActivityTime){
        this.lastActivityTime = lastActivityTime;
    }

    @Column(name = "last_yellow_alert_time")
    public Date getLastYellowAlertTime(){
        return this.lastYellowAlertTime;
    }

    public void setLastYellowAlertTime(Date lastYellowAlertTime){
        this.lastYellowAlertTime = lastYellowAlertTime;
    }

    @Column(name = "last_red_alert_time")
    public Date getLastRedAlertTime(){
        return this.lastRedAlertTime;
    }

    public void setLastRedAlertTime(Date lastRedAlertTime){
        this.lastRedAlertTime = lastRedAlertTime;
    }

    @Column(name = "enable_sharing  ")
    public Integer getEnableSharing() {
        return enableSharing;
    }

    public void setEnableSharing(int enableSharing) {
        this.enableSharing = enableSharing;
    }

    @Column(name = "device_chain_key")
    public String getDeviceChainKey(){
        return this.deviceChainKey;
    }

    public void setDeviceChainKey(String deviceChainKey){
        this.deviceChainKey = deviceChainKey;
    }

    @Column(name = "rent_clause")
    public String getRentClause(){
        return this.rentClause;
    }

    public void setRentClause(String rentClause){
        this.rentClause = rentClause;
    }

    @Column(name = "rent_price")
    public Double getRentPrice(){
        return this.rentPrice;
    }

    public void setRentPrice(Double rentPrice){
        this.rentPrice = rentPrice;
    }
}
