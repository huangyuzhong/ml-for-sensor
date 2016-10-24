package com.device.inspect.common.repository.firm;

import com.device.inspect.common.model.firm.Building;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Administrator on 2016/7/8.
 */
public interface BuildingRepository extends CrudRepository<Building,Integer> {
    public List<Building> findByCompanyIdAndEnable(Integer CompanyId,Integer Enable);
    public List<Building> findByCompanyId(Integer CompanyId);
}
