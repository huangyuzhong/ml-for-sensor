package com.device.inspect.common.restful.device;

import com.device.inspect.common.model.device.DeviceType;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;

/**
 * Created by Administrator on 2016/7/12.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestDeviceType {

    private Integer id;
    private String name;
    private String logo;

    public RestDeviceType(@NotNull DeviceType deviceType) {
        this.id = deviceType.getId();
        this.name = deviceType.getName();
        this.logo = deviceType.getLogo();
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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}
