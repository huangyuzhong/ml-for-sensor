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

    @Scheduled(cron = "0 */1 * * * ? ")
    public void scheduleTask() {
        LOGGER.info(String.format("Check Execut Deal: begin checking deal record which meets rent start time at %s", new Date()));
        List<DealRecord> beginRecords = dealRecordRepository.findByStatusAndBeginTimeBefore(ONCHAIN_DEAL_STATUS_DEAL, new Date());
        for(DealRecord record : beginRecords){
            try{
                LOGGER.info(String.format("Check Execute Deal: found deal to execute: %d", record.getId()));
                record.setStatus(ONCHAIN_DEAL_STATUS_EXECUTING);
                BlockChainDealDetail data = new BlockChainDealDetail(record.getId(), record.getDevice().getId(), record.getLessor(),
                        record.getLessee(), record.getPrice(), record.getBeginTime().getTime(), record.getEndTime().getTime(),
                        record.getDeviceSerialNumber(), record.getAggrement(), record.getStatus());
                BlockChainDealRecord value = new BlockChainDealRecord(DEAL_STATUS_TRANSFER_MAP.get(record.getStatus()), data);
                onchainService.sendStateUpdateTx("deal", String.valueOf(record.getId()) + String.valueOf(record.getDevice().getId()),
                        "", JSON.toJSONString(value));
                dealRecordRepository.save(record);
            }
            catch(Exception e){
                LOGGER.error("Check Finish Deal Error: " + e.getMessage());
            }
        }

        LOGGER.info("Check Finish Deal: begin checking deal record which meets rent end time");

        List<DealRecord> records = dealRecordRepository.findByStatusAndEndTimeBefore(ONCHAIN_DEAL_STATUS_EXECUTING, new Date());
        for(DealRecord record : records){
            try {
                record.setStatus(ONCHAIN_DEAL_STATUS_WAITING_MUTUAL_CONFIRM);
                BlockChainDealDetail data = new BlockChainDealDetail(record.getId(), record.getDevice().getId(), record.getLessor(),
                        record.getLessee(), record.getPrice(), record.getBeginTime().getTime(), record.getEndTime().getTime(),
                        record.getDeviceSerialNumber(), record.getAggrement(), record.getStatus());
                BlockChainDealRecord value = new BlockChainDealRecord(DEAL_STATUS_TRANSFER_MAP.get(record.getStatus()), data);
                onchainService.sendStateUpdateTx("deal", String.valueOf(record.getId()) + String.valueOf(record.getDevice().getId()),
                        "", JSON.toJSONString(value));
                dealRecordRepository.save(record);
            }
            catch(Exception e){
                LOGGER.error("Check Finish Deal Error: " + e.getMessage());
            }
        }
    }
}
