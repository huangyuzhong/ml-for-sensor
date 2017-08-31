package com.device.inspect.common.repository.device;

import com.device.inspect.common.model.device.OpeModelsLevel;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by fgz on 2017/8/31.
 */
public interface OpeModelsLevelRepository extends CrudRepository<OpeModelsLevel, Integer> {

    public List<OpeModelsLevel> findAllByOrderByIdAsc();

}
