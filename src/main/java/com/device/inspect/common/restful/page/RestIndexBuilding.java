package com.device.inspect.common.restful.page;

import com.device.inspect.common.model.charater.User;
import com.device.inspect.common.model.firm.Building;
import com.device.inspect.common.model.firm.Company;
import com.device.inspect.common.restful.firm.RestBuilding;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/7/10.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestIndexBuilding {

    private Integer id;
    private String background;
    private Integer deviceNum;
    private Integer alterNum;
    private List<RestBuilding> list;


    public RestIndexBuilding(@NotNull Company company){
        this.id = company.getId();
        this.background = company.getBackground();
        if (null!=company.getBuildings()&&company.getBuildings().size()>0){
            this.deviceNum = 0 ;
            this.alterNum = 0;
            list = new ArrayList<RestBuilding>();
            for (Building building:company.getBuildings()) {
                list.add(new RestBuilding(building));
                deviceNum+=building.getDeviceNum();
            }
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public Integer getDeviceNum() {
        return deviceNum;
    }

    public void setDeviceNum(Integer deviceNum) {
        this.deviceNum = deviceNum;
    }

    public Integer getAlterNum() {
        return alterNum;
    }

    public void setAlterNum(Integer alterNum) {
        this.alterNum = alterNum;
    }

    public List<RestBuilding> getList() {
        return list;
    }

    public void setList(List<RestBuilding> list) {
        this.list = list;
    }
}
