package com.device.inspect.common.model.device;

import javafx.scene.Camera;

import javax.persistence.*;

/**
 * Created by zyclincoln on 7/28/17.
 */
@Entity
@Table(name = "camera_list")
public class CameraList {
    @Id
    @GeneratedValue()
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "device_id")
    private Integer deviceId;

    @Column(name = "serial_no")
    private String serialNo;

    @Column(name = "url")
    private String url;

    @Column(name = "description")
    private String description;

    public CameraList(){

    }

    public CameraList(String name, Integer deviceId, String serialNo, String url, String description){
        this.name = name;
        this.deviceId = deviceId;
        this.serialNo = serialNo;
        this.url = url;
        this.description = description;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public Integer getId(){
        return this.id;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public void setDeviceId(Integer deviceId){
        this.deviceId = deviceId;
    }

    public Integer getDeviceId(){
        return this.deviceId;
    }

    public void setSerialNo(String serialNo){
        this.serialNo = serialNo;
    }

    public String getSerialNo(){
        return this.serialNo;
    }

    public void setUrl(String url){
        this.url = url;
    }

    public String getUrl(){
        return this.url;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getDescription(){
        return this.description;
    }
}
