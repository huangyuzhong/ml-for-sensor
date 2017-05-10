package com.device.inspect.common.service;

import com.device.inspect.common.model.device.Device;
import com.device.inspect.common.model.firm.Building;
import com.device.inspect.common.model.firm.Room;
import com.device.inspect.common.model.firm.Storey;

/**
 * Created by zyclincoln on 5/11/17.
 */
public class GetDeviceAddress {
    public static String getDeviceAddress(Device device){
        Room room = device.getRoom();
        Storey floor = room.getFloor();
        Building building = floor.getBuild();
        String location = new String();
        if(building != null){
            location += building.getName() + " ";
        }
        if(floor != null){
            location += floor.getName() + " ";
        }
        if(room != null){
            location += room.getName();
        }
        return location;
    }
}
