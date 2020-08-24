package com.spacex.tb.controller;

import java.awt.*;
import java.util.*;
import java.util.List;


import com.alibaba.fastjson.JSONArray;
import com.spacex.tb.config.TbConfig;
import com.spacex.tb.parm.TaskDetail;
import com.spacex.tb.parm.TaskInfo;
import com.spacex.tb.service.AccessTokenService;
import com.spacex.tb.service.TeamService;
import com.spacex.tb.util.HttpUtil;
import com.spacex.tb.util.JsonResult;
import com.spacex.tb.util.JsonUtils;
import com.spacex.tb.util.StringUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.alibaba.fastjson.JSONObject;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


@RestController
@ResponseBody
@RequestMapping(value = "/api/")
public class TbController {

    @Autowired
    private HttpServletRequest request;

    @Resource
    private TbConfig tbConfig;

    @Resource
    private TeamService teamService;
    @Resource
    private AccessTokenService accessTokenService;



    /**
     * 获取用户token
     * @param jsonObject
     * @return
     */
    @RequestMapping(value="/getUserToken",method = RequestMethod.POST)
    public JSONObject userToken(@RequestBody JSONObject jsonObject) {
        String url = tbConfig.getTBApiUrl()+"/oauth/userAccessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));
        Map requestParam = JSONObject.parseObject(jsonObject.toJSONString());
        requestParam.put("grantType","authorizationCode");
        requestParam.put("expires",86400);
        JSONObject result =  teamService.sendPost(url,headers,requestParam);

        return result;
    }

    /**
     * 获取应用appToken
     * @return
     */
    @RequestMapping(value="/getAppToken",method = RequestMethod.POST)
    public JSONObject appToken() {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("appToken",accessTokenService.appAccessToken());
        map.put("code",200);
        map.put("errorMessage","");

        JSONObject jsonObj = new JSONObject(map);
        return jsonObj;
    }

    /**
     * 获取用户信息
     * @param jsonObject
     * @return
     */
    @RequestMapping(value="/getuserinfo",method = RequestMethod.POST)
    public JSONObject getuserinfo(@RequestBody JSONObject jsonObject){
        String url=tbConfig.getTBApiUrl()+"/oauth/userInfo";
        String userAccessToken = jsonObject.getString("userAccessToken");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization",request.getHeader("Authorization"));
        Map<String, Object> requestParam = new HashMap<>();
        requestParam.put("userAccessToken",userAccessToken);
        JSONObject result =  teamService.sendPost(url,headers,requestParam);

        return result;
    }

