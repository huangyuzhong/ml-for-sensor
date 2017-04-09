package com.device.inspect.common.ftp;

import java.util.Map;

/**
 * Created by zyclincoln on 4/8/17.
 */
public class FTPConfig {
    private Map<String, String> ftp;

    public void setFtp(Map<String, String> ftp){
        this.ftp = ftp;
    }

    public Map<String, String> getFtp(){
        return this.ftp;
    }
}
