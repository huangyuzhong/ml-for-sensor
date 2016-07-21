package com.device.inspect.controller;

import com.device.inspect.common.model.charater.User;
import com.device.inspect.common.model.device.Device;
import com.device.inspect.common.model.device.DeviceType;
import com.device.inspect.common.model.firm.Building;
import com.device.inspect.common.model.firm.Floor;
import com.device.inspect.common.model.firm.Room;
import com.device.inspect.common.query.charater.DeviceQuery;
import com.device.inspect.common.query.charater.UserQuery;
import com.device.inspect.common.repository.charater.RoleRepository;
import com.device.inspect.common.repository.charater.UserRepository;
import com.device.inspect.common.repository.device.DeviceRepository;
import com.device.inspect.common.repository.device.DeviceTypeRepository;
import com.device.inspect.common.repository.firm.BuildingRepository;
import com.device.inspect.common.repository.firm.FloorRepository;
import com.device.inspect.common.repository.firm.RoomRepository;
import com.device.inspect.common.restful.RestResponse;
import com.device.inspect.common.restful.charater.RestUser;
import com.device.inspect.common.restful.device.RestDevice;
import com.device.inspect.common.restful.device.RestDeviceType;
import com.device.inspect.common.restful.page.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import javax.persistence.EntityManager;
import java.security.Principal;
import java.util.*;

/**
 * Created by Administrator on 2016/7/12.
 */
@RestController
@RequestMapping(value = "/api/rest/firm")
public class FirmApiController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private FloorRepository floorRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private DeviceTypeRepository deviceTypeRepository;

    @RequestMapping(value = "/person/info/{userId}")
    public RestResponse getUserMessage(Principal principal,@PathVariable Integer userId){
        User user = userRepository.findOne(userId);
        if (null==user)
            return new RestResponse("user not found!",1005,null);
        return new RestResponse(new RestUser(user));
    }

    @RequestMapping(value = "/person/mine/info")
    public RestResponse getMyMessage(Principal principal){
        User user = userRepository.findByName(principal.getName());
        if (null == user)
            return new RestResponse("user not found!",1005,null);
        return new RestResponse(new RestUser(user));
    }



    @RequestMapping(value = "/buildings")
    public RestResponse getBuildings(Principal principal){
        User user = userRepository.findByName(principal.getName());
        if (null == user&&null == user.getCompany()){
            return new RestResponse("user's information correct!",1005,null);
        }
        return new RestResponse(new RestIndexBuilding(user.getCompany()));
    }


     @RequestMapping(value = "/floors",method = RequestMethod.GET)
     public RestResponse getFloors(Principal principal,@RequestParam Integer buildId) {
         Building build = buildingRepository.findOne(buildId);
//         Floor floor = floorRepository.findOne(floorId);
         if (null == build && null ==build.getId()) {
             return new RestResponse("floors information correct!", 1005, null);
         }
         return new RestResponse(new RestIndexFloor(build));
     }

    @RequestMapping(value = "/rooms",method = RequestMethod.GET)
    public  RestResponse getRooms(Principal principal,@RequestParam Integer floorId){
        Floor floor = floorRepository.findOne(floorId);
        if (null == floor&&null ==floor.getId()){
            return  new RestResponse("rooms information correct!",1005,null);
        }
        return new RestResponse(new RestIndexRoom(floor));
    }


    @RequestMapping(value = "/devices",method = RequestMethod.GET)
    public  RestResponse getDevices(Principal principal,@RequestParam Integer roomId){
        Room room = roomRepository.findOne(roomId);
        if (null == room&&null ==room.getId()){
            return  new RestResponse("devices information correct!",1005,null);
        }
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

    @RequestMapping(value = "/device/types",method = RequestMethod.GET)
    public RestResponse getAllDeviceTypes(){
        Iterable<DeviceType> deviceTypeIterable = deviceTypeRepository.findAll();
        List<RestDeviceType> deviceTypes = new ArrayList<RestDeviceType>();
        if (null!=deviceTypeIterable){
            for (DeviceType deviceType: deviceTypeIterable){
                deviceTypes.add(new RestDeviceType(deviceType));
            }
        }
        return new RestResponse(deviceTypes);
    }

    @RequestMapping(value = "/manager/devices",method = RequestMethod.GET)
    public RestResponse getAllDevicesByManger(Principal principal,@RequestParam Map<String,String> requestParam){
        if (null == principal || null ==principal.getName())
            return new RestResponse("not login!",1005,null);
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

        if (!requestParam.containsKey("userId")){
            requestParam.put("userId",user.getId().toString());
        }

        Page<Device> devicePage = new DeviceQuery(entityManager)
                .query(requestParam, start, limit, new Sort(Sort.Direction.DESC, "createDate"));

        return new RestResponse(assembleDevices(devicePage));

    }

    @RequestMapping(value = "/employees",method = RequestMethod.GET)
    public RestResponse getAllEmployees(Principal principal,@RequestParam Map<String,String> requestParam){
        if (null == principal || null ==principal.getName())
            return new RestResponse("not login!",1005,null);
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
        requestParam.put("companyId",user.getCompany().getId().toString());
        Page<User> userPage = new UserQuery(entityManager)
                .query(requestParam, start, limit, new Sort(Sort.Direction.DESC, "createDate"));

        return new RestResponse(assembleUsers(user,userPage));
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

}