    /**
     * 获取公司信息
     * @param jsonObject
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/getcompanyinfo",method = RequestMethod.POST)
    public JSONObject companyInfo(@RequestBody JSONObject jsonObject) throws Exception {

        String url= tbConfig.getTBApiUrl()+"/org/info";
        System.out.println(headerMap());
        Map params = JSONObject.parseObject(jsonObject.toJSONString());
        String result = HttpUtil.getInstance().doGet(url,params,headerMap());
        JSONObject jsonResult = JSONObject.parseObject(result);
        return jsonResult;
    }

    /**
     * 获取项目列表
     * @param "projectIds:[]"
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/getprojectlist",method = RequestMethod.POST)
    public JSONObject getprojectlist(@RequestBody JSONObject jsonObject) throws Exception {
        String url=tbConfig.getTBApiUrl()+ "/project/query";
        Map params = JSONObject.parseObject(jsonObject.toJSONString());
        JSONObject result = teamService.sendPost(url,getHeader(),params) ;
        return result;
//        return JsonResult.success(JsonUtils.object2Json(JsonUtils.convertUtcJson2Obj(result,Map.class)));
    }
    // 获取任务
    @RequestMapping(value="/getTask",method = RequestMethod.POST)
    public JsonResult getTask(@RequestBody JSONObject jsonObject) throws Exception {
        String orgId = request.getHeader("X-Tenant-Id");
        String url = tbConfig.getTBApiUrl()+"/task/query";
        Map requestParam = JSONObject.parseObject(jsonObject.toJSONString());
        String result = HttpUtil.getInstance().doGet(url,requestParam,headerMap());
        JSONObject jsonResult = JSONObject.parseObject(result);

        if (jsonResult.getInteger("code") != 200){
            return JsonResult.fail(jsonResult.getInteger("code"),jsonResult.getString("errorMessage"));
        }
        JSONArray array = jsonResult.getJSONArray("result");
        System.out.println(array);
        List<TaskInfo>  taskInfos = new ArrayList<>();
        Map<String,String> userList = teamService.getUserList(headerMap(),orgId,false);
        for (int i = 0; i < array.size(); i++) {
            JSONObject object = (JSONObject) array.get(i);    //将array中的数据进行逐条转换
            String taskgroupName = teamService.getTaskGroupNameById(headerMap(),object.getString("taskgroupId"));
            TaskInfo task = (TaskInfo) JSONObject.toJavaObject(object,TaskInfo.class) ;//通过JSONObject.toBean()方法进行对象间的转换
            taskInfos.add(task);
        }
        return JsonResult.success(taskInfos);
    }

    /**
     * 查询任务
     * @param jsonObject
     * projectId  项目ID
     * startDate 开始时间
     * dueDate 结束时间
     * taskgroupId 任务组ID
     * isDone 是否完成
     * executorId 执行人ID 多个id 逗号分割
     * priority -10 较低 0 普通 1 紧急 2 非常紧急 多个用逗号隔开
     * involveMember 参与用户id 多个用逗号隔开
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/get/task/list",method = RequestMethod.POST)
    public JsonResult  taskList(@RequestBody JSONObject jsonObject) throws Exception {
        long groupType = 1;
        if( jsonObject.getInteger("is_group") != null && jsonObject.getInteger("is_group") == 0){
            groupType = 0 ;
        }else{
            groupType = 1;
        }
        List<TaskInfo> taskInfos = new ArrayList<>();
        Map<String,List<TaskInfo>> taskListMap = new HashMap<>();
        String orgId = request.getHeader("X-Tenant-Id");
        String projectId = jsonObject.getString("projectId");
        // 获取任务分组
        Map<String,String> queryParams = new HashMap<>();
        queryParams.put("projectId",projectId);
        JSONObject taskGroup = teamService.getTaskGroup(queryParams,headerMap());
        Map<String,String>  userList = teamService.getUserList(headerMap(),orgId,false);
        Map<String,String> taskListName = teamService.TaskListName(projectId,headerMap());
        if (taskGroup.getInteger("code") == 200){
            JSONArray groupArr = taskGroup.getJSONArray("result");
            for (int i = 0; i < groupArr.size(); i++) {
                TaskDetail group = new TaskDetail();
                JSONObject tGroup =  (JSONObject) groupArr.get(i);
//                System.out.println("fenzu--------------"+ tGroup.getString("taskgroupId"));
                TaskInfo groupInfo = new TaskInfo();
                jsonObject.put("taskgroupId",tGroup.getString("taskgroupId"));
                // 获取任务列表
                List<TaskInfo> taskInfoList = new ArrayList<>();



                long tasksSize = 0;
                if(groupType == 1) {
                    Date startTime = null;
                    Date endTime = null;
                    JSONObject taskList1 = teamService.getTaskList(getHeader(),jsonObject);
                    if (taskList1.getInteger("code") != 200){
                        return JsonResult.fail(taskList1.getInteger("code"),taskList1.getString("errorMessage"));
                    }
                    JSONArray tasks = taskList1.getJSONArray("result");
                    // System.out.println(tasks);
                    // 获取任务

                    for (int ti = 0; ti < tasks.size(); ti++) {

                        JSONObject obj = (JSONObject) tasks.get(ti);    //将array中的数据进行逐条转换
                        //                    System.out.println(obj);
                        obj.put("taskListName",taskListName.get(obj.getString("tasklistId")));
//                        System.out.println("------"+taskListName.get(obj.getString("tasklistId")));
                        TaskDetail taskDetail = StringUtil.setTaskDetail(obj);

                        if (obj.getString("executorId") == null || Objects.equals(obj.getString("executorId"),"") ){
                            taskDetail.setExecutorName("待分配");
                        }else{
                            taskDetail.setExecutorName(userList.get(obj.getString("executorId")));
                        }
//                        System.out.println(obj);
                        taskInfoList.add(StringUtil.setTaskInfo(obj, taskDetail));
                        // 获取分组内最小时间以及最大时间

                        if (obj.getDate("startDate") != null) {
                            if (startTime == null) {
                                if (obj.getDate("startDate") != null){
                                    startTime = obj.getDate("startDate");
                                }
                            } else {

                                if (!startTime.before(obj.getDate("startDate"))) {
                                    startTime = obj.getDate("startDate");
                                }
                            }
                        }
                        if (obj.getDate("dueDate") != null){
                            if (endTime == null){
                                if (obj.getDate("dueDate") != null){
                                    endTime = obj.getDate("dueDate");
                                }
                            }else{
                                if(endTime.before(obj.getDate("dueDate"))){
                                    endTime = obj.getDate("dueDate");
                                }
                            }
                        }
                    }
                    tasksSize = tasks.size();
                    System.out.println("======");
                    group  =  group.builder()
                            .biaoti(tGroup.getString("description"))
                            .title(tGroup.getString("name")+" · "+tasksSize).build();
                    groupInfo = groupInfo.builder().taskId(tGroup.getString("taskgroupId"))
                            .children(taskInfoList)
                            .is_group(1)
                            .start_time(StringUtil.timeToStamp(startTime))
                            .end_time(StringUtil.timeToStamp(endTime))
                            .taskgroupId(tGroup.getString("taskgroupId"))
                            .startTime(startTime)
                            .endTime(endTime)
                            .level(1)
                            .params(group).build();
                    taskInfos.add(groupInfo);
                }else {

                    JSONArray tasklistArray = tGroup.getJSONArray("tasklistIds");
                    for (int listNum =0 ;listNum< tasklistArray.size();listNum++) {
                        Date startTime = null;
                        Date endTime = null;
                        List<TaskInfo> taskInfoList1 = new ArrayList<>();

                        String TListName = taskListName.get(tasklistArray.get(listNum));
                        jsonObject.put("tasklistId",tasklistArray.get(listNum));
                        JSONObject taskList1 = teamService.getTaskList(getHeader(), jsonObject);

                        System.out.println(taskList1);
                        if (taskList1.getInteger("code") != 200) {
                            return JsonResult.fail(taskList1.getInteger("code"), taskList1.getString("errorMessage"));
                        }
                        JSONArray tasks = taskList1.getJSONArray("result");
                        // System.out.println(tasks);
                        // 获取任务
                        if(tasks.size() > 0){
                            for (int ti = 0; ti < tasks.size(); ti++) {
                                JSONObject obj = (JSONObject) tasks.get(ti);    //将array中的数据进行逐条转换
                                //                    System.out.println(obj);
                                obj.put("taskListName", taskListName.get(obj.getString("tasklistId")));
                                //                    System.out.println("------"+taskListName.get(obj.getString("tasklistId")));
                                TaskDetail taskDetail = StringUtil.setTaskDetail(obj);

                                if (obj.getString("executorId") == null || Objects.equals(obj.getString("executorId"), "")) {
                                    taskDetail.setExecutorName("待分配");
                                } else {
                                    taskDetail.setExecutorName(userList.get(obj.getString("executorId")));
                                }
                                //                    System.out.println(taskDetail.getTitle());
                                taskInfoList1.add(StringUtil.setTaskInfo(obj, taskDetail));
                                // 获取分组内最小时间以及最大时间
                                if (obj.getDate("startDate") != null) {
                                    if (startTime == null) {
                                        if (obj.getDate("startDate") != null) {
                                            startTime = obj.getDate("startDate");
                                        }
                                    } else {

                                        if (!startTime.before(obj.getDate("startDate"))) {
                                            startTime = obj.getDate("startDate");
                                        }
                                    }
                                }
                                if (obj.getDate("dueDate") != null) {
                                    if (endTime == null) {
                                        if (obj.getDate("dueDate") != null) {
                                            endTime = obj.getDate("dueDate");
                                        }

                                    } else {
                                        if (endTime.before(obj.getDate("dueDate"))) {
                                            endTime = obj.getDate("dueDate");
                                        }
                                    }
                                }
                            }
                        }

                        tasksSize = tasks.size();
                        group = group.builder()
                                .biaoti(tGroup.getString("description"))
                                .title(tGroup.getString("name") + " · " + TListName +" · " + tasksSize)
                                .build();
                        groupInfo = groupInfo.builder()
                                .taskId(tasklistArray.get(listNum).toString())
                                .children(taskInfoList1)
                                .taskgroupId(tGroup.getString("taskgroupId"))
                                .tasklistId(tasklistArray.get(listNum).toString())
                                .is_group(1)
                                .start_time(StringUtil.timeToStamp(startTime))
                                .end_time(StringUtil.timeToStamp(endTime))
                                .startTime(startTime)
                                .endTime(endTime)
                                .level(1)
                                .params(group).build();
                        taskInfos.add(groupInfo);
                    }


                }


            }
        }else{
            return JsonResult.fail(500,taskGroup.getString("errorMessage"));
        }

        taskInfos = teamService.taskList(taskInfos);
        return JsonResult.success(taskInfos);


    }


    /**
     * 获取用户信息
     * @param jsonObject
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/getUserList",method = RequestMethod.POST)
    public JsonResult getUserList(@RequestBody JSONObject jsonObject) throws Exception {
       Map<String,String> users = teamService.getUserList(headerMap(),request.getHeader("X-Tenant-Id"),true);
        return JsonResult.success(users);
    }


    /**
     * 获取任务列表
     * @param jsonObject
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/gettasklist",method = RequestMethod.POST)
    public JSONObject  getTaskList(@RequestBody JSONObject jsonObject) throws Exception {
        String url=tbConfig.getTBApiUrl()+ "/tasklist/query";
        Map requestParam = JSONObject.parseObject(jsonObject.toJSONString());
        String result = HttpUtil.getInstance().doGet(url,requestParam,headerMap());
        JSONObject jsonResult = JSONObject.parseObject(result);
        return jsonResult;
    }


    /**
     * 获取任务分组信息
     * @param jsonObject
     * @return
     */
    @RequestMapping(value="/gettaskgroup",method = RequestMethod.POST)
    public JSONObject gettaskgroup(@RequestBody JSONObject jsonObject) throws Exception {
        Map jsonToMap =  JSONObject.parseObject(jsonObject.toJSONString());
        JSONObject taskGroup = teamService.getTaskGroup(jsonToMap,headerMap());
        return taskGroup;
    }

