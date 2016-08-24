package com.device.inspect.common.util.time;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2016/8/23.
 */
public class MyCalendar {
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
}
