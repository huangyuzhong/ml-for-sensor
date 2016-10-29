package com.device.inspect.common.repository.device;

import com.device.inspect.common.model.device.DeviceFloor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Administrator on 2016/7/8.
 */
public interface DeviceFloorRepository extends CrudRepository<DeviceFloor,Integer> {
    public List<DeviceFloor> findByScientistId(Integer ScientistId);
    public List<DeviceFloor> findByDeviceId(Integer DeviceId);
}
