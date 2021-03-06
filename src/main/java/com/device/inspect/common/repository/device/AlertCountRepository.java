package com.device.inspect.common.repository.device;

import com.device.inspect.common.model.device.AlertCount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/8/31.
 */
public interface AlertCountRepository  extends CrudRepository<AlertCount,Integer> {
    public AlertCount findTopByDeviceIdAndInspectTypeIdAndTypeOrderByCreateDateDesc(Integer DeviceId,Integer InspcetTypeId,Integer type);
    public AlertCount findTopByDeviceIdAndTypeOrderByCreateDateDesc(Integer DeviceId,Integer type);

    public Long countByDeviceIdAndCreateDateBetween(Integer DeviceId, Date start,Date end);
    public Long countByDeviceIdAndTypeAndCreateDateBetween(Integer DeviceId, Integer Type,Date start,Date end);

    public List<AlertCount> findByDeviceIdAndCreateDateAfter(Integer DeviceId,Date date);

    public List<AlertCount> findByDeviceIdAndInspectTypeIdAndTypeAndCreateDateBetween(Integer DeviceId,
                                                                                   Integer InspectTypeId,
                                                                                   Integer type,
                                                                                   Date StartTime,
                                                                                   Date EndTime);
    public AlertCount findTopByDeviceIdAndInspectTypeIdOrderByCreateDateDesc(Integer DeviceId, Integer InspectTypeId);
    public AlertCount findTopByDeviceIdAndInspectTypeIdAndTypeAndFinishBeforeOrderByFinishDesc(Integer deviceId,
                                                                                               Integer inspectTypeId,
                                                                                               Integer type,
                                                                                               Date finishTime);
    public List<AlertCount> findByDeviceIdAndInspectTypeIdAndFinishAfterAndCreateDateBeforeOrderByFinishAsc(Integer deviceId,
                                                                                                            Integer inspectTypeId,
                                                                                                            Date finishTime,
                                                                                                            Date createDate);

    public Page<AlertCount> findByDeviceIdAndInspectTypeIdInAndTypeAndCreateDateGreaterThanEqualAndCreateDateLessThanEqualOrderByFinishAsc(Integer deviceId,
                                                                                                                           List<Integer> inspectTypeIds,
                                                                                                                           Integer alertType,
                                                                                                                           Date startTime,
                                                                                                                           Date endTime,
                                                                                                                           Pageable pageable);

    @Query(value = "SELECT SUM(timestampdiff(minute, create_date, finish_date)) FROM alert_count WHERE create_date > ?1 AND create_date < ?2 AND inspect_type_id = ?3 AND device_id=?4", nativeQuery = true)
    List<BigDecimal> findAlertSumDurationByCreateDateBetweenAndInspectTypeIdAndDeviceId(Date startTime, Date endTime, Integer inspectTypeId, Integer deviceId);


}
