package com.spacex.tb.parm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@Data // 实现了：1、所有属性的get和set方法；2、toString 方法；3、hashCode方法；4、equals方法
@Builder // 建造者模式
@NoArgsConstructor // 无参构造函数
@AllArgsConstructor // 有参构造函数
public class TaskDetail {
    private String title;
    private Date startTime;
    private Date endTime;
    private String biaoti;
    private String parentId;
    private String executorName;

}
