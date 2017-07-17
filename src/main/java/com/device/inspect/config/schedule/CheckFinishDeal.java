package com.device.inspect.config.schedule;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.device.inspect.common.model.record.DealRecord;
import com.device.inspect.common.repository.record.DealRecordRepository;
import com.device.inspect.common.restful.record.BlockChainDealDetail;
import com.device.inspect.common.restful.record.BlockChainDealRecord;
import com.device.inspect.common.service.OnchainService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import static com.device.inspect.common.setting.Defination.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by zyclincoln on 7/17/17.
 */
@Component
public class CheckFinishDeal {
    private static final Logger LOGGER = LogManager.getLogger(CheckFinishDeal.class);

    @Autowired
    private OnchainService onchainService;

    @Autowired
    private DealRecordRepository dealRecordRepository;

    @Scheduled(cron = "0 */10 * * * ? ")
    public void scheduleTask() {
        LOGGER.info("Check Finish Deal: begin checking deal record which meets rent end time");

        List<DealRecord> records = dealRecordRepository.findByStatusAndEndTimeBefore(ONCHAIN_DEAL_STATUS_EXECUTING, new Date());
        for(DealRecord record : records){
            try {
                record.setStatus(ONCHAIN_DEAL_STATUS_WAITING_MUTUAL_CONFIRM);
                BlockChainDealDetail data = new BlockChainDealDetail(record.getId(), record.getDevice().getId(), record.getLessor().getId(),
                        record.getLessee().getId(), record.getPrice(), record.getBeginTime().getTime(), record.getEndTime().getTime(),
                        record.getDeviceSerialNumber(), record.getAggrement(), record.getStatus());
                BlockChainDealRecord value = new BlockChainDealRecord("更新交易状态", data);
                JSONObject returnObject = onchainService.sendStateUpdateTx("deal", String.valueOf(record.getId()) + String.valueOf(record.getDevice().getId()),
                        "", JSON.toJSONString(value));
                if (!JSON.toJSONString(value).equals(JSON.toJSONString(returnObject))) {
                    throw new Exception("return value from block chain is not equal to original");
                }
            }
            catch(Exception e){
                LOGGER.error("Check Finish Deal Error: " + e.getMessage());
            }
            dealRecordRepository.save(record);
        }
    }
}