    /**
     * 查询任务类型
     * @param jsonObject
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/gettemplate",method = RequestMethod.POST)
    public JSONObject gettemplate(@RequestBody JSONObject jsonObject) throws Exception {
        String url= tbConfig.getTBApiUrl() + "/template/query";

        Map requestParam = JSONObject.parseObject(jsonObject.toJSONString());
        String result = HttpUtil.getInstance().doGet(url,requestParam,headerMap());
        JSONObject jsonResult = JSONObject.parseObject(result);
        return jsonResult;
    }


    /**
     * 创建任务
     */
    @RequestMapping(value="/task/create",method = RequestMethod.POST)
    public JsonResult createTask(@RequestBody JSONObject jsonObject) throws Exception {
        String orgId = request.getHeader("X-Tenant-Id");
        String url= tbConfig.getTBApiUrl() + "task/create";
        Map params = JSONObject.parseObject(jsonObject.toJSONString());
        if (jsonObject.getString("startDate") != null){
            params.put("startDate", StringUtil.string2Utc(jsonObject.getString("startDate")));
        }
        if (jsonObject.getString("dueDate") != null) {
            params.put("dueDate", StringUtil.string2Utc(jsonObject.getString("dueDate")));
        }
        JSONObject result =  teamService.sendPost(url,getHeader(),params);
        if (result.getInteger("code") !=200){
//            System.out.println(jsonResult);
            return JsonResult.fail(result.getInteger("code"),result.getString("errorMessage"));
        }
        Map<String,String> userList = teamService.getUserList(headerMap(),orgId,false);
        JSONObject object = result.getJSONObject("result");
        TaskDetail taskDetail = StringUtil.setTaskDetail(object);
        taskDetail.setExecutorName(userList.get("executorId"));
        TaskInfo taskInfo = StringUtil.setTaskInfo(object,taskDetail);

        return JsonResult.success(taskInfo);
    }

