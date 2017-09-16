package com.device.inspect.config.security;

/**
 * Created by gxu on 2/12/17.
 */

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class SessionListener implements HttpSessionListener{
    private static int totalActiveSessions;
    protected static Logger logger = LogManager.getLogger();

    public static int getTotalActiveSessions(){
        return totalActiveSessions;
    }

    @Override
    public void sessionCreated(HttpSessionEvent event){
        HttpSession session = event.getSession();
        totalActiveSessions ++;

        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Date dateobj = new Date();
        System.out.println(df.format(dateobj));
        System.out.println(String.format("session %s created, total session %d", session.getId(), totalActiveSessions));
        logger.info(String.format("session %s created, total session %d", session.getId(), totalActiveSessions));
        session.setMaxInactiveInterval(60*60*1); //1小时内无操作, session将expire
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event){
        HttpSession session = event.getSession();
        totalActiveSessions--;

        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Date dateobj = new Date();
        System.out.println(df.format(dateobj));

        System.out.println(String.format("session %s destroyed, total session %d ", session.getId(), totalActiveSessions));
        logger.info(String.format("session %s destroyed, total session %d ", session.getId(), totalActiveSessions));
    }

}
