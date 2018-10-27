package com.device.inspect.common.model.device;

import javax.persistence.*;

/**
 * Created by fgz on 2017/8/31.
 */
@Entity
@Table(name = "ope_models_level")
public class OpeModelsLevel {

    private Integer id;
    private Integer level;
    private Integer interval;
    private String description;

    @Id
    @GeneratedValue()
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "ope_level")
    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    @Column(name = "ope_interval")
    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
