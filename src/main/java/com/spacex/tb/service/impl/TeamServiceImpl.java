package com.spacex.tb.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.spacex.tb.config.TbConfig;
import com.spacex.tb.parm.Headers;
import com.spacex.tb.parm.Parms;
import com.spacex.tb.parm.TaskInfo;
import com.spacex.tb.service.AccessTokenService;
import com.spacex.tb.service.TeamService;
import com.spacex.tb.util.HttpUtil;
import com.spacex.tb.util.JsonResult;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Service
public class TeamServiceImpl implements TeamService {

    @Resource
    private TbConfig tbConfig;

    @Override
    public String sendPost(String url, HttpHeaders headers, Map<String, Object> requestParam ){
        String responseBody = null;
        // 构造消息头
        try {
            headers.add("Content-type", "application/json; charset=utf-8");
            RestTemplate restTemplate = new RestTemplate();

            HttpEntity<Map<String, Object>> request = new HttpEntity<Map<String, Object>>(requestParam, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST,request,String.class);
            responseBody = response.getBody();
        }catch  (Exception e) {
            e.printStackTrace();
        }
        return  responseBody;
    }

    @Override
    public String getTaskGroupNameById(Map<String,String> headMap, String taskGroupId) throws Exception {
        String url= tbConfig.getTBApiUrl() + "/taskgroup/query";
        Map<String, Object> requestParam = new HashMap<>();
        requestParam.put("taskGroupId",taskGroupId);
        String result = HttpUtil.getInstance().doGet(url,requestParam,headMap);
        JSONObject jsonResult = JSONObject.parseObject(result);
        System.out.println(jsonResult);
        if (jsonResult.getInteger("code") != 200){
            return "";
        }
        JSONArray array = jsonResult.getJSONArray("result");
        if(array.isEmpty()){
            return "";
        }
        JSONObject res = (JSONObject) array.get(0);
        return res.getString("name");
    }
    @Override
    public Map<String,String>  getUserList(Map<String,String> headMap,String orgId) throws Exception {
        String url= tbConfig.getTBApiUrl() + "/org/member/list";
        Map<String, Object> requestParam = new HashMap<>();
        requestParam.put("orgId",orgId);
        String result = HttpUtil.getInstance().doGet(url,requestParam,headMap);
        JSONObject jsonResult = JSONObject.parseObject(result);
        Map<String,String> userList = new HashMap<>();
        if (jsonResult.getInteger("code") != 200){
            return userList;
        }
        JSONArray array = jsonResult.getJSONArray("result");
        if(array.isEmpty()){
            return userList;
        }
        for (int i = 0; i < array.size(); i++) {
            JSONObject object = (JSONObject) array.get(i);    //将array中的数据进行逐条转换
            userList.put(object.getString("userId"),object.getString("name"));
        }
        return userList;
    }
}
