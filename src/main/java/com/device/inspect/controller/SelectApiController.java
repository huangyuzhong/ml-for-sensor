package com.device.inspect.controller;

import com.device.inspect.common.model.charater.User;
import com.device.inspect.common.model.device.Device;
import com.device.inspect.common.model.device.DeviceType;
import com.device.inspect.common.model.device.DeviceTypeInspect;
import com.device.inspect.common.model.device.InspectType;
import com.device.inspect.common.model.firm.Building;
import com.device.inspect.common.model.firm.Company;
import com.device.inspect.common.model.firm.Room;
import com.device.inspect.common.model.firm.Storey;
import com.device.inspect.common.query.charater.CompanyQuery;
import com.device.inspect.common.query.charater.DeviceQuery;
import com.device.inspect.common.query.charater.UserQuery;
import com.device.inspect.common.repository.charater.RoleRepository;
import com.device.inspect.common.repository.charater.UserRepository;
import com.device.inspect.common.repository.device.DeviceRepository;
import com.device.inspect.common.repository.device.DeviceTypeRepository;
import com.device.inspect.common.repository.device.InspectTypeRepository;
import com.device.inspect.common.repository.firm.BuildingRepository;
import com.device.inspect.common.repository.firm.CompanyRepository;
import com.device.inspect.common.repository.firm.StoreyRepository;
import com.device.inspect.common.repository.firm.RoomRepository;
import com.device.inspect.common.restful.RestResponse;
import com.device.inspect.common.restful.charater.RestUser;
import com.device.inspect.common.restful.device.RestDevice;
import com.device.inspect.common.restful.device.RestDeviceType;
import com.device.inspect.common.restful.device.RestInspectType;
import com.device.inspect.common.restful.firm.RestCompany;
import com.device.inspect.common.restful.page.*;
import com.device.inspect.common.util.transefer.ByteAndHex;
import com.device.inspect.controller.request.DeviceTypeRequest;
import com.device.inspect.controller.request.InspectTypeRequest;
import org.springframework.beans.factory.annotation.Autowired;
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

    private User judgeByPrincipal(Principal principal){
        if (null == principal||null==principal.getName())
            throw new UsernameNotFoundException("You are not login!");
        User user = userRepository.findByName(principal.getName());
        if (null==user)
            throw new UsernameNotFoundException("user not found!");
        return user;
    }

    @RequestMapping(value = "/person/info/{userId}")
    public RestResponse getUserMessage(Principal principal,@PathVariable Integer userId){
        User user = userRepository.findOne(userId);
        if (null==user)
            return new RestResponse("user not found!",1005,null);
        return new RestResponse(new RestUser(user));
    }

    @RequestMapping(value = "/person/mine/info")
    public RestResponse getMyMessage(Principal principal){
        User user = judgeByPrincipal(principal);
        return new RestResponse(new RestUser(user));
    }


    @RequestMapping(value = "/buildings")
    public RestResponse getBuildings(Principal principal,@RequestParam  Map<String,String> map){
        User user = judgeByPrincipal(principal);
        if (null == user.getCompany()){
            return new RestResponse("user's information correct!",1005,null);
        }
        List<Building> list = new ArrayList<Building>();
        if (null!=map.get("enable")&&(map.get("enable").equals("0")||map.get("enable").equals("1")))
            list = buildingRepository.findByCompanyIdAndEnable(user.getCompany().getId(),Integer.valueOf(map.get("enable")));
        else
            list = buildingRepository.findByCompanyId(user.getCompany().getId());
        user.getCompany().setBuildings(list);
        return new RestResponse(new RestIndexBuilding(user.getCompany()));
    }

     @RequestMapping(value = "/floors",method = RequestMethod.GET)
     public RestResponse getFloors(Principal principal,@RequestParam Map<String,String> map) {
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

    @RequestMapping(value = "/rooms",method = RequestMethod.GET)
    public  RestResponse getRooms(Principal principal,@RequestParam Map<String,String> map){
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


    @RequestMapping(value = "/devices",method = RequestMethod.GET)
    public  RestResponse getDevices(Principal principal,@RequestParam Map<String,String> map){
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

    @RequestMapping(value = "/device",method = RequestMethod.GET)
    public  RestResponse getDevice(Principal principal,@RequestParam Integer deviceId){
        Device device = deviceRepository.findOne(deviceId);
        if (null == device|| null ==device.getId()){
            return  new RestResponse("device information correct!",1005,null);
        }
        return new RestResponse(new RestDevice(device));
    }

    /**
     * 查询所有的设备种类
     * @return
     */
    @RequestMapping(value = "/device/types",method = RequestMethod.GET)
    public RestResponse getAllDeviceTypes(Principal principal,@RequestParam Integer enable){
        User user = judgeByPrincipal(principal);
        if (null==user.getCompany())
            return new RestResponse("user's information wrong!",1005,null);
        List<DeviceType> list = new ArrayList<DeviceType>();
        list.addAll(deviceTypeRepository.findByCompanyIdIsNull());
        if (null!=user.getRole().getRoleAuthority()&&user.getRole().getRoleAuthority().getName().startsWith("FIRM")){
            list.addAll(deviceTypeRepository.findByCompanyIdAndEnable(user.getCompany().getId(),enable));
        }
//        Iterable<DeviceType> deviceTypeIterable = deviceTypeRepository.findAll();
        List<RestDeviceType> deviceTypes = new ArrayList<RestDeviceType>();
        if (null!=list)
            for (DeviceType deviceType: list)
                deviceTypes.add(new RestDeviceType(deviceType));

        return new RestResponse(deviceTypes);
    }

    @RequestMapping(value = "/device/type/request/{deviceTypeId}")
    public RestResponse getCurrentDeviceTypeRequest(@PathVariable Integer deviceTypeId){
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
                requests.add(request);
            }
        }
        deviceTypeRequest.setId(deviceType.getId());
        deviceTypeRequest.setName(deviceType.getName());
        deviceTypeRequest.setList(requests);
        return new RestResponse(deviceTypeRequest);
    }

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

        Page<Device> devicePage = new DeviceQuery(entityManager)
                .query(requestParam, start, limit, new Sort(Sort.Direction.DESC, "createDate"));

        return new RestResponse(assembleDevices(devicePage));

    }

    @RequestMapping(value = "/employees",method = RequestMethod.GET)
    public RestResponse getAllEmployees(Principal principal,@RequestParam Map<String,String> requestParam){
//        if (null == principal || null ==principal.getName())
//            return new RestResponse("not login!",1005,null);
        User user = userRepository.findByName(principal.getName());
        if (null == user&&null == user.getCompany()&&user.getRole().getRoleAuthority().getChild()!=null){
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

        requestParam.put("authorityId",user.getRole().getRoleAuthority().getChild().toString());
        if (user.getRole().getRoleAuthority().getName().equals("FIRM_MANAGER")){
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
        if (user.getRole().getRoleAuthority().getName().equals("SERVICE_MANAGER")){
            Map<String,String> requestParam = new HashMap<String,String>();
            Page<Company> companyPage = new CompanyQuery(entityManager)
                    .query(requestParam, 0, 100000, new Sort(Sort.Direction.DESC, "createDate"));
            list = (List)assembleCompanies(companyPage).get("companies");
        }else if (user.getRole().getRoleAuthority().getName().equals("SERVICE_BUSINESS")){
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
    public RestResponse getCompanyById(@RequestParam String companyId){
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
        if (null == user.getCompany()&&user.getRole().getRoleAuthority().getChild()!=null){
            return new RestResponse("user's information correct!",1005,null);
        }
        if (user.getRole().getRoleAuthority()!=null){
            if (user.getRole().getRoleAuthority().getName().equals("SERVICE_BUSINESS")){
                requestParam.put("businessId",user.getId().toString());
            }else if (user.getRole().getRoleAuthority().getName().equals("SERVICE_MANAGER")){

            }else {
                return new RestResponse("权限不足！",null);
            }
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
            if(null!=userEnch.getRole().getRoleAuthority()&&(userEnch.getRole().getRoleAuthority().getName().equals("FIRM_WORKER")||
                    userEnch.getRole().getRoleAuthority().getName().equals("FIRM_MANAGER"))){
                RestUser restUser = new RestUser(userEnch);
                result.add(restUser);
            }
        }
        return new RestResponse(result);
    }

    @RequestMapping(value = "/query/inspect/type")
    public RestResponse getAllInspectType(){
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

    @RequestMapping("/test/company")
    public RestResponse updateCompanyURL(){
        Iterable<Company> companies = companyRepository.findAll();
        for (Company company:companies){

            try {
                company.setLogin("http://inmycars.ihengtian.top/inspect/Lab_login.html?company="+ ByteAndHex.convertMD5(URLEncoder.encode(company.getId().toString(),"UTF-8")));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            companyRepository.save(company);
        }
        return new RestResponse();
    }

    /**
     * 获取用户所在企业所有的科学家
     * @param principal
     * @return
     */
    @RequestMapping("/colleges/scientist")
    public RestResponse getMyScientist(Principal principal){
        User user = judgeByPrincipal(principal);

        List<User> list = userRepository.findByCompanyId(user.getCompany().getId());
        List<RestUser> result = new ArrayList<RestUser>();
        for (User userEnch : list){
            if(null!=userEnch.getRole().getRoleAuthority()&&userEnch.getRole().getRoleAuthority().getName().equals("FIRM_SCIENTIST")){
                RestUser restUser = new RestUser(userEnch);
                result.add(restUser);
            }
        }
        return new RestResponse(result);
    }

}
