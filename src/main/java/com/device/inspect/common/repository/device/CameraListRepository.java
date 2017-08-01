package com.device.inspect.common.repository.device;

import com.device.inspect.common.model.device.CameraList;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by zyclincoln on 7/28/17.
 */
public interface CameraListRepository  extends CrudRepository<CameraList, Integer> {
    List<CameraList> findByDeviceId(Integer deviceId);
}
