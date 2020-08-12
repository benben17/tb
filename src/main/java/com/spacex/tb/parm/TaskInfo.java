package com.spacex.tb.parm;


import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;


public class TaskInfo {
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
    private List<Object> ancestorIds;
    private List<String> participants;
    private List<Object> customfields;
    private  long isDone;
    private String creatorId;
    private Date created;
    private String modifierId;
    private Date updated;
    private String taskgroupName;
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getTasklistId() {
        return tasklistId;
    }

    public void setTasklistId(String tasklistId) {
        this.tasklistId = tasklistId;
    }

    public String getTaskgroupId() {
        return taskgroupId;
    }

    public void setTaskgroupId(String taskgroupId) {
        this.taskgroupId = taskgroupId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExecutorId() {
        return executorId;
    }

    public void setExecutorId(String executorId) {
        this.executorId = executorId;
    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public String getParentTaskId() {
        return parentTaskId;
    }

    public void setParentTaskId(String parentTaskId) {
        this.parentTaskId = parentTaskId;
    }

    public List<Object> getAncestorIds() {
        return ancestorIds;
    }

    public void setAncestorIds(List<Object> ancestorIds) {
        this.ancestorIds = ancestorIds;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public List<Object> getCustomfields() {
        return customfields;
    }

    public void setCustomfields(List<Object> customfields) {
        this.customfields = customfields;
    }

    public long getIsDone() {
        return isDone;
    }

    public void setIsDone(long isDone) {
        this.isDone = isDone;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getModifierId() {
        return modifierId;
    }

    public void setModifierId(String modifierId) {
        this.modifierId = modifierId;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getTaskgroupName() {
        return taskgroupName;
    }

    public void setTaskgroupName(String taskgroupName) {
        this.taskgroupName = taskgroupName;
    }
}
