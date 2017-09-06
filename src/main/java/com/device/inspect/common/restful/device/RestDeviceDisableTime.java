package com.device.inspect.common.restful.device;

import com.device.inspect.common.model.record.DeviceDisableTime;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by fgz on 2017/7/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestDeviceDisableTime {
    private Integer id;
    private Integer deviceId;
    private String strategyType;
    private Long[][] disablePeriod;
    private Long[][] ablePeriod = new Long[50][2];
    private Integer ablePeriodLength;

    public RestDeviceDisableTime() {
    }

    public RestDeviceDisableTime(@NotNull DeviceDisableTime deviceDisableTime) {
        this.id = deviceDisableTime.getId();
        this.deviceId = deviceDisableTime.getDevice().getId();
        this.strategyType = deviceDisableTime.getStrategyType();

        if (deviceDisableTime.getContent() != null && !("".equals(deviceDisableTime.getContent()))){
            String content = deviceDisableTime.getContent();
            String[] contents = content.split(";");

            disablePeriod = new Long[contents.length][2];
            for (int i=0; i<contents.length; i++){
                String[] startToEnd = contents[i].split(",");
                disablePeriod[i][0] = Long.parseLong(startToEnd[0]);
                disablePeriod[i][1] = Long.parseLong(startToEnd[1]);
            }
            ablePeriod[0][0] = new Date().getTime();
            long yearMill = 31104000000L; // 360天的毫秒值
            ablePeriod[0][1] = ablePeriod[0][0].longValue()+yearMill;
            int count = 1;
            for (int i=0; i<contents.length; i++){
                int countTemp = count;
                for (int j=0; j<count; j++){
                    if (ablePeriod[j][0] < disablePeriod[i][0] && ablePeriod[j][1] > disablePeriod[i][1]){
                        Long temp = ablePeriod[j][1];
                        ablePeriod[j][1] = disablePeriod[i][0];
                        ablePeriod[count][0] = disablePeriod[i][1];
                        ablePeriod[count][1] = temp;
                        countTemp++;
                    } else if (ablePeriod[j][0] < disablePeriod[i][0] && ablePeriod[j][1] == disablePeriod[i][1]){
                        ablePeriod[j][1] = disablePeriod[i][0];
                    } else if (ablePeriod[j][0] >= disablePeriod[i][0] && ablePeriod[j][1] > disablePeriod[i][1]){
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

        }else{
            this.ablePeriod[0][0]=new Date().getTime();
            long yearMill = 31104000000L; // 360天的毫秒值
            this.ablePeriod[0][1] = ablePeriod[0][0].longValue()+yearMill;
            this.ablePeriodLength=1;
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

    public Long[][] getDisablePeriod() {
        return disablePeriod;
    }

    public void setDisablePeriod(Long[][] disablePeriod) {
        this.disablePeriod = disablePeriod;
    }

    public Long[][] getAblePeriod() {
        return ablePeriod;
    }

    public void setAblePeriod(Long[][] ablePeriod) {
        this.ablePeriod = ablePeriod;
    }

    public Integer getAblePeriodLength() {
        return ablePeriodLength;
    }

    public void setAblePeriodLength(Integer ablePeriodLength) {
        this.ablePeriodLength = ablePeriodLength;
    }
}
