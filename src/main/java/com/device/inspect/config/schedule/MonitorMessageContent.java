package com.device.inspect.config.schedule;

import com.device.inspect.common.service.MessageSendService;
import com.device.inspect.common.service.WriteSerialPort;
import org.springframework.stereotype.Component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by FGZ on 2017/10/6.
 */
@Component
public class MonitorMessageContent {
    private static final Logger LOGGER = LogManager.getLogger(MonitorMessageContent.class);

    //    @Scheduled(cron = "55 * * * * ?")
    public void executeInternal(){
        int index = WriteSerialPort.monitorOnSIM800();
        if (index != -1){
            MessageSendService.readMessOnSIM800(index);  // 返回的内容格式为：手机号码//短信内容。如：18317958912//你好
        }
    }
}
