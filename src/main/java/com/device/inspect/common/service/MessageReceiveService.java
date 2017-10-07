package com.device.inspect.common.service;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alicom.mns.tools.MessageListener;
import com.aliyun.mns.model.Message;
import com.device.inspect.common.managers.MessageController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 接收客户短信回复内容
 * Created by hwd on 2017/9/29.
 */
@Component
public class MessageReceiveService {

    private static final Logger LOGGER = (Logger) LogManager.getLogger(MessageReceiveService.class);
    private static MessageReceiveService messageReceiveService;
    @Autowired
    private MessageController messageController;

    @PostConstruct
    public void init() {
        messageReceiveService = this;
        messageReceiveService.messageController = this.messageController;
    }

    public static class MyMessageListener implements MessageListener {

        @Override
        public boolean dealMessage(Message message) {

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //消息的几个关键值
            LOGGER.info(String.format("message receiver time from mns: %s", format.format(new Date())));
            LOGGER.info(String.format("message handle: %s", message.getReceiptHandle()));
            LOGGER.info(String.format("message body: %s", message.getMessageBodyAsString()));
            LOGGER.info(String.format("message id: %s", message.getMessageId()));
            LOGGER.info(String.format("message dequeue count: %d", message.getDequeueCount()));
            LOGGER.info(String.format("Thread: %s", Thread.currentThread().getName()));
            try {
                Map<String, Object> contentMap = JSONObject.parseObject(message.getMessageBodyAsString(), HashMap
                        .class);

                //TODO 根据文档中具体的消息格式进行消息体的解析
                String content = (String) contentMap.get("content");
                String phoneNumber = (String) contentMap.get("phone_number");

                messageReceiveService.messageController.processReceivedReplyMessage(phoneNumber, content);

            } catch (JSONException e) {
                LOGGER.warn("error_json_format:" + message.getMessageBodyAsString(), e);
                //理论上不会出现格式错误的情况，所以遇见格式错误的消息，只能先delete,否则重新推送也会一直报错
                return true;
            } catch (Throwable e) {
                //您自己的代码部分导致的异常，应该return false,这样消息不会被delete掉，而会根据策略进行重推
                return false;
            }

            //消息处理成功，返回true, SDK将调用MNS的delete方法将消息从队列中删除掉
            return true;
        }

    }
}
