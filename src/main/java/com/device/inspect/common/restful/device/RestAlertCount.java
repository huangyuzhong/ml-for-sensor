package com.device.inspect.common.restful.device;

import com.device.inspect.common.model.device.AlertCount;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;

/**
 * Created by fgz on 2017/9/21.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestAlertCount {
    private Long startTime;
    private Long endTime;
    private String deviceName;
    private Integer deviceId;
    private String deviceTypeName;
    private Integer deviceTypeId;
    private String inspectMeasurement;
    private Integer alertType;

    public RestAlertCount(@NotNull AlertCount alertCount){
        this.startTime = alertCount.getCreateDate().getTime();
//        this.
    }
}
