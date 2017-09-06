package com.device.inspect.config;

import java.util.Date;

/**
 * Created by fgz on 2017/9/5.
 */
public class OpeDeviceDisableTime {

    public static String SortAndJudgeOnDeviceDisableTime(String content){
        if (content == null || content.isEmpty())
            return null;

        if (content.charAt(0) == ';')
            content = content.substring(1);
        if (content.charAt(content.length()-1) == ';')
            content = content.substring(0, content.length()-1);

        String[] contents = content.split(";");
        Long[][] disablePeriod = new Long[contents.length][2];
        for (int i=0; i<contents.length; i++){
            String[] startToEnd = contents[i].split(",");
            disablePeriod[i][0] = Long.parseLong(startToEnd[0]);
            disablePeriod[i][1] = Long.parseLong(startToEnd[1]);
        }
        Long startTemp = 0L;
        Long endTemp = 0L;
        for (int i=disablePeriod.length-1; i>0; i--){
            for (int j=0; j<i; j++){
                if (disablePeriod[j+1][0] < disablePeriod[j][0]){
                    startTemp =  disablePeriod[j][0];
                    endTemp = disablePeriod[j][1];
                    disablePeriod[j][0] = disablePeriod[j+1][0];
                    disablePeriod[j][1] = disablePeriod[j+1][1];
                    disablePeriod[j+1][0] = startTemp;
                    disablePeriod[j+1][1] = endTemp;
                }
            }
        }
        for (int i=0; i<disablePeriod.length-1; i++){
            if (disablePeriod[i][1] > disablePeriod[i+1][0])
                return null;
        }
        String contentSort = "";
        for (int i=0; i<disablePeriod.length; i++){
            if (i != disablePeriod.length-1)
                contentSort += disablePeriod[i][0]+","+disablePeriod[i][1]+";";
            else
                contentSort += disablePeriod[i][0]+","+disablePeriod[i][1];
        }
        return contentSort;
    }

    public static String modifyOnDeviceDisableTime(String content){
        if (content == null || content.isEmpty())
            return null;

        String[] contents = content.split(";");
        Long[][] disablePeriod = new Long[contents.length][2];
        for (int i=0; i<contents.length; i++){
            String[] startToEnd = contents[i].split(",");
            disablePeriod[i][0] = Long.parseLong(startToEnd[0]);
            disablePeriod[i][1] = Long.parseLong(startToEnd[1]);
        }
        String contentSort = "";
        Long currentStamp = new Date().getTime();
        for (int i=0; i<disablePeriod.length; i++){
            if (disablePeriod[i][0] >= currentStamp) {
                if (i != disablePeriod.length - 1)
                    contentSort += disablePeriod[i][0] + "," + disablePeriod[i][1] + ";";
                else
                    contentSort += disablePeriod[i][0] + "," + disablePeriod[i][1];
            }
        }
        if (contentSort.isEmpty())
            return null;
        return contentSort;
    }
}
