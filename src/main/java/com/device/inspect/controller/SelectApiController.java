package com.device.inspect.controller;

import com.alibaba.fastjson.JSON;
import com.device.inspect.common.model.charater.Role;
import com.device.inspect.common.model.charater.RoleAuthority;
import com.device.inspect.common.model.charater.User;
import com.device.inspect.common.model.device.*;
import com.device.inspect.common.model.firm.Building;
import com.device.inspect.common.model.firm.Company;
import com.device.inspect.common.model.firm.Room;
import com.device.inspect.common.model.firm.Storey;
import com.device.inspect.common.model.record.DealAlertRecord;
import com.device.inspect.common.model.record.DealRecord;
import com.device.inspect.common.model.record.DeviceOrderList;
import com.device.inspect.common.query.charater.AlertCountQuery;
import com.device.inspect.common.query.charater.CompanyQuery;
import com.device.inspect.common.query.charater.DeviceQuery;
import com.device.inspect.common.query.charater.UserQuery;
import com.device.inspect.common.repository.charater.RoleAuthorityRepository;
import com.device.inspect.common.repository.charater.RoleRepository;
import com.device.inspect.common.repository.charater.UserRepository;
import com.device.inspect.common.repository.device.*;
import com.device.inspect.common.repository.firm.BuildingRepository;
import com.device.inspect.common.repository.firm.CompanyRepository;
import com.device.inspect.common.repository.firm.StoreyRepository;
import com.device.inspect.common.repository.firm.RoomRepository;
import com.device.inspect.common.repository.record.DealAlertRecordRepository;
import com.device.inspect.common.repository.record.DealRecordRepository;
import com.device.inspect.common.repository.record.DeviceOrderListRepository;
import com.device.inspect.common.repository.record.DeviceRunningStatusHistoryRepository;
import com.device.inspect.common.restful.RestResponse;
import com.device.inspect.common.restful.api.RestUserOperation;
import com.device.inspect.common.restful.charater.RestUser;
import com.device.inspect.common.restful.data.*;
import com.device.inspect.common.restful.device.*;
import com.device.inspect.Application;
import com.device.inspect.common.restful.firm.RestCompany;
import com.device.inspect.common.restful.page.*;
import com.device.inspect.common.restful.record.RestDealRecord;
import com.device.inspect.common.restful.version.RestDeviceVersion;
import com.device.inspect.common.service.GetCameraAccessToken;
import com.device.inspect.common.service.GetDeviceAddress;
import com.device.inspect.common.service.MKTCalculator;
import com.device.inspect.common.setting.Constants;
import com.device.inspect.common.util.time.MyCalendar;
import com.device.inspect.common.util.transefer.UrlParse;
import com.device.inspect.common.util.transefer.UserRoleDifferent;
import com.device.inspect.controller.request.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;

/**
 * Created by Administrator on 2016/7/12.
 */
@RestController
@RequestMapping(value = "/api/rest/firm")
public class SelectApiController {
    private static final Logger LOGGER = LogManager.getLogger(SelectApiController.class);
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
    private EntityManager entityManager;

    @Autowired
    private DeviceTypeRepository deviceTypeRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private InspectTypeRepository inspectTypeRepository;

    @Autowired
    private RoleAuthorityRepository roleAuthorityRepository;

    @Autowired
    private DeviceFloorRepository deviceFloorRepository;

    @Autowired
    private DeviceVersionRepository deviceVersionRepository;

    @Autowired
    private DeviceRunningStatusRepository deviceRunningStatusRepository;

    @Autowired
    private DeviceTypeInspectRunningStatusRepository deviceTypeInspectRunningStatusRepository;

    @Autowired
    private DeviceInspectRunningStatusRepository deviceInspectRunningStatusRepository;

    @Autowired
    private DeviceInspectRepository deviceInspectRepository;

    @Autowired
    private DealRecordRepository dealRecordRepository;

    @Autowired
    private DeviceRunningStatusHistoryRepository deviceRunningStatusHistoryRepository;

    @Autowired
    private CameraListRepository cameraListRepository;

    @Autowired
    private DeviceOrderListRepository deviceOrderListRepository;

    @Autowired
    private DealAlertRecordRepository dealAlertRecordRepository;

    @Autowired
    private AlertCountRepository alertCountRepository;

    @Autowired
    private DeviceTypeInspectRepository deviceTypeInspectRepository;

    private User judgeByPrincipal(Principal principal) {
        if (null == principal || null == principal.getName())
            throw new UsernameNotFoundException("You are not login!");
        User user = userRepository.findByName(principal.getName());
        if (null == user)
            throw new UsernameNotFoundException("user not found!");
        return user;
    }

    /**
     * 查询个人信息
     *
     * @param principal
     * @return
     */
    @RequestMapping(value = "/person/info/{userId}")
    public RestResponse getUserMessage(Principal principal, @PathVariable Integer userId) {
        User user1 = judgeByPrincipal(principal);
        if (user1 == null)
            return new RestResponse("用户未登录", 1005, null);
        User user = userRepository.findOne(userId);
        if (null == user)
            return new RestResponse("user not found!", 1005, null);
        return new RestResponse(new RestUser(user));
    }

    /**
     * 用户的个人信息
     *
     * @param principal
     * @return 用户的个人信息
     */
    @RequestMapping(value = "/person/mine/info")
    public RestResponse getMyMessage(Principal principal) {
        User user = judgeByPrincipal(principal);
        if (user == null)
            return new RestResponse("用户未登录", 1005, null);

        user = userRepository.findOne(user.getId());

        return new RestResponse(new RestUser(user));
    }

    /**
     * 查询所有的楼
     *
     * @param principal
     * @param map
     * @return
     */
    @RequestMapping(value = "/buildings")
    public RestResponse getBuildings(Principal principal, @RequestParam Map<String, String> map) {
        User user = judgeByPrincipal(principal);
        if (null == user.getCompany()) {
            return new RestResponse("user's information incorrect!", 1005, null);
        }

        Application.LOGGER.debug(String.format("Find buildings of company %s, %s", user.getCompany().getName(), user.getCompany().getId()));
        List<Building> list = new ArrayList<Building>();
        if (null != map.get("enable") && (map.get("enable").equals("0") || map.get("enable").equals("1")))
            list = buildingRepository.findByCompanyIdAndEnable(user.getCompany().getId(), Integer.valueOf(map.get("enable")));
        else
            list = buildingRepository.findByCompanyId(user.getCompany().getId());
        user.getCompany().setBuildings(list);
        return new RestResponse(new RestIndexBuilding(user.getCompany()));
    }

    /**
     * 查询所有的层
     *
     * @param principal
     * @param map
     * @return
     */
    @RequestMapping(value = "/floors", method = RequestMethod.GET)
    public RestResponse getFloors(Principal principal, @RequestParam Map<String, String> map) {
        User user = judgeByPrincipal(principal);
        if (user == null)
            return new RestResponse("用户未登录", 1005, null);
        String buildId = map.get("buildId");
        Building build = null;
        if (null != buildId) {
            build = buildingRepository.findOne(Integer.valueOf(buildId));
        }

        if (null == build) {
            return new RestResponse("floors information correct!", 1005, null);
        }
        List<Storey> list = new ArrayList<Storey>();
        if (null != map.get("enable") && (map.get("enable").equals("0") || map.get("enable").equals("1")))
            list = storeyRepository.findByBuildIdAndEnable(Integer.valueOf(buildId), Integer.valueOf(map.get("enable")));
        else list = storeyRepository.findByBuildId(Integer.valueOf(buildId));
        build.setFloorList(list);
        return new RestResponse(new RestIndexFloor(build));
    }

    /**
     * 查询所有的室
     *
     * @param principal
     * @param map
     * @return
     */
    @RequestMapping(value = "/rooms", method = RequestMethod.GET)
    public RestResponse getRooms(Principal principal, @RequestParam Map<String, String> map) {
        User user = judgeByPrincipal(principal);
        if (user == null)
            return new RestResponse("用户未登陆", 1005, null);
        String floorId = map.get("floorId");
        Storey floor = null;
        if (null != floorId)
            floor = storeyRepository.findOne(Integer.valueOf(floorId));
        if (null == floor) {
            return new RestResponse("rooms information correct!", 1005, null);
        }
        List<Room> list = new ArrayList<Room>();
        if (null != map.get("enable") && (map.get("enable").equals("0") || map.get("enable").equals("1")))
            list = roomRepository.findByFloorIdAndEnable(Integer.valueOf(floorId), Integer.valueOf(map.get("enable")));
        else list = roomRepository.findByFloorId(Integer.valueOf(floorId));
        floor.setRoomList(list);
        return new RestResponse(new RestIndexRoom(floor));
    }

    /**
     * 查询所有的设备
     *
     * @param principal
     * @param map
     * @return
     */
    @RequestMapping(value = "/devices", method = RequestMethod.GET)
    public RestResponse getDevices(Principal principal, @RequestParam Map<String, String> map) {
        User user = judgeByPrincipal(principal);
        if (user == null)
            return new RestResponse("用户未登陆", 1005, null);
        String roomId = map.get("roomId");
        Room room = null;
        if (null != roomId)
            room = roomRepository.findOne(Integer.valueOf(roomId));
        if (null == room || null == room.getId()) {
            return new RestResponse("devices information correct!", 1005, null);
        }
        List<Device> list = new ArrayList<Device>();
        if (null != map.get("enable") && (map.get("enable").equals("0") || map.get("enable").equals("1")))
            list = deviceRepository.findByRoomIdAndEnable(Integer.valueOf(roomId), Integer.valueOf(map.get("enable")));
        else list = deviceRepository.findByRoomId(Integer.valueOf(roomId));
        room.setDeviceList(list);
        return new RestResponse(new RestIndexDevice(room));
    }

