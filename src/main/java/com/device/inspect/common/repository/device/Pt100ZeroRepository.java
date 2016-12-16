package com.device.inspect.common.repository.device;

import com.device.inspect.common.model.device.Pt100Zero;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Straight on 2016/12/15.
 */
public interface Pt100ZeroRepository extends CrudRepository<Pt100Zero,Integer> {
    Pt100Zero findByCode(String Code);
}
