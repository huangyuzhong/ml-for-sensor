package com.device.inspect.controller;

import DNA.sdk.info.account.AccountAsset;
import DNA.sdk.wallet.UserWalletManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.device.inspect.common.model.charater.Role;
import com.device.inspect.common.model.charater.RoleAuthority;
import com.device.inspect.common.model.charater.User;
import com.device.inspect.common.model.device.*;
import com.device.inspect.common.model.firm.Building;
import com.device.inspect.common.model.firm.Company;
import com.device.inspect.common.model.firm.Room;
import com.device.inspect.common.model.firm.Storey;
import com.device.inspect.common.model.record.DealRecord;
import com.device.inspect.common.model.record.DeviceDisableTime;
import com.device.inspect.common.model.record.MessageSend;
import com.device.inspect.common.repository.charater.RoleAuthorityRepository;
import com.device.inspect.common.repository.charater.RoleRepository;
import com.device.inspect.common.repository.charater.UserRepository;
import com.device.inspect.common.repository.device.*;
import com.device.inspect.common.repository.firm.BuildingRepository;
import com.device.inspect.common.repository.firm.CompanyRepository;
import com.device.inspect.common.repository.firm.RoomRepository;
import com.device.inspect.common.repository.firm.StoreyRepository;
import com.device.inspect.common.repository.record.DealRecordRepository;
import com.device.inspect.common.repository.record.DeviceDisableTimeRepository;
import com.device.inspect.common.repository.record.MessageSendRepository;
import com.device.inspect.common.restful.RestResponse;
import com.device.inspect.common.restful.charater.RestUser;
import com.device.inspect.common.restful.device.*;
import com.device.inspect.common.restful.record.BlockChainDealDetail;
import com.device.inspect.common.restful.record.BlockChainDealRecord;
import com.device.inspect.common.restful.record.BlockChainDevice;
import com.device.inspect.common.restful.record.BlockChainDeviceRecord;
import com.device.inspect.common.service.InitWallet;
import com.device.inspect.common.service.MessageSendService;
import com.device.inspect.common.service.OnchainService;
import com.device.inspect.common.service.TemporalStrategyChecker;
import com.device.inspect.common.util.transefer.UserRoleDifferent;
import com.device.inspect.controller.request.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.device.inspect.common.setting.Defination.*;

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

    @Autowired
    private MonitorDeviceRepository monitorDeviceRepository;

    @Autowired
    private Pt100ZeroRepository pt100ZeroRepository;

    @Autowired
    private DeviceVersionRepository deviceVersionRepository;

    @Autowired
    private DeviceRunningStatusRepository deviceRunningStatusRepository;

    @Autowired
    private DeviceTypeInspectRunningStatusRepository deviceTypeInspectRunningStatusRepository;

    @Autowired
    private DeviceInspectRunningStatusRepository deviceInspectRunningStatusRepository;

    @Autowired
    private DeviceDisableTimeRepository deviceDisableTimeRepository;

    @Autowired
    private DealRecordRepository dealRecordRepository;

    @Autowired
    private OnchainService onchainService;

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
     * 查看共享设备信息
     * @param map
     * @return
     */
    @RequestMapping(value = "/find/deviceDisableTimeByDeviceId")
    public RestResponse findDeviceDisableTimeByDeviceId(Principal principal, @RequestParam Map<String,String> map){
        User user1=judgeByPrincipal(principal);
        if (user1==null)
            return new RestResponse("用户未登陆",1005,null);
        if (null != map.get("deviceId")){
            Integer deviceId = Integer.parseInt(map.get("deviceId"));
            List<DeviceDisableTime> deviceDisableTimes = deviceDisableTimeRepository.findByDeviceId(deviceId);
            RestDeviceDisableTime restDeviceDisableTime = new RestDeviceDisableTime(deviceDisableTimes.get(0));
            return new RestResponse(restDeviceDisableTime);
        }
        return new RestResponse("设备信息出错！",1005,null);
    }

    /**
     * 操作设备不可租赁时间段
     * @param principal
     * @param map
     * @return
     */
    @RequestMapping(value = "/add/deviceDisableTime")
    public RestResponse addDeviceDisableTime(Principal principal, @RequestParam Map<String,String> map){
        User user1=judgeByPrincipal(principal);
        if (user1==null)
            return new RestResponse("用户未登陆",1005,null);
        if (map.containsKey("id")){  // 修改设备不可租赁时间组
            DeviceDisableTime deviceDisableTime = deviceDisableTimeRepository.findOne(Integer.parseInt(map.get("id")));
            if (null != map.get("strategyType")){
                deviceDisableTime.setStrategyType(map.get("strategyType"));
            }
            if (null != map.get("content")){
                deviceDisableTime.setContent(map.get("content"));
            }

            Device device = null;
            if (null != map.get("deviceId")){
                device = deviceRepository.findOne(Integer.parseInt(map.get("deviceId")));
            }
            BlockChainDevice data = new BlockChainDevice(device, deviceDisableTime);
            BlockChainDeviceRecord value = new BlockChainDeviceRecord("新增设备", data);
            try {
                JSONObject returnObject = onchainService.sendStateUpdateTx("device", String.valueOf(device.getId()), "", JSON.toJSONString(value));
                if(!JSON.toJSONString(value).equals(JSON.toJSONString(returnObject))){
                    throw new Exception("return value from block chain is not equal to original");
                }
            }catch(Exception e){
                LOGGER.error(e.getMessage());
                return new RestResponse(("更新区块链失败"), 1007, null);
            }

            deviceDisableTimeRepository.save(deviceDisableTime);
            return new RestResponse(new RestDeviceDisableTime(deviceDisableTime));
        }else{  // 新增设备不可租赁时间组
            DeviceDisableTime deviceDisableTime = new DeviceDisableTime();
            if (null != map.get("deviceId")){
                Device device = deviceRepository.findOne(Integer.parseInt(map.get("deviceId")));
                if (device == null)
                    return new RestResponse("设备信息出错！",1005,null);
                else
                    deviceDisableTime.setDevice(device);
            }
            if (null != map.get("strategyType")){
                deviceDisableTime.setStrategyType(map.get("strategyType"));
            }
            if (null != map.get("content")){
                deviceDisableTime.setContent(map.get("content"));
            }

            BlockChainDevice data = new BlockChainDevice(deviceDisableTime.getDevice(), deviceDisableTime);
            BlockChainDeviceRecord value = new BlockChainDeviceRecord("新增设备", data);
            try {
                JSONObject returnObject = onchainService.sendStateUpdateTx("device", String.valueOf(deviceDisableTime.getDevice().getId()), "", JSON.toJSONString(value));
                if(!JSON.toJSONString(value).equals(JSON.toJSONString(returnObject))){
                    throw new Exception("return value from block chain is not equal to original");
                }
            }catch(Exception e){
                LOGGER.error(e.getMessage());
                return new RestResponse(("更新区块链失败"), 1007, null);
            }

            deviceDisableTimeRepository.save(deviceDisableTime);
            return new RestResponse("操作成功！", null);
        }
    }

    /**
     * 修改设备详细概况
     * @param deviceId
     * @param map
     * @return
     */
    @RequestMapping(value = "/deviceSharing/{deviceId}")
    public RestResponse operateDeviceDetail(Principal principal,@PathVariable Integer deviceId,@RequestParam Map<String,String> map){
        User user1=judgeByPrincipal(principal);
        if (user1==null)
            return new RestResponse("用户未登陆",1005,null);
        Device device = deviceRepository.findOne(deviceId);
        if (null == device)
            return new RestResponse("设备信息出错！",1005,null);

        if (null!=map.get("enableSharing")) {
            Integer enableSharing = Integer.parseInt(map.get("enableSharing"));
            device.setEnableSharing(enableSharing);

            if (enableSharing == 1 && device.getDeviceChainKey() == null){
                UserWalletManager wallet = InitWallet.getWallet();
                String deviceChainKey = wallet.createAccount();
                device.setDeviceChainKey(deviceChainKey);
            }
        }
        if (null!=map.get("rentClause"))
            device.setRentClause(map.get("rentClause"));
        if (null!=map.get("rentPrice"))
            device.setRentPrice(Double.parseDouble(map.get("rentPrice")));

        deviceRepository.save(device);
        return new RestResponse(new RestDevice(device));
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
        if (null!=map.get("name")) {
            try {
                device.setName(java.net.URLDecoder.decode(map.get("name"), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if (null!=map.get("serialNo"))
            device.setSerialNo(map.get("serialNo"));
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
                LOGGER.info(String.format("device purchase date %s is unexpected format", map.get("purchase")));
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
                LOGGER.info(String.format("device maintain date %s is unexpected format", map.get("maintainDate")));
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
            InspectTypeRequest deviceInspectParameters = new InspectTypeRequest();
            if (deviceInspect.getStandard()==null)
                deviceInspectParameters.setChosed(false);
            else {
                deviceInspectParameters.setChosed(true);
            }

            deviceInspectParameters.setHighUp(null == deviceInspect.getHighUp() ? null : deviceInspect.getHighUp().toString());
            deviceInspectParameters.setHighDown(deviceInspect.getHighDown() == null ? null : deviceInspect.getHighDown().toString());
            deviceInspectParameters.setLowUp(deviceInspect.getLowUp() == null ? null : deviceInspect.getLowUp().toString());
            deviceInspectParameters.setLowDown(deviceInspect.getLowDown() == null ? null : deviceInspect.getLowDown().toString());
            deviceInspectParameters.setStandard(deviceInspect.getStandard()==null?null:deviceInspect.getStandard().toString());
            deviceInspectParameters.setId(deviceInspect.getInspectType().getId());
            deviceInspectParameters.setDeviceInspectId(deviceInspect.getId());
            deviceInspectParameters.setName(deviceInspect.getInspectType().getName());
            deviceInspectParameters.setInspectPurpose(deviceInspect.getInspectPurpose());

            List<DeviceTypeInspectRunningStatusRequest> runningStatusesRequest= new ArrayList<>();
            List<DeviceInspectRunningStatus> runningStatuses = deviceInspectRunningStatusRepository.findByDeviceInspectId(deviceInspect.getId());
            if(runningStatuses != null && runningStatuses.size() > 0){
                for(DeviceInspectRunningStatus status : runningStatuses){
                    DeviceTypeInspectRunningStatusRequest statusRequest = new DeviceTypeInspectRunningStatusRequest();
                    statusRequest.setId(status.getId());
                    statusRequest.setRunningStatusId(status.getDeviceRunningStatus().getId());
                    statusRequest.setDeviceTypeInspectId(status.getDeviceInspect().getId());
                    statusRequest.setThreshold(status.getThreshold());
                    runningStatusesRequest.add(statusRequest);
                }
                deviceInspectParameters.setRunningStatus(runningStatusesRequest);
            }

            list.add(deviceInspectParameters);
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

    /**
     * Test Example: curl -H "Content-Type: application/json" -X POST
     * --data '{"id":215,"name":"温度+ 门+压差+电表","list":[{"id":1,"name":"温度（PT100）",
     * "lowUp":"30.0","lowDown":"10.0","highUp":"40.0","highDown":"0.0","standard":"20.0",
     * "chosed":true},{"id":6,"name":"房间压差","lowUp":"50.0","lowDown":"-10.0","highUp":"50.0",
     * "highDown":"-10.0","standard":"10.0","chosed":true},{"id":8,"name":"设备门状态","lowUp":"0.0",
     * "lowDown":"0.0","highUp":"5.0","highDown":"5.0","standard":"1.0","chosed":true},{"id":10,
     * "name":"有功电能","lowUp":"20.0","lowDown":"0.0","highUp":"20.0","highDown":"0.0","standard":"10.0",
     * "chosed":true},{"id":12,"name":"电压","lowUp":"250.0","lowDown":"200.0","highUp":"260.0","highDown":"190.0",
     * "standard":"220.0","chosed":true},{"id":13,"name":"电流","lowUp":"3.0","lowDown":"0.0","highUp":"3.0",
     * "highDown":"-1.0","standard":"0.0","chosed":true, "runningStatus":[{"id":4,"runningStatusId":1,
     * "deviceTypeInspectId":246,"threshold":1.0},{"id":5,"runningStatusId":2,"deviceTypeInspectId":246,
     * "threshold":2.0},{"id":6,"runningStatusId":3,"deviceTypeInspectId":246,"threshold":4.5}]},{"id":14,
     * "name":"有功功率","lowUp":"10.0","lowDown":"2.0","highUp":"50.0","highDown":"0.0","standard":"5.0","chosed":true}]}'
     * http://localhost/api/rest/operate/device/parameter/215
    */

    @RequestMapping(value = "/device/parameter/{deviceId}")
    public RestResponse operateDeviceData(Principal principal,@PathVariable Integer deviceId,@RequestBody DeviceTypeRequest request){
        User user=judgeByPrincipal(principal);
        if (user==null)
            return new RestResponse("用户未登录",1005,null);
        Device device = deviceRepository.findOne(deviceId);
        if (null == device)
            return new RestResponse("设备信息出错！",1005,null);
        if (device.getManager()!=null&&device.getManager()!=user)
            return new RestResponse("非设备管理员不能修改设备报警参数",1005,null);
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
                if(null!=inspectTypeRequest.getInspectPurpose())
                    deviceInspect.setInspectPurpose(Integer.valueOf(inspectTypeRequest.getInspectPurpose()));
                deviceInspectRepository.save(deviceInspect);

                List<DeviceInspectRunningStatus> runningStatuses = new ArrayList<>();
                Set<Integer> statusInPost = new HashSet<>();
                List<DeviceTypeInspectRunningStatusRequest> runningStatusRequests = inspectTypeRequest.getRunningStatus();
		if(runningStatusRequests == null){
			LOGGER.info("device running status is null");
		}
                if(runningStatusRequests != null && runningStatusRequests.size() >= 0){
                    Iterable<DeviceInspectRunningStatus> dbStatusList = deviceInspectRunningStatusRepository.findByDeviceInspectId(deviceInspect.getId());
                    for(DeviceTypeInspectRunningStatusRequest status : inspectTypeRequest.getRunningStatus()){
                        if (status.getId() != null) {
                            statusInPost.add(status.getId());
                        }
                    }

                    for(DeviceInspectRunningStatus status : dbStatusList){
                        if(!statusInPost.contains(status.getId())){
                            LOGGER.info("Delete device running status: " + status.getId());
                            deviceInspectRunningStatusRepository.delete(status);
                        }
                    }

                    for(DeviceTypeInspectRunningStatusRequest status : inspectTypeRequest.getRunningStatus()){
                        DeviceInspectRunningStatus deviceInspectRunningStatus = deviceInspectRunningStatusRepository.findById(status.getId());
                        if(deviceInspectRunningStatus == null){
                            LOGGER.info("Add device running status of " + status.getRunningStatusId() + " to " + status.getDeviceTypeInspectId());
                            deviceInspectRunningStatus = new DeviceInspectRunningStatus();
                        }
                        DeviceRunningStatus runningStatus = deviceRunningStatusRepository.findById(status.getRunningStatusId());
                        if (runningStatus == null) {
                            LOGGER.info("Running status illegal " + status.getRunningStatusId());
                            continue;
                        } else {
                            LOGGER.info("Edit device running status of id: " + runningStatus.getId());
                            deviceInspectRunningStatus.setDeviceRunningStatus(runningStatus);
                        }
                        deviceInspectRunningStatus.setDeviceInspect(deviceInspect);
                        deviceInspectRunningStatus.setThreshold(status.getThreshold());
                        runningStatuses.add(deviceInspectRunningStatus);
                    }
                    deviceInspectRunningStatusRepository.save(runningStatuses);
                }
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
        try {
            child.setUserName(java.net.URLDecoder.decode(map.get("userName"),"UTF-8"));
            child.setDepartment(java.net.URLDecoder.decode(map.get("department"),"UTF-8"));
            child.setJob(java.net.URLDecoder.decode(map.get("job"),"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        child.setJobNum(map.get("jobNum"));
        child.setRemoveAlert("0");

        // 新增用户前，给该用户上链
        UserWalletManager wallet = InitWallet.getWallet();
        String address = wallet.createAccount();
        child.setAccountAddress(address);

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

    /**
     * 修改设备种类及其参数
     * @param principal
     * @param deviceTypeReq
     * @return
     */

    /**
     * Test Example: curl -H "Content-Type: application/json"
     * -X POST  --data '{"id":132,"name":"家用冰箱","list":[{"id":1,"name":"温度（PT100）",
     * "lowUp":"-15.0","lowDown":"-25.0","highUp":"-10.0","highDown":"-30.0","standard":"-20.0",
     * "chosed":true,"runningStatus":[]},{"id":8,"name":"设备门状态","lowUp":"0.0","lowDown":"0.0",
     * "highUp":"5.0","highDown":"5.0","standard":"1.0","chosed":true,"runningStatus":[]},{"id":10,
     * "name":"有功电能","lowUp":"15.0","lowDown":"5.0","highUp":"20.0","highDown":"0.0","standard":"10.0",
     * "chosed":true,"runningStatus":[]},{"id":11,"name":"无功电能","lowUp":"15.0","lowDown":"5.0",
     * "highUp":"20.0","highDown":"0.0","standard":"10.0","chosed":true,"runningStatus":[]},{"id":12,
     * "name":"电压","lowUp":"15.0","lowDown":"5.0","highUp":"20.0","highDown":"0.0","standard":"10.0",
     * "chosed":true,"runningStatus":[]},{"id":13,"name":"电流","lowUp":"15.0","lowDown":"5.0","highUp":"20.0",
     * "highDown":"0.0","standard":"10.0","chosed":true,"runningStatus":[{"id":4,"runningStatusId":1,
     * "deviceTypeInspectId":205,"threshold":1},{"id":5,"runningStatusId":2,"deviceTypeInspectId":205,
     * "threshold":2},{"runningStatusId":3,"deviceTypeInspectId":205,"threshold":4}]},{"id":14,"name":"有功功率",
     * "lowUp":"15.0","lowDown":"5.0","highUp":"20.0","highDown":"0.0","standard":"10.0","chosed":true,
     * "runningStatus":[]},{"id":15,"name":"无功功率","lowUp":"15.0","lowDown":"5.0","highUp":"20.0","highDown":"0.0",
     * "standard":"10.0","chosed":true,"runningStatus":[]}]}' http://localhost/api/rest/operate/deviceType/
     */

    @RequestMapping(value = "/deviceType")
    public RestResponse operateDeviceType(Principal principal,@RequestBody DeviceTypeRequest deviceTypeReq){
        User user = judgeByPrincipal(principal);
        if (null == user)
            return new RestResponse("手机号出错！", null);
        DeviceType deviceType = new DeviceType();
        List<DeviceTypeInspect> deviceTypeInspects = new ArrayList<DeviceTypeInspect>();
        List<DeviceTypeInspectRunningStatus> runningStatuses = new ArrayList<>();
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
                                deviceTypeInspect.setInspectPurpose(null == inspectTypeRequest.getInspectPurpose() ? 0 : inspectTypeRequest.getInspectPurpose());
                                deviceTypeInspects.add(deviceTypeInspect);
                            }
                        }
                    }
                }
                deviceTypeInspectRepository.save(deviceTypeInspects);

                if (null != deviceTypeReq && deviceTypeReq.getList().size() > 0) {
                    for (InspectTypeRequest inspectTypeRequest : deviceTypeReq.getList()) {
                        if (inspectTypeRequest.isChosed()) {
                            if (null != inspectTypeRequest.getRunningStatus() && inspectTypeRequest.getRunningStatus().size() > 0) {
                                runningStatuses.clear();
                                Set<Integer> statusInPost = new HashSet<>();

                                for(DeviceTypeInspectRunningStatusRequest status : inspectTypeRequest.getRunningStatus()){
                                    if (status.getId() != null) {
                                        statusInPost.add(status.getId());
                                    }
                                }
                                DeviceTypeInspect deviceTypeInspect = deviceTypeInspectRepository.findByDeviceTypeIdAndInspectTypeId(deviceTypeReq.getId(), inspectTypeRequest.getId());
                                if(deviceTypeInspect == null){
                                    continue;
                                }

                                Iterable<DeviceTypeInspectRunningStatus> dbStatusList = deviceTypeInspectRunningStatusRepository.findByDeviceTypeInspectId(deviceTypeInspect.getId());
                                for(DeviceTypeInspectRunningStatus status : dbStatusList){
                                    if(!statusInPost.contains(status.getId())){
                                        deviceTypeInspectRunningStatusRepository.delete(status);
                                    }
                                }

                                for (DeviceTypeInspectRunningStatusRequest status : inspectTypeRequest.getRunningStatus()) {
                                    DeviceTypeInspectRunningStatus deviceTypeInspectRunningStatus = deviceTypeInspectRunningStatusRepository.findById(status.getId());
                                    if (deviceTypeInspectRunningStatus == null) {
                                        deviceTypeInspectRunningStatus = new DeviceTypeInspectRunningStatus();
                                    }
                                    DeviceRunningStatus runningStatus = deviceRunningStatusRepository.findById(status.getRunningStatusId());
                                    if (runningStatus == null) {
                                        continue;
                                    } else {
                                        deviceTypeInspectRunningStatus.setDeviceRunningStatus(runningStatus);
                                    }
                                    deviceTypeInspectRunningStatus.setDeviceTypeInspect(deviceTypeInspect);
                                    deviceTypeInspectRunningStatus.setThreshold(status.getThreshold());
                                    runningStatuses.add(deviceTypeInspectRunningStatus);
                                }
                                deviceTypeInspectRunningStatusRepository.save(runningStatuses);

                            }
                        }
                    }
                }
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
                            deviceTypeInspect.setInspectPurpose(null == inspectTypeRequest.getInspectPurpose() ? 0 : inspectTypeRequest.getInspectPurpose());
                            deviceTypeInspectRepository.save(deviceTypeInspect);

                            if (null != inspectTypeRequest.getRunningStatus() && inspectTypeRequest.getRunningStatus().size() > 0) {
                                runningStatuses.clear();

                                for (DeviceTypeInspectRunningStatusRequest status : inspectTypeRequest.getRunningStatus()) {
                                    DeviceTypeInspectRunningStatus deviceTypeInspectRunningStatus = new DeviceTypeInspectRunningStatus();
                                    DeviceRunningStatus runningStatus = deviceRunningStatusRepository.findById(status.getRunningStatusId());
                                    if (runningStatus == null) {
                                        continue;
                                    } else {
                                        deviceTypeInspectRunningStatus.setDeviceRunningStatus(runningStatus);
                                    }
                                    deviceTypeInspectRunningStatus.setDeviceTypeInspect(deviceTypeInspect);
                                    deviceTypeInspectRunningStatus.setThreshold(status.getThreshold());
                                    runningStatuses.add(deviceTypeInspectRunningStatus);
                                }
                                deviceTypeInspectRunningStatusRepository.save(runningStatuses);
                            }
                        }
                    }
                }

                if (null != deviceTypeReq && deviceTypeReq.getList().size() > 0) {
                    for (InspectTypeRequest inspectTypeRequest : deviceTypeReq.getList()) {
                        if (inspectTypeRequest.isChosed()) {
                        }
                    }
                }
            }
        } else {
            return new RestResponse("权限不足！",1005,null);
        }
        return new RestResponse("操作成功",new RestDeviceType(deviceType));
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
        messageSend.setDeviceInspect(null);
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
        boolean b=MessageSendService.sendEmaiToUser(user,map.get("email"),String.valueOf(verify),0);
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
        return new RestResponse("更换手机号成功", new RestUser(user));
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
        return new RestResponse("邮箱绑定成功", new RestUser(user));
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

    /**
     * 找回密码
     * @param name
     * @param map
     * @return
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
            boolean b=MessageSendService.sendEmaiToUser(user,number,user.getPassword(),2);
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
    public RestResponse  isLogin(Principal principal){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(auth.getName());
        User user = judgeByPrincipal(principal);

        return new RestResponse(new RestUser(user));

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
     * 设备上链
     **/
    @RequestMapping(value = "/device/upChain/{id}")
    public RestResponse deviceUpChain(Principal principal, @PathVariable String id){
        Device device = deviceRepository.findById(Integer.valueOf(id));
        if(device == null){
            return new RestResponse("设备不存在", 1005, null);
        }
        if(device.getDeviceChainKey() == null || device.getDeviceChainKey().equals("")){
            if(device.getRentPrice() == null){
                return new RestResponse("租金未设置", 1006, null);
            }
            return new RestResponse();
        }
        else{
            return new RestResponse(("设备已上链"), 1007, null);
        }
    }

    /**
     * 更新上链设备的上链相关信息
     */
    @RequestMapping(value = "/device/updateChainDeviceInfo", method = RequestMethod.POST)
    public RestResponse updateChainDeviceInfo(Principal principal, @RequestBody updateChainDeviceInfoRequest requestParam){
        return new RestResponse();
    }

    /**
     * 申请链上交易
     */
    @RequestMapping(value = "/device/makeChainDeal", method = RequestMethod.POST)
    public RestResponse makeChainDeal(Principal principal, @RequestBody makeChainDealRequest requestParam){
        // check parameter is validate
        if(requestParam.getLesseeId() == null){
            return new RestResponse(("租用者id不能为空"), 1006, null);
        }
        if(requestParam.getDeviceId() == null){
            return new RestResponse(("设备id不能为空"), 1006, null);
        }
        if(requestParam.getBeginTime() == null || requestParam.getEndTime() == null){
            return new RestResponse(("租用时间不能为空"), 1006, null);
        }

        User lessee = userRepository.findById(requestParam.getLesseeId());
        if(lessee == null){
            return new RestResponse(("租用者id不存在"), 1007, null);
        }
        if(lessee.getAccountAddress() == null || lessee.getAccountAddress().equals("")){
            return new RestResponse(("租用者不在区块链上"), 1007, null);
        }

        Device device = deviceRepository.findById(requestParam.getDeviceId());
        if(device == null){
            return new RestResponse(("设备id不存在"), 1007, null);
        }
        if(device.getDeviceChainKey() == null || device.getDeviceChainKey().equals("")){
            return new RestResponse(("设备不在区块链上"), 1007, null);
        }

        User lessor = device.getManager();
        if(lessor == null){
            return new RestResponse(("承租用户不存在"), 1007, null);
        }
        if(lessor.getAccountAddress() == null || lessor.getAccountAddress().equals("")){
            return new RestResponse(("承租者不在区块链上"), 1007, null);
        }

        // check request time interval is validate
        Date beginTime = new Date(requestParam.getBeginTime());
        Date endTime = new Date(requestParam.getEndTime());

        // check whether request time interval fit allowed temporal strategy
        List<DeviceDisableTime> deviceDisableTimes = deviceDisableTimeRepository.findByDeviceId(device.getId());
        for(DeviceDisableTime timeStrategy : deviceDisableTimes){
            if(!TemporalStrategyChecker.checkRequestTimeByStrategy(timeStrategy.getStrategyType(), timeStrategy.getContent(), beginTime, endTime)){
                return new RestResponse(("申请的使用时间已被禁用"), 1007, null);
            }
        }

        // check whether request time interval is not used by others
        int conflictDeal = dealRecordRepository.countByDeviceIdAndBeginTimeBetween(device.getId(), beginTime, endTime) +
                dealRecordRepository.countByDeviceIdAndEndTimeBetween(device.getId(), beginTime, endTime);
        if(conflictDeal > 0){
            return new RestResponse(("申请的使用时间与其他交易冲突"), 1007, null);
        }

        DealRecord dealRecord = new DealRecord();
        dealRecord.setStatus(ONCHAIN_DEAL_STATUS_DEAL);
        dealRecord.setAggrement(device.getRentClause());
        dealRecord.setBeginTime(beginTime);
        dealRecord.setEndTime(endTime);
        dealRecord.setDevice(device);
        dealRecord.setDeviceSerialNumber(device.getSerialNo());
        dealRecord.setLessee(lessee);
        dealRecord.setLessor(lessor);
        double price = (new Double(device.getRentPrice() * (requestParam.getEndTime() - requestParam.getBeginTime()) / 1000 / 3600)).intValue();
        dealRecord.setPrice(price);
        try{
            dealRecordRepository.save(dealRecord);
            DealRecord getRecord = dealRecordRepository.findTopByDeviceIdAndBeginTimeAndEndTime(device.getId(), beginTime, endTime);
            BlockChainDealDetail data = new BlockChainDealDetail(getRecord.getId(), getRecord.getDevice().getId(), getRecord.getLessor().getId(),
                    getRecord.getLessee().getId(), getRecord.getPrice(), getRecord.getBeginTime().getTime(), getRecord.getEndTime().getTime(),
                    getRecord.getDeviceSerialNumber(), getRecord.getAggrement(), getRecord.getStatus());
            BlockChainDealRecord value = new BlockChainDealRecord("创建交易", data);
            JSONObject returnObject = onchainService.sendStateUpdateTx("deal", String.valueOf(getRecord.getId()) + String.valueOf(getRecord.getDevice().getId()),
                    "", JSON.toJSONString(value));
            if(!JSON.toJSONString(value).equals(JSON.toJSONString(returnObject))){
                throw new Exception("return value from block chain is not equal to original");
            }

            // transfer rent price to agency
            AccountAsset info = onchainService.getAccountAsset(OnchainService.agencyAddr);
            if(info == null || info.canUseAssets == null || info.canUseAssets.size() == 0){
                LOGGER.error(String.format("Finish Deal: agency have no asset"));
                return new RestResponse(getRecord);
            }
            String assetId = info.canUseAssets.get(0).assetid;
            onchainService.transfer(assetId, getRecord.getPrice().intValue(), "锁定租金,交易id:"+getRecord.getId(), getRecord.getLessee().getCompany().getAccountAddress(), OnchainService.agencyAddr);

            return new RestResponse(getRecord);
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            DealRecord getRecord = dealRecordRepository.findTopByDeviceIdAndBeginTimeAndEndTime(device.getId(), beginTime, endTime);
            if(getRecord != null){
                dealRecordRepository.delete(getRecord);
            }
        }
        // try to make deal

        return new RestResponse(("申请失败"), 1007, null);
    }

    /**
     * 确认交易完成
     */
    @RequestMapping(value = "/device/finishChainDeal", method = RequestMethod.POST)
    public RestResponse finishChainDeal(Principal principal, @RequestBody finishChainDealRequest requestPapram){
        Integer dealId = requestPapram.getDealId();
        if(dealId == null){
            return new RestResponse(("交易id不能为空"), 1006, null);
        }
        Integer operatorId = requestPapram.getOperateUserId();
        if(operatorId == null){
            return new RestResponse(("操作申请者id不能为空"), 1006, null);
        }
        String operation = requestPapram.getOperation();
        if(operation == null){
            return new RestResponse(("操作不能为空"), 1006, null);
        }

        if(!ONCHAIN_DEAL_OPERATION_SET.contains(operation)){
            return new RestResponse(("操作类型不支持"), 1007, null);
        }

        DealRecord record = dealRecordRepository.findOne(dealId);
        if(record == null){
            return new RestResponse(("交易id无效"), 1007, null);
        }

        if(operatorId != record.getLessor().getId() && operatorId != record.getLessee().getId()){
            return new RestResponse(("操作申请者非租赁双方"), 1007, null);
        }
        if(record.getStatus() == ONCHAIN_DEAL_STATUS_FINISH){
            return new RestResponse(("无效操作，交易已结束"), 1007, null);
        }
        if(record.getStatus() == ONCHAIN_DEAL_STATUS_CANCELLED){
            return new RestResponse(("无效操作，交易已取消"), 1007, null);
        }
        if(record.getStatus() == ONCHAIN_DEAL_STATUS_EXECUTING){
            return new RestResponse(("无效操作，交易正在执行中"), 1007, null);
        }

        Integer original_status = record.getStatus();
        Boolean finish = false;
        if(operatorId == record.getLessor().getId()){
            if(record.getStatus() == ONCHAIN_DEAL_STATUS_WAITING_MUTUAL_CONFIRM){
                record.setStatus(ONCHAIN_DEAL_STATUS_WAITING_LESSEE_CONFIRM);
            }
            else if(record.getStatus() == ONCHAIN_DEAL_STATUS_WAITING_LESSOR_CONFIRM){
                record.setStatus(ONCHAIN_DEAL_STATUS_FINISH);
                finish = true;
            }
        }

        if(operatorId == record.getLessee().getId()){
            if(record.getStatus() == ONCHAIN_DEAL_STATUS_WAITING_MUTUAL_CONFIRM){
                record.setStatus(ONCHAIN_DEAL_STATUS_WAITING_LESSOR_CONFIRM);
            }
            else if(record.getStatus() == ONCHAIN_DEAL_STATUS_WAITING_LESSEE_CONFIRM){
                record.setStatus(ONCHAIN_DEAL_STATUS_FINISH);
                finish = true;
            }
        }

        try{
            BlockChainDealDetail data = new BlockChainDealDetail(record.getId(), record.getDevice().getId(), record.getLessor().getId(),
                    record.getLessee().getId(), record.getPrice(), record.getBeginTime().getTime(), record.getEndTime().getTime(),
                    record.getDeviceSerialNumber(), record.getAggrement(), record.getStatus());
            BlockChainDealRecord value = new BlockChainDealRecord("更新交易", data);
            JSONObject returnObject = onchainService.sendStateUpdateTx("deal", String.valueOf(record.getId()) + String.valueOf(record.getDevice().getId()),
                    "", JSON.toJSONString(value));
            if(!JSON.toJSONString(value).equals(JSON.toJSONString(returnObject))){
                throw new Exception("return value from block chain is not equal to original");
            }
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return new RestResponse(("更新区块链失败"), 1007, null);
        }

        dealRecordRepository.save(record);
        if(finish){
            AccountAsset info = onchainService.getAccountAsset(OnchainService.agencyAddr);
            if(info == null || info.canUseAssets == null || info.canUseAssets.size() == 0){
                LOGGER.error(String.format("Finish Deal: agency have no asset"));
                return new RestResponse(record);
            }
            String assetId = info.canUseAssets.get(0).assetid;
            onchainService.transfer(assetId, record.getPrice().intValue(), "支付租金,交易id:"+record.getId(), OnchainService.agencyAddr, record.getLessor().getCompany().getAccountAddress());

            AccountAsset pointInfo = onchainService.getAccountAsset(OnchainService.rewardAddr);
            if(pointInfo == null || pointInfo.canUseAssets == null || pointInfo.canUseAssets.size() == 0){
                LOGGER.error(String.format("Finish Deal: reward account have no asset"));
                return new RestResponse(record);
            }
            String rewardAssetId = pointInfo.canUseAssets.get(0).assetid;
            int rewardPoint = (int)(record.getPrice().intValue()*0.1);
            onchainService.transfer(rewardAssetId, rewardPoint, "支付积分,交易id:"+record.getId(), OnchainService.rewardAddr, record.getLessor().getAccountAddress());
            onchainService.transfer(rewardAssetId, rewardPoint, "支付积分,交易id:"+record.getId(), OnchainService.rewardAddr, record.getLessee().getAccountAddress());
        }

        return new RestResponse(record);
    }

//    /**
//     * 内部测试接口
//     * @param
//     * @return
//     */
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
//
//    @RequestMapping(value = "/test/company")
//    public RestResponse modifyCompanyId(){
//        List<Company> companyList=companyRepository.findAll();
//        if (companyList==null)
//            return new RestResponse("公司不存在",1005,null);
//        for (Company company:companyList){
//            company.setCompanyId(company.getLogin().substring(company.getLogin().indexOf("=")+1));
//            companyRepository.save(company);
//        }
//        return new RestResponse("修改成功",null);
//    }

}