    /**
     * 查询单个的设备
     *
     * @param principal
     * @param deviceId
     * @return
     */
    @RequestMapping(value = "/device", method = RequestMethod.GET)
    public RestResponse getDevice(Principal principal, @RequestParam Integer deviceId) {
        User user = judgeByPrincipal(principal);
        if (user == null)
            return new RestResponse("用户未登陆", 1005, null);
        Device device = deviceRepository.findOne(deviceId);
        if (null == device || null == device.getId()) {
            return new RestResponse("device information correct!", 1005, null);
        }
        List<DeviceFloor> deviceFloorList = deviceFloorRepository.findByDeviceIdAndEnable(deviceId, 1);
        device.setDeviceFloorList(deviceFloorList);

        RestDevice restDevice = new RestDevice(device);
        List<RestDeviceInspect> restDeviceInspects = new ArrayList<>();
        for (DeviceInspect inspect : device.getDeviceInspectList()) {
            List<DeviceInspectRunningStatus> statuses = deviceInspectRunningStatusRepository.findByDeviceInspectId(inspect.getId());
            RestDeviceInspect restInspect = new RestDeviceInspect(inspect);
            List<RestDeviceInspectRunningStatus> restStatuses = new ArrayList<>();
            for (DeviceInspectRunningStatus status : statuses) {
                restStatuses.add(new RestDeviceInspectRunningStatus(status));
            }
            restInspect.setRunningStatus(restStatuses);
            restDeviceInspects.add(restInspect);
        }
        restDevice.setDeviceInspects(restDeviceInspects);

        // 获取北京时间今日凌晨0点的时间戳， 此处需要拓展， 以后要按照设备的时区来。
        Date utcTimeForBeijingMidnight = MyCalendar.getUtcTimeForMidnight(new Date(), 8);

        Long todayHighAlert = alertCountRepository.countByDeviceIdAndTypeAndCreateDateBetween(device.getId(),
                2, utcTimeForBeijingMidnight, new Date());

        Long todayLowAlert = alertCountRepository.countByDeviceIdAndTypeAndCreateDateBetween(device.getId(),
                1, utcTimeForBeijingMidnight, new Date());

        restDevice.setYellowAlertCountToday(todayLowAlert);
        restDevice.setRedAlertCountToday(todayHighAlert);

        return new RestResponse(restDevice);
    }

    /**
     * 查询所有的设备种类
     *
     * @return
     */
    @RequestMapping(value = "/device/types", method = RequestMethod.GET)
    public RestResponse getAllDeviceTypes(Principal principal, @RequestParam Integer enable) {
        User user = judgeByPrincipal(principal);
        if (null == user.getCompany() && UserRoleDifferent.userStartWithFirm(user))
            return new RestResponse("user's information wrong!", 1005, null);
        List<DeviceType> list = new ArrayList<DeviceType>();
        list.addAll(deviceTypeRepository.findByEnableAndCompanyIdIsNull(enable));
        if (UserRoleDifferent.userStartWithFirm(user)) {
            list.addAll(deviceTypeRepository.findByCompanyIdAndEnable(user.getCompany().getId(), enable));
        }
//        Iterable<DeviceType> deviceTypeIterable = deviceTypeRepository.findAll();
        List<RestDeviceType> deviceTypes = new ArrayList<RestDeviceType>();
        if (null != list)
            for (DeviceType deviceType : list)
                deviceTypes.add(new RestDeviceType(deviceType));

        return new RestResponse(deviceTypes);
    }

    /**
     * 查询设备类型参数
     *
     * @param deviceTypeId
     * @return
     */
    @RequestMapping(value = "/device/type/request/{deviceTypeId}")
    public RestResponse getCurrentDeviceTypeRequest(Principal principal, @PathVariable Integer deviceTypeId) {
        User user = judgeByPrincipal(principal);
        if (user == null)
            return new RestResponse("用户未登陆", 1005, null);
        DeviceType deviceType = deviceTypeRepository.findOne(deviceTypeId);
        if (null == deviceType)
            return new RestResponse("当前设备类型不存在！", 1905, null);
        DeviceTypeRequest deviceTypeRequest = new DeviceTypeRequest();
        List<InspectTypeRequest> requests = new ArrayList<InspectTypeRequest>();
        if (null != deviceType.getDeviceTypeInspectList()) {
            for (DeviceTypeInspect deviceTypeInspect : deviceType.getDeviceTypeInspectList()) {
                InspectTypeRequest request = new InspectTypeRequest();
                request.setId(deviceTypeInspect.getInspectType().getId());
                request.setName(deviceTypeInspect.getInspectType().getName());
                request.setChosed(true);
                request.setHighDown(null == deviceTypeInspect.getHighDown() ? null : deviceTypeInspect.getHighDown().toString());
                request.setHighUp(null == deviceTypeInspect.getHighUp() ? null : deviceTypeInspect.getHighUp().toString());
                request.setStandard(null == deviceTypeInspect.getStandard() ? null : deviceTypeInspect.getStandard().toString());
                request.setLowDown(null == deviceTypeInspect.getLowDown() ? null : deviceTypeInspect.getLowDown().toString());
                request.setLowUp(null == deviceTypeInspect.getLowUp() ? null : deviceTypeInspect.getLowUp().toString());
                request.setInspectPurpose(null == deviceTypeInspect.getInspectPurpose() ? null : deviceTypeInspect.getInspectPurpose());

                List<DeviceTypeInspectRunningStatus> statuses = deviceTypeInspectRunningStatusRepository.findByDeviceTypeInspectId(deviceTypeInspect.getId());
                List<DeviceTypeInspectRunningStatusRequest> restStatus = new ArrayList<>();
                for (DeviceTypeInspectRunningStatus status : statuses) {
                    DeviceTypeInspectRunningStatusRequest statusRequest = new DeviceTypeInspectRunningStatusRequest();
                    statusRequest.setId(status.getId());
                    statusRequest.setThreshold(status.getThreshold());
                    statusRequest.setDeviceTypeInspectId(status.getDeviceTypeInspect().getId());
                    statusRequest.setRunningStatusId(status.getDeviceRunningStatus().getId());
                    restStatus.add(statusRequest);
                }
                request.setRunningStatus(restStatus);
                requests.add(request);
            }
        }
        deviceTypeRequest.setId(deviceType.getId());
        deviceTypeRequest.setName(deviceType.getName());
        deviceTypeRequest.setList(requests);
        return new RestResponse(deviceTypeRequest);
    }

    /**
     * 根据设备种类Id获取设备品牌
     */
    @RequestMapping(value = "/device/model")
    public RestResponse getDeviceModelList(Principal principal, @RequestParam(required = false) String deviceTypeId) {
        User user = judgeByPrincipal(principal);
        if (user == null) {
            return new RestResponse("用户未登录",1005,null);
        }

        Set<String> deviceModels = new HashSet<>();
        if (deviceTypeId == null || deviceTypeId.equals("")){
            List<Device> devices = deviceRepository.findByManagerId(user.getId());
            for (Device device : devices) {
                String model = device.getModel();
                if (model != null && !model.equals("")) {
                    deviceModels.add(model);
                }
            }
        } else {
            deviceModels = deviceRepository.findModelByDeviceTypeId(Integer.parseInt(deviceTypeId),user.getId());
        }

        List<DeviceModel> deviceModelList = new ArrayList<>();
        int i = 0;
        for (String deviceModel : deviceModels) {
            DeviceModel model = new DeviceModel();
            model.setId(i);
            model.setModel(deviceModel);
            deviceModelList.add(model);
            i++;
        }
        return new RestResponse(deviceModelList);

    }

    /**
     * 根据设备品牌和设备种类id获取设备id和name
     */
    @RequestMapping(value = "/query/deviceByModel")
    public RestResponse getDeviceListByModel(Principal principal, @RequestParam(required = false) String model,
                                             @RequestParam Integer deviceTypeId) {
        User user = judgeByPrincipal(principal);
        if (user == null) {
            return new RestResponse("用户未登录",1005,null);
        }
        List<Device> deviceList;
        if (model == null || model.equals("")) {
            deviceList = deviceRepository.findByManagerIdAndDeviceTypeId(user.getId(), deviceTypeId);
        } else {
            deviceList = deviceRepository.findByModelAndManagerIdAndDeviceTypeId(model,user.getId(), deviceTypeId);
        }
        System.out.println(deviceList.size());
        List<DeviceIdAndName> deviceIdAndNames = new ArrayList<>();
        for (Device device : deviceList) {
            DeviceIdAndName deviceIdAndName = new DeviceIdAndName();
            deviceIdAndName.setId(device.getId());
            deviceIdAndName.setName(device.getName());
            deviceIdAndNames.add(deviceIdAndName);
        }
        return new RestResponse(deviceIdAndNames);
    }

    /**
     * 查询用户设备列表信息
     *
     * @param principal
     * @param requestParam
     * @return
     */
    @RequestMapping(value = "/manager/devices", method = RequestMethod.GET)
    public RestResponse getAllDevicesByManger(Principal principal, @RequestParam Map<String, String> requestParam) {
//        if (null == principal || null ==principal.getName())
//            return new RestResponse("not login!",1005,null);
        User user = judgeByPrincipal(principal);
        if (null == user || null == user.getCompany()) {
            return new RestResponse("user's information error!", 1005, null);
        }

        Integer limit = 10;
        Integer start = 0;

        if (requestParam.containsKey("limit")) {
            limit = Integer.valueOf(requestParam.get("limit"));
            requestParam.remove("limit");
        }

        if (requestParam.containsKey("start")) {
            start = Integer.valueOf(requestParam.get("start"));
            requestParam.remove("start");
        }

        if (!requestParam.containsKey("userId")) {
            requestParam.put("userId", user.getId().toString());
        }

        if (requestParam.containsKey("roomId")) {
            if (requestParam.get("roomId").toString() != "") {
                if (requestParam.containsKey("floorId"))
                    requestParam.remove("floorId");
                if (requestParam.containsKey("buildingId"))
                    requestParam.remove("buildingId");
            } else {
                requestParam.remove("roomId");
            }
        }
        if (requestParam.containsKey("floorId")) {
            if (requestParam.get("floorId").toString() != "") {
                if (requestParam.containsKey("buildingId"))
                    requestParam.remove("buildingId");
            } else {
                requestParam.remove("floorId");
            }
        }

//        if (UserRoleDifferent.userScientistConfirm(user)){
//            requestParam.put("scientistId",user.getId().toString());
//        }

        Page<Device> devicePage = new DeviceQuery(entityManager)
                .query(requestParam, start, limit, new Sort(Sort.Direction.DESC, "createDate"));

        return new RestResponse(assembleDevices(devicePage));

    }

