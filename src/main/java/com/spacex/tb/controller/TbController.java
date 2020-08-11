package com.spacex.tb.controller;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.spacex.tb.config.TbConfig;
import com.spacex.tb.service.AccessTokenService;
import com.spacex.tb.service.TeamService;
import com.spacex.tb.util.HttpUtil;
import com.spacex.tb.util.JsonResult;
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

    @RequestMapping(value="/getUserToken",method = RequestMethod.POST)
    public JsonResult userToken(@RequestBody JSONObject jsonObject) {
        String url = tbConfig.getTBApiUrl()+"/oauth/userAccessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));
        Map requestParam = JSONObject.parseObject(jsonObject.toJSONString());
        String result =  teamService.sendPost(url,headers,requestParam);

        return JsonResult.success(result);
    }

    @RequestMapping(value="/getAppToken",method = RequestMethod.POST)
    public JsonResult appToken() {
        Map<String,String> map = new HashMap<>();
        map.put("appToken",accessTokenService.appAccessToken());
        return JsonResult.success(map);
    }

    @RequestMapping(value="/getuserinfo",method = RequestMethod.POST)
    public JsonResult getuserinfo(@RequestBody JSONObject jsonObject){
        String url=tbConfig.getTBApiUrl()+"/oauth/userInfo";
        String userAccessToken = jsonObject.getString("userAccessToken");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization",request.getHeader("Authorization"));
        Map<String, Object> requestParam = new HashMap<>();
        requestParam.put("userAccessToken",userAccessToken);
        String result =  teamService.sendPost(url,headers,requestParam);
        return JsonResult.success(result);
    }

    @RequestMapping(value="/getcompanyinfo",method = RequestMethod.POST)
    public JsonResult companyInfo(@RequestBody JSONObject jsonObject){
        String XTenantId = request.getHeader("XTenantId");
        String url= tbConfig.getTBApiUrl()+"/api/org/info?orgId="+XTenantId;
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(getHeader());
        ResponseEntity<String> response = restTemplate.exchange(url,HttpMethod.GET,entity,String.class);
        String result = response.getBody();
        return JsonResult.success(result);
    }




    @RequestMapping(value="/getprojectlist",method = RequestMethod.POST)
    public JsonResult getprojectlist(@RequestBody JSONObject jsonObject) throws Exception {
        String url=tbConfig.getTBApiUrl()+ "/project/query";
        Map params = JSONObject.parseObject(jsonObject.toJSONString());
        String result = HttpUtil.getInstance().doGet(url,params,headerMap());
        return JsonResult.success(result);
    }
    // 获取任务
    @RequestMapping(value="/getTask",method = RequestMethod.POST)
    public JsonResult getTask(@RequestBody JSONObject jsonObject){
        String url = tbConfig.getTBApiUrl()+"/api/task/query";
        Map<String, Object> requestParam = new HashMap<>();
        requestParam.put("userAccessToken",jsonObject.getString("userAccessToken"));
        String result =  teamService.sendPost(url,getHeader(),requestParam);
        return JsonResult.success(result);
    }

    /**
     * 获取任务列表
     * @param jsonObject
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/gettasklist",method = RequestMethod.POST)
    public JsonResult getTaskList(@RequestBody JSONObject jsonObject) throws Exception {
        String url=tbConfig.getTBApiUrl()+ "/task/query";
        System.out.println(jsonObject.getString("tasklistId"));
        Map requestParam = JSONObject.parseObject(jsonObject.toJSONString());
        String result = HttpUtil.getInstance().doGet(url,requestParam,headerMap());
        return JsonResult.success(result);
    }


    /**
     * 获取任务分组信息
     * @param jsonObject
     * @return
     */
    @RequestMapping(value="/gettaskgroup",method = RequestMethod.POST)
    public JsonResult gettaskgroup(@RequestBody JSONObject jsonObject) throws Exception {
        String url= tbConfig.getTBApiUrl() + "/taskgroup/query";
        Map<String, Object> requestParam = new HashMap<>();
        requestParam.put("projectId",jsonObject.getString("projectId"));
        String result = HttpUtil.getInstance().doGet(url,requestParam,headerMap());
        return JsonResult.success(result);
    }

    /**
     * 创建任务
     */
    @RequestMapping(value="/task/create",method = RequestMethod.POST)
    public JsonResult createTask(@RequestBody JSONObject jsonObject){
        String url= tbConfig.getTBApiUrl() + "task/create";
        Map requestParam = JSONObject.parseObject(jsonObject.toJSONString());
        String result =  teamService.sendPost(url,getHeader(),requestParam);
        return JsonResult.success(result);
    }

    /**
     * 任务编辑
     * @return
     */

    @RequestMapping(value="/update/task",method = RequestMethod.POST)
    public JsonResult updateTask(@RequestBody JSONObject jsonObject) throws Exception {
        String url= tbConfig.getTBApiUrl() + "task/update";
        Map params = JSONObject.parseObject(jsonObject.toJSONString());
        String result = teamService.sendPost(url,getHeader(),params);
        return JsonResult.success(result);
    }
    /**
     * 任务删除
     * @return
     */

    @RequestMapping(value="/task/del",method = RequestMethod.POST)
    public JsonResult delTask(@RequestBody JSONObject jsonObject) throws Exception {
        String url= tbConfig.getTBApiUrl() + "task/update";
        Map params = JSONObject.parseObject(jsonObject.toJSONString());
        String result = HttpUtil.getInstance().doGet(url,params,headerMap());
        return JsonResult.success(result);
    }

    private HttpHeaders getHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization",request.getHeader("Authorization"));
        headers.add("X-Tenant-Id",request.getHeader("X-Tenant-Id"));
        headers.add("X-Tenant-Type",request.getHeader("X-Tenant-Type"));
        return headers;
    }

    private Map<String, String> headerMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("Authorization",request.getHeader("Authorization"));
        map.put("X-Tenant-Id",request.getHeader("X-Tenant-Id"));
        map.put("X-Tenant-Type",request.getHeader("X-Tenant-Type"));
        return  map;
    }
}

