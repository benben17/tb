package com.spacex.tb.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.spacex.tb.config.TbConfig;
import com.spacex.tb.parm.Headers;
import com.spacex.tb.parm.Parms;
import com.spacex.tb.parm.TaskDetail;
import com.spacex.tb.parm.TaskInfo;
import com.spacex.tb.service.AccessTokenService;
import com.spacex.tb.service.TeamService;
import com.spacex.tb.util.HttpUtil;
import com.spacex.tb.util.JsonResult;

import com.spacex.tb.util.JsonUtils;
import com.spacex.tb.util.StringUtil;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;


@Service
public class TeamServiceImpl implements TeamService {

    @Resource
    private TbConfig tbConfig;

    @Override
    public JSONObject sendPost(String url, HttpHeaders headers, Map<String, Object> requestParam) {
        String responseBody = null;
        // 构造消息头
        try {
            headers.add("Content-type", "application/json; charset=utf-8");
            RestTemplate restTemplate = new RestTemplate();

            HttpEntity<Map<String, Object>> request = new HttpEntity<Map<String, Object>>(requestParam, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            responseBody = response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonResult = JSONObject.parseObject(responseBody);
        return jsonResult;
    }

    @Override
    public String getTaskGroupNameById(Map<String, String> headMap, String taskGroupId) throws Exception {
        String url = tbConfig.getTBApiUrl() + "/taskgroup/query";
        Map<String, Object> requestParam = new HashMap<>();

        requestParam.put("taskgroupId", taskGroupId);
//        System.out.println(taskGroupId);
        String result = HttpUtil.getInstance().doGet(url, requestParam, headMap);
        JSONObject jsonResult = JSONObject.parseObject(result);

        if (jsonResult.getInteger("code") != 200) {
            return "";
        }
        JSONArray array = jsonResult.getJSONArray("result");
        if (array.isEmpty()) {
            return "";
        }
        JSONObject res = (JSONObject) array.get(0);
        return res.getString("name");
    }

    @Override
    public Map<String, String> getUserList(Map<String, String> headMap, String orgId, Boolean filter) throws Exception {
        String url = tbConfig.getTBApiUrl() + "/org/member/list";
        Map<String, String> userList = new HashMap<>();
        Map<String, Object> requestParam = new HashMap<>();
        requestParam.put("orgId", orgId);
        if (filter) {
            requestParam.put("filter", "enable");
        }
        requestParam.put("pageSize", 1000);  // 一次获取1000个用户信息
        String result = HttpUtil.getInstance().doGet(url, requestParam, headMap);
        JSONArray array = JsonUtils.string2Json(result);

        if (array == null ||  array.isEmpty()) {
            return userList;
        }
        for (int i = 0; i < array.size(); i++) {
            JSONObject object = (JSONObject) array.get(i);    //将array中的数据进行逐条转换
            userList.put(object.getString("userId"), object.getString("name"));
        }
        return userList;
    }

    /**
     * 获取任务列表
     *
     * @param headers
     * @param jsonObject
     * @return
     */
    @Override
    public JSONObject getTaskList(HttpHeaders headers, JSONObject jsonObject) {
        String url = tbConfig.getTBApiUrl() + "/task/tqlsearch";
        Map<String, Object> params = new HashMap<>();
        String query = "";
        // 项目id
        if (jsonObject.getString("projectId") != null && !Objects.equals(jsonObject.getString("projectId"), "")) {
            query = query + "_projectId = " + jsonObject.getString("projectId");
        }
        //执行人ID
        if (jsonObject.getString("executorId") != null) {
            if (!Objects.equals(jsonObject.getString("executorId"), "")) {
                query = query + " AND _executorId IN (" + jsonObject.getString("executorId")+")";
            } else {
                query = query + " AND _executorId = " + null;
            }
        }
        // 开始时间
        if (jsonObject.getString("startDate") != null && !Objects.equals(jsonObject.getString("startDate"), "")) {
            query = query + " AND startDate > " + StringUtil.string2Utc(jsonObject.getString("startDate"));
        }
        // 结束时间
        if (jsonObject.getString("dueDate") != null && !Objects.equals(jsonObject.getString("dueDate"), "")) {
            query = query + " AND dueDate < " + StringUtil.string2Utc(jsonObject.getString("dueDate"));
        }
        // tasklistId
        if (jsonObject.getString("tasklistId") != null && !Objects.equals(jsonObject.getString("tasklistId"), "")) {
            query = query + " AND _stageId = " + jsonObject.getString("tasklistId");
        }

        // 是否完成0 未完成 1 完成
        if (jsonObject.getString("isDone") != null && !Objects.equals(jsonObject.getString("isDone"), "")) {
            query = query + " AND isDone = " + jsonObject.getString("isDone");
        }
        // 任务组id
        if (jsonObject.getString("taskgroupId") != null && !Objects.equals(jsonObject.getString("taskgroupId"), "")) {
            query = query + " AND _tasklistId = " + jsonObject.getString("taskgroupId");
        }
        // 参与人ID
        if (jsonObject.getString("involveMember") != null && !Objects.equals(jsonObject.getString("involveMember"), "")) {
            query = query + " AND involveMember IN (" + jsonObject.getString("involveMember")+")";
        }
        if (jsonObject.getString("priority") != null && !Objects.equals(jsonObject.getString("priority"), "")) {
            query = query + " AND priority IN (" + jsonObject.getString("priority") + ")";
        }
        params.put("pageSize", 1000);
        params.put("tql", query);
        params.put("pageToken", "");
        if (jsonObject.getString("orderBy") != null) {
            if (Objects.equals(jsonObject.getString("orderBy"), "")) {
                params.put("orderBy", "dueDate");
            } else {
                params.put("orderBy", jsonObject.getString("orderBy"));
            }
        }
        System.out.println(params);
        JSONObject result = sendPost(url, headers, params);
        return result;
    }


    /**
     * 获取任务列表名称
     *
     * @param
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, String> TaskListName(String projectId, Map<String, String> headerMap) throws Exception {
        String url = tbConfig.getTBApiUrl() + "/tasklist/query";
        Map<String, String> resMap = new HashMap<>();
        Map<String, Object> requestParam = new HashMap<>();
        requestParam.put("projectId", projectId);
        String result = HttpUtil.getInstance().doGet(url, requestParam, headerMap);
        JSONObject jsonResult = JSONObject.parseObject(result);

        if (jsonResult.getInteger("code") != 200) {
            return resMap;
        }
        JSONArray array = jsonResult.getJSONArray("result");

        for (int i = 0; i < array.size(); i++) {
            JSONObject object = (JSONObject) array.get(i);    //将array中的数据进行逐条转换
            resMap.put(object.getString("tasklistId"), object.getString("name"));
        }
        System.out.println(resMap);
        return resMap;

    }

    @Override
    public JSONObject getTaskGroup(Map<String, String> queryMap, Map<String, String> headerMap) throws Exception {
        String url = tbConfig.getTBApiUrl() + "/taskgroup/query";
        Map<String, Object> requestParam = new HashMap<>();
        if (queryMap.get("projectId") == null || !queryMap.get("projectId").isEmpty()) {
            requestParam.put("projectId", queryMap.get("projectId"));
        }
        if (queryMap.get("taskgroupId") == null || !queryMap.get("taskgroupId").isEmpty()) {
            requestParam.put("taskgroupId", queryMap.get("taskgroupId"));
        }
        String result = HttpUtil.getInstance().doGet(url, requestParam, headerMap);
        JSONObject jsonResult = JSONObject.parseObject(result);
        return jsonResult;
    }
    @Override
    public List<TaskInfo> taskList(List<TaskInfo> taskInfos){
        for (TaskInfo tinfo :taskInfos){
            if (tinfo.getChildren().size() > 0){
                List<TaskInfo> childs =  new ArrayList<>();
                for (TaskInfo child: tinfo.getChildren()){
                    if (child.getParentId() == null || child.getParentId().isEmpty()){
                        childs.add(child);
                    }
                }
                for (TaskInfo child:childs){
                    List<TaskInfo> level3Child = new ArrayList<>();
                    for (TaskInfo ch :tinfo.getChildren()){
                        if (ch.getParentId() != null && !ch.getParentId().isEmpty()){
                            if (Objects.equals(ch.getParentId(),child.getTaskId())){
                                ch.setLevel(3);
                                ch.setChildren(new ArrayList<>());
                                level3Child.add(ch);
//                                System.out.println(ch.getTaskId());
                            }
                        }
                    }
                    child.setChildren(level3Child);
                }
                tinfo.setChildren(childs);
            }
        }
        return taskInfos;
    }

    @Override
    public List<TaskInfo> taskGroupList(List<TaskInfo> taskInfos){
        Map<String,List<TaskInfo>>  stringListMap = new HashMap<>();
        for (TaskInfo tinfo :taskInfos){
            if (tinfo.getChildren().size() > 0){
                List<TaskInfo> childs =  new ArrayList<>();
                for (TaskInfo child: tinfo.getChildren()){
                    if (child.getParentId() == null || child.getParentId().isEmpty()){
                        childs.add(child);
                    }
                }
                for (TaskInfo child:childs){
                    List<TaskInfo> level3Child = new ArrayList<>();
                    for (TaskInfo ch :tinfo.getChildren()){
                        if (ch.getParentId() != null && !ch.getParentId().isEmpty()){
                            if (Objects.equals(ch.getParentId(),child.getTaskId())){
                                ch.setLevel(3);
                                ch.setChildren(new ArrayList<>());
                                level3Child.add(ch);
//                                System.out.println(ch.getTaskId());
                            }
                        }
                    }
                    child.setChildren(level3Child);
                }
                tinfo.setChildren(childs);
            }
        }
        return taskInfos;
    }


}
