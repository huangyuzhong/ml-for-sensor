package com.device.inspect.config;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.device.inspect.Application;
import com.device.inspect.common.model.charater.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import java.security.Principal;

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


        String requestUrl = request.getRequestURL().toString();


        // 只记录UI api操作， 不记录终端数据的api
        if(requestUrl.endsWith("socket/insert/data")){
            return;
        }

        if(Application.influxDBManager.writeAPIOperation(startTime, userName, request.getRequestURL().toString(), response.getStatus(), executeTime)){
            logger.info(String.format("+++ successfully write to influxdb -- Executing %s takes %d ms, return code: %d", request.getRequestURL().toString(), executeTime, response.getStatus()));
        }
        else{
            logger.warn(String.format("+++ Failed to write influxdb -- Executing %s takes %d ms, return code: %d", request.getRequestURL().toString(), executeTime, response.getStatus()));
        }


    }

}
