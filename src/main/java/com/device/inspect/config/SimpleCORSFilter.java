package com.device.inspect.config;

import org.hibernate.mapping.Collection;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;


import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Created by Administrator on 2016/3/16.
 */
@Component
public class SimpleCORSFilter implements Filter {

    protected static Logger logger = LogManager.getLogger();

    public SimpleCORSFilter(){
        logger.info("cors filter created");
    }
    /**
     * 设置响应头的参数信息，并且根据FilterChain中的doFilter()方法，用来过滤
     * @param req
     * @param res
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;

        logger.info(String.format("---- %s ---- \r\n", request.getRequestURL()));

        HttpServletResponse response = (HttpServletResponse) res;
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With, X-Auth-Token");
//        response.addHeader("Access-Control-Allow-Credentials","true");
        chain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig filterConfig) {
        logger.info("----- CORS filter init -----");
    }

    @Override
    public void destroy() {}

}