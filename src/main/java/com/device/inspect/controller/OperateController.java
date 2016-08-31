package com.device.inspect.controller;

import com.device.inspect.common.model.charater.Role;
import com.device.inspect.common.model.charater.RoleAuthority;
import com.device.inspect.common.model.charater.User;
import com.device.inspect.common.model.device.*;
import com.device.inspect.common.model.firm.Company;
import com.device.inspect.common.repository.charater.RoleAuthorityRepository;
import com.device.inspect.common.repository.charater.RoleRepository;
import com.device.inspect.common.repository.charater.UserRepository;
import com.device.inspect.common.repository.device.*;
import com.device.inspect.common.repository.firm.BuildingRepository;
import com.device.inspect.common.repository.firm.CompanyRepository;
import com.device.inspect.common.repository.firm.RoomRepository;
import com.device.inspect.common.repository.firm.StoreyRepository;
import com.device.inspect.common.restful.RestResponse;
import com.device.inspect.common.restful.charater.RestUser;
import com.device.inspect.common.restful.device.RestDevice;
import com.device.inspect.common.restful.device.RestDeviceType;
import com.device.inspect.controller.request.DeviceTypeRequest;
import com.device.inspect.controller.request.InspectTypeRequest;
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

    @Autowired
    private DeviceInspectRepository deviceInspectRepository;

    @Autowired
    private CompanyRepository companyRepository;

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
            device.setMaintain(map.get("maintain"));
        if (null!=map.get("maintainAlterDays"))
            device.setMaintainAlterDays(Integer.valueOf(map.get("maintainAlterDays")));
        if (null!=map.get("model"))
            device.setModel(map.get("model"));
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
        deviceRepository.save(device);

        return new RestResponse(new RestDevice(device));
    }

    @RequestMapping(value = "/device/data/{deviceId}")
    public RestResponse operateDeviceData(@PathVariable Integer deviceId,@RequestBody DeviceTypeRequest request){
        Device device = deviceRepository.findOne(deviceId);
        if (null == device)
            return new RestResponse("设备信息出错！",1005,null);
        if (null!=request.getList()&&request.getList().size()>0){
            for (InspectTypeRequest inspectTypeRequest:request.getList()){
                DeviceInspect deviceInspect = deviceInspectRepository.
                        findByInspectTypeIdAndDeviceId(inspectTypeRequest.getId(), deviceId);

                deviceInspect.setStandard(Float.valueOf(inspectTypeRequest.getStandard()));
                deviceInspect.setHighDown(Float.valueOf(inspectTypeRequest.getHighDown()));
                deviceInspect.setHighUp(Float.valueOf(inspectTypeRequest.getHighUp()));
                deviceInspect.setLowUp(Float.valueOf(inspectTypeRequest.getLowUp()));
                deviceInspect.setLowDown(Float.valueOf(inspectTypeRequest.getLowDown()));

                deviceInspectRepository.save(deviceInspect);
            }
        }
        return new RestResponse(new RestDevice(device));
    }

    @RequestMapping(value = "/create/user/{name}")
    public RestResponse createNewUser(@PathVariable String name,@RequestParam Map<String,String> map){
        User user = userRepository.findByName(name);
        if (null == user)
            return new RestResponse("用户信息错误！",1005,null);
        User child = new User();
        RoleAuthority roleAuthority = roleAuthorityRepository.findOne(user.getRole().getRoleAuthority().getChild());
        if (null==roleAuthority)
            return new RestResponse("权限不足，无法添加！",null);
        Company company = null;
        if (null==map.get("companyId"))
            company = companyRepository.findOne(Integer.valueOf(map.get("companyId")));
        if (null==map.get("name"))
            return new RestResponse("登录名不能为空！",1005,null);
        User judge = userRepository.findByName(map.get("name"));
        if (judge!=null)
            return new RestResponse("登录名已存在！",1005,null);
        child.setCompany(company);
        child.setCreateDate(new Date());
        child.setName(map.get("name"));
        child.setPassword(map.get("password"));
        child.setUserName(map.get("userName"));
        child.setDepartment(map.get("department"));
        child.setJobNum(map.get("jobNum"));
        child.setJob(map.get("job"));
        userRepository.save(user);
        Role role = new Role();
        role.setAuthority(roleAuthority.getName());
        role.setRoleAuthority(roleAuthority);
        role.setUser(user);
        roleRepository.save(role);
        return new RestResponse("创建成功！",null);
    }

    @RequestMapping(value = "/update/user/{name}")
    public RestResponse updateUserMessage(@PathVariable String name,@RequestParam Map<String,String> param){
        User user = userRepository.findByName(name);
        if (null == user)
            return new RestResponse("用户信息错误！",1005,null);
        if (null!=param.get("userName"))
            user.setUserName(param.get("userName"));
        if (null!=param.get("department"))
            user.setDepartment(param.get("department"));
        if (null!=param.get("jobNum"))
            user.setJobNum(param.get("jobNum"));
        if (null!=param.get("job"))
            user.setJob(param.get("job"));
        if (null!=param.get("password"))
            user.setPassword(param.get("password"));
        if (null!=param.get("mobile"))
            user.setMobile(param.get("mobile"));
        if (null!=param.get("telephone"))
            user.setTelephone(param.get("telephone"));
        if (null!=param.get("email"))
            user.setEmail(param.get("email"));
        userRepository.save(user);
        return new RestResponse(new RestUser(user));
    }

}
