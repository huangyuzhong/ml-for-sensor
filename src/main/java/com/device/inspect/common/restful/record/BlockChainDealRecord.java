package com.device.inspect.common.restful.record;

import org.springframework.security.access.method.P;

/**
 * Created by zyclincoln on 7/16/17.
 */
public class BlockChainDealRecord {
    private String Desc;
    private BlockChainDealDetail Data;

    public BlockChainDealRecord(){

    }

    public BlockChainDealRecord(String desc, BlockChainDealDetail data){
        this.Desc = desc;
        this.Data = data;
    }

    public void setDesc(String desc){
        this.Desc = desc;
    }

    public String getDesc(){
        return this.Desc;
    }

    public void setData(BlockChainDealDetail data){
        this.Data = data;
    }

    public BlockChainDealDetail getData(){
        return this.Data;
    }
}