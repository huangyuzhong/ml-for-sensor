package com.device.inspect.common.repository.device;

import com.device.inspect.common.model.device.AlertCount;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/8/31.
 */
public interface AlertCountRepository  extends CrudRepository<AlertCount,Integer> {
    public AlertCount findTopByDeviceIdAndInspectTypeIdAndTypeOrderByCreateDateDesc(Integer DeviceId,Integer InspcetTypeId,Integer type);
    public AlertCount findTopByDeviceIdAndTypeOrderByCreateDateDesc(Integer DeviceId,Integer type);

    public Long countByDeviceIdAndCreateDateBetween(Integer DeviceId, Date start,Date end);
    public Long countByDeviceIdAndTypeAndCreateDateBetween(Integer DeviceId, Integer Type,Date start,Date end);

    public List<AlertCount> findByDeviceIdAndCreateDateAfter(Integer DeviceId,Date date);

    public List<AlertCount> findByDeviceIdAndInspectTypeIdAndTypeAndCreateDateBetween(Integer DeviceId,
                                                                                   Integer InspectTypeId,
                                                                                   Integer type,
                                                                                   Date StartTime,
                                                                                   Date EndTime);
}
