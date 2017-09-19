package com.device.inspect.common.restful.device;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by fgz on 2017/9/19.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestDeviceIdAndName {
    private Integer deviceId;
    private String name;

    public RestDeviceIdAndName() {
    }

    public RestDeviceIdAndName(Integer deviceId, String name) {
        this.deviceId = deviceId;
        this.name = name;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
