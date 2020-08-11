package com.spacex.tb.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.net.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.Map.Entry;

public class HttpUtil {

    protected final Log LOG = LogFactory.getLog(HttpUtil.class);
    private static HttpUtil instance;
    protected Charset charset;

    private HttpUtil(){}

    public static HttpUtil getInstance() {
        return getInstance(Charset.defaultCharset());
    }

    public static HttpUtil getInstance(Charset charset){
        if(instance == null){
            instance = new HttpUtil();
        }
        instance.setCharset(charset);
        return instance;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    /**
     * get 请求
     * @param url
     * @return
     * @throws Exception
     */
    public  String doGet(String url) throws Exception {
        return doGet(url, null, null);
    }

    public  String doGet(String url, Map<String, Object> params) throws Exception {
        return doGet(url, params, null);
    }

    public  String doGet(String url, Map<String, Object> params, Map<String, String> header) throws Exception {
        String body = null;
        try {
            // Get请求

            HttpGet httpGet = new HttpGet(url.trim());
            // 设置参数
            if (params != null && !params.isEmpty()) {
                String str = EntityUtils.toString(new UrlEncodedFormEntity(map2NameValuePairList(params)));
                String uri = httpGet.getURI().toString();
                if(uri.indexOf("?") >= 0){
                    httpGet.setURI(new URI(httpGet.getURI().toString() + "&" + str));
                }else {
                    httpGet.setURI(new URI(httpGet.getURI().toString() + "?" + str));
                }
            }
            System.out.println("url: " + httpGet.getURI());
            // 设置Header
            if (header != null && !header.isEmpty()) {
                System.out.println("header: " + header);
                for (Iterator<Entry<String, String>> it = header.entrySet().iterator(); it.hasNext();) {
                    Entry<String, String> entry = (Entry<String, String>) it.next();
                    httpGet.setHeader(new BasicHeader(entry.getKey(), entry.getValue()));
                }

            }
            // 发送请求,获取返回数据
            body =  execute(httpGet);
        } catch (Exception e) {
            throw e;
        }
        System.out.println("  result: " + body);
        return body;
    }

    public String doPost(String url) throws Exception {
        return doPost(url, null, null);
    }

    public String doPost(String url, Map<String, Object> params) throws Exception {
        return doPost(url, params, null);
    }

    public String doPost(String url, Map<String, Object> params, Map<String, String> header) throws Exception {
        String body = null;
        try {
            // Post请求
            LOG.debug("protocol: POST");
            LOG.debug("url: " + url);
            HttpPost httpPost = new HttpPost(url.trim());
            // 设置参数

            httpPost.setEntity(new UrlEncodedFormEntity(map2NameValuePairList(params)));
            // 设置Header
            if (header != null && !header.isEmpty()) {

                for (Iterator<Entry<String, String>> it = header.entrySet().iterator(); it.hasNext();) {
                    Entry<String, String> entry = (Entry<String, String>) it.next();
                    httpPost.setHeader(new BasicHeader(entry.getKey(), entry.getValue()));
                }
            }
            // 发送请求,获取返回数据
            body = execute(httpPost);
        } catch (Exception e) {
            throw e;
        }
        LOG.debug("   result: " + body);
        return body;
    }


    private static List<NameValuePair> map2NameValuePairList(Map<String, Object> params) {
        if (params != null && !params.isEmpty()) {
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            Iterator<String> it = params.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                if(params.get(key) != null) {
                    String value = String.valueOf(params.get(key));
                    list.add(new BasicNameValuePair(key, value));
                }
            }
            return list;
        }
        return null;
    }

    private static String execute(HttpRequestBase requestBase) throws Exception {

        CloseableHttpClient httpclient = HttpClients.createDefault();
        String body = null;
        try {
            CloseableHttpResponse response = httpclient.execute(requestBase);
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    body = EntityUtils.toString(entity);
                }
                EntityUtils.consume(entity);
            } catch (Exception e) {
                throw e;
            }finally {
                response.close();
            }
        } catch (Exception e) {
            throw e;
        } finally {
            httpclient.close();
        }
        return body;
    }


}
