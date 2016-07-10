package com.device.inspect.common.model.record;

import com.device.inspect.common.model.firm.Room;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Administrator on 2016/7/8.
 */
@Entity
@Table(name = "room_daily")
public class RoomDaily {

    private Integer id;
    private Room room;
    private Date start;
    private Date end;
    private Integer alertNum;

    @Id
    @GeneratedValue
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne()
    @JoinColumn(name = "room_id")
    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
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
