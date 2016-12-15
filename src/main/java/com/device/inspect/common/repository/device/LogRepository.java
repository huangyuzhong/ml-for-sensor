package com.device.inspect.common.repository.device;

import com.device.inspect.common.model.device.Log;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Straight on 2016/12/15.
 */
public interface LogRepository extends CrudRepository<Log,Integer> {
    Log findById(Integer Id);
}
