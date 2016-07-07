package com.device.inspect.common.model.record;

import com.device.inspect.common.model.firm.Building;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Administrator on 2016/7/7.
 */
@Entity
@Table(name = "buildings_daily")
public class BuildingDaily {

    private Integer id;
    private Building build;
    private Date start;
    private Date end;
    private Integer alertNum;

    @Id
    @GeneratedValue()
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne()
    @JoinColumn(name = "build_id")
    public Building getBuild() {
        return build;
    }

    public void setBuild(Building build) {
        this.build = build;
    }

    @Column(name = "start_time")
    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    @Column(name = "end_time")
    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    @Column(name = "alert_num")
    public Integer getAlertNum() {
        return alertNum;
    }

    public void setAlertNum(Integer alertNum) {
        this.alertNum = alertNum;
    }
}
