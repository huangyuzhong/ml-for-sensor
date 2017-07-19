package com.device.inspect.common.restful.record;

/**
 * Created by fgz on 2017/7/18.
 */
public class BlockChainDeviceRecord {
    private String desc;
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
