package com.device.inspect.common.util.thread;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.device.inspect.Application;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.Random;

/**
 * Created by gxu on 6/20/17.
 */
public class IoTMessageWorker extends Thread {
    private static final Logger LOGGER = LogManager.getLogger(IoTMessageWorker.class);
    private static final String serverHost = "http://localhost:8999/api/rest/socket/insert/data?result=";

    private static final String EXCHANGE_NAME = "intelab";
    private int id;

    private boolean running = false;

    public IoTMessageWorker(int id){
        this.id = id;
    }

    private ConnectionFactory factory = null;
    private Connection mqConnection = null;
    private Channel mqChannel = null;

    public void Stop(){
        running = false;

        if(mqConnection != null && mqConnection.isOpen()){
            try {
                mqConnection.close();
            }catch (Exception ex){

            }finally {
                LOGGER.info("[IoTMessage Worker] - Closed connection to mq borker");
            }
        }
    }

    public void run(){

        String threadName = this.getName();
        LOGGER.info("IoT Message worker started. ID: " + id + " . Thread name: " + threadName);
        this.running = true;
        factory = new ConnectionFactory();
        factory.setHost(Application.generalConfig.getRabbitmq().get("broker_ip"));

        factory.setUsername(Application.generalConfig.getRabbitmq().get("username"));

        factory.setPassword(Application.generalConfig.getRabbitmq().get("password"));


        startConsumingMessge();
    }

    private void startConsumingMessge(){
        while(this.running){
            try {
                if (mqConnection == null || !mqConnection.isOpen()) {
                    mqConnection = factory.newConnection();

                    mqConnection.addShutdownListener(new ShutdownListener() {
                        @Override
                        public void shutdownCompleted(ShutdownSignalException cause) {
                            LOGGER.info("[IoTMessage Worker] - Connection shutdown: " + cause.getMessage());
                            if(cause.isHardError()){
                                Connection conn = (Connection)cause.getReference();
                                if(!cause.isInitiatedByApplication()){

                                    LOGGER.warn(String.format("[IoTMessage Worker] - Connection %s:%d to MQ shutdown unexpectedly because of connection issue. Reason: %s",
                                            conn.getId(), conn.getPort(), cause.getMessage()));
                                }
                            }else{
                                Channel ch = (Channel)cause.getReference();

                                if(!cause.isInitiatedByApplication()){
                                    LOGGER.warn(String.format("[IoTMessage Worker] - Channel %d shutdown unexpectedly. Reason: %s",
                                            ch.getChannelNumber(), cause.getMessage()));
                                }
                            }
                        }
                    });
                }

                if (mqChannel == null || !mqChannel.isOpen()) {
                    mqChannel = mqConnection.createChannel();

                }

                String queue_name = Application.generalConfig.getRabbitmq().get("telemetry_queue_name");

                mqChannel.exchangeDeclare(EXCHANGE_NAME, "topic");

                mqChannel.queueDeclare(queue_name, true, false, false, null);

                mqChannel.basicQos(1);

                mqChannel.queueBind(queue_name, EXCHANGE_NAME, "iot.*");

                final Consumer consumer = new DefaultConsumer(mqChannel) {
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                        String message = new String(body, "UTF-8");

                        LOGGER.info(String.format("[IoTMessage Worker] - IoT message Received by consumer %s. Message:" +
                                " %s", this.getConsumerTag(), message));
                        try {
                            String response_json = doWork(message);
                            LOGGER.info("[IoTMessage Worker] - HTTP Response : " + response_json);
                        } catch (Exception ex) {
                            LOGGER.error("[IoTMessage Worker] - Exception in message process. Err:" + ex.toString());
                        } finally {
                            long deliveryTag = envelope.getDeliveryTag();
                            LOGGER.info("[IoTMessage Worker] - sending message acknowledge " + deliveryTag);
                            mqChannel.basicAck(deliveryTag, false);
                        }
                    }
                };

                boolean autoAck = false;
                LOGGER.info(String.format("[IoTMessage Worker] - %s consuming incoming message", this.getName()));
                mqChannel.basicConsume(queue_name, autoAck, consumer);

                break;
            } catch (Exception ex) {
                LOGGER.error("[IoTMessage Worker] - Failed to connect to mq broker, retrying");
                try {
                    Thread.sleep(new Random().nextInt(5000) + 1000);
                } catch (Exception sleepEx) {
                    LOGGER.info("[IoTMessage Worker] - ThreadSleep fails: " + sleepEx.toString());
                }

                continue;
            }
        }
    }

    private String doWork(String message) throws Exception {
        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(serverHost+
                message);

        client.executeMethod(method);
        //打印服务器返回的状态
        LOGGER.info("insert data api returned HTTP status: " + method.getStatusLine());
        InputStream stream = method.getResponseBodyAsStream();

        BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        StringBuffer buf = new StringBuffer();
        String line;
        while (null != (line = br.readLine())) {
            buf.append(line).append("\n");
        }

        String response = buf.toString();
        JSONObject jsonObject = JSON.parseObject(response);
        String result = null;
        if (null!=jsonObject.get("data"))
            result = jsonObject.get("data").toString();

        //释放连接
        method.releaseConnection();
        LOGGER.info(String.format("Insert data 返回JSON: %s, 数据 %s", response, result));
        return result;
    }

}
