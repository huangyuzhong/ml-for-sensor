package com.device.inspect.common.service;

/**
 * Created by zyclincoln on 6/5/17.
 */

import com.device.inspect.common.cache.MemoryDevice;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MemoryCacheDevice {
    private ConcurrentHashMap<Integer, MemoryDevice> deviceHash = new ConcurrentHashMap<>();

    public void updateDeviceActivityTime(int deviceId, Date lastActivityTime){
        if(deviceHash.containsKey(deviceId)){
            MemoryDevice memoryDevice = deviceHash.get(deviceId);
            memoryDevice.updateNewestLastActivityTime(lastActivityTime);
        }
        else{
            MemoryDevice memoryDevice = new MemoryDevice(deviceId);
            memoryDevice.updateNewestLastActivityTime(lastActivityTime);
            deviceHash.put(deviceId, memoryDevice);
        }
    }

    public void updateDeviceAlertTimeAndType(int deviceId, Date lastAlertTime, int lastAlertType){
        if(deviceHash.containsKey(deviceId)){
            MemoryDevice memoryDevice = deviceHash.get(deviceId);
            memoryDevice.updateNewestLastAlertTimeAndType(lastAlertTime, lastAlertType);
        }
        else{
            MemoryDevice memoryDevice = new MemoryDevice(deviceId);
            memoryDevice.updateNewestLastAlertTimeAndType(lastAlertTime, lastAlertType);
            deviceHash.put(deviceId, memoryDevice);
        }

    }

    public Iterator getIterator(){
        return deviceHash.entrySet().iterator();
    }

    public MemoryDevice get(int deviceId){
        return deviceHash.get(deviceId);
    }
}
