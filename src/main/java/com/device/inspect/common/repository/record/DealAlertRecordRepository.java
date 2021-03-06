package com.device.inspect.common.repository.record;

import com.device.inspect.common.model.record.DealAlertRecord;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by zyclincoln on 8/7/17.
 */
public interface DealAlertRecordRepository extends CrudRepository<DealAlertRecord,Integer> {
    List<DealAlertRecord> findByDealIdOrderByHappenedTimeDesc(Integer dealId);
}
