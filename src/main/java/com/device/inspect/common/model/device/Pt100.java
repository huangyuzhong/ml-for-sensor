package com.device.inspect.common.model.device;

import javax.persistence.*;

/**
 * Created by Straight on 2016/12/7.
 */
@Entity
@Table(name = "pt100")
public class Pt100 {
    private Integer id;
//    private DeviceType deviceType;
    private String temperature;
    private Float resistance;

    @Id
    @GeneratedValue
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

//    @ManyToOne
//    public DeviceType getDeviceType() {
//        return deviceType;
//    }
//
//    public void setDeviceType(DeviceType deviceType) {
//        this.deviceType = deviceType;
//    }


    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public Float getResistance() {
        return resistance;
    }

    public void setResistance(Float resistance) {
        this.resistance = resistance;
    }

    @Override
    public String toString() {
        return "Pt100{" +
                "id=" + id +
                ", temperature='" + temperature + '\'' +
                ", resistance=" + resistance +
                '}';
    }
}
