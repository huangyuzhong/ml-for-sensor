package com.device.inspect.common.repository.record;

import com.device.inspect.common.model.record.Models;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by fgz on 2017/8/28.
 */
public interface ModelsRepository extends CrudRepository<Models, Integer> {
    public List<Models> findAll();
}
