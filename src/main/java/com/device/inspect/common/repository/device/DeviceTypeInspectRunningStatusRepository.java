package com.device.inspect.common.repository.device;

import com.device.inspect.common.model.device.DeviceTypeInspectRunningStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by zyclincoln on 3/14/17.
 */
public interface DeviceTypeInspectRunningStatusRepository extends CrudRepository<DeviceTypeInspectRunningStatus, Integer> {
    public DeviceTypeInspectRunningStatus findById(DeviceTypeInspectRunningStatus id);
    public List<DeviceTypeInspectRunningStatus> findByDeviceTypeInspectId(Integer DeviceTypeInspectId);
    public DeviceTypeInspectRunningStatus save(DeviceTypeInspectRunningStatus status);
}
