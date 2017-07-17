package com.device.inspect.common.repository.record;

import com.device.inspect.common.model.record.DeviceDisableTime;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by zyclincoln on 7/16/17.
 */
public interface DeviceDisableTimeRepository extends CrudRepository<DeviceDisableTime,Integer> {
    List<DeviceDisableTime>findByDeviceId(Integer deviceId);
}
