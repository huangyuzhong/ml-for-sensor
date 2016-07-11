package com.device.inspect.common.restful.page;

import com.device.inspect.common.restful.firm.RestFloor;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Created by Administrator on 2016/7/11.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestIndexFloor {

    private Integer devcieNum;
    private Integer alertNum;
    private List<RestFloor> floors;

    public RestIndexFloor(List<RestFloor> floors){
        super();
        this
    }

    public Integer getDevcieNum() {
        return devcieNum;
    }

    public void setDevcieNum(Integer devcieNum) {
        this.devcieNum = devcieNum;
    }

    public Integer getAlertNum() {
        return alertNum;
    }

    public void setAlertNum(Integer alertNum) {
        this.alertNum = alertNum;
    }

    public List<RestFloor> getFloors() {
        return floors;
    }

    public void setFloors(List<RestFloor> floors) {
        this.floors = floors;
    }
}
