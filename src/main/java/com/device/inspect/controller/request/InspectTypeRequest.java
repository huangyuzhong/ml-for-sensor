package com.device.inspect.controller.request;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by Administrator on 2016/8/29.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InspectTypeRequest {
    private Integer id;
    private String name;
    private String lowUp;
    private String lowDown;
    private String highUp;
    private String highDown;
    private String standard;
    private Integer lowAlter;
    private boolean chosed;

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

    public String getLowUp() {
        return lowUp;
    }

    public void setLowUp(String lowUp) {
        this.lowUp = lowUp;
    }

    public String getLowDown() {
        return lowDown;
    }

    public void setLowDown(String lowDown) {
        this.lowDown = lowDown;
    }

    public String getHighUp() {
        return highUp;
    }

    public void setHighUp(String highUp) {
        this.highUp = highUp;
    }

    public String getHighDown() {
        return highDown;
    }

    public void setHighDown(String highDown) {
        this.highDown = highDown;
    }

    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public Integer getLowAlter() {
        return lowAlter;
    }

    public void setLowAlter(Integer lowAlter) {
        this.lowAlter = lowAlter;
    }

    public boolean isChosed() {
        return chosed;
    }

    public void setChosed(boolean chosed) {
        this.chosed = chosed;
    }
}
