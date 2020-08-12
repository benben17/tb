package com.spacex.tb.parm;

import lombok.Builder;
import lombok.Data;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Data
@Builder
@Setter
public class Task {
    private String operatorId;
    private String projectId;
    //templateId
    private String templateId;
    private String tasklistId;
    private String taskgroupId;
    //任务内容，最长为 500 个字符
    private String content;
    //执行者的用户 ID，不传表示待认领
    private String executorId;
    //工作流状态 ID，通过
    private String statusId;
    private Date startDate;
    private Date dueDate;
    private String note;
    // 优先级：
    //0：普通（默认值）
    //1：紧急
    //2：非常紧急
    private String priority;
    private String visible;
    // 父任务 ID，传此参数相当于创建了一个子任务
    private String parentTaskId;
    private  long ancestorIds;
    private List<String> participants;
    private List<Object> customfields;
    private  long isDone;
    private String creatorId;
    private Date created;
    private String modifierId;
    private Date updated;


}
