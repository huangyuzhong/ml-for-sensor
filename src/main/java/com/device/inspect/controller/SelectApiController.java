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
import com.device.inspect.common.restful.RestResponse;
import com.device.inspect.common.restful.charater.RestUser;
import com.device.inspect.common.restful.device.*;
import com.device.inspect.Application;
import com.device.inspect.common.restful.firm.RestCompany;
import com.device.inspect.common.restful.page.*;
import com.device.inspect.common.restful.version.RestDeviceVersion;
import com.device.inspect.common.util.transefer.ByteAndHex;
import com.device.inspect.common.util.transefer.UserRoleDifferent;
import com.device.inspect.controller.request.DeviceTypeRequest;
import com.device.inspect.controller.request.InspectTypeRequest;
import com.device.inspect.controller.request.RunningStatusRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Version;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import javax.persistence.EntityManager;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.*;

/**
 * Created by Administrator on 2016/7/12.
 */
@RestController
@RequestMapping(value = "/api/rest/firm")
public class SelectApiController {

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

    private User judgeByPrincipal(Principal principal){
        if (null == principal||null==principal.getName())
            throw new UsernameNotFoundException("You are not login!");
        User user = userRepository.findByName(principal.getName());
        if (null==user)
            throw new UsernameNotFoundException("user not found!");
        return user;
    }

    /**
     * 查询个人信息
     * @param principal
     * @return
     */
    @RequestMapping(value = "/person/info/{userId}")
    public RestResponse getUserMessage(Principal principal,@PathVariable Integer userId){
        User user1=judgeByPrincipal(principal);
        if (user1==null)
            return new RestResponse("用户未登录",1005,null);
        User user = userRepository.findOne(userId);
        if (null==user)
            return new RestResponse("user not found!",1005,null);
        return new RestResponse(new RestUser(user));
    }

    /**
     * 用户的个人信息
     * @param principal
     * @return 用户的个人信息
     */
    @RequestMapping(value = "/person/mine/info")
    public RestResponse getMyMessage(Principal principal){
        User user = judgeByPrincipal(principal);
        if (user==null)
            return new RestResponse("用户未登录",1005,null);
        return new RestResponse(new RestUser(user));
    }

    /**
     * 查询所有的楼
     * @param principal
     * @param map
     * @return
     */
    @RequestMapping(value = "/buildings")
    public RestResponse getBuildings(Principal principal,@RequestParam  Map<String,String> map){
        User user = judgeByPrincipal(principal);
        if (null == user.getCompany()){
            return new RestResponse("user's information incorrect!",1005,null);
        }

        Application.LOGGER.info(String.format("Find buildings of company %s, %s", user.getCompany().getName(), user.getCompany().getId()));
        List<Building> list = new ArrayList<Building>();
        if (null!=map.get("enable")&&(map.get("enable").equals("0")||map.get("enable").equals("1")))
            list = buildingRepository.findByCompanyIdAndEnable(user.getCompany().getId(),Integer.valueOf(map.get("enable")));
        else
            list = buildingRepository.findByCompanyId(user.getCompany().getId());
        user.getCompany().setBuildings(list);
        return new RestResponse(new RestIndexBuilding(user.getCompany()));
    }

    /**
     * 查询所有的层
     * @param principal
     * @param map
     * @return
     */
     @RequestMapping(value = "/floors",method = RequestMethod.GET)
     public RestResponse getFloors(Principal principal,@RequestParam Map<String,String> map) {
         User user=judgeByPrincipal(principal);
         if (user==null)
             return new RestResponse("用户未登录",1005,null);
         String buildId = map.get("buildId");
         Building build = null;
         if (null!=buildId){
             build = buildingRepository.findOne(Integer.valueOf(buildId));
         }

         if (null == build) {
             return new RestResponse("floors information correct!", 1005, null);
         }
         List<Storey> list = new ArrayList<Storey>();
         if (null!=map.get("enable")&&(map.get("enable").equals("0")||map.get("enable").equals("1")))
            list = storeyRepository.findByBuildIdAndEnable(Integer.valueOf(buildId),Integer.valueOf(map.get("enable")));
         else list = storeyRepository.findByBuildId(Integer.valueOf(buildId));
         build.setFloorList(list);
         return new RestResponse(new RestIndexFloor(build));
     }

