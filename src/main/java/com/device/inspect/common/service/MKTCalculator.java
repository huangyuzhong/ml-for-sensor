package com.device.inspect.common.service;

import com.device.inspect.common.model.device.InspectData;
import com.device.inspect.controller.SocketMessageApi;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Created by zyclincoln on 4/18/17.
 */


public class MKTCalculator {
    private static final Logger LOGGER = LogManager.getLogger(MKTCalculator.class);

    private static double deltaH = 83.14472;
    private static double R = 8.314472/1000;
    public static Double calculateMKT(List<InspectData> inspectDatas){
        if(inspectDatas.size() == 0){
            LOGGER.info("MKTCalculator: inspect data size is 0");
            return null;
        }
        else{
            double deltaHDivR = deltaH / R;
            double sum = 0;
            long total_time = 0;
            for(int i = 0; i < inspectDatas.size() - 1; i++){
                long time = inspectDatas.get(i).getCreateDate().getTime() - inspectDatas.get(i + 1).getCreateDate().getTime();
                total_time += time;
                sum += time * Math.exp(- deltaHDivR/(Double.parseDouble(inspectDatas.get(i).getResult())*1.8 + 32));
            }
            sum /= total_time;
            return new Double(-deltaHDivR/Math.log(sum));
        }
    }
}
