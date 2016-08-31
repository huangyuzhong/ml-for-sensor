package com.device.inspect.common.repository.device;

import com.device.inspect.common.model.device.AlertCount;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Administrator on 2016/8/31.
 */
public interface AlertCountRepository  extends CrudRepository<AlertCount,Integer> {
    public AlertCount findTopByDeviceIdAndTypeInspectTypeIdAndOrderByCreateDateDesc(Integer DeviceId,Integer InspcetTypeId,Integer type);
}
