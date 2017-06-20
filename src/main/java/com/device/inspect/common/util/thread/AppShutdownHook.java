package com.device.inspect.common.util.thread;

import com.device.inspect.Application;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by gxu on 6/20/17.
 */
public class AppShutdownHook extends Thread{
    public static final Logger LOGGER = LogManager.getLogger(AppShutdownHook.class);
    @Override
    public void run() {
        LOGGER.info("intelab-wbe shutdown hook activated");
        Application.Stop();

        LOGGER.info("intelab-wbe stopped");

    }
}