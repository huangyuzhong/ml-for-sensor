package com.device.inspect.controller;

import com.device.inspect.common.model.charater.Role;
import com.device.inspect.common.model.charater.User;
import com.device.inspect.common.model.device.Device;
import com.device.inspect.common.model.firm.Company;
import com.device.inspect.common.model.firm.Floor;
import com.device.inspect.common.model.firm.Room;
import com.device.inspect.common.repository.charater.RoleRepository;
import com.device.inspect.common.repository.charater.UserRepository;
import com.device.inspect.common.repository.device.DeviceRepository;
import com.device.inspect.common.repository.firm.CompanyRepository;
import com.device.inspect.common.repository.firm.FloorRepository;
import com.device.inspect.common.repository.firm.RoomRepository;
import com.device.inspect.common.restful.RestResponse;
import com.device.inspect.common.restful.charater.RestUser;
import com.device.inspect.common.restful.page.RestIndexBuilding;
import com.device.inspect.common.restful.page.RestIndexFloor;
import com.device.inspect.common.restful.page.RestIndexRoom;
import com.device.inspect.common.restful.page.RestIndexUser;
import com.mysql.jdbc.V1toV2StatementInterceptorAdapter;
import netscape.security.UserTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2016/7/12.
 */
@RestController
@RequestMapping(value = "/api/rest")
public class ApiController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private FloorRepository floorRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoleRepository roleRepository;

    @RequestMapping(value = "/firm/buildings")
    public RestResponse getBuildings(Principal principal){
        User user = userRepository.findByName(principal.getName());
        if (null == user&&null == user.getCompany()){
            return new RestResponse("user's information correct!",1005,null);
        }
        return new RestResponse(new RestIndexBuilding(user.getCompany()));
    }


     @RequestMapping(value = "/firm/rooms",method = RequestMethod.GET)
     public RestResponse getRooms(Principal principal,@RequestParam Integer floorId) {
         Floor floor = floorRepository.findOne(floorId);
         if (null == floor && null == floor.getName()) {
             return new RestResponse("floors information correct!", 1005, null);
         }
         return new RestResponse(new RestIndexRoom(floor));
     }

    @RequestMapping(value = "/room/device",method = RequestMethod.GET)
    public  RestResponse getDevices(Principal principal,@RequestParam Integer roomId){
        Room room = roomRepository.findOne(roomId);
        if (null == room&&null ==room.getFloor()){
            return  new RestResponse("rooms information correct!",1005,null);
        }
        return new RestResponse(room);
    }

    @RequestMapping(value = "/my/employees",method = RequestMethod.GET)
    public RestResponse getAllEmployees(Principal principal){
        if (null == principal || null ==principal.getName())
            return new RestResponse("not login!",1005,null);
        User user = userRepository.findByName(principal.getName());
        if (null == user&&null == user.getCompany()){
            return new RestResponse("user's information correct!",1005,null);
        }
        Role role = roleRepository.findByUserId(user.getId());
        List<User> userList = new ArrayList<User>();
        if (role.getRoleAuthority().getChild()!=null){
            List<Role> roleList = roleRepository.
                    findByUserCompanyIdAndRoleAuthorityId(user.getCompany().getId(), role.getRoleAuthority().getChild());
            if (null!=roleList&&roleList.size()>0){
                for (Role roleEach:roleList){
                    userList.add(roleEach.getUser());
                }
            }
        }
        return new RestResponse(new RestIndexUser(user,userList));
    }

}
