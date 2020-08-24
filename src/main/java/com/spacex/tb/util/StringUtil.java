package com.spacex.tb.util;

import com.alibaba.fastjson.JSONObject;
import com.spacex.tb.parm.TaskDetail;
import com.spacex.tb.parm.TaskInfo;
import com.spacex.tb.service.TeamService;

import javax.annotation.Resource;
import java.security.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

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

    /*

     */
    public static TaskDetail setTaskDetail(JSONObject obj){
//         task = new TaskDetail();
        return  TaskDetail.builder().biaoti(obj.getString("note"))
                .title(obj.getString("content"))
                .endTime(obj.getDate("dueDate"))
                .startTime(obj.getDate("startDate"))
                .parentId(obj.getString("parentTaskId"))
                .biaoti(obj.getString("note")).build();
    }
    public static TaskInfo setTaskInfo(JSONObject obj,TaskDetail taskDetail){
        String bg_color = "";
        if (obj.getInteger("isDone") == 0){
            bg_color = "#64C7FE";
        }else{
            bg_color = "#BFBFBF";
        }
//        System.out.println(bg_color+obj.getInteger("isDone"));
        return TaskInfo.builder().taskId(obj.getString("taskId"))
                .end_time(timeToStamp(obj.getDate("dueDate")))
                .start_time(timeToStamp(obj.getDate("startDate")))
                .parentId(obj.getString("parentTaskId"))
                .tasklistId(obj.getString("tasklistId"))
                .taskListName(obj.getString("taskListName"))
                .projectId(obj.getString("projectId"))
                .templateId(obj.getString("templateId"))
                .taskgroupId(obj.getString("taskgroupId"))
                .bg_color(bg_color)
                .level(2).params(taskDetail).build();
    }

    /* //日期转换为时间戳 */
    public static long timeToStamp(Date ymd) {
        long dateStr = 0;
        if (ymd == null || Objects.equals(ymd,"")){
            return  dateStr;
        }
        SimpleDateFormat sformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");//日期格式
        String time = sformat.format(ymd);
        dateStr = (new SimpleDateFormat("yyyy-MM-dd HH:mm")).parse(time, new ParsePosition(0)).getTime();
        return dateStr;
    }

    /**
     * 时间转字符串
     * @param date
     * @return String
     */
    public static String dateToString(Date date) {
        SimpleDateFormat sformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");//日期格式
        String time = sformat.format(date);
        return time;
    }

    public static Date dateFormat(Date date,String Hours) throws ParseException {
        if (date != null){
            String strDateFormat = "yyyy-MM-dd";
            SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
            String ymd = sdf.format(date);
            ymd = ymd +" " + Hours;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");//注意月份是MM
            return simpleDateFormat.parse(ymd) ;
        }
        return null;

    }

}
