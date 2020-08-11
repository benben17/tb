package com.spacex.tb.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.spacex.tb.parm.Headers;
import com.spacex.tb.parm.Parms;
import com.spacex.tb.service.AccessTokenService;
import com.spacex.tb.service.TeamService;
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
    private AccessTokenService accessTokenService;

    @Override
    public JsonResult getAccessToken() {
        return null;
    }

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

}
