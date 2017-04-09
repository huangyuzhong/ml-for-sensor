package com.device.inspect.common.storage;

import java.util.Map;

/**
 * Created by zyclincoln on 4/9/17.
 */
public class StorageConfig {
    private Map<String, String> storage;

    public void setStorage(Map<String, String> mediaStorage){
        this.storage = mediaStorage;
    }

    public Map<String, String> getStorage(){
        return this.storage;
    }
}
