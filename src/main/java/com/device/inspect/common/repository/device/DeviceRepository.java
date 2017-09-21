package com.device.inspect.common.repository.device;

import com.device.inspect.common.model.device.Device;
import com.device.inspect.common.restful.device.RestDeviceIdAndName;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Date;
import java.util.Set;

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

    @Query(value="select d.model from device d where d.type_id=?1 and d.manager_user_id=?2 and d.model<>'' and d.model is not NULL", nativeQuery=true)
    public Set<String> findModelByDeviceTypeId(Integer deviceTypeId, Integer ManagerId);

    public List<Device> findByModelAndManagerIdAndDeviceTypeId(String model, Integer ManagerId, Integer TypeId);
    public List<Device> findByManagerIdAndDeviceTypeId(Integer ManagerId, Integer TypeId);
}
