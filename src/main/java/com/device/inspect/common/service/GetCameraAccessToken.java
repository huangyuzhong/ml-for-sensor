package com.device.inspect.common.service;


import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by zyclincoln on 7/28/17.
 */
public class GetCameraAccessToken {

    private static final Logger LOGGER = LogManager.getLogger(GetCameraAccessToken.class);
    private static String appKey = "2401b7d87e9144639ba74c6bdfb2e5b9";
    private static String appSecret = "a600efe5b1b18e91a298c3533cb68f9b";

    public static String getAccessToken(){
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost("https://open.ys7.com/api/lapp/token/get");
        List<NameValuePair> params = new ArrayList<>(2);
        params.add(new BasicNameValuePair("appKey",appKey));
        params.add(new BasicNameValuePair("appSecret", appSecret));

        try{
            httpPost.addHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity);
            JSONObject contentJson = JSONObject.parseObject(content);
            Map<String,Object> data = (Map)contentJson.get("data");
            String accessToken = (String)data.get("accessToken");
            if(accessToken != null && !accessToken.isEmpty()){
                return accessToken;
            }
            else{
                return "";
            }
        }
        catch(Exception e){
            LOGGER.error(String.format("Get Camera Access Token Failed: ", e.getMessage()));
            return "";
        }
    }
}
