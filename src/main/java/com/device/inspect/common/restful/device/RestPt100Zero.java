package com.device.inspect.common.restful.device;

import com.device.inspect.common.model.device.Pt100Zero;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sun.istack.internal.NotNull;

/**
 * Created by Straight on 2016/12/15.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestPt100Zero {
    private Integer id;
    private String code;
    private Double zeroValue;

    public RestPt100Zero(@NotNull Pt100Zero pt100Zero) {
        this.id=pt100Zero.getId();
        this.code=pt100Zero.getCode();
        this.zeroValue=pt100Zero.getZeroValue();

    }

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

    public Double getZeroValue() {
        return zeroValue;
    }

    public void setZeroValue(Double zeroValue) {
        this.zeroValue = zeroValue;
    }
}
