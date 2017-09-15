package com.device.inspect.common.restful.api;

import java.util.Date;

/**
 * Created by gxu on 9/14/17.
 */
public class RestUserOperation {
    private Date timeStamp;
    private String username;
    private String userDisplayName;
    private String operationType;
    private String url;
    private String operationName;
    private String content;

    public Date getTimeStamp() {return this.timeStamp; }
    public void setTimeStamp(Date timestamp) {this.timeStamp = timestamp;}

    public String getUsername() {return this.username; }
    public void setUsername(String username){this.username = username;}

    public String getUserDisplayName() { return this.userDisplayName; }
    public void setUserDisplayName(String userDisplayName) {this.userDisplayName = userDisplayName; }

    public String getOperationType(){return this.operationType;}
    public void setOperationType(String operationType)  {this.operationType = operationType;}

    public String getUrl() {return this.url;}
    public void setUrl(String url) {this.url = url;}

    public String getOperationName() {return this.operationName;}
    public void setOperationName(String operationName){this.operationName = operationName;}

    public String getContent() {return this.content;}
    public void setContent(String content){this.content = content;}

}
