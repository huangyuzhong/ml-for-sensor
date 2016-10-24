package com.device.inspect.common.repository.firm;

import com.device.inspect.common.model.firm.Room;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Administrator on 2016/7/8.
 */
public interface RoomRepository extends CrudRepository<Room,Integer> {
    public List<Room> findByFloorIdAndEnable(Integer FloorId,Integer Enable);
    public List<Room> findByFloorId(Integer FloorId);
}
