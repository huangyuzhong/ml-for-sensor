package com.device.inspect.common.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zyclincoln on 7/16/17.
 */
public class TemporalStrategyChecker {

    private static final Logger LOGGER = LogManager.getLogger(TemporalStrategyChecker.class);

    private static final Pattern dailyPattern = Pattern.compile("^d+:d+-d+:d+");

    public static boolean checkRequestTimeByStrategy(String strategy, String content, Date beginTime, Date endTime){
        if(strategy.equals("daily")){
            Matcher m = dailyPattern.matcher(content);
            if(m.find() && m.groupCount() == 5){
                Integer beginHour = Integer.parseInt(m.group(1));
                Integer beginMinute = Integer.parseInt(m.group(2));
                Integer endHour = Integer.parseInt(m.group(3));
                Integer endMinute = Integer.parseInt(m.group(4));

                Integer beginMinutes = beginHour*60 + beginMinute;
                Integer endMinutes = endHour*60 + endMinute;

                Calendar beginCal = Calendar.getInstance(TimeZone.getTimeZone("CST"));
                beginCal.setTime(beginTime);
                Calendar endCal = Calendar.getInstance(TimeZone.getTimeZone("CST"));
                endCal.setTime(endTime);

                Integer reqBeginMinutes = beginCal.get(Calendar.HOUR_OF_DAY) * 60 + beginCal.get(Calendar.MINUTE);
                Integer reqEndMinutes = endCal.get(Calendar.HOUR_OF_DAY)*60 + endCal.get(Calendar.MINUTE);

                boolean crossDay = false;
                if(beginCal.get(Calendar.HOUR_OF_DAY) > endCal.get(Calendar.HOUR_OF_DAY)){
                    crossDay = true;
                }

                if(crossDay){
                    if(reqBeginMinutes < beginMinutes){
                        return false;
                    }
                    else if(endMinutes < reqEndMinutes){
                        return false;
                    }
                    else {
                        return true;
                    }
                }
                else{
                    if(reqBeginMinutes < beginMinutes && beginMinutes < reqEndMinutes){
                        return false;
                    }
                    else if(reqBeginMinutes < endMinutes && endMinutes < reqEndMinutes){
                        return false;
                    }
                    else{
                        return true;
                    }
                }
            }
            else{
                LOGGER.error(String.format("Temporal Strategy Checker: illegal daily strategy content: %s", content));
                return false;
            }
        }
        else{
            LOGGER.error(String.format("Temporal Strategy Checker: unknown strategy", strategy));
            return false;
        }
    }
}