    /**
     * 查询用户设备列表信息
     *
     * @param requestParam
     * @return
     */
    @RequestMapping(value = "/enableSharing/devices", method = RequestMethod.GET)
    public RestResponse getAllDevicesByEnableSharing(@RequestParam Map<String, String> requestParam) {
        Integer limit = 10;
        Integer start = 0;

        requestParam.put("enableSharing", "1");

        if (requestParam.containsKey("limit")) {
            limit = Integer.valueOf(requestParam.get("limit"));
            requestParam.remove("limit");
        }

        if (requestParam.containsKey("start")) {
            start = Integer.valueOf(requestParam.get("start"));
            requestParam.remove("start");
        }

        if (requestParam.containsKey("roomId")) {
            if (requestParam.get("roomId").toString() != "") {
                if (requestParam.containsKey("floorId"))
                    requestParam.remove("floorId");
                if (requestParam.containsKey("buildingId"))
                    requestParam.remove("buildingId");
            } else {
                requestParam.remove("roomId");
            }
        }
        if (requestParam.containsKey("floorId")) {
            if (requestParam.get("floorId").toString() != "") {
                if (requestParam.containsKey("buildingId"))
                    requestParam.remove("buildingId");
            } else {
                requestParam.remove("floorId");
            }
        }

        Page<Device> devicePage = new DeviceQuery(entityManager)
                .query(requestParam, start, limit, new Sort(Sort.Direction.DESC, "createDate"));

        return new RestResponse(assembleDevices(devicePage));
    }


    /**
     * 平台用户查询设备列表
     *
     * @return
     */
    @RequestMapping(value = "/service/device", method = RequestMethod.GET)
    public RestResponse getAllDevicesByService(Principal principal, @RequestParam Map<String, String> requestParam) {
        User user = judgeByPrincipal(principal);
        if (null == user) {
            return new RestResponse("没有此用户", 1005, null);
        }
        Integer limit = 10;
        Integer start = 0;
        if (requestParam.containsKey("limit")) {
            limit = Integer.valueOf(requestParam.get("limit"));
            requestParam.remove("limit");
        }

        if (requestParam.containsKey("start")) {
            start = Integer.valueOf(requestParam.get("start"));
            requestParam.remove("start");
        }

        Page<Device> devicePage = new DeviceQuery(entityManager)
                .query(requestParam, start, limit, new Sort(Sort.Direction.DESC, "createDate"));

        return new RestResponse(assembleDevices(devicePage));
    }


    @RequestMapping(value = "/employees", method = RequestMethod.GET)
    public RestResponse getAllEmployees(Principal principal, @RequestParam Map<String, String> requestParam) {
//        if (null == principal || null ==principal.getName())
//            return new RestResponse("not login!",1005,null);
        User user = judgeByPrincipal(principal);
        if (user == null)
            return new RestResponse("用户未登陆", 1005, null);
        List<RoleAuthority> roleAuthorities = new ArrayList<RoleAuthority>();
        if (null != user.getRoles())
            for (Role role : user.getRoles()) {
                roleAuthorities.addAll(roleAuthorityRepository.findByParent(role.getRoleAuthority().getId()));
            }

        if (null == user && null == user.getCompany() && (null == roleAuthorities || roleAuthorities.size() == 0)) {
            return new RestResponse("user's information correct!", 1005, null);
        }

        Integer limit = 10;
        Integer start = 0;

        if (requestParam.containsKey("limit")) {
            limit = Integer.valueOf(requestParam.get("limit"));
            requestParam.remove("limit");
        }

        if (requestParam.containsKey("start")) {
            start = Integer.valueOf(requestParam.get("start"));
            requestParam.remove("start");
        }
        List<Integer> list = new ArrayList<Integer>();
        if (null != roleAuthorities)
            for (RoleAuthority roleAuthority : roleAuthorities) {
                list.add(roleAuthority.getId());
            }

        requestParam.put("authorityId", JSON.toJSONString(list));
        if (UserRoleDifferent.userFirmManagerConfirm(user)) {
            requestParam.put("companyId", user.getCompany().getId().toString());
        }


        Page<User> userPage = new UserQuery(entityManager)
                .query(requestParam, start, limit, new Sort(Sort.Direction.DESC, "createDate"));

        return new RestResponse(assembleUsers(user, userPage));
    }

    /**
     * 平台业务员查询所有的业务企业，企业员工查询自己的
     *
     * @param principal
     * @return
     */
    @RequestMapping(value = "/query/all/company")
    public RestResponse getAllCompany(Principal principal) {
        User user = judgeByPrincipal(principal);
        if (null == user) {
            return new RestResponse("用户信息不存在！", 1005, null);
        }
        List<RestCompany> list = new ArrayList<RestCompany>();
        if (UserRoleDifferent.userServiceManagerConfirm(user)) {
            Map<String, String> requestParam = new HashMap<String, String>();
            Page<Company> companyPage = new CompanyQuery(entityManager)
                    .query(requestParam, 0, 100000, new Sort(Sort.Direction.DESC, "createDate"));
            list = (List) assembleCompanies(companyPage).get("companies");
        } else if (UserRoleDifferent.userServiceWorkerConfirm(user)) {
            Map<String, String> requestParam = new HashMap<String, String>();
            requestParam.put("businessId", user.getId().toString());
            Page<Company> companyPage = new CompanyQuery(entityManager)
                    .query(requestParam, 0, 100000, new Sort(Sort.Direction.DESC, "createDate"));
            list = (List) assembleCompanies(companyPage).get("companies");
        } else {
            Company company = user.getCompany();
            list.add(new RestCompany(company));
        }

        return new RestResponse(list);
    }

    /**
     * 登陆页面根据公司URL获取公司信息
     *
     * @param companyId
     * @return
     */
    @RequestMapping(value = "/query/login/company")
    public RestResponse getCompanyById(Principal principal, @RequestParam String companyId) {
//        User user=judgeByPrincipal(principal);
//        if (user==null)
//            return new RestResponse("用户未登陆",1005,null);
        if (null == companyId || companyId.equals(""))
            return new RestResponse("没有正确的访问参数！", null);
        /*
        String realId = "";
        try {
            realId = URLDecoder.decode(ByteAndHex.convertMD5(companyId),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        */
        //Company company = companyRepository.findOne(Integer.valueOf(realId));
        Company company = companyRepository.findByCompanyId(companyId);
        if (null == company)
            return new RestResponse("当前登陆页面不是企业URL！", 1005, null);
        return new RestResponse(new RestCompany(company));
    }

    /**
     * 根据登录人获取所属公司信息
     *
     * @param principal
     * @param requestParam
     * @return
     */
    @RequestMapping(value = "/query/mine/company")
    public RestResponse getCompanyByUserName(Principal principal, @RequestParam Map<String, String> requestParam) {
        User user = judgeByPrincipal(principal);
        if (user == null)
            return new RestResponse("用户未登陆", 1005, null);

        if (UserRoleDifferent.userServiceWorkerConfirm(user)) {
            requestParam.put("businessId", user.getId().toString());
        } else if (UserRoleDifferent.userServiceManagerConfirm(user)) {

        } else {
            return new RestResponse("权限不足！", null);
        }

        Integer limit = 10;
        Integer start = 0;

        if (requestParam.containsKey("limit")) {
            limit = Integer.valueOf(requestParam.get("limit"));
            requestParam.remove("limit");
        }

        if (requestParam.containsKey("start")) {
            start = Integer.valueOf(requestParam.get("start"));
            requestParam.remove("start");
        }

        Page<Company> companyPage = new CompanyQuery(entityManager)
                .query(requestParam, start, limit, new Sort(Sort.Direction.DESC, "createDate"));

        return new RestResponse(assembleCompanies(companyPage));
    }

    private Map assembleCompanies(Page<Company> companyPage) {
        Map map = new HashMap();
        map.put("total", String.valueOf(companyPage.getTotalElements()));
        map.put("thisNum", String.valueOf(companyPage.getNumberOfElements()));
        List<RestCompany> list = new ArrayList<RestCompany>();
        for (Company company : companyPage.getContent()) {
            list.add(new RestCompany(company));
        }
        map.put("companies", list);
        return map;
    }

    private Map assembleDevices(Page<Device> devicePage) {
        Map map = new HashMap();
        map.put("total", String.valueOf(devicePage.getTotalElements()));
        map.put("thisNum", String.valueOf(devicePage.getNumberOfElements()));
        List<RestDevice> list = new ArrayList<RestDevice>();
        for (Device device : devicePage.getContent()) {
            list.add(new RestDevice(device));
        }
        map.put("devices", list);
        return map;
    }

    private Map assembleAlertCount(Page<AlertCount> alertCountPage) {
        Map map = new HashMap();
        map.put("total", String.valueOf(alertCountPage.getTotalElements()));
        map.put("thisNum", String.valueOf(alertCountPage.getNumberOfElements()));
        List<RestAlertCount> list = new ArrayList<>();
        for (AlertCount alertCount:alertCountPage.getContent()){
            list.add(new RestAlertCount(alertCount));
        }
        map.put("alertCountList", list);
        return map;
    }

    private Map assembleUsers(User userRoot, Page<User> userPage) {
        Map map = new HashMap();
        map.put("pages", String.valueOf(userPage.getTotalPages()));
        map.put("total", String.valueOf(userPage.getTotalElements()));
        map.put("thisNum", String.valueOf(userPage.getNumberOfElements()));
        List<User> list = new ArrayList<User>();
        for (User user : userPage.getContent()) {
            list.add(user);
        }

        map.put("userList", new RestIndexUser(userRoot, list));
        return map;
    }

