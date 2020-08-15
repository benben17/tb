package com.spacex.tb.controller;

import java.util.*;


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
        List<TaskInfo> taskInfos = new ArrayList<>();

        // 获取任务分组
        Map<String,String> queryParams = new HashMap<>();
        queryParams.put("projectId",jsonObject.getString("projectId"));
        JSONObject taskGroup = teamService.getTaskGroup(queryParams,headerMap());

        if (taskGroup.getInteger("code") == 200){
            JSONArray groupArr = taskGroup.getJSONArray("result");
            for (int i = 0; i < groupArr.size(); i++) {
                TaskDetail group = new TaskDetail();
                JSONObject tGroup =  (JSONObject) groupArr.get(i);
//                System.out.println("fenzu--------------"+ tGroup.getString("taskgroupId"));

                TaskInfo groupInfo = new TaskInfo();

                jsonObject.put("taskgroupId",tGroup.getString("taskgroupId"));
                // 获取任务列表
                JSONObject taskList1 = teamService.getTaskList(getHeader(),jsonObject);
                if (taskList1.getInteger("code") != 200){
                    return JsonResult.fail(taskList1.getInteger("code"),taskList1.getString("errorMessage"));
                }
                JSONArray tasks = taskList1.getJSONArray("result");
                System.out.println(tasks);
                // 获取任务
                List<TaskInfo> taskInfoList = new ArrayList<>();
                Date startTime = new Date();
                Date endTime = new Date();
                for (int ti = 0; ti < tasks.size(); ti++) {
                    JSONObject obj = (JSONObject) tasks.get(ti);    //将array中的数据进行逐条转换
//                    System.out.println(obj);
                    TaskDetail taskDetail = StringUtil.setTaskDetail(obj);
//                    System.out.println(taskDetail.getTitle());
                    taskInfoList.add(StringUtil.setTaskInfo(obj, taskDetail));
                    if (obj.getDate("startDate") != null) {
                        if (startTime == null) {
                            startTime = obj.getDate("startDate");

                        } else {
                            if (!startTime.before(obj.getDate("startDate"))) {
                                startTime = obj.getDate("startDate");
                            }
                        }
                     }
                    if (obj.getDate("dueDate") != null){
                        if (endTime == null){
                            endTime = obj.getDate("dueDate");
                        }else{
                            if(!endTime.before(obj.getDate("dueDate"))){
                                endTime = obj.getDate("dueDate");
                            }
                        }
                    }
                }
                group  =  group.builder()
                        .biaoti(tGroup.getString("description"))
                        .startTime(startTime)
                        .endTime(endTime)
                        .title(tGroup.getString("name")).build();
                groupInfo = groupInfo.builder().taskId(tGroup.getString("taskgroupId"))
                        .children(taskInfoList)
                        .start_time(startTime)
                        .end_time(endTime)
                        .level(1)
                        .params(group).build();
                taskInfos.add(groupInfo);
            }


        }else{
            return JsonResult.fail(500,taskGroup.getString("errorMessage"));
        }

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
                                level3Child.add(ch);
                                System.out.println(ch.getTaskId());
                            }
                        }
                    }
                    child.setChildren(level3Child);
                }
                tinfo.setChildren(childs);

            }

        }

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
    public JSONObject createTask(@RequestBody JSONObject jsonObject){
        String url= tbConfig.getTBApiUrl() + "task/create";
        Map requestParam = JSONObject.parseObject(jsonObject.toJSONString());
        requestParam.put("startDate", StringUtil.string2Utc(jsonObject.getString("startDate")));
        requestParam.put("dueDate", StringUtil.string2Utc(jsonObject.getString("dueDate")));
        JSONObject result =  teamService.sendPost(url,getHeader(),requestParam);

        return result;
    }

    /**
     * 任务编辑
     * @return
     */

    @RequestMapping(value="/update/task",method = RequestMethod.POST)
    public TaskInfo updateTask(@RequestBody JSONObject jsonObject) throws Exception {
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
            return taskInfo;
        }
        JSONObject object = result.getJSONObject("result");
        TaskDetail taskDetail = StringUtil.setTaskDetail(object);

        taskInfo = StringUtil.setTaskInfo(object,taskDetail);
        return taskInfo;
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

