package com.device.inspect.common.restful.device;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by zyclincoln on 3/20/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestHourlyUtilization {
    private Long hourlyTime;
    private Float runningPercentile;
    private Float idlePercentile;

    public RestHourlyUtilization(Long hourlyTime, Float runningPercentile, Float idlePercentile) {
        this.hourlyTime = hourlyTime;
        this.runningPercentile = runningPercentile;
        this.idlePercentile = idlePercentile;
    }

    public Long getHourlyTime() {
        return hourlyTime;
    }

    public void setHourlyTime(Long hourlyTime) {
        this.hourlyTime = hourlyTime;
    }

    public Float getRunningPercentile() {
        return runningPercentile;
    }

    public void setRunningPercentile(Float runningPercentile) {
        this.runningPercentile = runningPercentile;
    }

    public Float getIdlePercentile() {
        return idlePercentile;
    }

    public void setIdlePercentile(Float idlePercentile) {
        this.idlePercentile = idlePercentile;
    }

}
