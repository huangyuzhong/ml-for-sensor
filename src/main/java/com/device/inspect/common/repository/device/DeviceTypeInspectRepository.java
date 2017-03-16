package com.device.inspect.common.repository.device;

import com.device.inspect.common.model.device.DeviceType;
import com.device.inspect.common.model.device.DeviceTypeInspect;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Administrator on 2016/7/8.
 */
public interface DeviceTypeInspectRepository extends CrudRepository<DeviceTypeInspect,Integer> {
    public DeviceTypeInspect findById(Integer Id);
    public DeviceTypeInspect findByDeviceTypeIdAndInspectTypeId(Integer DeviceTypeId,Integer InspectTypeId);
}
