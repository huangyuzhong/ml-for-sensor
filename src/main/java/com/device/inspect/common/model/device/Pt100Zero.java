package com.device.inspect.common.model.device;

import javax.persistence.*;

/**
 * Created by Straight on 2016/12/15.
 */
@Entity
@Table(name = "pt100_zero")
public class Pt100Zero {
    private Integer id;
    private String code;
    private Double zeroValue;

    @Id
    @GeneratedValue
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "zero_value")
    public Double getZeroValue() {
        return zeroValue;
    }

    public void setZeroValue(Double zeroValue) {
        this.zeroValue = zeroValue;
    }
}
