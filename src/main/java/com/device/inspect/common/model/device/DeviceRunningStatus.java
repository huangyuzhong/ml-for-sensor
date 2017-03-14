package com.device.inspect.common.model.device;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by zyclincoln on 3/14/17.
 */
@Entity
@Table(name="device_running_status")
public class DeviceRunningStatus {
    private Integer id;
    private String name;
    private Integer level;
    private String description;

    @Id
    @GeneratedValue()
    public Integer getId(){
        return this.id;
    }
    public void setId(Integer id){
        this.id = id;
    }

    @Column(name = "name")
    @NotNull
    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }

    @Column(name = "level")
    @NotNull
    public Integer getLevel(){
        return this.level;
    }
    public void setLevel(Integer level){
        this.level = level;
    }

    @Column(name = "description")
    public String getDescription(){
        return this.description;
    }
    public void setDescription(String description){
        this.description = description;
    }

}
