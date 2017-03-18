package com.device.inspect.common.repository.device;

import com.device.inspect.common.model.device.Device;
import com.device.inspect.common.model.device.DeviceHourlyUtilization;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Created by zyclincoln on 3/19/17.
 */
public interface DeviceHourlyUtilizationRepository extends CrudRepository<DeviceHourlyUtilization,Integer> {

}
