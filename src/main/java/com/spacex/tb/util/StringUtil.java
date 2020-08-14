package com.spacex.tb.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtil {

    public static String getCurrentDate() {
        String dateStr = "";
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateStr = sdf.format(d);
        return dateStr;
    }

    public static String getDateStr(Long millis) {
        String dateStr = "";
        Date d = new Date(millis);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateStr = sdf.format(d);
        return dateStr;
    }

    /**
     * local time 转成 utc
     * @param ymd
     * @return
     */
    public  static String string2Utc(String ymd) {
        String strUtc = "";
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

        try {
            Date date = sdf1.parse(ymd);//拿到Date对象
            strUtc      = sdf2.format(date);//输出格式：
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strUtc;
    }
}