    /**
     * 查询所有的室
     * @param principal
     * @param map
     * @return
     */
    @RequestMapping(value = "/rooms",method = RequestMethod.GET)
    public  RestResponse getRooms(Principal principal,@RequestParam Map<String,String> map){
        User user=judgeByPrincipal(principal);
        if (user==null)
            return new RestResponse("用户未登陆",1005,null);
        String floorId = map.get("floorId");
        Storey floor = null;
        if (null!=floorId)
            floor = storeyRepository.findOne(Integer.valueOf(floorId));
        if (null == floor){
            return  new RestResponse("rooms information correct!",1005,null);
        }
        List<Room> list = new ArrayList<Room>();
        if (null!=map.get("enable")&&(map.get("enable").equals("0")||map.get("enable").equals("1")))
            list = roomRepository.findByFloorIdAndEnable(Integer.valueOf(floorId),Integer.valueOf(map.get("enable")));
        else list = roomRepository.findByFloorId(Integer.valueOf(floorId));
        floor.setRoomList(list);
        return new RestResponse(new RestIndexRoom(floor));
    }

    /**
     * 查询所有的设备
     * @param principal
     * @param map
     * @return
     */
    @RequestMapping(value = "/devices",method = RequestMethod.GET)
    public  RestResponse getDevices(Principal principal,@RequestParam Map<String,String> map){
        User user=judgeByPrincipal(principal);
        if (user==null)
            return new RestResponse("用户未登陆",1005,null);
        String roomId = map.get("roomId");
        Room room = null;
        if (null!=roomId)
            room = roomRepository.findOne(Integer.valueOf(roomId));
        if (null == room||null ==room.getId()){
            return  new RestResponse("devices information correct!",1005,null);
        }
        List<Device> list = new ArrayList<Device>();
        if (null!=map.get("enable")&&(map.get("enable").equals("0")||map.get("enable").equals("1")))
            list = deviceRepository.findByRoomIdAndEnable(Integer.valueOf(roomId),Integer.valueOf(map.get("enable")));
        else list = deviceRepository.findByRoomId(Integer.valueOf(roomId));
        room.setDeviceList(list);
        return new RestResponse(new RestIndexDevice(room));
    }

    /**
     * 查询单个的设备
     * @param principal
     * @param deviceId
     * @return
     */
    @RequestMapping(value = "/device",method = RequestMethod.GET)
    public  RestResponse getDevice(Principal principal,@RequestParam Integer deviceId){
        User user=judgeByPrincipal(principal);
        if (user==null)
            return new RestResponse("用户未登陆",1005,null);
        Device device = deviceRepository.findOne(deviceId);
        if (null == device|| null ==device.getId()){
            return  new RestResponse("device information correct!",1005,null);
        }
        List<DeviceFloor> deviceFloorList = deviceFloorRepository.findByDeviceIdAndEnable(deviceId,1);
        device.setDeviceFloorList(deviceFloorList);

        RestDevice response = new RestDevice(device);
        List<RestDeviceInspect> restDeviceInspects = new ArrayList<>();
        for(DeviceInspect inspect : device.getDeviceInspectList()){
            List<DeviceInspectRunningStatus> statuses = deviceInspectRunningStatusRepository.findByDeviceInspectId(inspect.getId());
            RestDeviceInspect restInspect = new RestDeviceInspect(inspect);
            List<RestDeviceInspectRunningStatus> restStatuses = new ArrayList<>();
            for(DeviceInspectRunningStatus status : statuses){
                restStatuses.add(new RestDeviceInspectRunningStatus(status));
            }
            restInspect.setRunningStatus(restStatuses);
            restDeviceInspects.add(restInspect);
        }
        response.setDeviceInspects(restDeviceInspects);
        return new RestResponse(response);
    }

    /**
     * 查询所有的设备种类
     * @return
     */
    @RequestMapping(value = "/device/types",method = RequestMethod.GET)
    public RestResponse getAllDeviceTypes(Principal principal,@RequestParam Integer enable){
        User user = judgeByPrincipal(principal);
        if (null==user.getCompany()&&UserRoleDifferent.userStartWithFirm(user))
            return new RestResponse("user's information wrong!",1005,null);
        List<DeviceType> list = new ArrayList<DeviceType>();
        list.addAll(deviceTypeRepository.findByEnableAndCompanyIdIsNull(enable));
        if (UserRoleDifferent.userStartWithFirm(user)){
            list.addAll(deviceTypeRepository.findByCompanyIdAndEnable(user.getCompany().getId(),enable));
        }
//        Iterable<DeviceType> deviceTypeIterable = deviceTypeRepository.findAll();
        List<RestDeviceType> deviceTypes = new ArrayList<RestDeviceType>();
        if (null!=list)
            for (DeviceType deviceType: list)
                deviceTypes.add(new RestDeviceType(deviceType));

        return new RestResponse(deviceTypes);
    }

