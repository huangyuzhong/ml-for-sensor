package com.device.inspect.common.repository.device;

import com.device.inspect.common.model.device.Device;
import com.device.inspect.common.model.device.DeviceType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Administrator on 2016/7/8.
 */
public interface DeviceTypeRepository extends CrudRepository<DeviceType,Integer> {
    public List<DeviceType> findByCompanyIdAndEnable(Integer CompanyId,Integer Enable);
    public List<DeviceType> findByEnableAndCompanyIdIsNull(Integer Enable);
    public List<DeviceType> findAll();
    public List<DeviceType> findByCompanyId(Integer CompanyId);
}
