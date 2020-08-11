package com.spacex.tb.parm;

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
    private String startDate;
    private String dueDate;
    private String note;
    // 优先级：
    //0：普通（默认值）
    //1：紧急
    //2：非常紧急
    private String priority;
    // 父任务 ID，传此参数相当于创建了一个子任务
    private String parentTaskId;

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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
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

    public String getParentTaskId() {
        return parentTaskId;
    }

    public void setParentTaskId(String parentTaskId) {
        this.parentTaskId = parentTaskId;
    }

    @Override
    public String toString() {
        return "Task{" +
                "operatorId='" + operatorId + '\'' +
                ", projectId='" + projectId + '\'' +
                ", templateId='" + templateId + '\'' +
                ", tasklistId='" + tasklistId + '\'' +
                ", taskgroupId='" + taskgroupId + '\'' +
                ", content='" + content + '\'' +
                ", executorId='" + executorId + '\'' +
                ", statusId='" + statusId + '\'' +
                ", startDate='" + startDate + '\'' +
                ", dueDate='" + dueDate + '\'' +
                ", note='" + note + '\'' +
                ", priority='" + priority + '\'' +
                ", parentTaskId='" + parentTaskId + '\'' +
                '}';
    }
}
