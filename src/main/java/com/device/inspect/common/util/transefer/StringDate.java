package com.device.inspect.common.util.transefer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Administrator on 2016/7/24.
 */
public class StringDate {
    /**
     * date类型转换为String类型
     *formatType格式为yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
     *data Date类型的时间
      * @param data
     * @param formatType
     * @return
     */
    public static String dateToString(Date data, String formatType) {
        return new SimpleDateFormat(formatType).format(data);
    }

    /**
     * long类型转换为String类型
     *currentTime要转换的long类型的时间
     *formatType要转换的string类型的时间格式
     * @param currentTime
     * @param formatType
     * @return
     * @throws ParseException
     */
    public static String longToString(long currentTime, String formatType)
            throws ParseException {
        Date date = longToDate(currentTime, formatType); // long类型转成Date类型
        String strTime = dateToString(date, formatType); // date类型转成String
        return strTime;
    }

    /**
     * string类型转换为date类型
     *strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
     *HH时mm分ss秒，
     *strTime的时间格式必须要与formatType的时间格式相同
     * @param strTime
     * @param formatType
     * @return
     * @throws ParseException
     */
    public static Date stringToDate(String strTime, String formatType)
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        date = formatter.parse(strTime);
        return date;
    }

    /**
     * long转换为Date类型
     * currentTime要转换的long类型的时间
     * formatType要转换的时间格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
     * @param currentTime
     * @param formatType
     * @return
     * @throws ParseException
     */
    public static Date longToDate(long currentTime, String formatType)
            throws ParseException {
        Date dateOld = new Date(currentTime); // 根据long类型的毫秒数生命一个date类型的时间
        String sDateTime = dateToString(dateOld, formatType); // 把date类型的时间转换为string
        Date date = stringToDate(sDateTime, formatType); // 把String类型转换为Date类型
        return date;
    }

    /**
     * string类型转换为long类型
     * strTime要转换的String类型的时间
     * formatType时间格式strTime的时间格式
     * 和formatType的时间格式必须相同
     * @param strTime
     * @param formatType
     * @return
     * @throws ParseException
     */
    public static long stringToLong(String strTime, String formatType)
            throws ParseException {
        Date date = stringToDate(strTime, formatType); // String类型转成date类型
        if (date == null) {
            return 0;
        } else {
            long currentTime = dateToLong(date); // date类型转成long类型
            return currentTime;
        }
    }

    /**
     * date类型转换为long类型
     * date要转换的date类型的时间
     * @param date
     * @return
     */
    public static long dateToLong(Date date) {
        return date.getTime();
    }


    public static long rfc3339ToLong(String time){
        try {
            String[] e = time.split("T");
            String datePart = e[0];
            String timePart = e[1].substring(0, e[1].length() - 1);
            SimpleDateFormat dateDF = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeDF = new SimpleDateFormat("HH:mm:ss.SSS");
            dateDF.setTimeZone(TimeZone.getTimeZone("UTC"));
            timeDF.setTimeZone(TimeZone.getTimeZone("UTC"));

            if(timePart.lastIndexOf(".") < 0){
                timePart += ".000";
            }

            return dateDF.parse(datePart).getTime() + timeDF.parse(timePart).getTime();
        } catch (Exception var6) {
            throw new RuntimeException("unexpected date format", var6);
        }
    }
}
