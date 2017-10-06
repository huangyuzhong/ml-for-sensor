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

    @Query(value = "select * from deal_record u where (u.lessor_id = ?1 or u.lessee_id = ?2) and u.device_id = ?3 and u.end_time < ?4 order by u.begin_time desc limit 9", nativeQuery=true)
    List<DealRecord> findTop9ByLessorOrLesseeOrderByEndTimeDesc(Integer lessorId, Integer lesseeId, Integer deviceId, Date currentDate);

    @Query(value = "select * from deal_record u where (u.lessor_id = ?1 or u.lessee_id = ?2) and u.device_id = ?3 and u.end_time >= ?4 order by u.begin_time asc limit 1", nativeQuery=true)
    List<DealRecord> findTop1ByLessorOrLesseeAndDeviceIdOrderByEndTimeDesc(Integer lessorId, Integer lesseeId, Integer
            deviceId, Date currentDate);

    @Query(value = "select * from deal_record u where (u.lessor_id = ?1 or u.lessee_id = ?2) and u.status in (3,4,5,8,9,10)" +
            "order by u.begin_time asc", nativeQuery=true)
    List<DealRecord> findByLessorOrLesseeOrderByEndTimeDesc(Integer lessorId, Integer lesseeId);

    List<DealRecord> findByDeviceIdAndStatus(Integer deiceId, Integer status);
}