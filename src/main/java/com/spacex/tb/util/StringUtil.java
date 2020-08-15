package com.spacex.tb.util;

import com.alibaba.fastjson.JSONObject;
import com.spacex.tb.parm.TaskDetail;
import com.spacex.tb.parm.TaskInfo;

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

    /*

     */
    public static TaskDetail setTaskDetail(JSONObject obj){
        TaskDetail task = new TaskDetail();
        return  task.builder().biaoti(obj.getString("note"))
                .title(obj.getString("content"))
                .endTime(obj.getDate("dueDate"))
                .startTime(obj.getDate("startDate"))
                .parentId(obj.getString("parentTaskId"))
                .biaoti(obj.getString("note")).build();
    }
    public static TaskInfo setTaskInfo(JSONObject obj,TaskDetail taskDetail){
        TaskInfo  taskInfo = new TaskInfo();
        String bg_color = "";
        if (obj.getInteger("isDone") == 0){
            bg_color = "#64C7FE";
        }else{
            bg_color = "#BFBFBF";
        }
        System.out.println(bg_color+obj.getInteger("isDone"));
        return taskInfo.builder().taskId(obj.getString("taskId"))
                .end_time(obj.getDate("dueDate"))
                .start_time(obj.getDate("startDate"))
                .parentId(obj.getString("parentTaskId"))
                .bg_color(bg_color)
                .level(2).params(taskDetail).build();
    }

}
