package com.device.inspect.common.repository.record;

import com.device.inspect.common.model.record.DeviceOrderList;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by zyclincoln on 8/6/17.
 */
public interface DeviceOrderListRepository extends CrudRepository<DeviceOrderList,Integer> {
    DeviceOrderList findTopByMonitorSerialNoAndExecuteStatusOrderByCreateTimeAsc(String monitorSerialNo, Integer executeStatus);
    List<DeviceOrderList> findByMonitorSerialNo(String monitorSerialNo);
}
