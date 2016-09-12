package com.device.inspect.common.repository.device;

import com.device.inspect.common.model.device.Files;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Administrator on 2016/7/8.
 */
public interface FileRepository extends CrudRepository<Files,Integer> {
}
