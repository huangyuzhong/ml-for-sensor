package com.device.inspect.controller;

import com.device.inspect.Application;
import com.device.inspect.common.model.charater.Role;
import com.device.inspect.common.model.charater.RoleAuthority;
import com.device.inspect.common.model.charater.User;
import com.device.inspect.common.model.device.*;
import com.device.inspect.common.model.firm.Building;
import com.device.inspect.common.model.firm.Company;
import com.device.inspect.common.model.firm.Room;
import com.device.inspect.common.model.firm.Storey;
import com.device.inspect.common.model.record.MessageSend;
import com.device.inspect.common.repository.charater.RoleAuthorityRepository;
import com.device.inspect.common.repository.charater.RoleRepository;
import com.device.inspect.common.repository.charater.UserRepository;
import com.device.inspect.common.repository.device.*;
import com.device.inspect.common.repository.firm.BuildingRepository;
import com.device.inspect.common.repository.firm.CompanyRepository;
import com.device.inspect.common.repository.firm.RoomRepository;
import com.device.inspect.common.repository.firm.StoreyRepository;
import com.device.inspect.common.repository.record.MessageSendRepository;
import com.device.inspect.common.restful.RestResponse;
import com.device.inspect.common.restful.charater.RestUser;
import com.device.inspect.common.restful.device.RestDevice;
import com.device.inspect.common.restful.device.RestDeviceType;
import com.device.inspect.common.service.MessageSendService;
import com.device.inspect.common.util.transefer.UserRoleDifferent;
import com.device.inspect.controller.request.DeviceTypeRequest;
import com.device.inspect.controller.request.InspectTypeRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import java.io.File;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    private static final Logger LOGGER = LogManager.getLogger(OperateController.class);

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

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private DeviceFileRepository deviceFileRepository;

    @Autowired
    private ScientistDeviceRepository scientistDeviceRepository;
    @Autowired
    private MessageSendRepository messageSendRepository;

    private User judgeByPrincipal(Principal principal){
        if (null == principal||null==principal.getName())
            throw new UsernameNotFoundException("You are not login!");
        User user = userRepository.findByName(principal.getName());
        if (null==user)
            throw new UsernameNotFoundException("user not found!");
        return user;
    }

    /**
     * 操作设备层
     * type 0 是添加设备层  1是修改设备层
     * @param deviceId
     * @param map
     * @return
     */
    @RequestMapping(value = "/device/floor/{deviceId}")
    public RestResponse operateDeviceFloor(Principal principal,@PathVariable Integer deviceId,@RequestParam Map<String,String> map){
        User user = judgeByPrincipal(principal);
        if (!UserRoleDifferent.userScientistConfirm(user))
            return new RestResponse("您的权限不足！",1005,null);

        Device device = deviceRepository.findOne(deviceId);
        if (null == device||null==map.get("type"))
            return new RestResponse("设备信息出错！",1005,null);
        ScientistDevice scientistDevice = scientistDeviceRepository.findByScientistIdAndDeviceId(user.getId(),deviceId);
        if (null == scientistDevice)
            return new RestResponse("您的权限不足！",1005,null);
        DeviceFloor deviceFloor = new DeviceFloor();

        if (map.get("type").equals("1")){
            if (map.get("floorId")==null)
                return new RestResponse("设备层信息出错！",1005,null);
            deviceFloor = deviceFloorRepository.findOne(Integer.valueOf(map.get("floorId")));
            if (null == deviceFloor)
                return new RestResponse("设备层信息出错！",1005,null);
            if (null!=map.get("floorNum"))
                deviceFloor.setFloorNum(Integer.valueOf(map.get("floorNum")));
            deviceFloor.setScientist(user);

        }else {
            deviceFloor.setDevice(device);
            deviceFloor.setFloorNum(null == map.get("floorNum") ? null : Integer.valueOf(map.get("floorNum")));
            deviceFloor.setEnable(1);
            deviceFloor.setScientist(user);
        }
//        deviceFloor.setScientist(map.get("scientist"));
        deviceFloor.setName(map.get("name"));
//        deviceFloor.setEmail(map.get("email"));
//        deviceFloor.setMobile(map.get("mobile"));
        deviceFloor.setProductNum(map.get("productNum")==null?null:Integer.valueOf(map.get("productNum")));
        deviceFloor.setType(map.get("productType")==null?null:map.get("productType"));
        if (null!=map.get("effective")){
            Date date = null;
            try
            {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                date = sdf.parse(map.get("effective"));
            }
            catch (ParseException e)
            {

            }
            deviceFloor.setOverDate(date);
        }
        deviceFloorRepository.save(deviceFloor);
        return new RestResponse(new RestDevice(device));
    }

    /**
     * 删除设备层
     * @param principal
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete/device/floor/{id}")
    public RestResponse deleteDeviceFloorById(Principal principal,@PathVariable Integer id){
        DeviceFloor deviceFloor = deviceFloorRepository.findOne(id);
        if (null==deviceFloor)
            return new RestResponse();
        User user = judgeByPrincipal(principal);
        if (!UserRoleDifferent.userScientistConfirm(user))
            return new RestResponse("您的权限不足！",1005,null);
        if(null==deviceFloor.getScientist()||!deviceFloor.getScientist().getId().equals(user.getId()))
            return new RestResponse("您的权限不足！",1005,null);
        deviceFloor.setEnable(0);
        deviceFloorRepository.save(deviceFloor);
        return new RestResponse("删除成功！",null);
    }

    /**
     * 修改设备基本信息
     * @param deviceId
     * @param map
     * @return
     */
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
        if (null!=map.get("purchase")){
            Date date = null;
            try
            {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                date = sdf.parse(map.get("purchase"));
            }
            catch (ParseException e)
            {

            }
            device.setPurchase(date);
        }

        if (null!=map.get("maintainDate")){
            Date date = null;
            try
            {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                date = sdf.parse(map.get("maintainDate"));
            }
            catch (ParseException e)
            {

            }
            device.setMaintainDate(date);
        }

        if(null!=map.get("managerId")){
            User user = userRepository.findOne(Integer.valueOf(map.get("managerId")));
            if (null!=user){
                device.setManager(user);
            }
        }
        deviceRepository.save(device);

        return new RestResponse(new RestDevice(device));
    }

    /**
     * 获取设备报警参数
     * @param deviceId
     * @return
     */
    @RequestMapping(value = "/get/device/parameter/{deviceId}")
    public RestResponse getDeviceParameter(@PathVariable Integer deviceId){
        Device device = deviceRepository.findOne(deviceId);
        if (null==device)
            return new RestResponse("设备信息出错！",1005,null);
        DeviceTypeRequest deviceTypeRequest = new DeviceTypeRequest();
        List<InspectTypeRequest> list = new ArrayList<InspectTypeRequest>();
        for (DeviceInspect deviceInspect : device.getDeviceInspectList()){
            InspectTypeRequest request = new InspectTypeRequest();
            if (deviceInspect.getStandard()==null)
                request.setChosed(false);
            else {
                request.setChosed(true);
            }

            request.setHighUp(null == deviceInspect.getHighUp() ? null : deviceInspect.getHighUp().toString());
            request.setHighDown(deviceInspect.getHighDown() == null ? null : deviceInspect.getHighDown().toString());
            request.setLowUp(deviceInspect.getLowUp() == null ? null : deviceInspect.getLowUp().toString());
            request.setLowDown(deviceInspect.getLowDown() == null ? null : deviceInspect.getLowDown().toString());
            request.setStandard(deviceInspect.getStandard()==null?null:deviceInspect.getStandard().toString());
            request.setId(deviceInspect.getInspectType().getId());
            request.setName(deviceInspect.getInspectType().getName());

            list.add(request);
        }
        deviceTypeRequest.setId(deviceId);
        deviceTypeRequest.setName(device.getDeviceType().getName());
        deviceTypeRequest.setList(list);
        return new RestResponse(deviceTypeRequest);
    }

    /**
     * 修改单个设备的参数
     * @param deviceId
     * @param request
     * @return
     */
    @RequestMapping(value = "/device/parameter/{deviceId}")
    public RestResponse operateDeviceData(@PathVariable Integer deviceId,@RequestBody DeviceTypeRequest request){
        Device device = deviceRepository.findOne(deviceId);
        if (null == device)
            return new RestResponse("设备信息出错！",1005,null);
        if (null!=request.getList()&&request.getList().size()>0){
            for (InspectTypeRequest inspectTypeRequest:request.getList()){
                DeviceInspect deviceInspect = deviceInspectRepository.
                        findByInspectTypeIdAndDeviceId(inspectTypeRequest.getId(), deviceId);
                if (null!=inspectTypeRequest.getStandard())
                    deviceInspect.setStandard(Float.valueOf(inspectTypeRequest.getStandard()));
                if (null!=inspectTypeRequest.getHighDown())
                    deviceInspect.setHighDown(Float.valueOf(inspectTypeRequest.getHighDown()));
                if (null!=inspectTypeRequest.getHighUp())
                    deviceInspect.setHighUp(Float.valueOf(inspectTypeRequest.getHighUp()));
                if (null!=inspectTypeRequest.getLowUp())
                    deviceInspect.setLowUp(Float.valueOf(inspectTypeRequest.getLowUp()));
                if (null!=inspectTypeRequest.getLowDown())
                    deviceInspect.setLowDown(Float.valueOf(inspectTypeRequest.getLowDown()));

                deviceInspectRepository.save(deviceInspect);
            }
        }
        return new RestResponse(new RestDevice(device));
    }


    /**
     * 创建新用户
     * @param
     * @param map
     * @return
     */
    @RequestMapping(value = "/create/user")
    public RestResponse createNewUser(Principal principal,@RequestParam Map<String,String> map){
        User user = judgeByPrincipal(principal);
        if (null == user)
            return new RestResponse("用户信息错误！",1005,null);
        User child = new User();
//        RoleAuthority roleAuthority = roleAuthorityRepository.findOne(user.getRole().getRoleAuthority().getChild());

        List<RoleAuthority> roleAuthorityList = new ArrayList<RoleAuthority>();
        if (null!=user.getRoles())
            for (Role role:user.getRoles()){
                roleAuthorityList.addAll(roleAuthorityRepository.findByParent(role.getRoleAuthority().getId()));
            }
        if (UserRoleDifferent.userFirmManagerConfirm(user))
            if (null==map.get("role")||"".equals(map.get("role")))
                return new RestResponse("必须选定添加员工角色！",1005,null);
        if (null==roleAuthorityList)
            return new RestResponse("权限不足，无法添加！",1005,null);

        if (null==map.get("name"))
            return new RestResponse("登录名不能为空！",1005,null);
        User judge = userRepository.findByName(map.get("name"));
        if (judge!=null)
            return new RestResponse("登录名已存在！",1005,null);

        if(UserRoleDifferent.userFirmManagerConfirm(user))
            child.setCompany(user.getCompany());
        child.setCreateDate(new Date());
        child.setName(map.get("name"));
        child.setPassword(null==map.get("password")?"123":map.get("password"));
        child.setUserName(map.get("userName"));
        child.setDepartment(map.get("department"));
        child.setJobNum(map.get("jobNum"));
        child.setJob(map.get("job"));
        userRepository.save(child);
        if (roleAuthorityList.size()==1){
            Role role = new Role();
            role.setAuthority(roleAuthorityList.get(0).getName());
            role.setRoleAuthority(roleAuthorityList.get(0));
            role.setUser(child);
            roleRepository.save(role);
        }else {
            if (UserRoleDifferent.userFirmManagerConfirm(user)){
                String[] roles = map.get("role").split(",");
                if (null!=roles&&roles.length>0){
                    for (String roleName:roles){
                        RoleAuthority roleAuthority = roleAuthorityRepository.findByName(roleName);
                        if (null!=roleAuthority){
                            Role role = new Role();
                            role.setAuthority(roleAuthority.getName());
                            role.setRoleAuthority(roleAuthority);
                            role.setUser(child);
                            roleRepository.save(role);
                        }
                    }
                }
            }
        }
        return new RestResponse("创建成功！",null);
    }

    /**
     * 修改用户信息
     * @param
     * @param param
     * @return
     */
    @RequestMapping(value = "/update/user",method = RequestMethod.GET)
    public RestResponse updateUserMessage(Principal principal,@RequestParam Map<String,String> param){
        User user = judgeByPrincipal(principal);
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
        if (null!=param.get("mobile"))
            user.setMobile(param.get("mobile"));
        if (null!=param.get("telephone"))
            user.setTelephone(param.get("telephone"));
        if (null!=param.get("email"))
            user.setEmail(param.get("email"));
        userRepository.save(user);
        return new RestResponse(new RestUser(user));
    }

    /**
     * 修改设备种类及其参数
     * @param principal
     * @param deviceTypeReq
     * @return
     */
    @RequestMapping(value = "/deviceType")
    public RestResponse operateDeviceType(Principal principal,@RequestBody DeviceTypeRequest deviceTypeReq){
        User user = judgeByPrincipal(principal);
        if (null == user)
            return new RestResponse("手机号出错！", null);
        DeviceType deviceType = new DeviceType();
        List<DeviceTypeInspect> deviceTypeInspects = new ArrayList<DeviceTypeInspect>();

        if (UserRoleDifferent.userFirmManagerConfirm(user)||UserRoleDifferent.userStartWithService(user)) {
            if (null != deviceTypeReq.getId()) {
                deviceType = deviceTypeRepository.findOne(deviceTypeReq.getId());
                if (null==deviceType)
                    return new RestResponse("当前设备不存在！",1005,null);
                if (deviceType.getCompany()==null&&!UserRoleDifferent.userStartWithService(user)){
                    return new RestResponse("企业管理员无法修改平台设备种类！",1005,null);
                }
                deviceType.setName(deviceTypeReq.getName());
                deviceTypeRepository.save(deviceType);
//                deviceTypeInspects = deviceType.getDeviceTypeInspectList();
                if (null != deviceTypeReq && deviceTypeReq.getList().size() > 0) {
                    for (InspectTypeRequest inspectTypeRequest : deviceTypeReq.getList()) {
                        if (inspectTypeRequest.isChosed()){
                            InspectType inspectType = inspectTypeRepository.findOne(inspectTypeRequest.getId());
                            DeviceTypeInspect deviceTypeInspect = deviceTypeInspectRepository.
                                    findByDeviceTypeIdAndInspectTypeId(deviceType.getId(), inspectType.getId());
                            if (null != deviceTypeInspect) {
                                deviceTypeInspect.setDeviceType(deviceType);
                                deviceTypeInspect.setInspectType(inspectType);
                                deviceTypeInspect.setHighDown(Float.valueOf(inspectTypeRequest.getHighDown()));
                                deviceTypeInspect.setHighUp(Float.valueOf(inspectTypeRequest.getHighUp()));
                                deviceTypeInspect.setStandard(Float.valueOf(inspectTypeRequest.getStandard()));
                                deviceTypeInspect.setLowDown(Float.valueOf(inspectTypeRequest.getLowDown()));
                                deviceTypeInspect.setLowUp(Float.valueOf(inspectTypeRequest.getLowUp()));
                                deviceTypeInspect.setLowAlter(null == inspectTypeRequest.getLowAlter() ? 10 : inspectTypeRequest.getLowAlter());
                                deviceTypeInspects.add(deviceTypeInspect);
//                                deviceTypeInspectRepository.save(deviceTypeInspect);
                            }
                        }
                    }
                }
                deviceTypeInspectRepository.save(deviceTypeInspects);
            } else {
                deviceType.setEnable(1);
                if (UserRoleDifferent.userFirmManagerConfirm(user))
                    deviceType.setCompany(user.getCompany());
                deviceType.setName(deviceTypeReq.getName());
                deviceTypeRepository.save(deviceType);
                if (null != deviceTypeReq && deviceTypeReq.getList().size() > 0) {
                    for (InspectTypeRequest inspectTypeRequest : deviceTypeReq.getList()) {
                        if (inspectTypeRequest.isChosed()){
                            InspectType inspectType = inspectTypeRepository.findOne(inspectTypeRequest.getId());
                            DeviceTypeInspect deviceTypeInspect = new DeviceTypeInspect();
                            deviceTypeInspect.setDeviceType(deviceType);
                            deviceTypeInspect.setInspectType(inspectType);
                            deviceTypeInspect.setHighDown(Float.valueOf(inspectTypeRequest.getHighDown()));
                            deviceTypeInspect.setHighUp(Float.valueOf(inspectTypeRequest.getHighUp()));
                            deviceTypeInspect.setStandard(Float.valueOf(inspectTypeRequest.getStandard()));
                            deviceTypeInspect.setLowDown(Float.valueOf(inspectTypeRequest.getLowDown()));
                            deviceTypeInspect.setLowUp(Float.valueOf(inspectTypeRequest.getLowUp()));
                            deviceTypeInspect.setLowAlter(null == inspectTypeRequest.getLowAlter() ? 10 : inspectTypeRequest.getLowAlter());
                            deviceTypeInspects.add(deviceTypeInspect);
                        }
                    }
                }
                deviceTypeInspectRepository.save(deviceTypeInspects);
            }
        } else {
            return new RestResponse("权限不足！",1005,null);
        }
        return new RestResponse(new RestDeviceType(deviceType));
    }

    /**
     * 删除设备文件
     * @param fileId
     * @param deviceId
     * @return
     */
    @RequestMapping(value = "/delete/file/{fileId}")
    public RestResponse deleteFileFromDevice(@PathVariable Integer fileId,@RequestParam Integer deviceId){
        Files files = fileRepository.findOne(fileId);
        if (null==files)
            return new RestResponse("该文件不存在！",1005,null);
        DeviceFile deviceFile = deviceFileRepository.findByDeviceIdAndFileId(deviceId,fileId);
        if (null == deviceFile)
            return new RestResponse("该设备无此文件！",1005,null);
        deviceFileRepository.delete(deviceFile);
        fileRepository.delete(files);
        return new RestResponse("删除成功！",null);
    }

    /**
     * 删除设备
     * @param principal
     * @param deviceId
     * @param enable
     * @return
     */
    @RequestMapping(value = "/manager/device/{deviceId}")
    public RestResponse deleteDeviceById(Principal principal,@PathVariable Integer deviceId,@RequestParam Integer enable){
        User user = judgeByPrincipal(principal);
        Device device = deviceRepository.findOne(deviceId);
        if (null==device)
            return new RestResponse("该设备不存在！",1005,null);
        if (device.getManager().getId().equals(user.getId())||(UserRoleDifferent.userFirmManagerConfirm(user)&&
        device.getManager().getCompany().getId().equals(user.getCompany().getId()))) {
            device.setEnable(enable);
            deviceRepository.save(device);
            return new RestResponse("删除成功！",null);
        }
        return new RestResponse("权限不足！",1005,null);
    }

    /**
     * 企业管理员删除室
     * @param principal
     * @param roomId
     * @param enable
     * @return
     */
    @RequestMapping(value = "/manager/room/{roomId}")
    public RestResponse deleteRoomById(Principal principal,@PathVariable Integer roomId,@RequestParam Integer enable){
        User user = judgeByPrincipal(principal);
        Room room = roomRepository.findOne(roomId);
        if (null == room)
            return new RestResponse("该房间不存在！",1005,null);
        if (UserRoleDifferent.userFirmManagerConfirm(user)&&user.getCompany().getId().equals(room.getFloor().getBuild().getCompany().getId())){
            List<Device> list = deviceRepository.findByRoomId(roomId);
            if (null!=list)
                for (Device device:list){
                    device.setEnable(enable);
                }
            deviceRepository.save(list);
            room.setEnable(enable);
            roomRepository.save(room);
            return new RestResponse("删除成功！",null);
        }
        return new RestResponse("权限不足！",1005,null);
    }

    /**
     * 企业管理员删除楼层
     * @param principal
     * @param floorId
     * @param enable
     * @return
     */
    @RequestMapping(value = "/manager/floor/{floorId}")
    public RestResponse deleteFloorById(Principal principal,@PathVariable Integer floorId,@RequestParam Integer enable){
        User user = judgeByPrincipal(principal);
        Storey floor = storeyRepository.findOne(floorId);
        if (null==floor)
            return new RestResponse("该楼层不存在！",1005,null);
        if (UserRoleDifferent.userFirmManagerConfirm(user)&&user.getCompany().getId().equals(floor.getBuild().getCompany().getId())){
            List<Room> roomList = roomRepository.findByFloorId(floorId);
            if (null!=roomList) {
                for (Room room : roomList) {
                    List<Device> deviceListlist = deviceRepository.findByRoomId(room.getId());
                    if (null != deviceListlist)
                        for (Device device : deviceListlist) {
                            device.setEnable(enable);
                        }
                    deviceRepository.save(deviceListlist);
                    room.setEnable(enable);
                }
                roomRepository.save(roomList);
            }
            floor.setEnable(enable);
            storeyRepository.save(floor);
            return new RestResponse("删除成功！",null);
        }
        return new RestResponse("权限不足！",1005,null);
    }

    /**
     * 企业管理员删除楼
     * @param principal
     * @param buildId
     * @param enable
     * @return
     */
    @RequestMapping(value = "/manager/build/{buildId}")
    public RestResponse deleteBuildById(Principal principal,@PathVariable Integer buildId,@RequestParam Integer enable){
        User user = judgeByPrincipal(principal);
        Building building = buildingRepository.findOne(buildId);
        if (null == building)
            return new RestResponse("该建筑不存在！",1005,null);
        if(UserRoleDifferent.userFirmManagerConfirm(user)&&user.getCompany().getId().equals(building.getCompany().getId())){
            List<Storey> floorList = storeyRepository.findByBuildId(buildId);
            if (null!=floorList){
                for (Storey floor:floorList){
                    List<Room> roomList = roomRepository.findByFloorId(floor.getId());
                    if (null!=roomList) {
                        for (Room room : roomList) {
                            List<Device> deviceListlist = deviceRepository.findByRoomId(room.getId());
                            if (null != deviceListlist)
                                for (Device device : deviceListlist) {
                                    device.setEnable(enable);
                                }
                            deviceRepository.save(deviceListlist);
                            room.setEnable(enable);
                        }
                        roomRepository.save(roomList);
                    }
                    floor.setEnable(enable);
                }
                storeyRepository.save(floorList);
            }
            building.setEnable(enable);
            buildingRepository.save(building);
            return new RestResponse("删除成功！",null);
        }
        return new RestResponse("权限不足！",1005,null);
    }

    /**
     * 删除设备种类
     * @param principal
     * @param typeId
     * @param enable
     * @return
     */
    @RequestMapping(value = "/manager/device/type/{typeId}")
    public RestResponse managerOperateDeviceTypeById(Principal principal,@PathVariable Integer typeId,@RequestParam Integer enable){
        User user = judgeByPrincipal(principal);
        DeviceType deviceType = deviceTypeRepository.findOne(typeId);
        if (null==deviceType.getCompany()){
            if (UserRoleDifferent.userStartWithService(user)){
                deviceType.setEnable(enable);
                deviceTypeRepository.save(deviceType);
                return new RestResponse("删除成功！",null);
            }else {
                return new RestResponse("权限不足！",1005,null);
            }
        }else {
            if (UserRoleDifferent.userFirmManagerConfirm(user)&&
                    user.getCompany().getId().equals(deviceType.getCompany().getId())){
                deviceType.setEnable(enable);
                deviceTypeRepository.save(deviceType);
                return new RestResponse("删除成功！",null);
            }else {
                return new RestResponse("权限不足！",1005,null);
            }
        }
    }

    /**
     * 当前仅支持企业用户级删除
     * @param principal
     * @return
     */
    @RequestMapping(value = "/delete/user/{userId}")
    public RestResponse deleteUserById(Principal principal, @PathVariable Integer userId,@RequestParam Integer takeId){
        try {
            User manager = judgeByPrincipal(principal);
            if (!UserRoleDifferent.userFirmManagerConfirm(manager))
                return new RestResponse("权限不足，无法删除！",1005,null);
            User old = userRepository.findOne(userId);
            User take = userRepository.findOne(takeId);
            if (null == old)
                return new RestResponse("该员工不存在，无法删除！",1005,null);
            if (null == take)
                return new RestResponse("没有交接人，无法删除！",1005,null);
            if (!take.getCompany().getId().toString().equals(manager.getCompany().getId().toString()))
                return new RestResponse("交接人权限不足，无法删除！",1005,null);
            boolean workerFlag = UserRoleDifferent.userFirmWorkerConfirm(old);
            boolean scientistFlag = UserRoleDifferent.userScientistConfirm(old);
            if (workerFlag){
                if (!UserRoleDifferent.userFirmWorkerConfirm(take)&&!UserRoleDifferent.userFirmManagerConfirm(take))
                    return new RestResponse("交接人权限不足，无法删除！",1005,null);
            }
            if (scientistFlag){
                if (!UserRoleDifferent.userScientistConfirm(take))
                    return new RestResponse("交接人权限不足，无法删除！",1005,null);
            }
            if (workerFlag){
                List<Device> deviceList = deviceRepository.findByManagerId(old.getId());
                if (null!=deviceList)
                    for (Device device:deviceList){
                        device.setManager(take);
                        deviceRepository.save(device);
                    }
            }
            if (scientistFlag){
                List<ScientistDevice> scientistDeviceList = scientistDeviceRepository.findByScientistId(old.getId());
                if (null!=scientistDeviceList)
                    for (ScientistDevice scientistDevice:scientistDeviceList){
                        ScientistDevice over = scientistDeviceRepository.findByScientistIdAndDeviceId(takeId,scientistDevice.getDevice().getId());
                        if (null==over){
                            scientistDevice.setScientist(take);
                            scientistDeviceRepository.save(over);
                        }
                    }
                List<DeviceFloor> deviceFloorList = deviceFloorRepository.findByScientistId(old.getId());
                if (null!=deviceFloorList)
                    for (DeviceFloor deviceFloor:deviceFloorList){
                        deviceFloor.setScientist(take);
                        deviceFloorRepository.save(deviceFloor);
                    }
            }
            if (null!=old.getRoles())
                for (Role role : old.getRoles()){
                    roleRepository.delete(role);
                }
            userRepository.delete(old);
            return new RestResponse("删除成功！",null);
        }catch (Exception e){
            LOGGER.error(e.getMessage());
            return new RestResponse("删除出错！",null);
        }
    }

    /**
     * 发送手机验证码
     * @param principal
     * @param
     * @return
     */
    @RequestMapping(value = "/send/mobile/verify/{mobile}")
    public RestResponse sendVerifyForMobile(Principal principal,@PathVariable String mobile){
        User user = judgeByPrincipal(principal);
        Double password = Math.random() * 9000 + 1000;
        int verify = password.intValue();
        MessageSend messageSend = new MessageSend();
        //短信发送验证码
        boolean b=MessageSendService.sendMessage(user,mobile,String.valueOf(verify),0);
        if (b)
            messageSend.setEnable(1);
        else{
            messageSend.setEnable(0);
            messageSend.setError("短信发送验证码失败");
        }
        messageSend.setUser(user);
        messageSend.setReason("verify");
        messageSend.setType("mobile");
        messageSend.setCreate(new Date());
        messageSendRepository.save(messageSend);

        user.setVerify(verify);
        if (user.getMobile()!=null&&!user.getMobile().equals(""))
            user.setBindMobile(1);
        else
            user.setBindMobile(0);

        userRepository.save(user);
        return new RestResponse(new RestUser(user));
    }

    /**
     * 发送邮箱验证码
    // * @param principal
     * @param
     * @return
     */
    @RequestMapping(value = "/send/email/verify",method = RequestMethod.POST)
    public RestResponse sendVerifyForEmail(Principal principal,
                                           @RequestBody Map<String,String> map){
        User user = judgeByPrincipal(principal);
        Double password = Math.random() * 9000 + 1000;
        int verify = password.intValue();
        MessageSend messageSend = new MessageSend();
        if (null==map.get("email")||"".equals(map.get("email")))
            return new RestResponse("参数为空！",null);
        //邮箱发送验证码
        boolean b=MessageSendService.sendEmai(user,map.get("email"),String.valueOf(verify),0);
        if (b)
            messageSend.setEnable(1);
        else{
            messageSend.setEnable(0);
            messageSend.setError("邮箱发送验证码失败");
        }
        messageSend.setReason("verify");
        messageSend.setType("email");
        messageSend.setCreate(new Date());
        messageSend.setUser(user);
        messageSendRepository.save(messageSend);

        user.setVerify(verify);
        if (user.getEmail()!=null&&!user.getEmail().equals(""))
            user.setBindEmail(1);
        else
            user.setBindEmail(0);
        userRepository.save(user);
        return new RestResponse(new RestUser(user));
    }

    /**
     * 绑定手机号
     * @param principal
     * @param mobile
     * @param verify
     * @return
     */
    @RequestMapping(value = "/update/mobile/{mobile}")
    public RestResponse updateMobileByMobile(Principal principal,@PathVariable String mobile,@RequestParam String verify){
        User user = judgeByPrincipal(principal);
        if (!user.getVerify().toString().equals(verify))
            return new RestResponse("绑定参数出错！",1005,null);
        user.setBindMobile(1);
        user.setMobile(mobile);
        userRepository.save(user);
        return new RestResponse(new RestUser(user));
    }

    /**
     * 绑定邮箱
     * @param principal
     * @param map
     * @return
     */
    @RequestMapping(value = "/update/email",method = RequestMethod.POST)
    public RestResponse updateEmailByEmail(Principal principal,@RequestBody Map<String,String> map){
        User user = judgeByPrincipal(principal);
        if(null==map.get("email")||null==map.get("verify")||"".equals(map.get("email"))||"".equals(map.get("verify")))
            return new RestResponse("请求参数出错！",1005,null);
        if (!user.getVerify().toString().equals(map.get("verify")))
            return new RestResponse("绑定参数出错！",1005,null);
        user.setBindEmail(1);
        user.setEmail(map.get("email"));
        userRepository.save(user);
        return new RestResponse(new RestUser(user));
    }

    /**
     * 修改密码
     * @param principal
     * @param old
     * @param password
     * @return
     */
    @RequestMapping(value = "/modify/password/{old}")
    public RestResponse modifyPassword(Principal principal,@PathVariable String old,@RequestParam String password){
        User user = judgeByPrincipal(principal);
        if (null==old||!old.equals(user.getPassword()))
            return new RestResponse("原密码输入有误！",1005,null);
        if (null==password||password.equals(""))
            return new RestResponse("新密码不能为空！",1005,null);
        user.setPassword(password);
        userRepository.save(user);
        return new RestResponse("修改成功！",null);
    }

    /**
     * 找回密码
     * @param name
     * @param map
     * @return
     */
    @RequestMapping(value="/forget/find/password/{name}",method = RequestMethod.POST)
    public RestResponse findPassword(@PathVariable String name,@RequestBody Map<String,String> map){
        User user=userRepository.findByName(name);
        if (null==user)
            return new RestResponse("账号输入有误！",null);
//        if (user.getBindEmail()!=1&&user.getBindMobile()!=1)
//            return new RestResponse("您未绑定手机号或邮箱！请联系管理员！",null);
        if(null==map.get("number")||"".equals(map.get("number")))
            return new RestResponse("请输入正确的手机号或验证码！",null);
        String number = map.get("number");
        if (number.equals(user.getMobile())&&user.getBindMobile()==1){
            //用户输入手机号，发送短信密码
            boolean b=MessageSendService.sendMessage(user,number,user.getPassword(),2);
            if (b){
                return new RestResponse("密码已经发送到你的手机上！",0,null);
            }else {
                return new RestResponse("短信发送失败",1005,null);
            }
        }else if (number.equals(user.getEmail())&&user.getBindEmail()==1){
            //用户输入的是邮箱，通过邮箱发送密码
            boolean b=MessageSendService.sendEmai(user,number,user.getPassword(),2);
            if (b){
                return new RestResponse("密码已经发送到你的邮箱上！",null);
            }else {
                return new RestResponse("邮件发送失败!",1005,null);
            }
        }else {
            //用户未绑定手机号或者邮箱
            return new RestResponse("未绑定手机号和邮箱，请联系管理员！");
        }
    }
}
