package com.device.inspect.common.repository.record;

import com.device.inspect.common.model.record.DeviceRunningStatusHistory;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by zyclincoln on 7/17/17.
 */
public interface DeviceRunningStatusHistoryRepository extends CrudRepository<DeviceRunningStatusHistory,Integer> {
    List<DeviceRunningStatusHistory> findByDeviceIdAndChangeTimeBetweenOrderByChangeTimeAsc(Integer deviceId, Date beginTime, Date endTime);
    List<DeviceRunningStatusHistory> findByDeviceIdAndChangeTimeAfterOrderByChangeTimeAsc(Integer device, Date beginTime);
}
