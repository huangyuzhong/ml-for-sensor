package com.device.inspect.common.repository.device;

import com.device.inspect.common.model.device.DeviceInspectRunningStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by zyclincoln on 3/14/17.
 */
public interface DeviceInspectRunningStatusRepository extends CrudRepository<DeviceInspectRunningStatus,Integer> {
    public List<DeviceInspectRunningStatus> findByDeviceInspectId(Integer DeviceInspectId);

}
