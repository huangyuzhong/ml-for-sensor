package com.device.inspect.common.repository.record;

import com.device.inspect.common.model.record.DealRecord;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by zyclincoln on 7/16/17.
 */
@Repository("DealRecordRepository")
public interface DealRecordRepository extends CrudRepository<DealRecord, Integer> {
    Integer countByDeviceIdAndBeginTimeBetween(Integer deviceId, Date beginTime, Date endTime);

    Integer countByDeviceIdAndEndTimeBetween(Integer deviceId, Date beginTime, Date endTime);

    Integer countByDeviceIdAndBeginTimeBeforeAndEndTimeAfter(Integer deviceId, Date beginTIme, Date endTime);

    DealRecord findTopByDeviceIdAndBeginTimeAndEndTime(Integer deviceId, Date beginTime, Date endTime);

    List<DealRecord> findByStatusAndEndTimeBefore(Integer status, Date endTime);

    List<DealRecord> findByStatusAndBeginTimeBefore(Integer status, Date beginTime);

    @Query("select u from DealRecord u where (u.lessor = ?1 or u.lessee = ?2) and u.device.id = ?3 and u.endTime < ?4 order by u.beginTime desc ")
    List<DealRecord> findTop10ByLessorOrLesseeOrderByEndTimeDesc(Integer lessorId, Integer lesseeId, Integer deviceId, Date currentDate);

    @Query("select u from DealRecord u where (u.lessor = ?1 or u.lessee = ?2) and u.device.id = ?3 and u.beginTime >= ?4 order by u.beginTime asc ")
    List<DealRecord> findTop10ByLessorOrLesseeAndEndTimeOrderByEndTimeDesc(Integer lessorId, Integer lesseeId, Integer deviceId, Date currentDate);

    List<DealRecord> findByDeviceIdAndStatus(Integer deiceId, Integer status);
}
