package com.device.inspect.common.azure;

import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by gxu on 2/26/17.
 */
public class AzureConfig {
    private Map<String, String> storage;
    private Map<String, String> streamAnalysis;
    private Map<String, String> powerbi;

    public void setStorage(Map<String, String> mediaStorage){
        this.storage = mediaStorage;
    }

    public Map<String, String> getStorage(){
        return this.storage;
    }

    public void setStreamAnalysis(Map<String, String> streamAnalysis){
        this.streamAnalysis = streamAnalysis;
    }

    public Map<String, String> getStreamAnalysis(){
        return this.streamAnalysis;
    }

    public void setPowerbi(Map<String, String> powerbiSettings){ this.powerbi = powerbiSettings;}

    public Map<String, String> getPowerbi(){ return this.powerbi;}
}
