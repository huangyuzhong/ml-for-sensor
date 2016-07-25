package com.device.inspect.common.repository.device;

import com.device.inspect.common.model.device.InspectData;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Administrator on 2016/7/8.
 */
public interface InspectDataRepository extends CrudRepository<InspectData,Integer> {
    public InspectData findTopByDeviceIdAndDeviceInspectIdOrderByCreateDateDesc(Integer DeviceId,Integer DeviceInspectId);
}
