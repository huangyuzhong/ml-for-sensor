package com.device.inspect.common.ai;

import com.device.inspect.common.model.record.MLResults;
import com.device.inspect.common.repository.record.MLResultsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by FGZ on 17/8/30.
 */
@Component("KMeansUse")
public class KMeansUse {

    @Autowired
    private MLResultsRepository mlResultsRepository;

    public double[][] getResult(String device_id, String inspect_para){
        MLResults mlResults = mlResultsRepository.findByDeviceIdAndInspectPara(device_id, inspect_para);
        String result[] = mlResults.getTrainingResult().split(",");
        double[][] rules = new double[result.length/2][2];
        for (int i=0; i<result.length; i++){
            if (i%2 == 0){
                rules[i/2][0] = Double.parseDouble(result[i]);
            }else{
                rules[i/2][1] = Double.parseDouble(result[i]);
            }
        }
        return rules;
    }

    public int use(String device_id,String inspect_para,String val){
        double[][] rules = getResult(device_id, inspect_para);
        double valDou = Double.parseDouble(val);
        if (rules == null || rules.length == 0)
            return -1;
        else{
            int index = -1;
            double[] temp = new double[rules.length];
            for (int i=0; i<rules.length; i++){
                temp[i] = Math.abs(valDou - rules[i][0]);
                if (valDou >= (rules[i][0]-rules[i][1]) && valDou <= (rules[i][0]+rules[i][1])){
                    index = i;
                    break;
                }
            }
            if (index == -1){
                double min = temp[0];
                index = 0;
                for (int i=1; i<temp.length; i++){
                    if (temp[i]<min){
                        min = temp[i];
                        index = i;
                    }
                }
            }
            return index;
        }
    }

    public Integer doTask(String device_id, String inspect_para, String val) {
        Integer type=use(device_id, inspect_para, val);
        System.out.print(type);
        return type;
    }

}
