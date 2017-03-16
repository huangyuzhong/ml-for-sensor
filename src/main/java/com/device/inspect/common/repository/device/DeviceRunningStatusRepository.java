package com.device.inspect.common.repository.device;

import com.device.inspect.common.model.device.DeviceRunningStatus;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by zyclincoln on 3/14/17.
 */
public interface DeviceRunningStatusRepository extends CrudRepository<DeviceRunningStatus, Integer> {
    public DeviceRunningStatus findById(Integer id);
    public DeviceRunningStatus save(DeviceRunningStatus status);
}