    /**
     * 获取当前企业所有员工(仅包含企业管理员和设备管理员)
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/colleges/manager")
    public RestResponse getMyCompanyWorkers(Principal principal) {
        User user = judgeByPrincipal(principal);
        if (null == user)
            return new RestResponse("用户信息出错！", 1005, null);

        List<User> list = userRepository.findByCompanyId(user.getCompany().getId());
        List<RestUser> result = new ArrayList<RestUser>();
        for (User userEnch : list) {
            if (null != userEnch.getRoles() && (UserRoleDifferent.userFirmWorkerConfirm(userEnch) ||
                    UserRoleDifferent.userFirmManagerConfirm(userEnch))) {
                RestUser restUser = new RestUser(userEnch);
                result.add(restUser);
            }
        }
        return new RestResponse(result);
    }

    /**
     * 获取设备的检测参数
     *
     * @return
     */
    @RequestMapping(value = "/query/inspect/type")
    public RestResponse getAllInspectType(Principal principal) {
//        if (null==principal)
//            throw new UsernameNotFoundException("you are not login!");
//        User user = userRepository.findByName(principal.getName());
//        if (null==user)
//            throw new UsernameNotFoundException("user's name isn't correct!");

        Iterable<InspectType> iterable = inspectTypeRepository.findAll();

        DeviceTypeRequest deviceTypeRequest = new DeviceTypeRequest();
        List<InspectTypeRequest> list = new ArrayList<InspectTypeRequest>();
        if (null != iterable) {
            for (InspectType inspectType : iterable) {
                InspectTypeRequest inspectTypeRequest = new InspectTypeRequest();
                inspectTypeRequest.setId(inspectType.getId());
                inspectTypeRequest.setName(inspectType.getName());
                List<DeviceTypeInspectRunningStatus> runningStatuses =
                        deviceTypeInspectRunningStatusRepository.findByDeviceTypeInspectId(inspectType.getId());
                List<DeviceTypeInspectRunningStatusRequest> runningStatusRequests = new ArrayList<>();
                if (null != runningStatuses) {
                    for (DeviceTypeInspectRunningStatus status : runningStatuses) {
                        runningStatusRequests.add(new DeviceTypeInspectRunningStatusRequest(status));
                    }
                    inspectTypeRequest.setRunningStatus(runningStatusRequests);
                }
                list.add(inspectTypeRequest);
            }
        }
        deviceTypeRequest.setList(list);
        return new RestResponse(deviceTypeRequest);
    }

//    @RequestMapping("/test/company")
//    public RestResponse updateCompanyURL(){
//        Iterable<Company> companies = companyRepository.findAll();
//        for (Company company:companies){
//
//            try {
//                company.setLogin("http://inmycars.ihengtian.top/inspect/Lab_login.html?company="+ ByteAndHex.convertMD5(URLEncoder.encode(company.getId().toString(),"UTF-8")));
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//            companyRepository.save(company);
//        }
//        return new RestResponse();
//    }

    /**
     * 获取用户所在企业所有的科学家
     *
     * @param principal
     * @return
     */
    @RequestMapping("/colleges/scientist")
    public RestResponse getMyScientist(Principal principal) {
        User user = judgeByPrincipal(principal);
        if (user == null)
            return new RestResponse("用户未登陆", 1005, null);

        List<User> list = userRepository.findByCompanyId(user.getCompany().getId());
        List<RestUser> result = new ArrayList<RestUser>();
        for (User userEnch : list) {
            if (UserRoleDifferent.userScientistConfirm(userEnch)) {
                RestUser restUser = new RestUser(userEnch);
                result.add(restUser);
            }
        }
        return new RestResponse(result);
    }

    /**
     * 删除公司人员
     *
     * @param principal
     * @param userId
     * @return
     */
    @RequestMapping(value = "/take/over/colleges")
    public RestResponse getAllCompanyColleges(Principal principal, @RequestParam Integer userId) {
        User user = judgeByPrincipal(principal);
        if (user == null)
            return new RestResponse("用户未登陆", 1005, null);

        if (!UserRoleDifferent.userFirmManagerConfirm(user))
            return new RestResponse("权限不足，无法查询！", 1005, null);
        User old = userRepository.findOne(userId);
        boolean deviceManagerFlag = UserRoleDifferent.userFirmWorkerConfirm(old);
        boolean scentistFlag = UserRoleDifferent.userScientistConfirm(old);

        if (null == old)
            return new RestResponse("请选择要删除的人员！", 1005, null);
        List<User> list = userRepository.findByCompanyId(user.getCompany().getId());
        List<RestUser> result = new ArrayList<RestUser>();
        for (User userEnch : list) {
            if (!userEnch.getId().equals(old.getId())) {
                //判断是否是设备管理员
                boolean overManageFlag = UserRoleDifferent.userFirmWorkerConfirm(userEnch);
                //是否是科学家
                boolean overScientist = UserRoleDifferent.userScientistConfirm(userEnch);
                if (deviceManagerFlag && scentistFlag) {
                    if (overManageFlag && overScientist) {
                        RestUser restUser = new RestUser(userEnch);
                        result.add(restUser);
                    }
                } else {
                    if (deviceManagerFlag) {
                        if (UserRoleDifferent.userFirmManagerConfirm(userEnch) ||
                                deviceManagerFlag == overManageFlag) {
                            RestUser restUser = new RestUser(userEnch);
                            result.add(restUser);
                        }
                    }
                    if (scentistFlag && scentistFlag == overScientist) {
                        RestUser restUser = new RestUser(userEnch);
                        result.add(restUser);
                    }
                }
            }
        }
        return new RestResponse(result);
    }

    /**
     * 查询所有版本号接口
     *
     * @param principal 验证用户是否登陆
     * @return
     */
    @RequestMapping(value = "/service/get/versions")
    public RestResponse getAllVersion(Principal principal) {
        User user = judgeByPrincipal(principal);
        if (user == null)
            return new RestResponse("用户未登陆", 1005, null);

        if (UserRoleDifferent.userServiceManagerConfirm(user)) {
            Iterable<DeviceVersion> iterable = deviceVersionRepository.findAll();
            List<RestDeviceVersion> list = new ArrayList<RestDeviceVersion>();
            if (null != iterable) {

                for (DeviceVersion deviceVersion : iterable) {
                    list.add(new RestDeviceVersion(deviceVersion));
                }
                return new RestResponse(list);
            } else {
                return new RestResponse("没有历史版本更新", 1005, null);
            }
        } else {
            return new RestResponse("权限不足！", 1005, null);
        }
    }


    /**
     * 获取所有运行状态
     *
     * @param principal
     * @return
     */
    @RequestMapping(value = "/device/running_status", method = RequestMethod.GET)
    public RestResponse getDeviceRunningStatus(Principal principal) {
//        User user = judgeByPrincipal(principal);
//        if(user == null)
//            return new RestResponse("用户未登陆",1005,null);
        Iterable<DeviceRunningStatus> iterable = deviceRunningStatusRepository.findAll();

        List<RunningStatusRequest> list = new ArrayList<RunningStatusRequest>();
        if (null != iterable) {
            for (DeviceRunningStatus status : iterable) {
                RunningStatusRequest runningStatusRequest = new RunningStatusRequest();
                runningStatusRequest.setId(status.getId());
                runningStatusRequest.setName(status.getName());
                runningStatusRequest.setLevel(status.getLevel());
                runningStatusRequest.setDescription(status.getDescription());
                list.add(runningStatusRequest);
            }
        }
        return new RestResponse(list);
    }

    /**
     * 获取设备种类监控参数对应状态
     *
     * @param deviceTypeInspectId
     * @return
     */
    @RequestMapping(value = "/device/type/status", method = RequestMethod.GET)
    public RestResponse getDeviceTypeInspectRunningStatus(Principal principal, @RequestParam Integer deviceTypeInspectId) {
//        User user = judgeByPrincipal(principal);
//        if(user == null)
//            return new RestResponse("用户未登陆",1005,null);
        List<DeviceTypeInspectRunningStatus> iterable = deviceTypeInspectRunningStatusRepository.findByDeviceTypeInspectId(deviceTypeInspectId);
        List<RestDeviceTypeInspectRunningStatus> list = new ArrayList<>();
        if (null != iterable) {
            for (DeviceTypeInspectRunningStatus status : iterable) {
                RestDeviceTypeInspectRunningStatus deviceTypeInspectRunningStatus = new RestDeviceTypeInspectRunningStatus(status);
                list.add(deviceTypeInspectRunningStatus);
            }
        }
        return new RestResponse(list);
    }

    /**
     * 获取设备监控参数对应状态
     *
     * @param deviceInspectId
     * @return
     */
    @RequestMapping(value = "/device/inspect/status", method = RequestMethod.GET)
    public RestResponse getDeviceInspectRunningStatus(Principal principal, @RequestParam Integer deviceInspectId) {
//        User user = judgeByPrincipal(principal);
//        if(user == null)
//            return new RestResponse("用户未登陆",1005,null);
        List<DeviceInspectRunningStatus> iterable = deviceInspectRunningStatusRepository.findByDeviceInspectId(deviceInspectId);
        List<RestDeviceInspectRunningStatus> list = new ArrayList<>();
        if (null != iterable) {
            for (DeviceInspectRunningStatus status : iterable) {
                RestDeviceInspectRunningStatus deviceInspectRunningStatus = new RestDeviceInspectRunningStatus(status);
                list.add(deviceInspectRunningStatus);
            }
        }
        return new RestResponse(list);
    }

    /**
     * 获取最近一周的某设备的hourly设备利用率
     */
    @RequestMapping(value = "/device/utilization", method = RequestMethod.GET)
    public RestResponse getWeekUtilization(Principal principal, @RequestParam Map<String, String> requestParam) {
        User user = judgeByPrincipal(principal);
        if(user == null)
            return new RestResponse("用户未登陆",1005,null);
        if (!requestParam.containsKey("deviceId")) {
            return new RestResponse("设备id为空", 1006, null);
        }
        Device device;
        device = deviceRepository.findById(Integer.parseInt(requestParam.get("deviceId")));
        if (device == null) {
            return new RestResponse("设备不存在", 1006, null);
        }

        Date beginTime, endTime;
        if (requestParam.containsKey("startTime")) {
            beginTime = new Date(Long.parseLong(requestParam.get("startTime")));
            if (requestParam.containsKey("endTime")) {
                endTime = new Date(Long.parseLong(requestParam.get("endTime")));
            } else {
                Calendar endCalendar = Calendar.getInstance();
                endCalendar.setTime(beginTime);
                endCalendar.set(Calendar.DATE, endCalendar.get(Calendar.DATE) + 6);
                endCalendar.set(Calendar.HOUR, 23);
                endCalendar.set(Calendar.MINUTE, 59);
                endTime = endCalendar.getTime();
            }
        } else {
            Calendar endCalendar = Calendar.getInstance();
            endTime = endCalendar.getTime();
            Calendar beginCalendar = Calendar.getInstance();
            beginCalendar.set(Calendar.DATE, endCalendar.get(Calendar.DATE) - 6);
            beginCalendar.set(Calendar.HOUR, 0);
            beginCalendar.set(Calendar.MINUTE, 0);
            beginCalendar.set(Calendar.SECOND, 0);
            beginCalendar.set(Calendar.MILLISECOND, 0);
            beginTime = beginCalendar.getTime();
        }
        System.out.println("begin time: " + beginTime + ", end time: " + endTime);


        List<List<Object>> utilizationList = Application.influxDBManager.readDeviceUtilizationInTimeRange(device.getId(), beginTime, endTime);

        List<RestHourlyUtilization> restHourlyUtilizations = new ArrayList<>();
        for (int i = 0; i < utilizationList.size(); i++) {
            long timeStamp = TimeUtil.fromInfluxDBTimeFormat((String) utilizationList.get(i).get(0));
            float runningSeconds = ((Double) utilizationList.get(i).get(1)).floatValue();
            float idleSeconds = ((Double) utilizationList.get(i).get(2)).floatValue();

            RestHourlyUtilization restHourlyUtilization = new RestHourlyUtilization(timeStamp,
                    runningSeconds / 3600, idleSeconds / 3600);
            restHourlyUtilizations.add(restHourlyUtilization);

        }

        return new RestResponse(new RestHourlyUtilizationList(device.getId(), restHourlyUtilizations));
    }

