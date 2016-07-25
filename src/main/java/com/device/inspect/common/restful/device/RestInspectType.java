package com.device.inspect.common.restful.device;

import com.device.inspect.common.model.device.InspectType;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;

/**
 * Created by Administrator on 2016/7/20.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestInspectType {
    private Integer id;
    private String name;
    private String code;

    public RestInspectType() {
    }

    public RestInspectType(@NotNull InspectType inspectType) {
        this.id = inspectType.getId();
        this.name = inspectType.getName();
        this.code = inspectType.getCode();
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
