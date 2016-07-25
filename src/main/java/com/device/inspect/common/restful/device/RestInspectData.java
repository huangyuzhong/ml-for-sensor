package com.device.inspect.common.restful.device;

import com.device.inspect.common.model.device.Device;
import com.device.inspect.common.model.device.DeviceInspect;
import com.device.inspect.common.model.device.InspectData;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by Administrator on 2016/7/25.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestInspectData {
    private Integer id;
//    private Device device;
    private RestDeviceInspect deviceInspect;
    private String result;
    private Date createDate;

    public RestInspectData() {
    }

    public RestInspectData(@NotNull InspectData inspectData){
        this.id = inspectData.getId();
        this.deviceInspect = null==inspectData.getDeviceInspect()?null:new RestDeviceInspect(inspectData.getDeviceInspect());
        this.result = inspectData.getResult();
        this.createDate = inspectData.getCreateDate();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RestDeviceInspect getDeviceInspect() {
        return deviceInspect;
    }

    public void setDeviceInspect(RestDeviceInspect deviceInspect) {
        this.deviceInspect = deviceInspect;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
