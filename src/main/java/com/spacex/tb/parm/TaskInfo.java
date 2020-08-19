package com.spacex.tb.parm;


import lombok.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data // 实现了：1、所有属性的get和set方法；2、toString 方法；3、hashCode方法；4、equals方法
@Builder // 建造者模式
@NoArgsConstructor // 无参构造函数
@AllArgsConstructor // 有参构造函数
public class TaskInfo {
    private String taskId;
    private TaskDetail params;
    private long  start_time;
    private long end_time;
    private String bg_color;
    private String parentId;
    private long level;
    private long is_group;
    private String tasklistId;
    private String taskListName;
    private String taskgroupId;
    private String templateId;
    private String projectId;
    private List<TaskInfo> children;
}