    /**
     * 获取日报警统计
     * @param principal
     * @param requestParam
     * @return
     * curl -H "SESSIONID:FD9AE35D84C593958ADF4EA36AE7EC2E" http://localhost:8999/api/rest/firm/report/daily/alert?deviceId=233\&startTime=1493078400000\&endTime=1493251200000 | json_pp
     */
    @RequestMapping(value = "/report/daily/alert", method = RequestMethod.GET)
    public RestResponse getReportDailyAlert(Principal principal, @RequestParam Map<String, String> requestParam){

        //判断用户是否登录
        judgeByPrincipal(principal);

        //获取参数
        Date beginTime, endTime;
        if (requestParam.containsKey("startTime")) {
            beginTime = new Date(Long.parseLong(requestParam.get("startTime")));


        }else{
            // if startTime is not in parameter, use 30 days before
            Calendar endCalendar = Calendar.getInstance();
            Calendar beginCalendar = Calendar.getInstance();
            beginCalendar.set(Calendar.DATE, endCalendar.get(Calendar.DATE) - 30);
            beginCalendar.set(Calendar.HOUR, 0);
            beginCalendar.set(Calendar.MINUTE, 0);
            beginCalendar.set(Calendar.SECOND, 0);
            beginCalendar.set(Calendar.MILLISECOND, 0);
            beginTime = beginCalendar.getTime();
        }
        if (requestParam.containsKey("endTime")) {
            endTime = new Date(Long.parseLong(requestParam.get("endTime")));

        } else {
            // if endTime is not in parameter, use current time
            endTime = new Date();
        }

        final int HARD_LIMIT = 100;

        int limit = HARD_LIMIT;
        if(requestParam.containsKey("limit")){
            limit = Integer.parseInt(requestParam.get("limit"));
            if (limit > HARD_LIMIT){
                limit = HARD_LIMIT;
            }
        }

        int offset = 0;
        if(requestParam.containsKey("offset")){
            offset = Integer.parseInt(requestParam.get("offset"));
            if (offset < 0){
                offset = 0;
            }
        }

        int deviceTypeId = -1;

        if(requestParam.containsKey("deviceTypeId")  && StringUtils.isNotEmpty(requestParam.get("deviceTypeId"))){
            deviceTypeId = Integer.parseInt(requestParam.get("deviceTypeId"));
        }

        int inspectTypeId = -1;
        if(requestParam.containsKey("inspectTypeId")  && StringUtils.isNotEmpty(requestParam.get("inspectTypeId"))){
            inspectTypeId = Integer.parseInt(requestParam.get("inspectTypeId"));
        }

        int deviceId = -1;
        if(requestParam.containsKey("deviceId")  && StringUtils.isNotEmpty(requestParam.get("deviceId"))){
            deviceId = Integer.parseInt(requestParam.get("deviceId"));
        }

        String deviceModel = null;
        if(requestParam.containsKey("deviceModel")  && StringUtils.isNotEmpty(requestParam.get("deviceModel"))){
            deviceModel = requestParam.get("deviceModel");
        }

        DailyAlertTS alertTS = new DailyAlertTS();

        List<QueryResult.Series> influxdbSeries;
        if (deviceId >= 0){
            influxdbSeries = Application.influxDBManager.readDailyAlertByDeviceIdTimeRange(beginTime, endTime,
                    deviceId, inspectTypeId, limit, offset);
        }
        else if(deviceTypeId >= 0 ) {
            if (deviceModel != null) {
                influxdbSeries = Application.influxDBManager.readDailyAlertByDeviceTypeDeviceModelTimeRange(beginTime, endTime,
                        deviceTypeId, deviceModel, inspectTypeId, limit, offset);
            } else {
                influxdbSeries = Application.influxDBManager.readDailyAlertByDeviceTypeTimeRange(beginTime, endTime,
                        deviceTypeId, inspectTypeId, limit, offset);
            }
        }
        else {
            influxdbSeries = Application.influxDBManager.readDailyAlertByTimeRange(beginTime, endTime,
                    inspectTypeId, limit, offset);
        }

        alertTS.setDeviceTypeId(deviceTypeId);
        alertTS.setInspectTypeId(inspectTypeId);
        alertTS.setDeviceId(deviceId);
        alertTS.setDeviceModel(deviceModel);
        if(deviceTypeId >= 0){
            try {
                alertTS.setDeviceType(deviceTypeRepository.findOne(deviceTypeId).getName());

            }catch (Exception ex){
                LOGGER.info(String.format("API parameter deviceTypeId %d not exist in DB. Err: %s", deviceTypeId, ex.getMessage()));
                return new RestResponse("Device type nod found", 1011, null);
            }
        }

        if(inspectTypeId >= 0){
            try {
                alertTS.setInspectType(inspectTypeRepository.findOne(inspectTypeId).getName());
                alertTS.setMeasurement(inspectTypeRepository.findOne(inspectTypeId).getMeasurement());
            }catch (Exception ex){
                LOGGER.info(String.format("API parameter inspectTypeId %d not exist in DB. Err: %s", inspectTypeId, ex.getMessage()));
                return new RestResponse("Inspect type nod found", 1011, null);
            }
        }
        List<List<Object>> yellowAlertCount = null;
        List<List<Object>> redAlertCount = null;
        if(influxdbSeries != null) {
            for (QueryResult.Series series : influxdbSeries) {
                Map<String, String> tags = series.getTags();
                if (tags.containsKey("alert_type")) {
                    List<List<Object>> alertList = series.getValues();

                    if (tags.get("alert_type").equals("1")) {
                        yellowAlertCount = alertList;
                    } else {
                        redAlertCount = alertList;
                    }
                    for (List<Object> alertItem : alertList) {

                        try {
                            long timeStamp = DateUtils.parseDate((String)alertItem.get(0), "yyyy-MM-dd'T'HH:mm:ss'Z'").getTime();

                            alertItem.set(0, timeStamp);
                        }catch(Exception ex){
                            LOGGER.warn("Daily report alert series contains illegal time " + alertItem.get(0).toString());
                            continue;
                        }


                        if (alertItem.get(1) != null) {
                            Double avgCount = new BigDecimal((Double) alertItem.get(1)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

                            alertItem.set(1, avgCount);
                        } else {
                            alertItem.set(1, 0.0);
                        }
                    }

                }
            }
        }

        alertTS.setRedAlertCount(redAlertCount);
        alertTS.setYellowAlertCount(yellowAlertCount);

        return new RestResponse(alertTS);
    }


    /**
     * 获取日均监控统计数据
     * @param principal
     * @param requestParam
     * @return
     * curl -H "SESSIONID:FD9AE35D84C593958ADF4EA36AE7EC2E" http://localhost:8999/api/rest/firm/report/daily/monitoring?deviceId=233\&inspectTypeId=12\&startTime=1497657600000\&endTime=1497830400000 | json_pp
     */
    @RequestMapping(value = "/report/daily/monitoring", method = RequestMethod.GET)
    public RestResponse getReportDailyMonitoring(Principal principal, @RequestParam Map<String, String> requestParam){

        //判断用户是否登录
        judgeByPrincipal(principal);

        //获取参数
        Date beginTime, endTime;
        if (requestParam.containsKey("startTime")) {
            beginTime = new Date(Long.parseLong(requestParam.get("startTime")));


        }else{
            // if startTime is not in parameter, use 30 days before
            Calendar endCalendar = Calendar.getInstance();
            Calendar beginCalendar = Calendar.getInstance();
            beginCalendar.set(Calendar.DATE, endCalendar.get(Calendar.DATE) - 30);
            beginCalendar.set(Calendar.HOUR, 0);
            beginCalendar.set(Calendar.MINUTE, 0);
            beginCalendar.set(Calendar.SECOND, 0);
            beginCalendar.set(Calendar.MILLISECOND, 0);
            beginTime = beginCalendar.getTime();
        }
        if (requestParam.containsKey("endTime")) {
            endTime = new Date(Long.parseLong(requestParam.get("endTime")));

        } else {
            // if endTime is not in parameter, use current time
            endTime = new Date();
        }

        final int HARD_LIMIT = 100;

        int limit = HARD_LIMIT;
        if(requestParam.containsKey("limit")){
            limit = Integer.parseInt(requestParam.get("limit"));
            if (limit > HARD_LIMIT){
                limit = HARD_LIMIT;
            }
        }

        int offset = 0;
        if(requestParam.containsKey("offset")){
            offset = Integer.parseInt(requestParam.get("offset"));
            if (offset < 0){
                offset = 0;
            }
        }

        int deviceTypeId = -1;

        if(requestParam.containsKey("deviceTypeId") && StringUtils.isNotEmpty(requestParam.get("deviceTypeId"))){
            deviceTypeId = Integer.parseInt(requestParam.get("deviceTypeId"));
        }

        int inspectTypeId = -1;
        if(requestParam.containsKey("inspectTypeId")  && StringUtils.isNotEmpty(requestParam.get("inspectTypeId"))){
            inspectTypeId = Integer.parseInt(requestParam.get("inspectTypeId"));
        }else{
            return new RestResponse("Parameter inspectTypeId is missing", 1013, null);
        }

        int deviceId = -1;
        if(requestParam.containsKey("deviceId")  && StringUtils.isNotEmpty(requestParam.get("deviceId"))){
            deviceId = Integer.parseInt(requestParam.get("deviceId"));
        }

        String deviceModel = null;
        if(requestParam.containsKey("deviceModel")  && StringUtils.isNotEmpty(requestParam.get("deviceModel"))){
            deviceModel = requestParam.get("deviceModel");
        }

        DailyMonitoringTS monitoringTS = new DailyMonitoringTS();

        List<QueryResult.Series> influxdbSeries;
        List<Object> aggregateData;
        if (deviceId >= 0){
            influxdbSeries = Application.influxDBManager.readDailyMonitoringDataByDeviceIdInspectTypeTimeRange(beginTime, endTime,
                    deviceId, inspectTypeId, limit, offset);
            aggregateData = Application.influxDBManager.readDailyAggregateMonitoringDataByDeviceIdInspectTypeTimeRange(beginTime, endTime, deviceId, inspectTypeId);
        }
        else if(deviceTypeId >= 0 ) {
            if (deviceModel != null) {
                influxdbSeries = Application.influxDBManager.readDailyMonitoringDataByDeviceTypeDeviceModelInspectTypeTimeRange(beginTime, endTime,
                        deviceTypeId, deviceModel, inspectTypeId, limit, offset);

                aggregateData = Application.influxDBManager.readDailyAggregateMonitoringMByDeviceTypeDeviceModelInspectTypeTimeRange(beginTime, endTime, deviceTypeId, deviceModel, inspectTypeId);
            } else {
                influxdbSeries = Application.influxDBManager.readDailyMonitoringDataByDeviceTypeInspectTypeTimeRange(beginTime, endTime,
                        deviceTypeId, inspectTypeId, limit, offset);

                aggregateData = Application.influxDBManager.readDailyAggregateMonitoringDataByDeviceTypeInspectTypeTimeRange(beginTime, endTime, deviceTypeId, inspectTypeId);
            }
        }
        else {
            return new RestResponse("Must select a device type", 1015, null);
        }

        monitoringTS.setDeviceTypeId(deviceTypeId);
        monitoringTS.setInspectTypeId(inspectTypeId);
        monitoringTS.setDeviceId(deviceId);
        monitoringTS.setDeviceModel(deviceModel);
        if(deviceTypeId >= 0){
            try {
                monitoringTS.setDeviceType(deviceTypeRepository.findOne(deviceTypeId).getName());

            }catch (Exception ex){
                LOGGER.info(String.format("API parameter deviceTypeId %d not exist in DB. Err: %s", deviceTypeId, ex.getMessage()));
                return new RestResponse("Device type nod found", 1011, null);
            }
        }

        if(inspectTypeId >= 0){
            try {
                monitoringTS.setInspectType(inspectTypeRepository.findOne(inspectTypeId).getName());
                monitoringTS.setMeasurement(inspectTypeRepository.findOne(inspectTypeId).getMeasurement());
                monitoringTS.setUnit(inspectTypeRepository.findOne(inspectTypeId).getUnit());
            }catch (Exception ex){
                LOGGER.info(String.format("API parameter inspectTypeId %d not exist in DB. Err: %s", inspectTypeId, ex.getMessage()));
                return new RestResponse("Inspect type nod found", 1011, null);
            }
        }
        List<List<Object>> averageTelemetry = null;
        if(influxdbSeries != null) {
            for (QueryResult.Series series : influxdbSeries) {
                List<List<Object>> avgTelemetryList = series.getValues();

                averageTelemetry = avgTelemetryList;
                for (List<Object> alertItem : avgTelemetryList) {

                    try {
                        long timeStamp = DateUtils.parseDate((String)alertItem.get(0), "yyyy-MM-dd'T'HH:mm:ss'Z'").getTime();

                        alertItem.set(0, timeStamp);
                    }catch(Exception ex){
                        LOGGER.warn("Daily report monitoring series contains illegal time " + alertItem.get(0).toString());
                        continue;
                    }


                    if (alertItem.get(1) != null) {
                        Double avgCount = new BigDecimal((Double) alertItem.get(1)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

                        alertItem.set(1, avgCount);
                    }
                }
                // there is only 1 type of telemetry, so only one series
                break;
            }

        }

        monitoringTS.setDailyAverage(averageTelemetry);

        if(aggregateData != null && aggregateData.size() > 3){
            monitoringTS.setOveralMax((Double)aggregateData.get(1));
            monitoringTS.setOveralMin((Double)aggregateData.get(2));
            monitoringTS.setOveralAverage((Double)aggregateData.get(3));
        }

        return new RestResponse(monitoringTS);
    }


    /**
     * 获取用户操作记录
     * @param principal
     * @param requestParam http GET 参数
     *         startTime: 起始时间  optional
     *         endTime: 结束时间    optional
     *         username: 用户名     optional
     *         operationtype: 操作类型  optional, 可选值: query (对应HTTP GET), update (对应HTTP POST, PUT, DELETE)
     *         limit: 获取操作条目上限 optional 默认为100
     *         offset: 获取操作条目偏移量 optional  默认为0
     * @return
     */
    @RequestMapping(value = "/user/operations", method = RequestMethod.GET)
    public RestResponse getUserOperations(Principal principal, @RequestParam Map<String, String> requestParam){


        User user = judgeByPrincipal(principal);
        String companyId = user.getCompany().getCompanyId();

        Date beginTime, endTime;
        if (requestParam.containsKey("startTime")) {
            beginTime = new Date(Long.parseLong(requestParam.get("startTime")));

        }else{
            // if startTime is not in parameter, use 3 day before
            Calendar endCalendar = Calendar.getInstance();
            Calendar beginCalendar = Calendar.getInstance();
            beginCalendar.set(Calendar.DATE, endCalendar.get(Calendar.DATE) - 2);
            beginCalendar.set(Calendar.HOUR, 0);
            beginCalendar.set(Calendar.MINUTE, 0);
            beginCalendar.set(Calendar.SECOND, 0);
            beginCalendar.set(Calendar.MILLISECOND, 0);
            beginTime = beginCalendar.getTime();
        }
        if (requestParam.containsKey("endTime")) {
            endTime = new Date(Long.parseLong(requestParam.get("endTime")));
        } else {
            // if endTime is not in parameter, use current time
            endTime = new Date();
        }

        final int HARD_LIMIT = 100;

        int limit = HARD_LIMIT;
        if(requestParam.containsKey("limit")){
            limit = Integer.parseInt(requestParam.get("limit"));
            if (limit > HARD_LIMIT){
                limit = HARD_LIMIT;
            }
        }

        int offset = 0;
        if(requestParam.containsKey("offset")){
            offset = Integer.parseInt(requestParam.get("offset"));
            if (offset < 0){
                offset = 0;
            }
        }

        String userName = null;
        String operationType = null;
        String userCompany = null; // the username saved in table is in format username@companyId, e.g., John@BAX

        if (requestParam.containsKey("username")){
            userName = requestParam.get("username");
            if(userName.isEmpty()){
                userName = null;
            }
            else if(!userName.contains("@")){
                userCompany = String.format("%s@%s", userName, companyId);
            }else{
                userCompany = userName;
            }
        }

        if(requestParam.containsKey("operationtype")){
            operationType = requestParam.get("operationtype");
            if(operationType.isEmpty()){
                operationType = null;
            }
        }

        List<List<Object>> userOps = null;
        if (userName != null && operationType != null){
            userOps = Application.influxDBManager.readAPIByTypeMethodTypeUsernameTimeRange(
                    UrlParse.API_TYPE_USER_OPERATION,
                    operationType.equals("query"),
                    userCompany,
                    beginTime,
                    endTime,
                    limit,
                    offset);
        }
        else if(userName != null){
            userOps = Application.influxDBManager.readAPIByTypeUsernameTimeRange(
                    UrlParse.API_TYPE_USER_OPERATION,
                    userCompany,
                    beginTime,
                    endTime,
                    limit,
                    offset);
        }
        else if(operationType != null){
            userOps = Application.influxDBManager.readAPIByTypeMethodTypeCompanyTimeRange(
                    UrlParse.API_TYPE_USER_OPERATION,
                    operationType.equals("query"),
                    companyId,
                    beginTime,
                    endTime,
                    limit,
                    offset);
        }
        else{
            userOps = Application.influxDBManager.readAPIByTypeCompanyTimeRange(
                    UrlParse.API_TYPE_USER_OPERATION,
                    companyId,
                    beginTime,
                    endTime,
                    limit,
                    offset);

        }


        List<RestUserOperation> operationList = new ArrayList<>();
        if (userOps != null) {
            for (List<Object> op : userOps) {
                //List<Object> is an operation item got our from influxdb query: time, url, method, api_parameter, username
                RestUserOperation ruop = new RestUserOperation();
                ruop.setTimeStamp(new Date(TimeUtil.fromInfluxDBTimeFormat((String) op.get(0))));
                String apiUrl = (String) op.get(1);
                ruop.setUrl(apiUrl);
                ruop.setContent((String) op.get(3));
                String username = (String) op.get(4);
                ruop.setUsername(username);
                User op_user = userRepository.findByName(username);
                if (op_user == null) {
                    LOGGER.warn(String.format("Cannot find user by name %s, may be deleted", username));
                    ruop.setUserDisplayName("deleted");

                } else {
                    ruop.setUserDisplayName(op_user.getUserName());
                }
                String operationName = UrlParse.urlUserOperationMap.get(apiUrl);
                if (operationName == null) {
                    LOGGER.error(String.format("Cannot find operation name for url %s, please check UrlParse.urlUserOperationMap to make sure the url is contained", apiUrl));
                    continue;
                }

                ruop.setOperationName(operationName);
                if (op.get(2).equals("GET")) {
                    ruop.setOperationType("query");

                } else {
                    ruop.setOperationType("update");
                }

                operationList.add(ruop);
            }
        }


        return new RestResponse(operationList);
    }


    /**
     * 获取昨日设备利用率情况
     */
    @RequestMapping(value = "/device/daily/utilization", method = RequestMethod.GET)
    public RestResponse getYesterdayUtilization(Principal principal, @RequestParam Map<String, String> requestParam) {
//        User user = judgeByPrincipal(principal);
//        if(user == null)
//            return new RestResponse("用户未登陆",1005,null);
        if (!requestParam.containsKey("deviceId")) {
            return new RestResponse("设备id为空", 1006, null);
        }
        Device device;
        device = deviceRepository.findById(Integer.parseInt(requestParam.get("deviceId")));
        if (device == null) {
            return new RestResponse("设备不存在", 1006, null);
        }


        Date beginTime, endTime;
        if (requestParam.containsKey("date")) {
            beginTime = new Date(Long.parseLong(requestParam.get("date")));
            // endTime is 23 hours and 30 minutes later, to ensure including util data of 24 hours
            endTime = new Date(Long.parseLong(requestParam.get("date")) + 23 * 60 * 60 * 1000 + 30 * 60 * 1000);
        } else {
            Calendar beginCalendar = Calendar.getInstance();
            beginCalendar.set(Calendar.MINUTE, 0);
            beginCalendar.set(Calendar.SECOND, 0);
            beginCalendar.set(Calendar.MILLISECOND, 0);
            beginCalendar.set(Calendar.HOUR, 0);
            beginCalendar.set(Calendar.DATE, beginCalendar.get(Calendar.DATE) - 1);
            beginTime = beginCalendar.getTime();
            beginCalendar.set(Calendar.HOUR, 23);
            beginCalendar.set(Calendar.MINUTE, 59);
            endTime = beginCalendar.getTime();
        }
        System.out.println("begin time: " + beginTime + ", end time: " + endTime);

        List<List<Object>> utilizationList = Application.influxDBManager.readDeviceUtilizationInTimeRange(device.getId(), beginTime, endTime);


        Float totalRunningHours = new Float(0);
        Float totalIdleHours = new Float(0);
        Float powerLowerBound = new Float(Float.MAX_VALUE);
        Float powerUpperBound = new Float(-1);
        Float totalConsumedEnergy = new Float(0);
        Long mostOftenUsedHour = new Long(-1);
        Integer mostOftenUsedHourUsedTime = new Integer(-1);
        Long leastOftenUsedHour = new Long(-1);
        Integer leastOftenUsedHourUsedTime = new Integer(Integer.MAX_VALUE);
        Float offTimeHours = new Float(0);

        if (utilizationList != null) {
            for (int i = 0; i < utilizationList.size(); i++) {
                long timeStamp = TimeUtil.fromInfluxDBTimeFormat((String) utilizationList.get(i).get(0));
                Integer runningSeconds = ((Double) utilizationList.get(i).get(1)).intValue();
                Integer idleSeconds = ((Double) utilizationList.get(i).get(2)).intValue();

                float power_lower = ((Double) utilizationList.get(i).get(3)).floatValue();
                float power_upper = ((Double) utilizationList.get(i).get(4)).floatValue();
                float energy = ((Double) utilizationList.get(i).get(5)).floatValue();

                totalRunningHours += (float) runningSeconds / 3600;

                totalIdleHours += (float) idleSeconds / 3600;

                if (power_lower < powerLowerBound) {
                    powerLowerBound = power_lower;
                }
                if (power_upper > powerUpperBound) {
                    powerUpperBound = power_upper;
                }

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date(timeStamp));
                Integer currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                totalConsumedEnergy += energy;
                if (runningSeconds > mostOftenUsedHourUsedTime) {
                    mostOftenUsedHourUsedTime = runningSeconds;
                    mostOftenUsedHour = calendar.getTimeInMillis();
                }
                if (runningSeconds < leastOftenUsedHourUsedTime) {
                    leastOftenUsedHourUsedTime = runningSeconds;
                    leastOftenUsedHour = calendar.getTimeInMillis();
                }

                if (currentHour < 9 || currentHour >= 18) {
                    offTimeHours += (float) runningSeconds / 3600;
                }
            }
        }

        Map map = new HashMap();
        map.put("totalRunningHours", totalRunningHours);
        map.put("totalIdleHours", totalIdleHours);
        map.put("powerLowerBound", powerLowerBound);
        map.put("powerUpperBound", powerUpperBound);
        map.put("totalConsumedEnergy", totalConsumedEnergy);
        map.put("mostOftenUsedHour", mostOftenUsedHour);
        map.put("leastOftenUsedHour", leastOftenUsedHour);
        map.put("offTimeHours", offTimeHours);
        return new RestResponse(map);
    }


    /**
     * 获取设备在时间段内的数据
     */

    /**
     * Test Example
     * curl -H "Content-type: application/json" -X POST --data
     * '{"deviceId":"228","beginTime":"1492606828000", "endTime":"1492606978000",
     * "mktId":"279", "monitorId":["277", "278", "279"]}'
     * http://localhost/api/rest/firm/device/monitorData
     */
    @RequestMapping(value = "/device/monitorData", method = RequestMethod.POST)
    public RestResponse getMonitorData(Principal principal, @RequestBody MonitorDataOfDeviceRequest requestParam, HttpServletRequest request) {
        judgeByPrincipal(principal);

        request.setAttribute(Constants.HTTP_REQUEST_CUSTOM_ATTRIBUTE_POST_BODY, requestParam);

        if (requestParam.getDeviceId() == null) {
            return new RestResponse("设备id为空", 1006, null);
        }
        Device device;
        device = deviceRepository.findById(Integer.parseInt(requestParam.getDeviceId()));
        if (device == null) {
            return new RestResponse("设备不存在", 1006, null);
        }

        if (requestParam.getBeginTime() == null || requestParam.getEndTime() == null) {
            return new RestResponse("起止时间未设置", 1006, null);
        }
        Date beginTime = new Date(Long.parseLong(requestParam.getBeginTime()));
        Date endTime = new Date(Long.parseLong(requestParam.getEndTime()));
        long interval = 60 * 1000 / Integer.parseInt(requestParam.getSampleRate());
        LOGGER.debug(String.format("Get Device Monitor: Begin Time %s, End Time %s, interval: %s.", beginTime.toString(), endTime.toString(), String.valueOf(interval)));
        if (requestParam.getMonitorId() == null) {
            return new RestResponse("监控参数ID未设置", 1006, null);
        }

        List<String> deviceInspectIds = requestParam.getMonitorId();
        List<DeviceInspect> deviceInspects = new ArrayList<>();

        for (String deviceInspectId : deviceInspectIds) {
            DeviceInspect deviceInspect = deviceInspectRepository.findById(Integer.parseInt(deviceInspectId));
            if (deviceInspect != null) {
                LOGGER.debug(String.format("Get Device Monitor: monitor %d is found.", deviceInspect.getId()));
                deviceInspects.add(deviceInspect);
            } else {
                LOGGER.debug(String.format("Get Device Monitor: Device Inspect Id %s is not found.", deviceInspectId));
            }
        }

        // decide the time granularity according to the timespan between beginTime and endTime

        long timeSpan = (endTime.getTime() - beginTime.getTime()) / 1000; //in seconds


        // shorter than 6 hours, using non-aggregated data
        int timeGranularity = Calendar.SECOND;


        if (timeSpan > 3600 * 24 * 30) {
            // longer than a month, using daily average data
            timeGranularity = Calendar.DATE;
//        } else if (timeSpan > 3600 * 24 * 5) {
//            // longer than 5 days, using hourly data, at most 30 * 24 entry per inspect
//            timeGranularity = Calendar.HOUR;
        } else if (timeSpan > 3600 * 6) {
            // longer than 6 hours, using 10-min data, at most 144 * 5 entry per inspect
            timeGranularity = Calendar.MINUTE;
        }

        List<TelemetryData> telemetryDatas = new ArrayList<>();

        for (DeviceInspect deviceInspect : deviceInspects) {
            List<List<Object>> inspectDatas = Application.influxDBManager.readTelemetryInTimeRange(
                    deviceInspect.getInspectType().getMeasurement(),
//                    InspectProcessTool.getMeasurementByCode(deviceInspect.getInspectType().getCode()),
                    device.getId(), deviceInspect.getId(), beginTime, endTime, timeGranularity);


            if (inspectDatas == null || inspectDatas.size() == 0) {
                continue;
            }

            List<Long> timeSeries = new ArrayList<Long>();
            List<Float> dataSeries = new ArrayList<Float>();
            AggregateData aggregateData = new AggregateData();
            Float maxValue = new Float(Float.MIN_VALUE);
            Long maxValueTime = null;
            Float minValue = new Float(Float.MAX_VALUE);
            Long minValueTime = null;
            Float sumValue = new Float(0);
            Integer yellowAlertCount = new Integer(0);
            Long yellowAlertTime = new Long(0);
            Integer redAlertCount = new Integer(0);
            Long redAlertTime = new Long(0);


            for (int i = 0; i < inspectDatas.size(); i++) {
                Float result = ((Double) inspectDatas.get(i).get(1)).floatValue();
                Long timeTick = TimeUtil.fromInfluxDBTimeFormat((String) inspectDatas.get(i).get(0));

                sumValue += result;
                if (result > maxValue) {
                    maxValue = result;
                    maxValueTime = timeTick;
                }
                if (result < minValue) {
                    minValue = result;
                    minValueTime = timeTick;
                }

                timeSeries.add(timeTick);
                dataSeries.add(result);


            }

            aggregateData.setMaxValue(maxValue);
            aggregateData.setMaxValueTime(maxValueTime);
            aggregateData.setMinValue(minValue);
            aggregateData.setMinValueTime(minValueTime);
            aggregateData.setAvgValue(sumValue / inspectDatas.size());

            List<AlertCount> yellowAlertCounts = alertCountRepository.findByDeviceIdAndInspectTypeIdAndTypeAndCreateDateBetween(device.getId(),
                    deviceInspect.getInspectType().getId(),
                    1, beginTime, endTime);
            List<AlertCount> redAlertCounts = alertCountRepository.findByDeviceIdAndInspectTypeIdAndTypeAndCreateDateBetween(device.getId(),
                    deviceInspect.getInspectType().getId(),
                    2, beginTime, endTime);
            if (yellowAlertCounts != null && yellowAlertCounts.size() > 0) {
                yellowAlertCount = yellowAlertCounts.size();
                for (AlertCount alertCount : yellowAlertCounts) {
                    // GX: alert_count.finishDate can be null due to device go offline
                    // this is a temp hack, using alert_num * 20 sec
                    if (alertCount.getFinish() == null) {
                        yellowAlertTime += alertCount.getNum() * 20 * 1000;

                    } else {
                        yellowAlertTime += alertCount.getFinish().getTime() - alertCount.getCreateDate().getTime();
                    }
                }
            }
            if (redAlertCounts != null && redAlertCounts.size() > 0) {
                redAlertCount = redAlertCounts.size();
                for (AlertCount alertCount : redAlertCounts) {
                    // GX: alert_count.finishDate can be null due to device go offline
                    // this is a temp hack, using alert_num * 20 sec
                    if (alertCount.getFinish() == null) {
                        redAlertTime += alertCount.getNum() * 20 * 1000;
                    } else {
                        redAlertTime += alertCount.getFinish().getTime() - alertCount.getCreateDate().getTime();
                    }
                }
            }
            aggregateData.setRedAlertCount(redAlertCount);
            aggregateData.setRedAlertTotalTime(redAlertTime);
            aggregateData.setYellowAlertCount(yellowAlertCount);
            aggregateData.setYellowAlertTotalTime(yellowAlertTime);
            Float MKT = null;
            if (requestParam.getMktId() != null && !requestParam.getMktId().isEmpty() && Integer.parseInt(requestParam.getMktId()) == deviceInspect.getId()) {

                if (inspectDatas != null) {
                    LOGGER.debug("MKT calculation: device inspect id " + deviceInspect.getId() + " has size " + inspectDatas.size());
                    MKT = MKTCalculator.calculateMKTValue(inspectDatas);
                } else {
                    LOGGER.debug("MKT Monitor Inspect's data is null");
                }

            }

            if (MKT != null) {
                aggregateData.setMktdata(MKT);
            }


            TelemetryData telemetryData = new TelemetryData();

            //TODO: this will be changed when one device have multiple monitors
            telemetryData.setMonitorId(device.getMonitorDevice().getNumber());

            telemetryData.setDeviceInspectId(deviceInspect.getId());
            telemetryData.setName(deviceInspect.getInspectType().getMeasurement());
            //InspectProcessTool.getMeasurementByCode(deviceInspect.getInspectType().getCode()));
            telemetryData.setTsData(dataSeries);
            telemetryData.setTsTime(timeSeries);
            telemetryData.setAggregateData(aggregateData);

            telemetryDatas.add(telemetryData);

        }

        RestMonitorDataOfDevice monitorDataOfDevice = new RestMonitorDataOfDevice();
        monitorDataOfDevice.setDeviceId(device.getId().toString());
        monitorDataOfDevice.setDeviceLocation(GetDeviceAddress.getDeviceAddress(device));
        monitorDataOfDevice.setDeviceLogo(device.getPhoto());
        monitorDataOfDevice.setDeviceManager(device.getManager().getName());
        monitorDataOfDevice.setDeviceName(device.getName());
        monitorDataOfDevice.setEndTime(String.valueOf(endTime.getTime()));
        monitorDataOfDevice.setStartTime(String.valueOf(String.valueOf(beginTime.getTime())));
        monitorDataOfDevice.setTelemetryDataList(telemetryDatas);

        return new RestResponse(monitorDataOfDevice);
    }

    @RequestMapping(value = "/dealHistory", method = RequestMethod.GET)
    public RestResponse getDealHistory(Principal principal, @RequestParam Integer userId, @RequestParam Integer deviceId) {
        Date currentDate = new Date();
        List<DealRecord> dealRecordsHistory = dealRecordRepository.findTop9ByLessorOrLesseeOrderByEndTimeDesc(userId, userId, deviceId, currentDate);
        List<DealRecord> dealRecordsFutureList;
        if (deviceId == null) {
            dealRecordsFutureList = dealRecordRepository.findByLessorOrLesseeOrderByEndTimeDesc(userId, userId);
        } else {
            dealRecordsFutureList = dealRecordRepository.findTop1ByLessorOrLesseeAndDeviceIdOrderByEndTimeDesc(userId, userId, deviceId, currentDate);
        }
        List<RestDealRecord> restDealRecords = new ArrayList<>();
        if (dealRecordsFutureList != null && dealRecordsFutureList.size() != 0){
            for (DealRecord dealRecord : dealRecordsFutureList) {
                RestDealRecord recordFuture = new RestDealRecord(dealRecord.getId(), dealRecord.getDevice().getId(), dealRecord.getLessor(), dealRecord.getLessee(),
                        dealRecord.getPrice(), dealRecord.getBeginTime().getTime(), dealRecord.getEndTime().getTime(), dealRecord.getDeviceSerialNumber(),
                        dealRecord.getAggrement(), dealRecord.getStatus(), dealRecord.getRealEndTime());
                restDealRecords.add(recordFuture);
            }
        }

        if (dealRecordsHistory != null && dealRecordsHistory.size() != 0) {
            for (DealRecord dealRecord : dealRecordsHistory) {
                RestDealRecord recordHistory = new RestDealRecord(dealRecord.getId(), dealRecord.getDevice().getId(), dealRecord.getLessor(), dealRecord.getLessee(),
                        dealRecord.getPrice(), dealRecord.getBeginTime().getTime(), dealRecord.getEndTime().getTime(), dealRecord.getDeviceSerialNumber(),
                        dealRecord.getAggrement(), dealRecord.getStatus(), dealRecord.getRealEndTime());
                restDealRecords.add(recordHistory);
            }
        }
        return new RestResponse(restDealRecords);
    }

    /**
     * 获取设备运行状态历史
     * Test Example
     * curl "http://fm.test.ilabservice.cloud/api/rest/firm/device/runningStatusHistory?deviceId=339&startTime=1501122059000&endTime=1501123059000"
     **/

    private static List<List<Object>> statusHistories = new ArrayList<>();

    @RequestMapping(value = "/device/runningStatusHistory", method = RequestMethod.GET)
    public RestResponse getDeviceRunningStatusHistory(Principal principal, @RequestParam Map<String, String> requestParam) {
//        User user = judgeByPrincipal(principal);
//        if(user == null)
//            return new RestResponse("用户未登陆",1005,null);
        if (!requestParam.containsKey("deviceId")) {
            return new RestResponse("设备id为空", 1006, null);
        }
        Integer deviceId = Integer.parseInt(requestParam.get("deviceId"));
        if (!requestParam.containsKey("startTime")) {
            return new RestResponse("起始时间未设置", 1006, null);
        }
        Date beginTime = new Date(Long.parseLong(requestParam.get("startTime")));

        Date endTime = new Date();
        if(requestParam.containsKey("endTime")){
            endTime.setTime(Long.parseLong(requestParam.get("endTime")));
        }

        LOGGER.info(String.format("Getting running status history of device %s, from %s to %s", deviceId, beginTime, endTime));
        statusHistories = Application.influxDBManager.readDeviceOperatingStatusInTimeRange(deviceId, beginTime, endTime);
        if(statusHistories == null || statusHistories.size() == 0){
            final List<Object> statusHistory = Application.influxDBManager.readLatestDeviceOperatingStatus(deviceId, beginTime);
            if(statusHistory == null || statusHistory.size() == 0){
                return new RestResponse("该设备没有状态历史",1007, null);
            }

            statusHistories = new ArrayList<List<Object>>(){{
                add(statusHistory);
            }};
        }

        Map<String, Object> result = new HashMap<>();
        result.put("deviceId", deviceId);
        result.put("operatingStatusList", statusHistories);

        LOGGER.debug(String.format("Successfully got running status history of device %s", deviceId));

        return new RestResponse(result);
    }

    /**
     * 获取设备对应摄像头列表
     *
     * test example
     *
     * query:
     * curl "http://localhost/api/rest/firm/device/cameraList?deviceId=339"
     *
     * return:
     * {"error":0,"message":"OK","data":{"cameraList":[{"id":1,
     * "name":"Test Camera","deviceId":339,"serialNo":"728370397",
     * "url":"http://hls.open.ys7.com/openlive/5760a3686c444b379392293aaf425b75.hd.m3u8","description":null}]}}
     */

    @RequestMapping(value = "/device/cameraList", method = RequestMethod.GET)
    public RestResponse getDeviceCameraList(Principal principal, @RequestParam Map<String, String> param){
        LOGGER.info("Program input cameraList");

        if(param.get("deviceId") == null){
            return new RestResponse("设备id不能为空", 1006, null);
        }

        List<CameraList> cameraLists = cameraListRepository.findByDeviceId(Integer.parseInt(param.get("deviceId")));
        Map<String, Object> result = new HashMap<>();
        result.put("cameraList", cameraLists);

        LOGGER.info("Input url of cameraLists: ", cameraLists.get(0).getUrl());
        return new RestResponse(result);
    }

    /**
     * 获取摄像头的AccessToken
     *
     * test example
     *
     * query:
     * curl "http://localhost/api/rest/firm/device/cameraToken"
     *
     * return:
     * {"error":0,"message":"OK","data":{"accessToken":"at.c06civwza7mkneps8yrzkarb2z0p3aa1-45er9dgqis-1enogbg-j8b3bghts"}}
     */

    @RequestMapping(value = "/device/cameraToken", method = RequestMethod.GET)
    public RestResponse getCameraToken(Principal principal){
        String accessToken = GetCameraAccessToken.getAccessToken();
        if(accessToken != null && !accessToken.isEmpty()){
            Map<String, Object> result = new HashMap<>();
            result.put("accessToken", accessToken);
            return new RestResponse(result);
        }
        else{
            return new RestResponse("获取accessToken失败", 1006, null);
        }
    }

    /**
     * 获取某个monitor的动作历史
     */
    @RequestMapping(value = "/monitor/actionList", method = RequestMethod.GET)
    public RestResponse getActionList(Principal principal, @RequestParam String monitorSerialNo){
        List<DeviceOrderList> orderList = deviceOrderListRepository.findByMonitorSerialNo(monitorSerialNo);
        return new RestResponse(orderList);
    }

    /**
     * 获取交易的报警列表
     */
    @RequestMapping(value = "/dealRecord/alert", method = RequestMethod.GET)
    public RestResponse getDealRecordAlert(Principal principal, @RequestParam(required = false) Integer dealRecordId){
        List<DealAlertRecord> dealAlertRecords = dealAlertRecordRepository.findByDealIdOrderByHappenedTimeDesc(dealRecordId);
        return new RestResponse(dealAlertRecords);
    }

    /**
     * 获取所有报警信息
     */
    @RequestMapping(value = "/query/alert", method = RequestMethod.GET)
    public RestResponse getAllAlert(Principal principal, @RequestParam Map<String, String> param){
        User user = judgeByPrincipal(principal);
        if(user == null){
            return new RestResponse("用户未登陆", 1005, null);
        }

        Integer limit = new Integer(10);
        Integer offset = new Integer(0);

        param.put("userId", user.getId().toString());

        if (!param.containsKey("deviceId")){
            return new RestResponse("请传入deviceId参数", 1006, null);
        }
        if (!param.containsKey("deviceTypeId")){
            return new RestResponse("请传入deviceTypeId参数", 1006, null);
        }
        if (!param.containsKey("alertType")){
            return new RestResponse("请传入alertType参数", 1006, null);
        }
        if (!param.containsKey("startTime")){
            return new RestResponse("请传入startTime参数", 1006, null);
        }
        if (!param.containsKey("endTime")){
            return new RestResponse("请传入endTime参数", 1006, null);
        }
        if (param.containsKey("limit")){
            limit = Integer.parseInt(param.get("limit"));
            param.remove("limit");
        }
        if (param.containsKey("offset")){
            offset = Integer.parseInt(param.get("offset"));
            param.remove("offset");
        }

        Page<AlertCount> alertCountPage = new AlertCountQuery(entityManager).query(param, offset, limit, new Sort(Sort.Direction.DESC, "createDate"));

        return new RestResponse(assembleAlertCount(alertCountPage));
    }
}