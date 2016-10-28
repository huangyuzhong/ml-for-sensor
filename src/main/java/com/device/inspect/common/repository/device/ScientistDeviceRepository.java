package com.device.inspect.common.repository.device;

import com.device.inspect.common.model.device.ScientistDevice;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Administrator on 2016/10/28.
 */
public interface ScientistDeviceRepository extends CrudRepository<ScientistDevice,Integer> {
    public ScientistDevice findByScientistIdAndDeviceId(Integer ScientistId,Integer DeviceId);
    public List<ScientistDevice> findByScientistId(Integer ScientistId);
}
