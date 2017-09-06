package com.device.inspect.common.restful.record;

import com.device.inspect.common.model.device.Device;
import com.device.inspect.common.model.record.DeviceDisableTime;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by fgz on 2017/7/18.
 */
public class BlockChainDevice {
    private Integer id;
    private String code;
    private String name;
    private String deviceTypeName;
    private String serialNo;
    private Date purchase;
    private Date maintainDate;
    private Integer managerId;
    private Double rentPrice;
    private Long[][] disablePeriod;
    private String rentClause;
    private Long timeStamp;

    public BlockChainDevice() {
    }

    public BlockChainDevice(Device device, DeviceDisableTime deviceDisableTime) {
        this.id = device.getId();
        this.code = device.getCode();
        this.name = device.getName();
        this.deviceTypeName = device.getDeviceType().getName();
        this.serialNo = device.getSerialNo();
        this.purchase = device.getPurchase();
        this.maintainDate = device.getMaintainDate();
        this.managerId = device.getManager().getId();
        this.rentPrice = device.getRentPrice();
        this.rentClause = device.getRentClause();

        if (deviceDisableTime.getContent() != null && !("".equals(deviceDisableTime.getContent()))){
            String content = deviceDisableTime.getContent();
            String[] contents = content.split(";");

            disablePeriod = new Long[contents.length][2];
            for (int i=0; i<contents.length; i++){
                String[] startToEnd = contents[i].split(",");
                disablePeriod[i][0] = Long.parseLong(startToEnd[0]);
                disablePeriod[i][1] = Long.parseLong(startToEnd[1]);
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

    public String getDeviceTypeName() {
        return deviceTypeName;
    }

    public void setDeviceTypeName(String deviceTypeName) {
        this.deviceTypeName = deviceTypeName;
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

    public Date getMaintainDate() {
        return maintainDate;
    }

    public void setMaintainDate(Date maintainDate) {
        this.maintainDate = maintainDate;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    public Double getRentPrice() {
        return rentPrice;
    }

    public void setRentPrice(Double rentPrice) {
        this.rentPrice = rentPrice;
    }

    public Long[][] getDisablePeriod() {
        return disablePeriod;
    }

    public void setDisablePeriod(Long[][] disablePeriod) {
        this.disablePeriod = disablePeriod;
    }

    public String getRentClause() {
        return rentClause;
    }

    public void setRentClause(String rentClause) {
        this.rentClause = rentClause;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