    /**
     * 任务编辑
     * @return
     */

    @RequestMapping(value="/update/task",method = RequestMethod.POST)
    public JsonResult updateTask(@RequestBody JSONObject jsonObject) throws Exception {
        TaskInfo taskInfo = new TaskInfo();
        String orgId = request.getHeader("X-Tenant-Id");
        String url= tbConfig.getTBApiUrl() + "task/update";
        Map<String,Object> params = JSONObject.parseObject(jsonObject.toJSONString());
//        Map<String,Object> params = new HashMap<>();
        if (jsonObject.getString("startDate") != null){
            params.put("startDate", StringUtil.string2Utc(jsonObject.getString("startDate")));
        }
        if (jsonObject.getString("dueDate") != null) {
            params.put("dueDate", StringUtil.string2Utc(jsonObject.getString("dueDate")));
        }
        System.out.println(params);
        JSONObject result = teamService.sendPost(url,getHeader(),params);
        if (result.getInteger("code") !=200){
//            System.out.println(jsonResult);
            return JsonResult.fail(result.getInteger("code"),result.getString("errorMessage"));
        }
        JSONObject object = result.getJSONObject("result");
        TaskDetail taskDetail = StringUtil.setTaskDetail(object);
        Map<String,String> userList = teamService.getUserList(headerMap(),orgId,false);
        if(!Objects.equals(object.getString("executorId"),"")){
            taskDetail.setExecutorName(userList.get(object.getString("executorId")));
        }else{
            taskDetail.setExecutorName("待领取");
        }

        taskInfo = StringUtil.setTaskInfo(object,taskDetail);
        return JsonResult.success(taskInfo);
    }

