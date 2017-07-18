package com.device.inspect.common.restful.device;

import com.device.inspect.common.model.record.DeviceDisableTime;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fgz on 2017/7/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestDeviceDisableTime {
    private Integer id;
    private Integer deviceId;
    private String strategyType;
    private Map<Integer, Integer> disableTime;
    private Map<Integer, Integer> ableTime;

    private int[][] disablePeriod;
    private int[][] ablePeriod = new int[100][2];
    private Integer duration;

    public RestDeviceDisableTime(@NotNull DeviceDisableTime deviceDisableTime) {
        this.id = deviceDisableTime.getId();
        this.deviceId = deviceDisableTime.getDevice().getId();
        this.strategyType = deviceDisableTime.getStrategyType();

        this.disableTime = new HashMap<Integer, Integer>();
        this.ableTime = new HashMap<Integer, Integer>();
        this.ableTime.put(0, 24);
        String content = deviceDisableTime.getContent();
        String[] contents = content.split(";");

        disablePeriod = new int[contents.length][2];
        for (int i=0; i<contents.length; i++){
            String[] startToEnd = contents[i].split(",");
            disablePeriod[i][0] = Integer.parseInt(startToEnd[0]);
            disablePeriod[i][1] = Integer.parseInt(startToEnd[1]);
        }
        for (int i=0; i<contents.length; i++){

        }

        for (int i=0; i<contents.length; i++){
            String[] startToEnd = contents[i].split(",");
            this.disableTime.put(Integer.parseInt(startToEnd[0]), Integer.parseInt(startToEnd[1]));
        }

        Map<Integer, Integer> ableTimeTemp = this.ableTime;
        for (Map.Entry<Integer, Integer> entryDis : disableTime.entrySet()) {
            for (Map.Entry<Integer, Integer> entryAble : ableTime.entrySet()) {
                if (entryAble.getKey() < entryDis.getKey() && entryAble.getValue() > entryDis.getValue()){
//                    ableTimeTemp.remove(entryAble.getKey());
                    ableTimeTemp.put(entryAble.getKey(), entryDis.getKey());
                    ableTimeTemp.put(entryDis.getValue(), entryAble.getValue());
                }
            }
            this.ableTime = ableTimeTemp;
        }

        this.duration = 0;
        for (Map.Entry<Integer, Integer> entryAble : ableTime.entrySet()) {
            this.duration += (entryAble.getValue()-entryAble.getKey());
        }
    }
}
