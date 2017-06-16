package com.device.inspect.common.model.firm;

import com.device.inspect.common.model.device.Device;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/7/8.
 */
@Entity
@Table(name = "room")
public class Room {
    private Integer id;
    private String name;
    private Storey floor;
    private Float xPoint;
    private Float yPoint;
    private Integer deviceNum;
    private Date createDate;
    private String background;
    private Device device;
    private List<Device> deviceList;
//    private Integer alterNum;
    private Integer lowAlert;
    private Integer highAlert;
    private Integer online;
    private Integer offline;
    private Integer total=0;
    private Float score;
    private Integer enable;

    @Id
    @GeneratedValue()
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne()
    @JoinColumn(name = "floor_id")
    public Storey getFloor() {
        return floor;
    }

    public void setFloor(Storey floor) {
        this.floor = floor;
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

    @Column(name = "device_num")
    public Integer getDeviceNum() {
        return deviceNum;
    }

    public void setDeviceNum(Integer deviceNum) {
        this.deviceNum = deviceNum;
    }

    @Column(name = "create_date")
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Column(name = "background_url")
    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    @OneToOne()
    @JoinColumn(name = "device_id")
    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

//    @OneToMany(mappedBy = "room")
    @Transient
    public List<Device> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<Device> deviceList) {
        this.deviceList = deviceList;
    }

    @Column(name = "low_alert_num")
    public Integer getLowAlert() {
        return lowAlert;
    }

    public void setLowAlert(Integer lowAlert) {
        this.lowAlert = lowAlert;
    }
    @Column(name = "high_alert_num")
    public Integer getHighAlert() {
        return highAlert;
    }

    public void setHighAlert(Integer highAlert) {
        this.highAlert = highAlert;
    }
    @Column(name = "online_num")
    public Integer getOnline() {
        return online;
    }

    public void setOnline(Integer online) {
        this.online = online;
    }
    @Column(name = "offline_num")
    public Integer getOffline() {
        return offline;
    }

    public void setOffline(Integer offline) {
        this.offline = offline;
    }
    @Column(name = "total_num")
    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
    @Column(name = "assets_health")
    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }
}
