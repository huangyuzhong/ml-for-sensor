package com.device.inspect.config;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.device.inspect.Application;
import com.device.inspect.common.model.charater.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.device.inspect.common.repository.charater.UserRepository;

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
            logger.info("got prinncipal --- " + userName);
        }

        String requestUrl = request.getRequestURI().toString();

        // 过滤掉循环调用的api, 不记录到数据库
        // TODO 目前暂时hardcode, 之后会以可拓展的方式从数据库或者配置文件读出

        List<String> repeatApis = new ArrayList<String>();

        repeatApis.add("/api/rest/firm/buildings/");
        repeatApis.add("/api/rest/firm/device/runningStatusHistory");
        repeatApis.add("/api/rest/firm/dealHistory");
        repeatApis.add("/api/rest/device/current/data");

        if (repeatApis.contains(requestUrl)){
            logger.debug("ignore repeat api " + requestUrl);
            return;
        }


        String jsonRequestParam =  "";

        if ("POST".equalsIgnoreCase(request.getMethod())){
            try {
                Scanner s = new Scanner(request.getInputStream(), "UTF-8").useDelimiter("\\A");
                jsonRequestParam = s.hasNext() ? s.next() : "";
            }catch (IOException ex){
                logger.warn("Failed parse http POST request body. Err: " + ex.toString());
            }
        }else{
            try{
                jsonRequestParam = new ObjectMapper().writeValueAsString(request.getParameterMap());
            }catch (Exception ex){
                logger.warn("Failed to parse http request parameters to json string. Err: " + ex.toString());
            }
        }


        // 只记录UI api操作， 不记录终端数据的api
        // TODO: investigate why receved another api /error after login failure.
        if(requestUrl.endsWith("socket/insert/data") || requestUrl.endsWith("device/current/data") || requestUrl.equals("/error")){
            return;
        }

        if(Application.influxDBManager.writeAPIOperation(startTime, userName, requestUrl, request.getMethod(), jsonRequestParam, response.getStatus(), executeTime)){
            logger.info(String.format("+++ successfully write to influxdb -- Executing %s takes %d ms, return code: %d", requestUrl, executeTime, response.getStatus()));
        }
        else{
            logger.warn(String.format("+++ Failed to write influxdb -- Executing %s takes %d ms, return code: %d", requestUrl, executeTime, response.getStatus()));
        }


    }

}
