package com.spacex.tb.service;

import com.alibaba.fastjson.JSONObject;
import com.spacex.tb.parm.Headers;
import com.spacex.tb.parm.Parms;
import com.spacex.tb.util.JsonResult;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.util.Map;

public interface TeamService {

    String sendPost(String url, HttpHeaders headers, Map<String, Object> requestParam);
    String getTaskGroupNameById(Map<String,String> headMap, String taskGroupId) throws Exception;

    // 获取组织内的所有的员工信息
    Map<String,String>  getUserList(Map<String,String> headMap,String orgId)throws Exception;
}
