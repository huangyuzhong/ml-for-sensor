package com.device.inspect.config.security.stateless;

import com.device.inspect.Application;
import com.device.inspect.common.util.transefer.UrlParse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.security.Principal;

/**
 * Created by gxu on 6/5/17.
 */
public class CustomLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler implements LogoutSuccessHandler {

    protected static Logger logger = LogManager.getLogger(CustomLogoutSuccessHandler.class);
    @Override
    public void onLogoutSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {

        Principal principal = request.getUserPrincipal();

        String userName = "Anonymous";



        if(authentication != null){
            userName =  authentication.getName();
        }


        if(Application.influxDBManager.writeAPIOperation(System.currentTimeMillis(), userName, request.getRequestURI().toString(), request.getMethod(), UrlParse.API_TYPE_USER_OPERATION, "", response.getStatus(), 0)){
            logger.info(String.format("+++ successfully write to influxdb -- Executing %s [%s] takes %d ms, return code: %d", request.getRequestURI().toString(), userName, 0, response.getStatus()));
        }
        else{
            logger.warn(String.format("+++ Failed to write influxdb -- Executing %s [%s] takes %d ms, return code: %d", request.getRequestURI().toString(), userName, 0, response.getStatus()));
        }

        super.onLogoutSuccess(request, response, authentication);
    }
}
