package com.spacex.tb.controller;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.spacex.tb.config.TbConfig;
import com.spacex.tb.parm.Headers;
import com.spacex.tb.parm.Parms;
import com.spacex.tb.parm.Task;
import com.spacex.tb.service.AccessTokenService;
import com.spacex.tb.service.TeamService;
import com.spacex.tb.util.HttpUtil;
import com.spacex.tb.util.JsonResult;
import org.apache.http.Header;
import org.omg.CORBA.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
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
        headers.add("Authorization","Bearer "+ accessTokenService.appAccessToken());
        Map<String, Object> requestParam = new HashMap<>();
        requestParam.put("grantType","authorizationCode");
        requestParam.put("code",jsonObject.getString("code"));
        requestParam.put("expires",86400);
        String result =  teamService.sendPost(url,headers,requestParam);

        JSONObject json = JSONObject.parseObject(result);
        return json;
    }

    @RequestMapping(value="/getAppToken",method = RequestMethod.POST)
    public String appToken() {
        return accessTokenService.appAccessToken();
    }

    @RequestMapping(value="/getuserinfo",method = RequestMethod.POST)
    public JSONObject getuserinfo(@RequestBody JSONObject jsonObject){
        String url=tbConfig.getTBApiUrl()+"/oauth/userInfo";
        String userAccessToken = jsonObject.getString("userAccessToken");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization",accessTokenService.appAccessToken());

        Map<String, Object> requestParam = new HashMap<>();
        requestParam.put("userAccessToken",userAccessToken);
        String result =  teamService.sendPost(url,headers,requestParam);

        JSONObject json = JSONObject.parseObject(result);
        return json;
    }

    @RequestMapping(value="/getcompanyinfo",method = RequestMethod.POST)
    public String companyInfo(@RequestBody JSONObject jsonObject){
        String XTenantId = request.getHeader("XTenantId");

        String url= tbConfig.getTBApiUrl()+"/api/org/info?orgId="+XTenantId;


        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(getHeader());
        ResponseEntity<String> response = restTemplate.exchange(url,HttpMethod.GET,entity,String.class);
        String json = response.getBody();
        return json;
    }




    @RequestMapping(value="/getprojectlist",method = RequestMethod.POST)
    public String getprojectlist(@RequestBody JSONObject jsonObject){
        String url="https://open.teambition.com/api/project/query";
        String appAccessToken = jsonObject.getString("appAccessToken");


        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> requestParam = new HashMap<>();
        requestParam.put("userAccessToken",accessTokenService.appAccessToken());
        HttpEntity<Map<String, Object>> request = new HttpEntity<Map<String, Object>>(requestParam, getHeader());
        ResponseEntity<String> response = restTemplate.exchange(url,HttpMethod.POST,request,String.class);
        String json = response.getBody();
        return json;
    }
    // 获取任务
    @RequestMapping(value="/getTask",method = RequestMethod.POST)
    public String gettask(@RequestBody JSONObject jsonObject){

        String url = tbConfig.getTBApiUrl()+"/api/task/query";

        Map<String, Object> requestParam = new HashMap<>();
        requestParam.put("userAccessToken","eyJhbGciOiJFZDI1NTE5IiwidHlwIjoiSldUIn0.eyJhcHAiOiI1ZTk1MzM2Nzk2MDM0Y2MyYzczYmVlMTMiLCJhdWQiOiIiLCJleHAiOjE1ODcwMTExODYsImlhdCI6MTU4NjkyNDc4NiwiaXNzIjoidHdzIiwianRpIjoibzZoc1lUSFVhZHQyZXNQRXdpQ3R6alViWUV0dW1LSk5iQXZnU01DXzNwVT0iLCJyZW5ld2VkIjoxNTc4ODk2NzE0Nzg4LCJzY3AiOlsiYXBwX3VzZXIiLCJlbWFpbCIsInBob25lIiwiYmlydGhkYXkiLCJsb2NhdGlvbiJdLCJzdWIiOiI1ZTFjMGQ0YWNjMDdmYzAwMDE5M2VhMzYiLCJ0eXAiOiJhY2Nlc3NfdG9rZW4ifQ.SNfT2d8NFlC676yms3Ybndf7ukv_ccjt3T2CgAGswwjikNOJDv4ZeI_cIFTyraaHdzOEmNFzLZx57CQBQtkRDQ");
        String json =  teamService.sendPost(url,getHeader(),requestParam);
        return json;
    }

    @RequestMapping(value="/gettasklist",method = RequestMethod.POST)
    public String getTaskList(@RequestBody JSONObject jsonObject) throws Exception {
        String url=tbConfig.getTBApiUrl()+ "/task/query";
        System.out.println(jsonObject.getString("tasklistId"));
        Map requestParam = JSONObject.parseObject(jsonObject.toJSONString());
        String json = HttpUtil.getInstance().doGet(url);
        return json;
    }


    @RequestMapping(value="/getbaidu",method = RequestMethod.POST)
    public String getbaidu(@RequestBody JSONObject jsonObject) throws Exception {
        String url="zhihu.com";
        System.out.println(url);
        Map requestParam = JSONObject.parseObject(jsonObject.toJSONString());
        String json = HttpUtil.getInstance().doGet(url);
        return json;
    }

    /**
     * 获取任务分组信息
     * @param jsonObject
     * @return
     */
    @RequestMapping(value="/gettaskgroup",method = RequestMethod.POST)
    public String gettaskgroup(@RequestBody JSONObject jsonObject){
        String projectId = jsonObject.getString("projectId");
        String url= tbConfig.getTBApiUrl() + "/taskgroup/query?projectId="+projectId;
        Map<String, Object> requestParam = new HashMap<>();
        requestParam.put("userAccessToken",jsonObject.getString("userAccessToken"));
        String json =  teamService.sendPost(url,getHeader(),requestParam);
        return json;
    }

    /**
     * 创建任务
     */
    @RequestMapping(value="/create/task",method = RequestMethod.POST)
    public String createTask(@RequestBody JSONObject jsonObject){
        String url= tbConfig.getTBApiUrl() + "task/create";
        Map requestParam = JSONObject.parseObject(jsonObject.toJSONString());
        String json =  teamService.sendPost(url,getHeader(),requestParam);
        return json;
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

