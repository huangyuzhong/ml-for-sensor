package com.device.inspect.common.setting;

import java.util.Map;

/**
 * Created by zyclincoln on 4/9/17.
 */
public class GeneralConfig {
    private Map<String, String> storage;

    private Map<String, String> email;

    private Map<String, String> influxdb;

    private Map<String, String> rabbitmq;

    private Map<String, String> message;

    public void setStorage(Map<String, String> mediaStorage){
        this.storage = mediaStorage;
    }

    public Map<String, String> getStorage(){
        return this.storage;
    }

    public void setEmail(Map<String, String> email){ this.email = email;}

    public Map<String, String > getEmail() {return this.email; }

    public Map<String, String> getInfluxdb() { return this.influxdb; }

    public void setInfluxdb(Map<String, String> influxdb) { this.influxdb = influxdb; }

    public Map<String, String> getRabbitmq() { return this.rabbitmq; }

    public void setRabbitmq(Map<String, String> rabbitmq) { this.rabbitmq = rabbitmq; }

    public Map<String, String> getMessage() {
        return message;
    }

    public void setMessage(Map<String, String> message) {
        this.message = message;
    }
}
