package com.spacex.tb.controller;

import java.util.*;


import com.alibaba.fastjson.JSONArray;
import com.spacex.tb.config.TbConfig;
import com.spacex.tb.parm.TaskInfo;
import com.spacex.tb.service.AccessTokenService;
import com.spacex.tb.service.TeamService;
import com.spacex.tb.util.HttpUtil;
import com.spacex.tb.util.JsonResult;
import com.spacex.tb.util.JsonUtils;
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
        String result =  teamService.sendPost(url,headers,requestParam);
        JSONObject jsonResult = JSONObject.parseObject(result);
        return jsonResult;
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
        String result =  teamService.sendPost(url,headers,requestParam);
        JSONObject jsonResult = JSONObject.parseObject(result);
        return jsonResult;
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
    public JsonResult getprojectlist(@RequestBody JSONObject jsonObject) throws Exception {
        String url=tbConfig.getTBApiUrl()+ "/project/query";
        Map params = JSONObject.parseObject(jsonObject.toJSONString());
        String result = teamService.sendPost(url,getHeader(),params) ;
        JSONObject jsonResult = JSONObject.parseObject(result);
        return JsonResult.success(jsonResult);
//        return JsonResult.success(JsonUtils.object2Json(JsonUtils.convertUtcJson2Obj(result,Map.class)));
    }
    // 获取任务
    @RequestMapping(value="/getTask",method = RequestMethod.POST)
    public JsonResult getTask(@RequestBody JSONObject jsonObject) throws Exception {
        String url = tbConfig.getTBApiUrl()+"/task/query";
        Map requestParam = JSONObject.parseObject(jsonObject.toJSONString());
        String result = HttpUtil.getInstance().doGet(url,requestParam,headerMap());
        JSONObject jsonResult = JSONObject.parseObject(result);

        if (jsonResult.getInteger("code") != 200){
            return JsonResult.success(jsonResult);
        }
        JSONArray array = jsonResult.getJSONArray("result");
        System.out.println(array);
        List<TaskInfo>  taskInfos = new ArrayList<>();
        Map<String,String> userList = teamService.getUserList(headerMap(),request.getHeader("X-Tenant-Id"));
        for (int i = 0; i < array.size(); i++) {
            JSONObject object = (JSONObject) array.get(i);    //将array中的数据进行逐条转换
            System.out.println("-------");

            System.out.println(object.getString("taskgroupId"));
            String taskgroupName = teamService.getTaskGroupNameById(headerMap(),object.getString("taskgroupId"));

            TaskInfo task = (TaskInfo) JSONObject.toJavaObject(object,TaskInfo.class) ;//通过JSONObject.toBean()方法进行对象间的转换
            task.setTaskgroupName(taskgroupName);
            task.setUserName(userList.get(object.getString("creatorId")));
            taskInfos.add(task);
        }
        return JsonResult.success(taskInfos);
    }

    /**
     * 获取任务列表
     * @param jsonObject
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/gettasklist",method = RequestMethod.POST)
    public JsonResult  getTaskList(@RequestBody JSONObject jsonObject) throws Exception {
//        String url=tbConfig.getTBApiUrl()+ "/tasklist/query";
//        Map requestParam = JSONObject.parseObject(jsonObject.toJSONString());
//        String result = HttpUtil.getInstance().doGet(url,requestParam,headerMap());
//        JSONObject jsonResult = JSONObject.parseObject(result);
//        return jsonResult;

        Map<String,String> user = teamService.getUserList(headerMap(),jsonObject.getString("orgId"));
        return  JsonResult.success(user);
    }


    /**
     * 获取任务分组信息
     * @param jsonObject
     * @return
     */
    @RequestMapping(value="/gettaskgroup",method = RequestMethod.POST)
    public JSONObject gettaskgroup(@RequestBody JSONObject jsonObject) throws Exception {
        String url= tbConfig.getTBApiUrl() + "/taskgroup/query";
        Map<String, Object> requestParam = new HashMap<>();
        requestParam.put("projectId",jsonObject.getString("projectId"));
        String result = HttpUtil.getInstance().doGet(url,requestParam,headerMap());
        JSONObject jsonResult = JSONObject.parseObject(result);
        return jsonResult;
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
        String result =  teamService.sendPost(url,getHeader(),requestParam);
        JSONObject jsonResult = JSONObject.parseObject(result);
        return jsonResult;
    }

    /**
     * 任务编辑
     * @return
     */

    @RequestMapping(value="/update/task",method = RequestMethod.POST)
    public JSONObject updateTask(@RequestBody JSONObject jsonObject) throws Exception {
        String url= tbConfig.getTBApiUrl() + "task/update";
        Map params = JSONObject.parseObject(jsonObject.toJSONString());
        String result = teamService.sendPost(url,getHeader(),params);
        JSONObject jsonResult = JSONObject.parseObject(result);
        return jsonResult;
    }
    /**
     * 任务删除
     * @return
     */

    @RequestMapping(value="/task/del",method = RequestMethod.POST)
    public JSONObject delTask(@RequestBody JSONObject jsonObject) throws Exception {
        String url= tbConfig.getTBApiUrl() + "task/update";
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

