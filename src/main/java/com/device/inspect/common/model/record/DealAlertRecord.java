package com.device.inspect.common.model.record;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by zyclincoln on 8/7/17.
 */
@Entity
@Table(name="deal_alert_record")
public class DealAlertRecord {
    @Id
    @GeneratedValue()
    private Integer id;

    @Column(name = "happened_time")
    @NotNull
    private Date happenedTime;

    @Column(name = "deal_id")
    @NotNull
    private Integer dealId;

    @Column(name = "message")
    @NotNull
    private String message;

    public DealAlertRecord(){

    }

    public DealAlertRecord(Date happenedTime, Integer dealId, String message){
        this.happenedTime = happenedTime; this.dealId = dealId; this.message =message;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public Integer getId(){
        return this.id;
    }

    public void setHappenedTime(Date happenedTime){
        this.happenedTime = happenedTime;
    }

    public Date getHappenedTime(){
        return this.happenedTime;
    }

    public void setMessage(){
        this.message = message;
    }

    public String getMessage(){
        return this.message;
    }
}
