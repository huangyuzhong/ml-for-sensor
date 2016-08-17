package com.device.inspect.controller;

import com.device.inspect.common.model.charater.RoleAuthority;
import com.device.inspect.common.model.charater.User;
import com.device.inspect.common.model.device.Device;
import com.device.inspect.common.model.device.DeviceType;
import com.device.inspect.common.model.device.DeviceTypeInspect;
import com.device.inspect.common.model.device.InspectType;
import com.device.inspect.common.repository.charater.RoleAuthorityRepository;
import com.device.inspect.common.repository.charater.RoleRepository;
import com.device.inspect.common.repository.charater.UserRepository;
import com.device.inspect.common.repository.device.DeviceRepository;
import com.device.inspect.common.repository.device.DeviceTypeInspectRepository;
import com.device.inspect.common.repository.device.DeviceTypeRepository;
import com.device.inspect.common.repository.device.InspectTypeRepository;
import com.device.inspect.common.repository.firm.BuildingRepository;
import com.device.inspect.common.repository.firm.RoomRepository;
import com.device.inspect.common.repository.firm.StoreyRepository;
import com.device.inspect.common.restful.RestResponse;
import com.device.inspect.common.restful.device.RestDeviceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import java.security.Principal;
import java.util.ArrayList;
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

    @RequestMapping(value = "/operate/device/type")
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

//    @RequestMapping(value = "")
//    public RestResponse opereateBuilding(Principal principal,@RequestParam String userName,
//            @RequestParam String name,){
//
//    }

    @RequestMapping(value = "/operate/device")
    public RestResponse operateDevice(Principal principal,@RequestParam Map<String,String> map){
        Device device = new Device();
//        device.setCode();


        return null;
    }

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

}
