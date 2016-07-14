package com.device.inspect.config;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Created by xyz on 4/15/15.
 */
@Aspect
@Component
public class Auditing {

    //@After("execution(public * org.springframework.data.repository.Repository+.save(..))")
    public void myaspect() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            //System.out.println("Principal is null");
            return;
        }
        //System.out.println("Principal is " + authentication.getPrincipal().toString());
    }
}