    /**
     * 查询设备类型参数
     * @param deviceTypeId
     * @return
     */
    @RequestMapping(value = "/device/type/request/{deviceTypeId}")
    public RestResponse getCurrentDeviceTypeRequest(Principal principal,@PathVariable Integer deviceTypeId){
        User user=judgeByPrincipal(principal);
        if (user==null)
            return new RestResponse("用户未登陆",1005,null);
        DeviceType deviceType = deviceTypeRepository.findOne(deviceTypeId);
        if (null==deviceType)
            return new RestResponse("当前设备类型不存在！",1905,null);
        DeviceTypeRequest deviceTypeRequest = new DeviceTypeRequest();
        List<InspectTypeRequest> requests = new ArrayList<InspectTypeRequest>();
        if (null!=deviceType.getDeviceTypeInspectList()){
            for (DeviceTypeInspect deviceTypeInspect:deviceType.getDeviceTypeInspectList()){
                InspectTypeRequest request = new InspectTypeRequest();
                request.setId(deviceTypeInspect.getInspectType().getId());
                request.setName(deviceTypeInspect.getInspectType().getName());
                request.setChosed(true);
                request.setHighDown(null == deviceTypeInspect.getHighDown() ? null : deviceTypeInspect.getHighDown().toString());
                request.setHighUp(null == deviceTypeInspect.getHighUp() ? null : deviceTypeInspect.getHighUp().toString());
                request.setStandard(null == deviceTypeInspect.getStandard() ? null : deviceTypeInspect.getStandard().toString());
                request.setLowDown(null == deviceTypeInspect.getLowDown() ? null : deviceTypeInspect.getLowDown().toString());
                request.setLowUp(null == deviceTypeInspect.getLowUp() ? null : deviceTypeInspect.getLowUp().toString());

                List<DeviceTypeInspectRunningStatus> statuses = deviceTypeInspectRunningStatusRepository.findByDeviceTypeInspectId(deviceTypeInspect.getId());
                List<RestDeviceTypeInspectRunningStatus> restStatus = new ArrayList<>();
                for(DeviceTypeInspectRunningStatus status : statuses){
                    restStatus.add(new RestDeviceTypeInspectRunningStatus(status));
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
     * 查询用户设备列表信息
     * @param principal
     * @param requestParam
     * @return
     */
    @RequestMapping(value = "/manager/devices",method = RequestMethod.GET)
    public RestResponse getAllDevicesByManger(Principal principal,@RequestParam Map<String,String> requestParam){
//        if (null == principal || null ==principal.getName())
//            return new RestResponse("not login!",1005,null);
        User user = judgeByPrincipal(principal);
        if (null == user||null == user.getCompany()){
            return new RestResponse("user's information error!",1005,null);
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

        if (!requestParam.containsKey("userId")){
            requestParam.put("userId",user.getId().toString());
        }

//        if (UserRoleDifferent.userScientistConfirm(user)){
//            requestParam.put("scientistId",user.getId().toString());
//        }

        Page<Device> devicePage = new DeviceQuery(entityManager)
                .query(requestParam, start, limit, new Sort(Sort.Direction.DESC, "createDate"));

        return new RestResponse(assembleDevices(devicePage));

    }



    /**
     * 平台用户查询设备列表
     * @return
     */
    @RequestMapping(value = "/service/device",method = RequestMethod.GET)
    public RestResponse getAllDevicesByService(Principal principal,@RequestParam Map<String,String> requestParam){
        User user=judgeByPrincipal(principal);
        if (null == user){
            return new RestResponse("没有此用户",1005,null);
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




    @RequestMapping(value = "/employees",method = RequestMethod.GET)
    public RestResponse getAllEmployees(Principal principal,@RequestParam Map<String,String> requestParam){
//        if (null == principal || null ==principal.getName())
//            return new RestResponse("not login!",1005,null);
        User user = judgeByPrincipal(principal);
        if (user==null)
            return new RestResponse("用户未登陆",1005,null);
        List<RoleAuthority> roleAuthorities = new ArrayList<RoleAuthority>();
        if (null!=user.getRoles())
            for (Role role:user.getRoles()){
                roleAuthorities.addAll(roleAuthorityRepository.findByParent(role.getRoleAuthority().getId()));
            }

        if (null == user&&null == user.getCompany()&&(null==roleAuthorities||roleAuthorities.size()==0)){
            return new RestResponse("user's information correct!",1005,null);
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
        if (null!=roleAuthorities)
            for (RoleAuthority roleAuthority : roleAuthorities){
                list.add(roleAuthority.getId());
            }

        requestParam.put("authorityId", JSON.toJSONString(list));
        if (UserRoleDifferent.userFirmManagerConfirm(user)){
            requestParam.put("companyId",user.getCompany().getId().toString());
        }


        Page<User> userPage = new UserQuery(entityManager)
                .query(requestParam, start, limit, new Sort(Sort.Direction.DESC, "createDate"));

        return new RestResponse(assembleUsers(user, userPage));
    }

    /**
     * 平台业务员查询所有的业务企业，企业员工查询自己的
     * @param principal
     * @return
     */
    @RequestMapping(value = "/query/all/company")
    public RestResponse getAllCompany(Principal principal){
        User user = judgeByPrincipal(principal);
        if (null == user){
            return new RestResponse("用户信息不存在！",1005,null);
        }
        List<RestCompany> list = new ArrayList<RestCompany>();
        if (UserRoleDifferent.userServiceManagerConfirm(user)){
            Map<String,String> requestParam = new HashMap<String,String>();
            Page<Company> companyPage = new CompanyQuery(entityManager)
                    .query(requestParam, 0, 100000, new Sort(Sort.Direction.DESC, "createDate"));
            list = (List)assembleCompanies(companyPage).get("companies");
        }else if (UserRoleDifferent.userServiceWorkerConfirm(user)){
            Map<String,String> requestParam = new HashMap<String,String>();
            requestParam.put("businessId",user.getId().toString());
            Page<Company> companyPage = new CompanyQuery(entityManager)
                    .query(requestParam, 0, 100000, new Sort(Sort.Direction.DESC, "createDate"));
            list = (List)assembleCompanies(companyPage).get("companies");
        }else {
            Company company = user.getCompany();
            list.add(new RestCompany(company));
        }

        return new RestResponse(list);
    }

    /**
     * 登陆页面根据公司URL获取公司信息
     * @param companyId
     * @return
     */
    @RequestMapping(value = "/query/login/company")
    public RestResponse getCompanyById(Principal principal,@RequestParam String companyId){
//        User user=judgeByPrincipal(principal);
//        if (user==null)
//            return new RestResponse("用户未登陆",1005,null);
        if (null==companyId||companyId.equals(""))
            return new RestResponse("没有正确的访问参数！",null);
        String realId = "";
        try {
            realId = URLDecoder.decode(ByteAndHex.convertMD5(companyId),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Company company = companyRepository.findOne(Integer.valueOf(realId));
        if (null==company)
            return new RestResponse("当前登陆页面不是企业URL！",1005,null);
        return new RestResponse(new RestCompany(company));
    }

    /**
     * 根据登录人获取所属公司信息
     * @param principal
     * @param requestParam
     * @return
     */
    @RequestMapping(value = "/query/mine/company")
    public RestResponse getCompanyByUserName(Principal principal,@RequestParam Map<String,String> requestParam){
        User user = judgeByPrincipal(principal);
        if (user==null)
            return new RestResponse("用户未登陆",1005,null);

        if (UserRoleDifferent.userServiceWorkerConfirm(user)){
            requestParam.put("businessId",user.getId().toString());
        }else if (UserRoleDifferent.userServiceManagerConfirm(user) ){

        }else {
            return new RestResponse("权限不足！",null);
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

    private Map assembleCompanies(Page<Company> companyPage){
        Map map = new HashMap();
        map.put("total",String.valueOf(companyPage.getTotalElements()));
        map.put("thisNum",String.valueOf(companyPage.getNumberOfElements()));
        List<RestCompany> list = new ArrayList<RestCompany>();
        for (Company company:companyPage.getContent()){
            list.add(new RestCompany(company));
        }
        map.put("companies",list);
        return map;
    }

    private Map assembleDevices(Page<Device> devicePage){
        Map map = new HashMap();
        map.put("total",String.valueOf(devicePage.getTotalElements()));
        map.put("thisNum",String.valueOf(devicePage.getNumberOfElements()));
        List<RestDevice> list = new ArrayList<RestDevice>();
        for (Device device:devicePage.getContent()){
            list.add(new RestDevice(device));
        }
        map.put("devices",list);
        return map;
    }

    private Map assembleUsers(User userRoot,Page<User> userPage){
        Map map = new HashMap();
        map.put("pages",String.valueOf(userPage.getTotalPages()));
        map.put("total",String.valueOf(userPage.getTotalElements()));
        map.put("thisNum",String.valueOf(userPage.getNumberOfElements()));
        List<User> list = new ArrayList<User>();
        for (User user:userPage.getContent()){
            list.add(user);
        }

        map.put("userList",new RestIndexUser(userRoot,list));
        return map;
    }

    /**
     *获取当前企业所有员工(仅包含企业管理员和设备管理员)
     * @param
     * @return
     */
    @RequestMapping(value = "/colleges/manager")
    public RestResponse getMyCompanyWorkers(Principal  principal){
        User user = judgeByPrincipal(principal);
        if (null==user)
            return new RestResponse("用户信息出错！",1005,null);

        List<User> list = userRepository.findByCompanyId(user.getCompany().getId());
        List<RestUser> result = new ArrayList<RestUser>();
        for (User userEnch : list){
            if(null!=userEnch.getRoles()&&(UserRoleDifferent.userFirmWorkerConfirm(userEnch)||
                    UserRoleDifferent.userFirmManagerConfirm(userEnch))){
                RestUser restUser = new RestUser(userEnch);
                result.add(restUser);
            }
        }
        return new RestResponse(result);
    }

    /**
     * 获取设备的检测参数
     * @return
     */
    @RequestMapping(value = "/query/inspect/type")
    public RestResponse getAllInspectType(Principal principal){
//        if (null==principal)
//            throw new UsernameNotFoundException("you are not login!");
//        User user = userRepository.findByName(principal.getName());
//        if (null==user)
//            throw new UsernameNotFoundException("user's name isn't correct!");

        Iterable<InspectType> iterable = inspectTypeRepository.findAll();

        DeviceTypeRequest deviceTypeRequest = new DeviceTypeRequest();
        List<InspectTypeRequest> list = new ArrayList<InspectTypeRequest>();
        if (null!=iterable){
            for (InspectType inspectType:iterable){
                InspectTypeRequest inspectTypeRequest = new InspectTypeRequest();
                inspectTypeRequest.setId(inspectType.getId());
                inspectTypeRequest.setName(inspectType.getName());
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
     * @param principal
     * @return
     */
    @RequestMapping("/colleges/scientist")
    public RestResponse getMyScientist(Principal principal){
        User user = judgeByPrincipal(principal);
        if (user==null)
            return new RestResponse("用户未登陆",1005,null);

        List<User> list = userRepository.findByCompanyId(user.getCompany().getId());
        List<RestUser> result = new ArrayList<RestUser>();
        for (User userEnch : list){
            if(UserRoleDifferent.userScientistConfirm(userEnch)){
                RestUser restUser = new RestUser(userEnch);
                result.add(restUser);
            }
        }
        return new RestResponse(result);
    }

    /**
     * 删除公司人员
     * @param principal
     * @param userId
     * @return
     */
    @RequestMapping(value = "/take/over/colleges")
    public RestResponse getAllCompanyColleges(Principal principal,@RequestParam Integer userId){
        User user = judgeByPrincipal(principal);
        if (user==null)
            return new RestResponse("用户未登陆",1005,null);

        if (!UserRoleDifferent.userFirmManagerConfirm(user))
            return new RestResponse("权限不足，无法查询！",1005,null);
        User old = userRepository.findOne(userId);
        boolean deviceManagerFlag = UserRoleDifferent.userFirmWorkerConfirm(old);
        boolean scentistFlag = UserRoleDifferent.userScientistConfirm(old);

        if (null==old)
            return new RestResponse("请选择要删除的人员！",1005,null);
        List<User> list = userRepository.findByCompanyId(user.getCompany().getId());
        List<RestUser> result = new ArrayList<RestUser>();
        for (User userEnch : list){
            if (!userEnch.getId().equals(old.getId())) {
                //判断是否是设备管理员
                boolean overManageFlag = UserRoleDifferent.userFirmWorkerConfirm(userEnch);
                //是否是科学家
                boolean overScientist = UserRoleDifferent.userScientistConfirm(userEnch);
                if(deviceManagerFlag&&scentistFlag) {
                    if (overManageFlag && overScientist) {
                        RestUser restUser = new RestUser(userEnch);
                        result.add(restUser);
                    }
                }else {
                    if (deviceManagerFlag){
                        if (UserRoleDifferent.userFirmManagerConfirm(userEnch)||
                                deviceManagerFlag==overManageFlag){
                            RestUser restUser = new RestUser(userEnch);
                            result.add(restUser);
                        }
                    }
                    if (scentistFlag&&scentistFlag==overScientist){
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
     * @param principal 验证用户是否登陆
     * @return
     */
    @RequestMapping(value = "/service/get/versions")
    public RestResponse getAllVersion(Principal principal){
        User user=judgeByPrincipal(principal);
        if (user==null)
            return new RestResponse("用户未登陆",1005,null);

       if (UserRoleDifferent.userServiceManagerConfirm(user)){
           Iterable<DeviceVersion> iterable=deviceVersionRepository.findAll();
           List<RestDeviceVersion> list=new ArrayList<RestDeviceVersion>();
           if (null!=iterable){

               for (DeviceVersion deviceVersion:iterable){
                   list.add(new RestDeviceVersion(deviceVersion));
               }
               return new RestResponse(list);
           }else {
               return new RestResponse("没有历史版本更新",1005,null);
           }
       }else {
           return new RestResponse("权限不足！",1005,null);
       }
    }


    /**
     * 获取所有运行状态
     * @param principal
     * @return
     */
    @RequestMapping(value = "/device/running_status", method = RequestMethod.GET)
    public RestResponse getDeviceRunningStatus(Principal principal){
//        User user = judgeByPrincipal(principal);
//        if(user == null)
//            return new RestResponse("用户未登陆",1005,null);
        Iterable<DeviceRunningStatus> iterable = deviceRunningStatusRepository.findAll();

        List<RunningStatusRequest> list = new ArrayList<RunningStatusRequest>();
        if (null!=iterable){
            for (DeviceRunningStatus status:iterable){
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
     * @param deviceTypeInspectId
     * @return
     */
    @RequestMapping(value = "/device/type/status", method = RequestMethod.GET)
    public RestResponse getDeviceTypeInspectRunningStatus(Principal principal, @RequestParam Integer deviceTypeInspectId){
//        User user = judgeByPrincipal(principal);
//        if(user == null)
//            return new RestResponse("用户未登陆",1005,null);
        List<DeviceTypeInspectRunningStatus> iterable = deviceTypeInspectRunningStatusRepository.findByDeviceTypeInspectId(deviceTypeInspectId);
        List<RestDeviceTypeInspectRunningStatus> list = new ArrayList<>();
        if(null!=iterable){
            for(DeviceTypeInspectRunningStatus status: iterable){
                RestDeviceTypeInspectRunningStatus deviceTypeInspectRunningStatus = new RestDeviceTypeInspectRunningStatus(status);
                list.add(deviceTypeInspectRunningStatus);
            }
        }
        return new RestResponse(list);
    }

    /**
     * 获取设备监控参数对应状态
     * @param deviceInspectId
     * @return
     */
    @RequestMapping(value = "/device/inspect/status", method = RequestMethod.GET)
    public RestResponse getDeviceInspectRunningStatus(Principal principal, @RequestParam Integer deviceInspectId){
//        User user = judgeByPrincipal(principal);
//        if(user == null)
//            return new RestResponse("用户未登陆",1005,null);
        List<DeviceInspectRunningStatus> iterable = deviceInspectRunningStatusRepository.findByDeviceInspectId(deviceInspectId);
        List<RestDeviceInspectRunningStatus> list = new ArrayList<>();
        if(null!=iterable){
            for(DeviceInspectRunningStatus status: iterable){
                RestDeviceInspectRunningStatus deviceInspectRunningStatus = new RestDeviceInspectRunningStatus(status);
                list.add(deviceInspectRunningStatus);
            }
        }
        return new RestResponse(list);
    }
}
