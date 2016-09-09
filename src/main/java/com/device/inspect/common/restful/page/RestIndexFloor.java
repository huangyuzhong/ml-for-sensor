package com.device.inspect.common.restful.page;

import com.device.inspect.common.model.firm.Building;
import com.device.inspect.common.model.firm.Storey;
import com.device.inspect.common.restful.firm.RestFloor;
import com.device.inspect.common.util.time.MyCalendar;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/7/11.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestIndexFloor {
    private  Integer id;
    private String name;
    private Integer devcieNum;
    private Integer alertNum;
    private Integer days;
    private List<RestFloor> floors;

    public RestIndexFloor(@NotNull Building building){
        this.id = building.getId();
        this.name = building.getName();
        devcieNum = 0;
        alertNum = 0 ;
        if (null!=building.getCreateDate())
            days = MyCalendar.getDateSpace(building.getCreateDate(),new Date());
        if(null!= building.getFloorList()&&building.getFloorList().size()>0){
            floors = new ArrayList<RestFloor>();
            for (Storey floor:building.getFloorList()){
                devcieNum += floor.getDeviceNum();
                floors.add(new RestFloor(floor));
            }
        }
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
