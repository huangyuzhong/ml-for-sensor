package com.device.inspect.common.restful.device;

import com.device.inspect.common.model.device.DeviceType;
import com.device.inspect.common.model.device.DeviceTypeInspect;
import com.device.inspect.controller.request.DeviceTypeRequest;
import com.device.inspect.controller.request.InspectTypeRequest;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/12.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestDeviceType {

    private Integer id;
    private String name;
    private String logo;
    private List<RestInspectType> inspectTypes;

    public RestDeviceType(@NotNull DeviceType deviceType) {
        this.id = deviceType.getId();
        this.name = deviceType.getName();
        this.logo = deviceType.getLogo();
        if (null!=deviceType.getDeviceTypeInspectList()){
            inspectTypes = new ArrayList<RestInspectType>();
            for (DeviceTypeInspect deviceTypeInspect:deviceType.getDeviceTypeInspectList()){
                if (null!=deviceTypeInspect.getInspectType())
                    inspectTypes.add(new RestInspectType(deviceTypeInspect.getInspectType()));
            }
        }
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

    public List<RestInspectType> getInspectTypes() {
        return inspectTypes;
    }

    public void setInspectTypes(List<RestInspectType> inspectTypes) {
        this.inspectTypes = inspectTypes;
    }

}
