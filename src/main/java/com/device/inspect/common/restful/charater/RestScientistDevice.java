package com.device.inspect.common.restful.charater;

import com.device.inspect.common.model.device.ScientistDevice;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;

/**
 * Created by FGZ on 2017/6/22.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestScientistDevice {

    private Integer id;
    private RestUser scientist;

    public RestScientistDevice(@NotNull ScientistDevice scientistDevice){
        this.id = scientistDevice.getId();
        this.scientist = null == scientistDevice.getScientist()?null:new RestUser(scientistDevice.getScientist());
    }



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RestUser getScientist() {
        return scientist;
    }

    public void setScientist(RestUser scientist) {
        this.scientist = scientist;
    }
}
