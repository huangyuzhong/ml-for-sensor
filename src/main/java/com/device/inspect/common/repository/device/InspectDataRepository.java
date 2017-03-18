package com.device.inspect.common.repository.device;

import com.device.inspect.common.model.device.InspectData;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/7/8.
 */
public interface InspectDataRepository extends CrudRepository<InspectData,Integer> {
    public InspectData findTopByDeviceIdOrderByCreateDateDesc(Integer DeviceId);
    public InspectData findTopByDeviceIdAndDeviceInspectIdOrderByCreateDateDesc(Integer DeviceId,Integer DeviceInspectId);
    public List<InspectData> findTop7ByDeviceIdAndDeviceInspectIdOrderByCreateDateDesc(Integer DeviceId,Integer DeviceInspectId);
    public List<InspectData> findTop20ByDeviceIdAndDeviceInspectIdOrderByCreateDateDesc(Integer DeviceId,Integer DeviceInspectId);
    public List<InspectData> findByDeviceInspectIdAndCreateDateBetweenOrderByCreateDateAsc(Integer DeviceInspectId, Date BeginTime, Date EndTime);
    public List<InspectData> findByDeviceInspectIdAndCreateDateBetweenOrderByRealValueDesc(Integer DeviceInspectId, Date BeginTime, Date EndTime);
}
