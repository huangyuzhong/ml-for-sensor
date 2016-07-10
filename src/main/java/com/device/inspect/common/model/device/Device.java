package com.device.inspect.common.model.device;

import com.device.inspect.common.model.charater.User;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Administrator on 2016/7/8.
 */
@Entity
@Table(name = "device")
public class Device {

    private Integer id;
    private Integer number;
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

    @Id
    @GeneratedValue()
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
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

    @Column(name = "manager_user_id")
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
}
