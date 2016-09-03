package com.device.inspect.controller.request;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Created by Administrator on 2016/8/29.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceTypeRequest {
    private Integer id;
    private String name;
    private Integer type;
    private List<InspectTypeRequest> list;

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public List<InspectTypeRequest> getList() {
        return list;
    }

    public void setList(List<InspectTypeRequest> list) {
        this.list = list;
    }
}
