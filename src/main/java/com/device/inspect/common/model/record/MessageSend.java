package com.device.inspect.common.model.record;

import com.device.inspect.common.model.charater.User;
import com.device.inspect.common.model.device.Device;
import com.device.inspect.common.model.device.DeviceInspect;
import com.device.inspect.common.model.device.DeviceTypeInspect;
import com.device.inspect.common.model.device.InspectType;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Administrator on 2016/10/18.
 */
@Entity
@Table(name = "message_send")
public class MessageSend {
    private Integer id;
    private String reason;
    private String type;        // email , mobile
    private User user;
    private String error;
    private Device device;
    private Date create;
    private Integer enable;         //1成功,0失败
    private DeviceInspect deviceInspect;

    @Id
    @GeneratedValue()
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "send_reason")
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
    @Column(name = "send_type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    @ManyToOne()
    @JoinColumn(name = "user_id")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column(name = "error_reason")
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @ManyToOne()
    @JoinColumn(name = "device_id")
    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    @Column(name = "create_date")
    public Date getCreate() {
        return create;
    }

    public void setCreate(Date create) {
        this.create = create;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    @ManyToOne()
    @JoinColumn(name = "device_inspect_id")
    public DeviceInspect getDeviceInspect(){
        return this.deviceInspect;
    }

    public void setDeviceInspect(DeviceInspect deviceInspect){
        this.deviceInspect = deviceInspect;
    }
}
