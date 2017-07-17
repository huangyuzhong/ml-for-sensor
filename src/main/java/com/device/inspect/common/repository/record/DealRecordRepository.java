package com.device.inspect.common.repository.record;

import com.device.inspect.common.model.record.DealRecord;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by zyclincoln on 7/16/17.
 */
public interface DealRecordRepository extends CrudRepository<DealRecord,Integer> {
    Integer countByDeviceIdAndBeginTimeBetween(Integer deviceId, Date beginTime, Date endTime);
    Integer countByDeviceIdAndEndTimeBetween(Integer deviceId, Date beginTime, Date endTime);
    DealRecord findTopByDeviceIdAndBeginTimeAndEndTime(Integer deviceId, Date beginTime, Date endTime);
    List<DealRecord> findByStatusAndEndTimeBefore(Integer status, Date endTime);
    List<DealRecord> findByLessorIdOrLesseeId(Integer lessorId, Integer lesseeId);
}