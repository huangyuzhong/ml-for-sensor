package com.device.inspect.config.schedule;

import com.device.inspect.Application;
import com.device.inspect.common.managers.MessageController;
import com.device.inspect.common.service.MessageSendService;
import com.device.inspect.common.service.WriteSerialPort;
import com.device.inspect.common.setting.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by FGZ on 2017/10/6.
 */
@Component
public class MonitorMessageContent {
    private static final Logger LOGGER = LogManager.getLogger(MonitorMessageContent.class);

    @Autowired
    private MessageController messageController;

    //    @Scheduled(cron = "55 * * * * ?")
    public void executeInternal(){
        if (Application.smsMedia != Constants.SMS_MEDIA_TYPE_MODULE){
            return;
        }
        int index = WriteSerialPort.monitorOnSIM800();
        if (index != -1){
            // 返回的内容格式为：手机号码//短信内容。如：18317958912//你好
            String phoneNumAndContent = MessageSendService.readMessOnSIM800(index);
            String[] str = phoneNumAndContent.split("//");
            messageController.processReceivedReplyMessage(str[0], str[1]);
            LOGGER.info(String.format("Message body: %s", phoneNumAndContent));
        }
    }
}
