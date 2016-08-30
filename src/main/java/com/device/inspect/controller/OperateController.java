package com.device.inspect.controller;

import com.device.inspect.common.model.charater.RoleAuthority;
import com.device.inspect.common.model.charater.User;
import com.device.inspect.common.model.device.*;
import com.device.inspect.common.repository.charater.RoleAuthorityRepository;
import com.device.inspect.common.repository.charater.RoleRepository;
import com.device.inspect.common.repository.charater.UserRepository;
import com.device.inspect.common.repository.device.*;
import com.device.inspect.common.repository.firm.BuildingRepository;
import com.device.inspect.common.repository.firm.RoomRepository;
import com.device.inspect.common.repository.firm.StoreyRepository;
import com.device.inspect.common.restful.RestResponse;
import com.device.inspect.common.restful.device.RestDevice;
import com.device.inspect.common.restful.device.RestDeviceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/1.
 */
@RestController
@RequestMapping(value = "/api/rest/operate")
public class OperateController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private StoreyRepository storeyRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DeviceTypeRepository deviceTypeRepository;

    @Autowired
    private InspectTypeRepository inspectTypeRepository;

    @Autowired
    private DeviceTypeInspectRepository deviceTypeInspectRepository;

    @Autowired
    private RoleAuthorityRepository roleAuthorityRepository;

    @Autowired
    private DeviceFloorRepository deviceFloorRepository;

    @RequestMapping(value = "/device/type")
    public RestResponse operateDeviceType(Principal principal,@RequestParam Map<String,String> map){
        User user = userRepository.findByName(principal.getName());
        DeviceType deviceType = new DeviceType();
        deviceType.setName(map.get("name"));
        deviceTypeRepository.save(deviceType);
        List<DeviceTypeInspect> list = new ArrayList<DeviceTypeInspect>();
        String[] types = map.get("types").split(",");
        for (String str : types){
            InspectType inspectType = inspectTypeRepository.findOne(Integer.valueOf(str));

            DeviceTypeInspect deviceTypeInspect = new DeviceTypeInspect();
            deviceTypeInspect.setDeviceType(deviceType);
            deviceTypeInspect.setInspectType(inspectType);
            deviceTypeInspectRepository.save(deviceTypeInspect);
            list.add(deviceTypeInspect);
        }
        deviceType.setDeviceTypeInspectList(list);
        return new RestResponse(new RestDeviceType(deviceType));
    }

    /**
     * type 0 是添加  1是修改
     * @param deviceId
     * @param map
     * @return
     */
    @RequestMapping(value = "/device/floor/{deviceId}")
    public RestResponse operateDeviceFloor(@PathVariable Integer deviceId,@RequestParam Map<String,String> map){
        Device device = deviceRepository.findOne(deviceId);
        if (null == device||null==map.get("type"))
            return new RestResponse("设备信息出错！",1005,null);
        DeviceFloor deviceFloor = new DeviceFloor();

        if (map.get("type").equals("1")){
            if (map.get("floorId")==null)
                return new RestResponse("设备层信息出错！",1005,null);
            deviceFloor = deviceFloorRepository.findOne(Integer.valueOf(map.get("floorId")));
            if (null == deviceFloor)
                return new RestResponse("设备层信息出错！",1005,null);
            if (null!=map.get("floorNum"))
                deviceFloor.setFloorNum(Integer.valueOf(map.get("floorNum")));

        }else {
            deviceFloor.setDevice(device);
            deviceFloor.setFloorNum(null == map.get("floorNum") ? null : Integer.valueOf(map.get("floorNum")));
        }
        deviceFloor.setScientist(map.get("scientist"));
        deviceFloor.setName(map.get("name"));
        deviceFloor.setEmail(map.get("email"));
        deviceFloor.setMobile(map.get("mobile"));
        deviceFloor.setProductNum(map.get("productNum")==null?null:Integer.valueOf(map.get("productNum")));
        deviceFloorRepository.save(deviceFloor);

        return new RestResponse(new RestDevice(device));

    }

//    @RequestMapping(value = "")
//    public RestResponse opereateBuilding(Principal principal,@RequestParam String userName,
//            @RequestParam String name,){
//
//    }


    @RequestMapping(value = "/create/user/{name}")
    public RestResponse createNewUser(Principal principal,@PathVariable String name,@RequestParam Map<String,String> map){
        User user = userRepository.findByName(name);
        RoleAuthority roleAuthority = roleAuthorityRepository.findOne(user.getRole().getRoleAuthority().getChild());
        if (null == roleAuthority)
            return new RestResponse("权限不足",1005,null);

        User under = new User();
//        user.setName();

        under.setEmail(map.get("email") == null ? null : map.get("email"));
//        under.setName();

        return null;
    }

    @RequestMapping(value = "/device/{deviceId}")
    public RestResponse operateDevice(@PathVariable Integer deviceId,@RequestParam Map<String,String> map){
        Device device = deviceRepository.findOne(deviceId);
        if (null == device)
            return new RestResponse("设备信息出错！",1005,null);
        if (null!=map.get("name"))
            device.setName(map.get("name"));
        if (null!=map.get("creator"))
            device.setCreator(map.get("creator"));
        if (null!=map.get("maintain"))
            device.setCreator(map.get("maintain"));
        if (null!=map.get("maintainAlterDays"))
            device.setCreator(map.get("maintainAlterDays"));
        if (null!=map.get("model"))
            device.setCreator(map.get("model"));
        if (null!=map.get("purchase"))
            device.setPurchase(new Date());
        if (null!=map.get("maintainDate"))
            device.setMaintainDate(new Date());
        if(null!=map.get("managerId")){
            User user = userRepository.findOne(Integer.valueOf(map.get("managerId")));
            if (null!=user){
                device.setManager(user);
            }
        }

        return new RestResponse(new RestDevice(device));
    }

//    @RequestMapping(value = "/device/data/{deviceId}")
//    public RestResponse operateDeviceData(@PathVariable Integer deviceId,@RequestBody ){
//        Device device = deviceRepository.findOne(deviceId);
//        if (null == device)
//            return new RestResponse("设备信息出错！",1005,null);
//
//    }

}
