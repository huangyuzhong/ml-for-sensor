package com.device.inspect.controller.request;

/**
 * Created by zyclincoln on 7/16/17.
 */
public class finishChainDealRequest {
    private Integer dealId;
    private Integer operateUserId;
    private String operation;

    public void setDealId(Integer dealId){
        this.dealId = dealId;
    }

    public Integer getDealId(){
        return this.dealId;
    }

    public void setOperateUserId(Integer operateUserId){
        this.operateUserId = operateUserId;
    }

    public Integer getOperateUserId(){
        return this.operateUserId;
    }

    public void setOperation(String operation){
        this.operation = operation;
    }

    public String getOperation(){
        return this.operation;
    }
}
