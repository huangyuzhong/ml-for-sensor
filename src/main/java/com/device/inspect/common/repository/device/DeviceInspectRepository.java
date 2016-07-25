package com.device.inspect.common.repository.device;

import com.device.inspect.common.model.device.DeviceInspect;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Administrator on 2016/7/8.
 */
public interface DeviceInspectRepository extends CrudRepository<DeviceInspect,Integer> {
    public DeviceInspect findByInspectTypeIdAndDeviceId(Integer InspectTypeId,Integer DeviceId );
    public List<DeviceInspect> findByDeviceId(Integer DeviceId);
}
