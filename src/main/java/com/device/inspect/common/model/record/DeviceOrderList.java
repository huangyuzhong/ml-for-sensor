package com.device.inspect.common.model.record;

import com.device.inspect.common.model.device.Device;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by zyclincoln on 8/6/17.
 */
@Entity
@Table(name = "device_order_list")
public class DeviceOrderList {
    @Id
    @GeneratedValue()
    private Integer id;

    @NotNull
    @Column(name = "create_time")
    private Date createTime;

    @NotNull
    @Column(name = "monitor_serial_no")
    private String monitorSerialNo;

    @NotNull
    @Column(name = "order_desc")
    private String orderDesc;

    @Column(name = "execute_status")
    private Integer executeStatus;

    public DeviceOrderList(){

    }

    public DeviceOrderList(Date createTime, String monitorSerialNo, String orderDesc, Integer executeStatus){
        this.createTime = createTime;
        this.monitorSerialNo = monitorSerialNo;
        this.orderDesc = orderDesc;
        this.executeStatus = executeStatus;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public Integer getId(){
        return this.id;
    }

    public void setCreateTime(Date createTime){
        this.createTime = createTime;
    }

    public Date getCreateTime(){
        return this.createTime;
    }

    public void setMonitorSerialNo(String monitorSerialNo){
        this.monitorSerialNo = monitorSerialNo;
    }

    public String getMonitorSerialNo(){
        return this.monitorSerialNo;
    }

    public void setOrderDesc(String orderDesc){
        this.orderDesc = orderDesc;
    }

    public String getOrderDesc(){
        return this.orderDesc;
    }

    public void setExecuteStatus(Integer executeStatus){
        this.executeStatus = executeStatus;
    }

    public Integer getExecuteStatus(){
        return this.executeStatus;
    }

}
