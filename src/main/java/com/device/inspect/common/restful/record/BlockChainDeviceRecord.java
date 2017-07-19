package com.device.inspect.common.restful.record;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by fgz on 2017/7/18.
 */
public class BlockChainDeviceRecord {
    @JSONField(name = "Desc")
    private String desc;
    @JSONField(name = "Data")
    private BlockChainDevice data;

    public BlockChainDeviceRecord() {
    }

    public BlockChainDeviceRecord(String desc, BlockChainDevice data) {
        this.desc = desc;
        this.data = data;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public BlockChainDevice getData() {
        return data;
    }

    public void setData(BlockChainDevice data) {
        this.data = data;
    }
}
