package com.device.inspect.common.repository.firm;

import com.device.inspect.common.model.firm.Storey;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Administrator on 2016/7/8.
 */
public interface StoreyRepository extends CrudRepository<Storey,Integer> {
    public List<Storey> findByBuildIdAndEnable(Integer BuildId,Integer Enable);
}
