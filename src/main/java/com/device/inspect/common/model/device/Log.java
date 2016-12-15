package com.device.inspect.common.model.device;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Straight on 2016/12/15.
 */
@Entity
@Table(name = "log")
public class Log {
    private Integer id;
    private String r;

    @Id
    @GeneratedValue
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getR() {
        return r;
    }

    public void setR(String r) {
        this.r = r;
    }
}
