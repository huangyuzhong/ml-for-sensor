package com.device.inspect.common.repository.device;

import com.device.inspect.common.model.device.InspectType;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Administrator on 2016/7/8.
 */
public interface InspectTypeRepository extends CrudRepository<InspectType,Integer> {
    public InspectType findByCode(String Code);
}
