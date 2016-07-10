package com.device.inspect.common.restful.firm;

import com.device.inspect.common.model.firm.Building;
import com.device.inspect.common.model.firm.Company;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by Administrator on 2016/7/10.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestBuilding {
    private Integer id;
    private String name;
    private Float xpoint;
    private Float ypoint;
    private Integer deviceNum;
    private Date createDate;


    public RestBuilding(@NotNull Building building) {
        this.id = building.getId();
        this.name = building.getName();
        this.xpoint = building.getXpoint();
        this.ypoint = building.getYpoint();
        this.deviceNum = building.getDeviceNum();
        this.createDate = building.getCreateDate();
    }

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

    public Float getXpoint() {
        return xpoint;
    }

    public void setXpoint(Float xpoint) {
        this.xpoint = xpoint;
    }

    public Float getYpoint() {
        return ypoint;
    }

    public void setYpoint(Float ypoint) {
        this.ypoint = ypoint;
    }

    public Integer getDeviceNum() {
        return deviceNum;
    }

    public void setDeviceNum(Integer deviceNum) {
        this.deviceNum = deviceNum;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
