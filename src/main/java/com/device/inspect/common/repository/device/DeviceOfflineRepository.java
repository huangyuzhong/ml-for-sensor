package com.device.inspect.common.repository.device;

import com.device.inspect.common.model.device.DeviceOffline;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/10/18.
 */
public interface DeviceOfflineRepository extends CrudRepository<DeviceOffline,Integer> {
    public List<DeviceOffline> findByDeviceId(Integer DeviceId);
    public Long countByDeviceIdAndOfflineDateBetween(Integer DeviceId,Date start,Date end);
}
