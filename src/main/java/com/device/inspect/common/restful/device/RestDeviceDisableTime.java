package com.device.inspect.common.restful.device;

import com.device.inspect.common.model.record.DeviceDisableTime;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;

/**
 * Created by fgz on 2017/7/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestDeviceDisableTime {
    private Integer id;
    private Integer deviceId;
    private String strategyType;
    private Integer[][] disablePeriod;
    private Integer[][] ablePeriod = new Integer[50][2];
    private Integer ablePeriodLength;
    private Integer duration;

    public RestDeviceDisableTime() {
    }

    public RestDeviceDisableTime(@NotNull DeviceDisableTime deviceDisableTime) {
        this.id = deviceDisableTime.getId();
        this.deviceId = deviceDisableTime.getDevice().getId();
        this.strategyType = deviceDisableTime.getStrategyType();

        String content = deviceDisableTime.getContent();
        String[] contents = content.split(";");

        disablePeriod = new Integer[contents.length][2];
        for (int i=0; i<contents.length; i++){
            String[] startToEnd = contents[i].split(",");
            disablePeriod[i][0] = Integer.parseInt(startToEnd[0]);
            disablePeriod[i][1] = Integer.parseInt(startToEnd[1]);
        }
        ablePeriod[0][0] = 0;
        ablePeriod[0][1] = 24;
        int count = 1;
        for (int i=0; i<contents.length; i++){
            int countTemp = count;
            for (int j=0; j<count; j++){
                if (ablePeriod[j][0] < disablePeriod[i][0] && ablePeriod[j][1] > disablePeriod[i][1]){
                    int temp = ablePeriod[j][1];
                    ablePeriod[j][1] = disablePeriod[i][0];
                    ablePeriod[count][0] = disablePeriod[i][1];
                    ablePeriod[count][1] = temp;
                    countTemp++;
                } else if (ablePeriod[j][0] < disablePeriod[i][0] && ablePeriod[j][1] == disablePeriod[i][1]){
                    ablePeriod[j][1] = disablePeriod[i][0];
                } else if (ablePeriod[j][0] == disablePeriod[i][0] && ablePeriod[j][1] > disablePeriod[i][1]){
                    ablePeriod[j][0] = disablePeriod[i][1];
                } else if (ablePeriod[j][0] == disablePeriod[i][0] && ablePeriod[j][1] == disablePeriod[i][1]){
                    ablePeriod[j][1] = disablePeriod[i][0];
                }
            }
            count = countTemp;
        }

        for (int i=0; i<count; i++){
            if (ablePeriod[i][0] == ablePeriod[i][1]){
                for (int j=i; j<count-1; j++){
                    ablePeriod[j][0]=ablePeriod[j+1][0];
                    ablePeriod[j][1]=ablePeriod[j+1][1];
                }
                count--;
            }
        }

        this.ablePeriodLength = count;

        this.duration=0;
        for (int i=0; i<count; i++){
            this.duration += (ablePeriod[i][1] - ablePeriod[i][0]);
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public String getStrategyType() {
        return strategyType;
    }

    public void setStrategyType(String strategyType) {
        this.strategyType = strategyType;
    }

    public Integer[][] getDisablePeriod() {
        return disablePeriod;
    }

    public void setDisablePeriod(Integer[][] disablePeriod) {
        this.disablePeriod = disablePeriod;
    }

    public Integer[][] getAblePeriod() {
        return ablePeriod;
    }

    public void setAblePeriod(Integer[][] ablePeriod) {
        this.ablePeriod = ablePeriod;
    }

    public Integer getAblePeriodLength() {
        return ablePeriodLength;
    }

    public void setAblePeriodLength(Integer ablePeriodLength) {
        this.ablePeriodLength = ablePeriodLength;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }
}
