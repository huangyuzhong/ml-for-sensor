package com.device.inspect.common.repository.device;

import com.device.inspect.common.model.device.Device;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Administrator on 2016/7/8.
 */
public interface DeviceRepository extends CrudRepository<Device,Integer> {
    public Device findByCode(String Code);
    public List<Device> findByRoomIdAndEnable(Integer RoomId,Integer Enable);
    public List<Device> findByRoomId(Integer RoomId);

    public List<Device> findByManagerId(Integer ManagerId);


}
