package com.device.inspect.common.restful.page;

import com.device.inspect.common.model.charater.User;
import com.device.inspect.common.model.firm.Building;
import com.device.inspect.common.model.firm.Company;
import com.device.inspect.common.restful.firm.RestBuilding;
import com.device.inspect.common.util.time.MyCalendar;
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
    private String name;
    private String background;
    private Integer days;
    private List<RestBuilding> list;
    private Integer lowAlert;
    private Integer highAlert;
    private Integer online;
    private Integer offline;
    private Integer total;
    private Float score;
    private String logo;


    public RestIndexBuilding(@NotNull Company company){
        this.id = company.getId();
        this.name = company.getName();
        this.background = company.getBackground();
        this.lowAlert = company.getLowAlert();
        this.highAlert = company.getHighAlert();
        this.online = company.getOnline();
        this.offline = company.getOffline();
        this.total = company.getTotal();
        this.score = company.getScore();
        this.logo = company.getLogo();
        if (null!=company.getCreateDate())
            days = MyCalendar.getDateSpace(company.getCreateDate(),new Date());
        if (null!=company.getBuildings()&&company.getBuildings().size()>0){
            list = new ArrayList<RestBuilding>();
            for (Building building:company.getBuildings()) {
                list.add(new RestBuilding(building));
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

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public List<RestBuilding> getList() {
        return list;
    }

    public void setList(List<RestBuilding> list) {
        this.list = list;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLowAlert() {
        return lowAlert;
    }

    public void setLowAlert(Integer lowAlert) {
        this.lowAlert = lowAlert;
    }

    public Integer getHighAlert() {
        return highAlert;
    }

    public void setHighAlert(Integer highAlert) {
        this.highAlert = highAlert;
    }

    public Integer getOnline() {
        return online;
    }

    public void setOnline(Integer online) {
        this.online = online;
    }

    public Integer getOffline() {
        return offline;
    }

    public void setOffline(Integer offline) {
        this.offline = offline;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}
