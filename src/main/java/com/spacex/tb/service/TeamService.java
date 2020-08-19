package com.spacex.tb.service;

import com.alibaba.fastjson.JSONObject;
import com.spacex.tb.parm.Headers;
import com.spacex.tb.parm.Parms;
import com.spacex.tb.parm.TaskInfo;
import com.spacex.tb.util.JsonResult;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface TeamService {

    JSONObject sendPost(String url, HttpHeaders headers, Map<String, Object> requestParam);
    String getTaskGroupNameById(Map<String,String> headMap, String taskGroupId) throws Exception;

    // 获取组织内的所有的员工信息
    Map<String,String>  getUserList(Map<String,String> headMap,String orgId,Boolean filter)throws Exception;
    JSONObject getTaskList(HttpHeaders headers, JSONObject jsonObject);
    Map<String,String> TaskListName(String projectId,Map<String,String> headerMap) throws Exception;
    // 获取所有的分组
    JSONObject getTaskGroup(Map<String,String>  queryMap,Map<String,String> headerMap) throws Exception;

    List<TaskInfo> taskList(List<TaskInfo> taskInfos);
     List<TaskInfo> taskGroupList(List<TaskInfo> taskInfos);
}
