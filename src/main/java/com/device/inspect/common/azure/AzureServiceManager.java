package com.device.inspect.common.azure;

import java.util.Map;
/**
 * Created by gxu on 2/26/17.
 */
public class AzureServiceManager {
    protected String serviceName;
    protected String accountName;
    protected String accessKey1;
    protected String accessKey2;
    protected String azureCloud;
    protected String endPoint;


    public AzureServiceManager(String name, Map<String, String> config){
        this.serviceName = name;
        this.accountName = config.get("account");
        this.accessKey1 = config.get("access_key_1");
        this.accessKey2 = config.get("access_key_2");
        this.azureCloud = config.get("azure_cloud");
        this.endPoint = config.get("endpoint");
    }
}
