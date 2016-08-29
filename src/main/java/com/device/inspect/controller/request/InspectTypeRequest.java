package com.device.inspect.controller.request;

/**
 * Created by Administrator on 2016/8/29.
 */
public class InspectTypeRequest {
    private Integer id;
    private String name;
    private String lowUp;
    private String lowDown;
    private String highUp;
    private String highDown;

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
}
