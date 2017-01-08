package com.device.inspect.controller;

import com.alibaba.fastjson.JSON;
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
import com.device.inspect.common.restful.device.RestDeviceInspect;
import com.device.inspect.common.restful.device.RestDeviceType;
import com.device.inspect.common.restful.device.RestDeviceVersion;
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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private DeviceFileRepository deviceFileRepository;

    @Autowired
    private ScientistDeviceRepository scientistDeviceRepository;
    @Autowired
    private MessageSendRepository messageSendRepository;

    @Autowired
    private MonitorDeviceRepository monitorDeviceRepository;

    @Autowired
    private Pt100ZeroRepository pt100ZeroRepository;

    @Autowired
    private DeviceVersionRepository deviceVersionRepository;

    private User judgeByPrincipal(Principal principal){
        if (null == principal||null==principal.getName())
            throw new UsernameNotFoundException("You are not login!");
        User user = userRepository.findByName(principal.getName());
        if (null==user)
            throw new UsernameNotFoundException("user not found!");
        return user;
    }

    /**
     * type 0 是添加  1是修改
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
     * 修改设备基本概况
     * @param deviceId
     * @param map
     * @return
     */
    @RequestMapping(value = "/device/{deviceId}")
    public RestResponse operateDevice(Principal principal,@PathVariable Integer deviceId,@RequestParam Map<String,String> map){
        User user1=judgeByPrincipal(principal);
        if (user1==null)
            return new RestResponse("用户未登陆",1005,null);
        Device device = deviceRepository.findOne(deviceId);
        if (null == device)
            return new RestResponse("设备信息出错！",1005,null);
        if (device.getManager()!=null&&device.getManager()!=user1)
            return new RestResponse("你不是此设备的设备管理员",1005,null);
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

        if(null!=map.get("managerId")&&!"undefined".equals(map.get("managerId"))&&!"".equals(map.get("managerId"))){
            User user = userRepository.findOne(Integer.valueOf(map.get("managerId")));
            if (null!=user){
                device.setManager(user);
            }
        }
        //修改坐标
        if (null!=map.get("xPoint")&&!"".equals(map.get("xPoint")))
            device.setxPoint(Float.valueOf(map.get("xPoint")));
        if (null!=map.get("yPoint")&&!"".equals(map.get("yPoint")))
            device.setyPoint(Float.valueOf(map.get("yPoint")));
        //添加科学家
        if (null!=map.get("scientist")){
            String[] scientist = map.get("scientist").split(",");
            for (String id:scientist){
                if (null!=id&&!"".equals(id)){
                    ScientistDevice scientistDevice = null;
                    User keeper = userRepository.findOne(Integer.valueOf(id));
                    if (null==keeper)
                        continue;
                    scientistDevice = scientistDeviceRepository.findByScientistIdAndDeviceId(keeper.getId(),device.getId());
                    if (null!=scientistDevice)
                        continue;
                    scientistDevice = new ScientistDevice();
                    scientistDevice.setDevice(device);
                    scientistDevice.setScientist(keeper);
                    scientistDeviceRepository.save(scientistDevice);
                }
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
     */
    @RequestMapping(value = "/device/parameter/{deviceId}")
    public RestResponse operateDeviceData(Principal principal,@PathVariable Integer deviceId,@RequestBody DeviceTypeRequest request){
        User user=judgeByPrincipal(principal);
        if (user==null)
            return new RestResponse("用户未登录",1005,null);
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
        //判断登录名已存在
        User judge=null;
        if(UserRoleDifferent.userStartWithFirm(user))
            judge= userRepository.findByName(map.get("name")+"@"+user.getCompany().getCompanyId());
        else
            judge=userRepository.findByName(map.get("name"));
        if (judge!=null)
            return new RestResponse("登录名已存在！",1005,null);
        //判断用户是企业管理员，同一个企业的的用户工号不能相同，
        if (UserRoleDifferent.userFirmManagerConfirm(user)){
            Company company=user.getCompany();
            List<User> list=userRepository.findByCompanyId(company.getId());
            if (list!=null&&list.size()>0){
                for (User user1:list){
                    if (user1.getJobNum()!=null&&!"".equals(user1.getJobNum())&&map.get("jobNum").equals(user1.getJobNum()))
                        return new RestResponse("该工号已经存在，请勿重复添加",1005,null);
                }
            }
        }
        //判断用户是平台管理员
        if (UserRoleDifferent.userServiceManagerConfirm(user)){
            List<User> list=userRepository.findAll();
            if (list!=null&&list.size()>0){
                for (User user2:list){
                    //判断用户是不是平台用户
                    if (UserRoleDifferent.userStartWithService(user2)){
                        if (user2.getJobNum()!=null&&!"".equals(user2.getJobNum())&&map.get("jobNum").equals(user2.getJobNum()))
                            return new RestResponse("该工号已经存在，请勿重复添加",1005,null);
                    }
                }
            }
        }
        //判断用户是平台管理员还是企业管理员
        if(UserRoleDifferent.userFirmManagerConfirm(user)) {
            child.setCompany(user.getCompany());
            Company company=user.getCompany();
            child.setName(map.get("name")+"@"+company.getCompanyId());
        }else {
            child.setName(map.get("name"));
        }
        child.setCreateDate(new Date());
        child.setPassword(null==map.get("password")?"123":map.get("password"));
        child.setUserName(map.get("userName"));
        child.setDepartment(map.get("department"));
        child.setJobNum(map.get("jobNum"));
        child.setJob(map.get("job"));
        child.setRemoveAlert("0");
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
     * 0 是短信  1是邮箱   2是不允许报警
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
        if (null!=param.get("removeAlert")&&!"".equals(param.get("removeAlert"))) {
            user.setRemoveAlert(param.get("removeAlert"));
            List<Device> list=deviceRepository.findByManagerId(user.getId());
            if (list!=null&&list.size()>0){
                if (param.get("removeAlert").equals("0")){
                    for (Device device :list){
                        if (device!=null)
                            device.setPushType("短信");
                    }
                }
                if (param.get("removeAlert").equals("1")){
                    for (Device device :list){
                        if (device!=null)
                            device.setPushType("邮箱");
                    }
                }
                if (param.get("removeAlert").equals("2")){
                    for (Device device :list){
                        if (device!=null)
                            device.setPushType("禁止推送");
                    }
                }
            }
        }
        userRepository.save(user);
        return new RestResponse(new RestUser(user));
    }

    @RequestMapping(value = "/deviceType")
    public RestResponse operateDeviceType(Principal principal,@RequestBody DeviceTypeRequest deviceTypeReq){
        User user = judgeByPrincipal(principal);
        if (null == user)
            return new RestResponse("手机号出错！", null);
        DeviceType deviceType = new DeviceType();
        List<DeviceTypeInspect> deviceTypeInspects = new ArrayList<DeviceTypeInspect>();

        if (UserRoleDifferent.userFirmManagerConfirm(user)||UserRoleDifferent.userStartWithService(user)) {
            if (deviceTypeReq.getName()==null||"".equals(deviceTypeReq.getName()))
                return new RestResponse("设备种类名称不能为空",1005,null);
            if (null != deviceTypeReq.getId()) {
                deviceType = deviceTypeRepository.findOne(deviceTypeReq.getId());
                if (null==deviceType)
                    return new RestResponse("当前设备不存在！",1005,null);
                if (deviceType.getCompany()==null&&!UserRoleDifferent.userStartWithService(user)){
                    return new RestResponse("企业管理员无法修改平台设备种类！",1005,null);
                }


                //如果是平台管理员
                if (UserRoleDifferent.userStartWithService(user)){
                    List<DeviceType> list=new ArrayList<DeviceType>();
                    list=deviceTypeRepository.findAll();
                    if (list!=null&&list.size()>0){
                        for (DeviceType deviceType1:list){
                            if (deviceType1.getCompany()==null&&deviceType1.getName()!=null&&!deviceType1.getId().equals(deviceTypeReq.getId()) &&deviceType1.getName().equals(deviceTypeReq.getName())){
                                    return new RestResponse("该设备种类名称已存在",1005,null);
                            }
                        }
                    }
                }
                if (UserRoleDifferent.userFirmManagerConfirm(user)){
                    List<DeviceType> list=new ArrayList<DeviceType>();
                    Company company=user.getCompany();
                    list=deviceTypeRepository.findByCompanyId(Integer.valueOf(company.getId()));
                    if (list!=null&&list.size()>0){
                        for (DeviceType deviceType1:list){
                            if (!deviceType1.getId().equals(deviceTypeReq.getId())&&deviceType1.getName()!=null&&deviceType1.getName().equals(deviceTypeReq.getName())){
                                    return new RestResponse("该设备种类名称已存在",1005,null);
                            }
                        }
                    }
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
                if (UserRoleDifferent.userStartWithService(user)){
                    List<DeviceType> list=new ArrayList<DeviceType>();
                    list=deviceTypeRepository.findAll();
                    if (list!=null&&list.size()>0) {
                        for (DeviceType deviceType1 : list) {
                            if (deviceType1.getCompany() == null&&deviceType1.getName()!=null&&deviceType1.getName().equals(deviceTypeReq.getName())) {
                                    return new RestResponse("该设备种类名称已存在", 1005, null);
                            }
                        }
                    }
                }
                if (UserRoleDifferent.userFirmManagerConfirm(user)){
                    List<DeviceType> list=new ArrayList<DeviceType>();
                    Company company=user.getCompany();
                    list=deviceTypeRepository.findByCompanyId(Integer.valueOf(company.getId()));
                    if (list!=null&&list.size()>0){
                        for (DeviceType deviceType1:list){
                            if (deviceType1.getName()!=null&&deviceType1.getName().equals(deviceTypeReq.getName())){
                                return new RestResponse("该设备种类名称已存在",1005,null);
                            }
                        }
                    }
                }
                deviceType.setEnable(1);
                if (UserRoleDifferent.userFirmManagerConfirm(user))
                    deviceType.setCompany(user.getCompany());
                deviceType.setName(deviceTypeReq.getName());
                deviceTypeRepository.save(deviceType);
                System.out.println("deviceTypeReq.getList().size()："+deviceTypeReq.getList().size());
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
        return new RestResponse("操作成功",new RestDeviceType(deviceType));
    }

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
            return new RestResponse("删除出错！",null);
        }
    }

    /**
     * 发送短信验证码
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
        if (mobile.length()!=11)
            return new RestResponse("手机号格式不正确",1005,null);
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
        return new RestResponse("短信验证码发送成功",new RestUser(user));
    }

    /**
     * 发送邮箱验证码
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
            return new RestResponse("参数为空！",1005,null);
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
        return new RestResponse("邮箱验证码发送成功",null);
    }

    /**
     * 更换手机号
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
        return new RestResponse("更换手机号成功",null);
    }

    /**
     *  绑定邮箱
     * @param principal
     * @param map
     * @return
     */
    @RequestMapping(value = "/update/email",method = RequestMethod.POST)
    public RestResponse updateEmailByEmail(Principal principal,@RequestBody Map<String,String> map){
        User user = judgeByPrincipal(principal);
        if(map ==null||null==map.get("email")||null==map.get("verify")||"".equals(map.get("email"))||"".equals(map.get("verify")))
            return new RestResponse("邮箱或者验证码为空！",1005,null);
        if (!user.getVerify().toString().equals(map.get("verify")))
            return new RestResponse("验证码不正确！",1005,null);
        user.setBindEmail(1);
        user.setEmail(map.get("email"));
        userRepository.save(user);
        return new RestResponse("邮箱绑定成功",null);
    }


    /**
     * 修改密码
     * @param principal
     * @param map
     * @return
     */
    @RequestMapping(value = "/modify/password",method = RequestMethod.GET)
    public RestResponse modifyPassword(Principal principal,@RequestParam Map<String,String> map){
        User user = judgeByPrincipal(principal);
        if (map!=null){
            if (map.get("old")==null||"".equals(map.get("old"))||!map.get("old").equals(user.getPassword()))
                return new RestResponse("原密码输入有误！",1005,null);
            if (map.get("password")==null||"".equals(map.get("password")))
                return new RestResponse("新密码不能为空！",1005,null);
            user.setPassword(map.get("password"));
            userRepository.save(user);
            return new RestResponse("修改成功！",null);
        }else {
            return new RestResponse("原密码和新密码不能为空！",1005,null);
        }
    }
    //    /**
//     * 修改密码
//     * @param principal
//     * @param old
//     * @param password
//     * @return
//     */
//    @RequestMapping(value = "/modify/password/{old}")
//    public RestResponse modifyPassword(Principal principal,@PathVariable String old,@RequestParam String password){
//        User user = judgeByPrincipal(principal);
//        if (null==old||!old.equals(user.getPassword()))
//            return new RestResponse("原密码输入有误！",1005,null);
//        if (null==password||password.equals(""))
//            return new RestResponse("新密码不能为空！",1005,null);
//        user.setPassword(password);
//        userRepository.save(user);
//        return new RestResponse("修改成功！",null);
//    }

    /*
     *找回密码
     */
    @RequestMapping(value="/forget/find/password/{name}",method = RequestMethod.POST)
    public RestResponse findPassword(@PathVariable String name,@RequestBody Map<String,String> map){
        if (map==null)
            return new RestResponse("输入有误",1005,null);
        //根据用户名找回密码
        User user=null;
        //判断是否是平台人员
        if (null==map.get("companyId")||"".equals(map.get("companyId"))||"undefined".equals(map.get("companyId")))
            user=userRepository.findByName(name);
        else
            user=userRepository.findByName(name+"@"+map.get("companyId"));

        if (null==user)
            return new RestResponse("账号输入有误",1005,null);
//        if (user.getBindEmail()!=1&&user.getBindMobile()!=1)
//            return new RestResponse("您未绑定手机号或邮箱！请联系管理员！",null);
        if(null==map.get("number")||"".equals(map.get("number")))
            return new RestResponse("请输入正确的手机号或验证码！",1005,null);
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
            return new RestResponse("身份验证输入错误或未绑定，请核对！",1005,null);
        }
    }

    /**
     * 修改终端编号
     */
    @RequestMapping(value = "/device/code/{number}")
    public RestResponse modifyDeviceCode(Principal principal, @PathVariable String number, @RequestParam String newNumber,
                                         HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException,SerialException {
        User user=judgeByPrincipal(principal);
        if (user==null)
            return new RestResponse("用户未登录",1005,null);
        MonitorDevice monitorDevice=monitorDeviceRepository.findByNumber(number);
        if (monitorDevice==null)
            return new RestResponse("找不到终端编号",1005,null);
        MonitorDevice monitorDevice1=monitorDeviceRepository.findByNumber(newNumber);
        if (monitorDevice1!=null)
            return new RestResponse("终端编号已经存在",1005,null);
        monitorDevice.setNumber(newNumber);
        monitorDeviceRepository.save(monitorDevice);
        return new RestResponse("终端编号修改成功",null);
    }


    /**
     * 选择版本接口
     */
    @RequestMapping(value = "/select/device/version")
    public RestResponse selectDeviceVersion(Principal principal){
        User user=judgeByPrincipal(principal);
        if (user==null)
            return new RestResponse("用户未登陆",1005,null);
        List<DeviceVersion> list=deviceVersionRepository.findAll();
        if (list!=null&&list.size()>0)
            return new RestResponse(list);
        else
            return new RestResponse("目前没有设备版本",1005,null);
    }

    /**
     * 版本更新接口
     */
     @RequestMapping(value = "/update/device/version/{id}")
    public RestResponse updateDeviceVersion(Principal principal,@PathVariable String id,@RequestParam String deviceVersionId){
         User user=judgeByPrincipal(principal);
         if (user==null)
             return new RestResponse("用户未登录",1005,null);
         Device device=deviceRepository.findById(Integer.valueOf(id));
         if (device==null)
             return new RestResponse("没有此设备",1005,null);
         DeviceVersion deviceVersion=deviceVersionRepository.findById(Integer.valueOf(deviceVersionId));
         if (deviceVersion==null)
             return new RestResponse("没有此设备版本",1005,null);
         device.setDeviceVersion(deviceVersion);
         deviceRepository.save(device);
         System.out.println(device);
         return new RestResponse("版本更新成功",null);
     }

    /**
     * 多选版本更新接口
     */
    @RequestMapping(value = "/update/multi/device/version/{id}")
    public RestResponse multiDeviceVersion(Principal principal,@PathVariable String id,@RequestParam String multiversion){
        User user=judgeByPrincipal(principal);
        if (user==null)
            return new RestResponse("用户未登录",1005,null);
        if (id==null||"".equals(id))
            return new RestResponse("没有选择版本",1005,null);
        DeviceVersion deviceVersion=deviceVersionRepository.findById(Integer.valueOf(id));
        if (deviceVersion==null)
            return new RestResponse("版本选择有误",1005,null);
        if (multiversion==null||"".equals(multiversion))
            return new RestResponse("没有选择要更新的设备",1005,null);
        String[] multi=multiversion.split(",");
        if (multi==null)
            return new RestResponse("设备选择有误",1005,null);
        for (String str:multi){
            if (str==null||"".equals(str))
                continue;
            Device device=deviceRepository.findById(Integer.valueOf(str));
            if (device==null)
                continue;
            device.setDeviceVersion(deviceVersion);
            deviceRepository.save(device);
        }
        return new RestResponse("版本更新成功",null);
    }

    /**
     * 硬件版本说明接口
     * @param principal
     * @param id 版本的id
     * @return
     */
     @RequestMapping(value = "/device/version/explain/{id}")
     public RestResponse versionExplain(Principal principal,@PathVariable String id){
         User user=judgeByPrincipal(principal);
         if (user==null)
             return new RestResponse("用户未登录",1005,null);
         DeviceVersion deviceVersion=deviceVersionRepository.findById(Integer.valueOf(id));
         if (deviceVersion==null)
             return new RestResponse("硬件版本不正确",1005,null);
         return new RestResponse(new RestDeviceVersion(deviceVersion));
     }

    /**
     * 零票设置
     * @param principal
     * @param id  设备的id
     * @return
     */
     @RequestMapping(value = "/set/zero/{id}")
    public RestResponse setZero(Principal principal,@PathVariable String id){
         User user=judgeByPrincipal(principal);
         if (user==null)
             return new RestResponse("用户未登录",1005,null);
         List<DeviceInspect> list=new ArrayList<DeviceInspect>();
         list = deviceInspectRepository.findByDeviceId(Integer.valueOf(id));
         List<RestDeviceInspect> deviceInspectList=new ArrayList<RestDeviceInspect>();
         if (list!=null&&list.size()>0) {
             for (DeviceInspect deviceInspect : list) {
                 deviceInspectList.add(new RestDeviceInspect(deviceInspect));
             }
             return new RestResponse(deviceInspectList);
         }
         else
             return new RestResponse("此设备没有添加参数",1005,null);
     }

    /**
     * 修改零票值
     * @param principal
     * @param id   device_inspect的id
     * @param zero 零票值
     * @return
     */
     @RequestMapping(value = "/modify/zero/{id}")
    public RestResponse modifyZero(Principal principal,@PathVariable String id,@RequestParam String zero){
         User user=judgeByPrincipal(principal);
         if (user==null)
             return new RestResponse("用户未登录",1005,null);
         DeviceInspect deviceInspect=deviceInspectRepository.findById(Integer.valueOf(id));
         if (deviceInspect==null)
             return new RestResponse("传感器不存在",1005,null);
         if (zero==null||"".equals(zero))
             return new RestResponse("零票值不正确",1005,null);
         deviceInspect.setZero(Float.valueOf(zero));
         deviceInspect.setCorrectionValue(deviceInspect.getOriginalValue()-(Float.valueOf(zero)));
         deviceInspectRepository.save(deviceInspect);
         return new RestResponse("零漂值修改成功",null);
     }

     @RequestMapping(value = "/is/login")
    public void  isLogin(Principal principal){
         judgeByPrincipal(principal);
     }

    /**
     * 查询设备没有绑定的科学家
     */
    @RequestMapping(value = "/is/device/sicentist/{deviceId}")
    public RestResponse scientist(@PathVariable String deviceId){
        Device device=deviceRepository.findById(Integer.valueOf(deviceId));
        if (device==null)
            return new RestResponse("设备不存在",1005,null);
        User manager=device.getManager();
        Company company=manager.getCompany();
        //更具公司id找到所有的用户
        List<User> list=userRepository.findByCompanyId(company.getId());
        if (list==null)
            return new RestResponse("公司不存在",1005,null);
        //该设备科学家集合
        List<RestUser> deviceScientist=new ArrayList<RestUser>();
        //在判定用户是否是科学家
        for (User user:list){
            if (!UserRoleDifferent.userScientistConfirm(user))
                continue;
            //如果是科学家根据科学家的id和设备id找到ScientistDevice
            ScientistDevice scientistDevice=scientistDeviceRepository.findByScientistIdAndDeviceId(user.getId(),Integer.valueOf(deviceId));
            //如果ScientistDevice为空说明这个科学家没有绑定这个设备
            if (scientistDevice==null)
                deviceScientist.add(new RestUser(user));
        }
        return new RestResponse(deviceScientist);
    }

    /**
     * 内部测试接口
     * @param
     * @return
     */
//    @RequestMapping(value = "/test/user")
//    public RestResponse testUser(){
//        List<User> userList=userRepository.findAll();
//        for (User user1:userList){
//            Company company=user1.getCompany();
//            if (company!=null){
//                user1.setName(user1.getName()+"@"+company.getCompanyId());
//                userRepository.save(user1);
//            }
//        }
//        return new RestResponse("修改成功",null);
//    }

}
