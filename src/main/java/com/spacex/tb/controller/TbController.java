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
    public JSONObject userToken(@RequestBody JSONObject jsonObject) {
        String url = tbConfig.getTBApiUrl()+"/oauth/userAccessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));
        Map requestParam = JSONObject.parseObject(jsonObject.toJSONString());
        String result =  teamService.sendPost(url,headers,requestParam);
        JSONObject jsonResult = JSONObject.parseObject(result);
        return jsonResult;
    }

    @RequestMapping(value="/getAppToken",method = RequestMethod.POST)
    public JSONObject appToken() {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("appToken",accessTokenService.appAccessToken());
        map.put("code",200);
        map.put("errorMessage","");

        JSONObject jsonObj = new JSONObject(map);
        return jsonObj;
    }

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

    @RequestMapping(value="/getcompanyinfo",method = RequestMethod.POST)
    public JSONObject companyInfo(@RequestBody JSONObject jsonObject) throws Exception {

        String url= tbConfig.getTBApiUrl()+"/org/info";
        System.out.println(headerMap());
        Map params = JSONObject.parseObject(jsonObject.toJSONString());
        String result = HttpUtil.getInstance().doGet(url,params,headerMap());
        JSONObject jsonResult = JSONObject.parseObject(result);
        return jsonResult;
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
    public JSONObject getTask(@RequestBody JSONObject jsonObject){
        String url = tbConfig.getTBApiUrl()+"/api/task/query";
        Map<String, Object> requestParam = new HashMap<>();
        requestParam.put("userAccessToken",jsonObject.getString("userAccessToken"));
        String result =  teamService.sendPost(url,getHeader(),requestParam);
        JSONObject jsonResult = JSONObject.parseObject(result);
        return jsonResult;
    }

    /**
     * 获取任务列表
     * @param jsonObject
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/gettasklist",method = RequestMethod.POST)
    public JSONObject getTaskList(@RequestBody JSONObject jsonObject) throws Exception {
        String url=tbConfig.getTBApiUrl()+ "/task/query";
        System.out.println(jsonObject.getString("tasklistId"));
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
        String url= tbConfig.getTBApiUrl() + "/taskgroup/query";
        Map<String, Object> requestParam = new HashMap<>();
        requestParam.put("projectId",jsonObject.getString("projectId"));
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