    /**
     * 获取项目成员信息
     * @param jsonObject
     * @return
     */
    @RequestMapping(value="/project/member",method = RequestMethod.POST)
    public JSONObject getProjectMember(@RequestBody JSONObject jsonObject) throws Exception {
        String url= tbConfig.getTBApiUrl() + "project/member/list";
        Map<String,Object> params = new HashMap<>();
        params.put("projectId",jsonObject.getString("projectId"));
        params.put("pageSize",1000);
        String result = HttpUtil.getInstance().doGet(url,params,headerMap());
        JSONObject jsonResult = JSONObject.parseObject(result);
        return jsonResult;
    }
    /**
     * 任务删除
     * @return
     */

    @RequestMapping(value="/task/del",method = RequestMethod.POST)
    public JSONObject delTask(@RequestBody JSONObject jsonObject) throws Exception {
        String url= tbConfig.getTBApiUrl() + "task/delete";
        Map params = JSONObject.parseObject(jsonObject.toJSONString());
        String result = HttpUtil.getInstance().doGet(url,params,headerMap());
        JSONObject jsonResult = JSONObject.parseObject(result);
        return jsonResult;
    }

    private HttpHeaders getHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization",request.getHeader("Authorization"));
        headers.add("X-Tenant-Id",request.getHeader("X-Tenant-Id"));
        headers.add("X-Tenant-Type","organization");
        return headers;
    }

    private Map<String, String> headerMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("Authorization",request.getHeader("Authorization"));
        map.put("X-Tenant-Id",request.getHeader("X-Tenant-Id"));
        map.put("X-Tenant-Type","organization");
        return  map;
    }


}

