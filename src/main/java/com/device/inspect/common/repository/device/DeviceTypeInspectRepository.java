package com.device.inspect.common.repository.device;

import com.device.inspect.common.model.device.DeviceType;
import com.device.inspect.common.model.device.DeviceTypeInspect;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Administrator on 2016/7/8.
 */
public interface DeviceTypeInspectRepository extends CrudRepository<DeviceTypeInspect,Integer> {
    public DeviceTypeInspect findById(Integer Id);
    public DeviceTypeInspect findByDeviceTypeIdAndInspectTypeId(Integer DeviceTypeId,Integer InspectTypeId);
    public List<DeviceTypeInspect> findByDeviceTypeId(Integer deviceTypeId);
}
