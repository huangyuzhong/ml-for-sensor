package com.device.inspect.common.model.record;

import com.device.inspect.common.model.firm.Storey;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Administrator on 2016/7/8.
 */
@Entity
@Table(name = "floors_daily")
public class FloorDaily {

    private Integer id;
    private Storey floor;
    private Date start;
    private Date end;
    private Integer alterNum;

    @Id
    @GeneratedValue()
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne()
    @JoinColumn(name = "floor_id")
    public Storey getFloor() {
        return floor;
    }

    public void setFloor(Storey floor) {
        this.floor = floor;
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
    public Integer getAlterNum() {
        return alterNum;
    }

    public void setAlterNum(Integer alterNum) {
        this.alterNum = alterNum;
    }
}
