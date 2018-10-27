package com.device.inspect.config;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.device.inspect.Application;
import com.device.inspect.common.setting.Constants;
import com.device.inspect.common.util.transefer.UrlParse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


/**
 * Created by gxu on 6/5/17.
 */
public class ApiInterceptor extends HandlerInterceptorAdapter{

    private static final Logger logger = LogManager.getLogger(ApiInterceptor.class);


    public boolean preHandle(HttpServletRequest request,HttpServletResponse response, Object handler){


        request.setAttribute("startTime", System.currentTimeMillis());

        return true;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView){

        long endTime = System.currentTimeMillis();

        Object objStartTime = request.getAttribute("startTime");
        if(objStartTime == null){
            logger.warn(String.format("Cannot find startTime in request %s", request.getRequestURI()));
            return;
        }

        long startTime = (Long)objStartTime;

        long executeTime = endTime - startTime;

        Principal principal = request.getUserPrincipal();

        String userName = "Anonymous";

        if(principal != null){
            userName = principal.getName();
            logger.debug("got principal --- " + userName);
        }

        String requestUrl = request.getRequestURI().toString();

        // 抹去url里末尾的'/'
        if(requestUrl.endsWith("/")){
            requestUrl = requestUrl.substring(0, requestUrl.length()-1);
        }


        // 过滤掉循环调用的api, 不记录到数据库
        // TODO 目前暂时hardcode, 之后会以可拓展的方式从数据库或者配置文件读出

        List<String> repeatApis = new ArrayList<String>();

        repeatApis.add("/api/rest/firm/buildings");
        repeatApis.add("/api/rest/firm/device/runningStatusHistory");
        repeatApis.add("/api/rest/firm/dealHistory");
        repeatApis.add("/api/rest/device/current/data");
        repeatApis.add("/api/rest/operate/monitor/getFirstNotActActionAndUpdate");
        repeatApis.add("/api/rest/firm/dealRecord/alert");

        if (repeatApis.contains(requestUrl)){
            logger.debug("ignore repeat api " + requestUrl);
            return;
        }


        // 如果url结尾为 /<id>, 把最后一部分从url里出去, 保存为parameterId, 存到parameter里
        String[] url_parts = requestUrl.split("/");
        String paramId = null;
        if (url_parts.length > 0) {
            String url_last_part = url_parts[url_parts.length - 1];
            if(url_last_part.matches("[0-9]+") && !url_last_part.isEmpty()){
                paramId = url_last_part;
                requestUrl = requestUrl.substring(0, requestUrl.lastIndexOf("/"));

            }

        }


        String jsonRequestParam =  "";


        // we can only get request.inputstream once, for post data, we can only get once. Thus we cannot read it here, otherwise,
        // we cannot get the post data in controllers. Thus, we do not read POST method. Currently we only record GET method in the interceptor
        // POST method will be recorded in controllers
        if ("GET".equalsIgnoreCase(request.getMethod())){
            Map<String, String[]> allParameters = null;
            try{
                Map<String, String[]> paramMap = request.getParameterMap();

                if(paramId != null) {
                    // since paramMap is locked, we need to clone it
                    Map<String, String[]> cloneMap = new HashMap<>();
                    for(String key: paramMap.keySet()){
                        cloneMap.put(key, paramMap.get(key));
                    }
                    cloneMap.put("urlParameterId", new String[]{paramId});
                    allParameters = cloneMap;
                }
                else{
                    allParameters = paramMap;
                }
                jsonRequestParam = new ObjectMapper().writeValueAsString(allParameters);
            }catch (Exception ex){
                logger.warn("Failed to parse http request parameters to json string. Err: " + ex.toString());
            }

        }else if ("POST".equalsIgnoreCase(request.getMethod())){
            // 因为一些api使用不规范, 导致一些post的方法错误的把 body放到的api的参数部分.
            // 目前的做法是在每一个post的api的controller method里获取参数和body然后得带一个map放到request的attribute "postBody"
            // TODO:之后的工作规范的api之后, 这里的逻辑会修改
            try{
                Object postBodyObj = request.getAttribute(Constants.HTTP_REQUEST_CUSTOM_ATTRIBUTE_POST_BODY);
                if(postBodyObj != null){
                    jsonRequestParam = new ObjectMapper().writeValueAsString(postBodyObj);
                }
            }catch (Exception ex){
                logger.info("Failed to get postBody in POST http request, the controller method may forget adding it. Err:" + ex.toString());
            }

        }

        // 只记录UI api操作， 不记录终端数据的api
        // TODO: investigate why receved another api /error after login failure.
        if(requestUrl.endsWith("socket/insert/data") || requestUrl.endsWith("device/current/data") || requestUrl.equals("/error")){
            return;
        }

        String apiType = UrlParse.API_TYPE_WEB_QUERY;
        if (UrlParse.urlUserOperationMap.containsKey(requestUrl)){
            apiType = UrlParse.API_TYPE_USER_OPERATION;
        }


//        if(Application.influxDBManager.writeAPIOperation(startTime, userName, requestUrl, request.getMethod(), apiType, jsonRequestParam, response.getStatus(), executeTime)){
//            logger.info(String.format("+++ successfully write to influxdb -- Executing %s takes %d ms, return code: %d", requestUrl, executeTime, response.getStatus()));
//        }
//        else{
//            logger.warn(String.format("+++ Failed to write influxdb -- Executing %s takes %d ms, return code: %d", requestUrl, executeTime, response.getStatus()));
//        }


    }

}
