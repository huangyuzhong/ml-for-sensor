package com.device.inspect.common.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zyclincoln on 7/16/17.
 */
public class TemporalStrategyChecker {

    private static final Logger LOGGER = LogManager.getLogger(TemporalStrategyChecker.class);

    private static final Pattern dailyPattern = Pattern.compile("(\\d+),(\\d+)");
//
//    public static void main(String[] args){
//        System.out.println(checkRequestTimeByStrategy("daily","0,5;9,10",new Date(), new Date()));
//    }

    public static boolean checkRequestTimeByStrategy(String strategy, String content, Date beginTime, Date endTime){
        if(strategy.equals("daily")) {
            String[] contents = content.split(";");
            for (String subContent : contents) {
                Matcher m = dailyPattern.matcher(subContent);
                if(m.find()){
                    Integer beginHour = Integer.parseInt(m.group(1));
                    Integer endHour = Integer.parseInt(m.group(2));
                    Integer beginMinutes = beginHour*60;
                    Integer endMinutes = endHour*60;

                    Calendar beginCal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"));
                    beginCal.setTime(beginTime);

                    Calendar endCal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"));
                    endCal.setTime(endTime);

                    Integer reqBeginMinutes = beginCal.get(Calendar.HOUR_OF_DAY) * 60 + beginCal.get(Calendar.MINUTE);
                    Integer reqEndMinutes = endCal.get(Calendar.HOUR_OF_DAY)*60 + endCal.get(Calendar.MINUTE);

                    if(beginMinutes < reqBeginMinutes && reqBeginMinutes < endMinutes){
                        return false;
                    }
                    else if(beginMinutes < reqEndMinutes && reqEndMinutes < endMinutes){
                        return false;
                    }
                }

            }

        }
        return true;
    }
}
