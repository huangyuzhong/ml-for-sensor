package com.device.inspect.config;

/**
 * Created by gxu on 2/12/17.
 */

import com.device.inspect.config.security.SessionListener;
import org.springframework.boot.context.embedded.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpSessionListener;

@Configuration
public class ApplicationSessionConfiguration {

    @Bean
    public ServletListenerRegistrationBean<HttpSessionListener> sessionListener(){
        return new ServletListenerRegistrationBean<HttpSessionListener>(new SessionListener());
    }
}
