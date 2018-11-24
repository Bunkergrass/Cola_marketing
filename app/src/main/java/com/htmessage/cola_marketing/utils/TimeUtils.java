package com.htmessage.cola_marketing.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class TimeUtils {

    public static String getCurrentTime(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String date=sdf.format(new Date());
        return date;
    }

    private static long getTimeMillis(String strTime) { long returnMillis = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        Date d = null;
        try {
            d = sdf.parse(strTime);
            returnMillis = d.getTime();
        } catch (ParseException e) {
            //
        }
        return returnMillis;
    }

    public static String getTimeExpend(String startTime, String endTime){
        long longStart = getTimeMillis(startTime); //传入字串类型 2016/06/28 08:30
        long longEnd = getTimeMillis(endTime); //获取开始时间毫秒数
        long longExpend = longEnd - longStart; //获取结束时间毫秒数
        long longMinutes = longExpend / (60 * 1000);
        long longSeconds = (longExpend - longMinutes * (60 * 1000)) / 1000;
        String min,sec;
        if(longMinutes < 10)
            min = "0"+longMinutes;
        else
            min = ""+longMinutes;
        if(longSeconds < 10)
            sec = "0"+longSeconds;
        else
            sec = ""+longSeconds;
        return min + ":" + sec;//根据时间差来计算分钟数
    }
}
