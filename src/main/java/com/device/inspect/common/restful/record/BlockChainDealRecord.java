package com.device.inspect.common.restful.record;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.security.access.method.P;

/**
 * Created by zyclincoln on 7/16/17.
 */
public class BlockChainDealRecord {
    @JSONField(name = "Desc")
    private String desc;
    @JSONField(name = "Data")
    private BlockChainDealDetail data;

    public BlockChainDealRecord(){

    }

    public BlockChainDealRecord(String desc, BlockChainDealDetail data){
        this.desc = desc;
        this.data = data;
    }

    public void setDesc(String desc){
        this.desc = desc;
    }

    public String getDesc(){
        return this.desc;
    }

    public void setData(BlockChainDealDetail data){
        this.data = data;
    }

    public BlockChainDealDetail getData(){
        return this.data;
    }
}