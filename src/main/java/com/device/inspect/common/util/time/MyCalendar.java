package com.device.inspect.common.util.time;

import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2016/8/23.
 */
public class MyCalendar {
    /**
     * 获取开始日期和结束日期之间的天数
     * @param start
     * @param end
     * @return
     */
    public static int getDateSpace(Date start, Date end){

        int result = 0;

        Calendar calst = Calendar.getInstance();;
        Calendar caled = Calendar.getInstance();

        calst.setTime(start);
        caled.setTime(end);

        //设置时间为0时
        calst.set(Calendar.HOUR_OF_DAY, 0);
        calst.set(Calendar.MINUTE, 0);
        calst.set(Calendar.SECOND, 0);
        caled.set(Calendar.HOUR_OF_DAY, 0);
        caled.set(Calendar.MINUTE, 0);
        caled.set(Calendar.SECOND, 0);
        //得到两个日期相差的天数
        int days = ((int)(caled.getTime().getTime()/1000)-(int)(calst.getTime().getTime()/1000))/3600/24;

        return days;
    }

    /**
     * 给定一个utc时间和一时区的offset hour， 获取与该时区同一时间所在日的凌晨0点所对应的UTC时间
     * 例如 给定utc时间2月1日22：30，offset hour为8即北京所在的东八区时间为2月2日6：30，
     * 那么北京时间当日2月2日0点对应的utc时间为2月1日16：00
     * @param utcTime
     * @param timeZoneOffset
     * @return
     */
    public static Date getUtcTimeForMidnight(Date utcTime, int timeZoneOffset){
        Date currentZonedTime = new Date(utcTime.getTime() + timeZoneOffset * 60 * 60 * 1000);
        Date thisMidnight = DateUtils.round(currentZonedTime, Calendar.DATE);
        Date utcTimeForTimezoneMidnight = new Date(thisMidnight.getTime() - timeZoneOffset * 60 * 60 * 1000);

        return utcTimeForTimezoneMidnight;

    }
}
