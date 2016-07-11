package com.device.inspect.common.restful.firm;

import com.device.inspect.common.model.firm.Building;
import com.device.inspect.common.model.firm.Floor;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by Administrator on 2016/7/10.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestFloor {

    private Integer id;
    private Integer num;
    private String name;
//    private RestBuilding build;
    private Integer deviceNum;
    private Date createDate;

    public RestFloor(@NotNull Floor floor){
        this.id = floor.getId();
        this.num = floor.getNum();
        this.name = floor.getName();
//        this.build = null==floor.getBuild()?null:new RestBuilding(floor.getBuild());
        this.deviceNum = floor.getDeviceNum();
        this.createDate = floor.getCreateDate();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public RestBuilding getBuild() {
//        return build;
//    }
//
//    public void setBuild(RestBuilding build) {
//        this.build = build;
//    }

    public Integer getDeviceNum() {
        return deviceNum;
    }

    public void setDeviceNum(Integer deviceNum) {
        this.deviceNum = deviceNum;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
