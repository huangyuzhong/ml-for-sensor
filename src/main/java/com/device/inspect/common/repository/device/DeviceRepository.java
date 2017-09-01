package com.device.inspect.common.repository.device;

import com.device.inspect.common.model.device.Device;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Date;

/**
 * Created by Administrator on 2016/7/8.
 */
public interface DeviceRepository extends CrudRepository<Device,Integer> {
    public Device findByCode(String Code);
    public List<Device> findByRoomIdAndEnable(Integer RoomId,Integer Enable);
    public List<Device> findByRoomId(Integer RoomId);
    public Integer countByRoomIdAndEnableAndLastYellowAlertTimeAfter(Integer RoomId, Integer Enable, Date time);
    public Integer countByRoomIdAndEnableAndLastRedAlertTimeAfter(Integer RoomId, Integer Enable, Date time);
    public List<Device> findByRoomIdAndEnableAndLastYellowAlertTimeAfter(Integer RoomId, Integer Enable, Date time);
    public List<Device> findByRoomIdAndEnableAndLastRedAlertTimeAfter(Integer RoomId, Integer Enable, Date time);

    public List<Device> findByRoomIdAndEnableAndLastActivityTimeAfter(Integer RoomId, Integer Enable, Date time);
    public List<Device> findByRoomIdAndEnableAndLastActivityTimeBefore(Integer RoomId, Integer Enable, Date time);

    public List<Device> findByManagerId(Integer ManagerId);
    public List<Device> findByEnable(Integer Enable);
    public Integer countByRoomIdAndEnable(Integer RoomId, Integer Enable);
    public Device findById(Integer Id);
    public Device save(Device device);

}
