package com.device.inspect.common.repository.device;

import com.device.inspect.common.model.device.DeviceHourlyUtilization;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by zyclincoln on 3/19/17.
 */
public interface DeviceHourlyUtilizationRepository extends CrudRepository<DeviceHourlyUtilization,Integer> {
    public List<DeviceHourlyUtilization> findByDeviceIdIdAndStartHourBetweenOrderByStartHourAsc(Integer DeviceInspectId, Date BeginTime, Date EndTime);

}
