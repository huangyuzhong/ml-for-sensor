package com.device.inspect.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.device.inspect.common.model.charater.User;
import com.device.inspect.common.model.device.*;
import com.device.inspect.common.model.firm.Building;
import com.device.inspect.common.model.firm.Room;
import com.device.inspect.common.model.firm.Storey;
import com.device.inspect.common.repository.charater.RoleAuthorityRepository;
import com.device.inspect.common.repository.charater.RoleRepository;
import com.device.inspect.common.repository.charater.UserRepository;
import com.device.inspect.common.repository.device.*;
import com.device.inspect.common.repository.firm.BuildingRepository;
import com.device.inspect.common.repository.firm.RoomRepository;
import com.device.inspect.common.repository.firm.StoreyRepository;
import com.device.inspect.common.restful.RestResponse;
import com.device.inspect.common.restful.device.RestDevice;
import com.device.inspect.common.restful.device.RestDeviceType;
import com.device.inspect.common.restful.firm.RestBuilding;
import com.device.inspect.common.restful.firm.RestFloor;
import com.device.inspect.common.restful.firm.RestRoom;
import com.device.inspect.controller.request.DeviceTypeRequest;
import com.device.inspect.controller.request.InspectTypeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialException;
import java.io.*;
import java.io.File;
import java.util.*;

/**
 * Created by Administrator on 2016/8/16.
 */
@Controller
@RequestMapping(value = "/api/rest/file")
public class FileController {

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
    private DeviceInspectRepository deviceInspectRepository;

    @Autowired
    private MonitorDeviceRepository monitorDeviceRepository;



    /**
     *
     * @param name
     * @param param         type 0位新增，1为修改
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     * @throws SerialException
     */
    @RequestMapping(value = "/create/building/{name}")
    public void createBuilding(@PathVariable String name,@RequestParam Map<String,String> param,
                               HttpServletRequest request,HttpServletResponse response)
            throws ServletException, IOException,SerialException {
        User user = userRepository.findByName(name);
        RestResponse restResponse = new RestResponse();
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        if (null == user)
            restResponse = new RestResponse("该用户不存在！", null);
        else {
            if (user.getRole().getRoleAuthority().getName().equals("FIRM_MANAGER")) {

                Building building = new Building();
                if (null!=param.get("type")&&null!=param.get("buildId")&&param.get("type").equals("1")){
                    building = buildingRepository.findOne(Integer.valueOf(param.get("buildId")));
                }else {
                    building.setCreateDate(new Date());
                    building.setDeviceNum(0);
                    building.setCompany(user.getCompany());
                }

                building.setName(null == param.get("name") ? null : param.get("name"));
                building.setXpoint(null == param.get("xpoint") ? null : Float.valueOf(param.get("xpoint")));
                building.setYpoint(null == param.get("ypoint") ? null : Float.valueOf(param.get("ypoint")));
                try {
                    MultipartHttpServletRequest multirequest = (MultipartHttpServletRequest) request;
                    MultiValueMap<String, MultipartFile> map = multirequest.getMultiFileMap();
                    Set<String> keys = map.keySet();
                    for (String key : keys) {
                        JSONObject jobj = new JSONObject();
                        String path = "";

                        path = request.getSession().getServletContext().getRealPath("/") + "photo/company/";
                        File add = new File(path);
                        if (!add.exists() && !add.isDirectory()) {
                            add.mkdir();
                        }

                        List<MultipartFile> files = map.get(key);
                        if (null != files && files.size() > 0) {
                            MultipartFile file = files.get(0);
//                String name  = file.getOriginalFilename();
                            String fileName = UUID.randomUUID().toString() + ".jpg";
                            InputStream is = file.getInputStream();
                            File f = new File(path + fileName);
                            FileOutputStream fos = new FileOutputStream(f);
                            int hasRead = 0;
                            byte[] buf = new byte[1024];
                            while ((hasRead = is.read(buf)) > 0) {
                                fos.write(buf, 0, hasRead);
                            }
                            fos.close();
                            is.close();

                            building.setBackground("/photo/company/" + fileName);
//                        userRepository.save(user);
                        }


                    }
                }catch (ClassCastException e){
                    e.printStackTrace();
                }
                buildingRepository.save(building);
                restResponse = new RestResponse("操作成功！",new RestBuilding(building));
            } else {
                restResponse = new RestResponse("权限不足！",1005,null);
            }
        }

        out.print(JSON.toJSONString(restResponse));
        out.flush();
        out.close();
    }

    @RequestMapping(value = "/create/floor/{name}")
    public void createFloor(@PathVariable String name,@RequestParam Map<String,String> param,
                            HttpServletRequest request,HttpServletResponse response)
            throws ServletException, IOException,SerialException{

        User user = userRepository.findByName(name);
        RestResponse restResponse = null;
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        if (null == user)
            restResponse = new RestResponse("手机号出错！", null);
        else if(null==param.get("buildId")){
            restResponse = new RestResponse("楼建筑信息出错！", null);
        }
        else if (user.getRole().getRoleAuthority().getName().equals("FIRM_MANAGER")) {

            Storey floor = new Storey();

            if (null!=param.get("type")&&null!=param.get("floorId")&&param.get("type").equals("1")){
                floor = storeyRepository.findOne(Integer.valueOf(param.get("floorId")));
            }else {
                floor.setCreateDate(new Date());
                floor.setDeviceNum(0);
            }

            floor.setBuild(param.get("buildId") == null ? null : buildingRepository.findOne(Integer.valueOf(param.get("buildId"))));
            floor.setName(null == param.get("name") ? null : param.get("name"));
            floor.setXpoint(null == param.get("xpoint") ? null : Float.valueOf(param.get("xpoint")));
            floor.setYpoint(null==param.get("ypoint")?null:Float.valueOf(param.get("ypoint")));
            try {
                MultipartHttpServletRequest multirequest = (MultipartHttpServletRequest) request;
                MultiValueMap<String, MultipartFile> map = multirequest.getMultiFileMap();
                Set<String> keys = map.keySet();
                for (String key : keys) {
                    JSONObject jobj = new JSONObject();
                    String path = "";

                    path = request.getSession().getServletContext().getRealPath("/") + "photo/company/";
                    File add = new File(path);
                    if (!add.exists() && !add.isDirectory()) {
                        add.mkdir();
                    }

                    List<MultipartFile> files = map.get(key);
                    if (null != files && files.size() > 0) {
                        MultipartFile file = files.get(0);
//                String name  = file.getOriginalFilename();
                        String fileName = UUID.randomUUID().toString() + ".jpg";
                        InputStream is = file.getInputStream();
                        File f = new File(path + fileName);
                        FileOutputStream fos = new FileOutputStream(f);
                        int hasRead = 0;
                        byte[] buf = new byte[1024];
                        while ((hasRead = is.read(buf)) > 0) {
                            fos.write(buf, 0, hasRead);
                        }
                        fos.close();
                        is.close();

                        floor.setBackground("/photo/company/" + fileName);
//                    userRepository.save(user);
                    }
                }
            }catch (ClassCastException e){
                e.printStackTrace();
            }
            storeyRepository.save(floor);
            restResponse = new RestResponse("操作成功！",new RestFloor(floor));
        } else {
            restResponse = new RestResponse("权限不足！",1005,null);
        }
        out.print(JSON.toJSONString(restResponse));
        out.flush();
        out.close();
    }

    @RequestMapping(value = "/create/device/{name}")
    public void createDevice(@PathVariable String name,@RequestParam Map<String,String> param,
                             HttpServletRequest request,HttpServletResponse response)
            throws ServletException, IOException,SerialException{
        User user = userRepository.findByName(name);
        RestResponse restResponse = null;
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        Device device = new Device();
        if (null == user)
            restResponse = new RestResponse("用户信息出错！", null);
        else if(null==param.get("roomId"))
            restResponse = new RestResponse("房间信息信息出错！", null);
        else if (null==param.get("typeId"))
            restResponse = new RestResponse("设备种类信息出错！", null);
        else if (user.getRole().getRoleAuthority().getName().equals("FIRM_MANAGER")){
            Room room = roomRepository.findOne(Integer.valueOf(param.get("roomId")));
            DeviceType deviceType = deviceTypeRepository.findOne(Integer.valueOf(param.get("typeId")));

            device.setCreateDate(new Date());
            device.setCode(param.get("code"));
            device.setAlterNum(null == param.get("alterNum") ? 0 : Integer.valueOf(param.get("alterNum")));
            device.setDeviceType(deviceType);
            device.setManager(null == param.get("managerId") ? user : userRepository.findOne(Integer.valueOf(param.get("managerId"))));
            device.setxPoint(null == param.get("xPoint") ? 0 : Float.valueOf(param.get("xPoint")));
            device.setyPoint(null == param.get("yPoint") ? 0 : Float.valueOf(param.get("yPoint")));
            device.setName(param.get("name"));
            device.setRoom(room);
            deviceRepository.save(device);
            MonitorDevice monitorDevice = new MonitorDevice();
            monitorDevice.setBattery("100");
            monitorDevice.setDevice(device);
            monitorDevice.setNumber(param.get("monitorCode"));
            monitorDevice.setOnline("在线");
            monitorDeviceRepository.save(monitorDevice);

            if (null!=deviceType.getDeviceTypeInspectList()){
                for (DeviceTypeInspect deviceTypeInspect : deviceType.getDeviceTypeInspectList()){
                    DeviceInspect deviceInspect = new DeviceInspect();
                    deviceInspect.setDevice(device);
                    deviceInspect.setInspectType(deviceTypeInspect.getInspectType());
                    deviceInspect.setStandard(deviceTypeInspect.getStandard());
                    deviceInspect.setHighUp(deviceTypeInspect.getHighUp());
                    deviceInspect.setHighDown(deviceTypeInspect.getHighDown());
                    deviceInspect.setLowDown(deviceTypeInspect.getLowDown());
                    deviceInspect.setLowUp(deviceTypeInspect.getLowUp());
                    deviceInspect.setLowAlter(deviceTypeInspect.getLowAlter());
                    deviceInspectRepository.save(deviceInspect);
                }
            }

            try {
                MultipartHttpServletRequest multirequest = (MultipartHttpServletRequest) request;
                MultiValueMap<String, MultipartFile> map = multirequest.getMultiFileMap();
                Set<String> keys = map.keySet();
                for (String key : keys) {
                    JSONObject jobj = new JSONObject();
                    String path = "";

                    path = request.getSession().getServletContext().getRealPath("/") + "photo/device/";
                    File add = new File(path);
                    if (!add.exists() && !add.isDirectory()) {
                        add.mkdir();
                    }

                    List<MultipartFile> files = map.get(key);
                    if (null != files && files.size() > 0) {
                        MultipartFile file = files.get(0);
//                String name  = file.getOriginalFilename();
                        String fileName = UUID.randomUUID().toString() + ".jpg";
                        InputStream is = file.getInputStream();
                        File f = new File(path + fileName);
                        FileOutputStream fos = new FileOutputStream(f);
                        int hasRead = 0;
                        byte[] buf = new byte[1024];
                        while ((hasRead = is.read(buf)) > 0) {
                            fos.write(buf, 0, hasRead);
                        }
                        fos.close();
                        is.close();

                        device.setPhoto("/photo/device/" + fileName);
//                    userRepository.save(user);
                    }
                }
            }catch (ClassCastException e){
                e.printStackTrace();
            }
            restResponse = new RestResponse("操作成功！",new RestDevice(device));
        }else {
            restResponse = new RestResponse("权限不足！",1005,null);
        }
    }

    @RequestMapping(value = "/create/room/{name}")
    public void createRoom(@PathVariable String name,@RequestParam Map<String,String> param,
                           HttpServletRequest request,HttpServletResponse response)
            throws ServletException, IOException,SerialException{
        User user = userRepository.findByName(name);
        RestResponse restResponse = null;
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        if (null == user)
            restResponse = new RestResponse("手机号出错！", null);
        else if(null==param.get("floorId")){
            restResponse = new RestResponse("楼层信息出错！", null);
        }
        else if (user.getRole().getRoleAuthority().getName().equals("FIRM_MANAGER")) {

            Room room = new Room();

            if (null!=param.get("type")&&null!=param.get("floorId")&&param.get("type").equals("1")){
                room = roomRepository.findOne(Integer.valueOf(param.get("roomId")));
            }else {
                room.setCreateDate(new Date());
                room.setDeviceNum(0);
            }

            room.setFloor(null == param.get("floorId") ? null : storeyRepository.findOne(Integer.valueOf(param.get("floorId"))));
            room.setName(null == param.get("name") ? null : param.get("name"));
            room.setxPoint(null == param.get("xpoint") ? null : Float.valueOf(param.get("xpoint")));
            room.setyPoint(null == param.get("ypoint") ? null : Float.valueOf(param.get("ypoint")));

            try {
                MultipartHttpServletRequest multirequest = (MultipartHttpServletRequest) request;
                MultiValueMap<String, MultipartFile> map = multirequest.getMultiFileMap();
                Set<String> keys = map.keySet();
                for (String key : keys) {
                    JSONObject jobj = new JSONObject();
                    String path = "";

                    path = request.getSession().getServletContext().getRealPath("/") + "photo/company/";
                    File add = new File(path);
                    if (!add.exists() && !add.isDirectory()) {
                        add.mkdir();
                    }

                    List<MultipartFile> files = map.get(key);
                    if (null != files && files.size() > 0) {
                        MultipartFile file = files.get(0);
//                String name  = file.getOriginalFilename();
                        String fileName = UUID.randomUUID().toString() + ".jpg";
                        InputStream is = file.getInputStream();
                        File f = new File(path + fileName);
                        FileOutputStream fos = new FileOutputStream(f);
                        int hasRead = 0;
                        byte[] buf = new byte[1024];
                        while ((hasRead = is.read(buf)) > 0) {
                            fos.write(buf, 0, hasRead);
                        }
                        fos.close();
                        is.close();

                        room.setBackground("/photo/company/" + fileName);
                    }
//                    restResponse = new RestResponse("添加成功！",null);
                }
            }catch (ClassCastException e){
                e.printStackTrace();
            }

            roomRepository.save(room);
            restResponse = new RestResponse("操作成功！",new RestRoom(room));
        } else {
            restResponse = new RestResponse("权限不足！",1005,null);
        }
        out.print(JSON.toJSONString(restResponse));
        out.flush();
        out.close();
    }

    @RequestMapping(value = "/create/deviceType/{name}")
    public void createDeviceType(@PathVariable String name,@RequestBody DeviceTypeRequest deviceTypeReq,
                                 HttpServletRequest request,HttpServletResponse response)
            throws ServletException, IOException,SerialException{
        User user = userRepository.findByName(name);
        RestResponse restResponse = null;
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        if (null == user)
            restResponse = new RestResponse("手机号出错！", null);

        DeviceType deviceType = new DeviceType();
        List<DeviceTypeInspect> deviceTypeInspects = new ArrayList<DeviceTypeInspect>();
        if (user.getRole().getRoleAuthority().getName().equals("FIRM_MANAGER")) {
            if (null!=deviceTypeReq.getType()&&null!=deviceTypeReq.getId()&&1==deviceTypeReq.getId()){
                deviceType = deviceTypeRepository.findOne(deviceTypeReq.getId());
                deviceTypeInspects = deviceType.getDeviceTypeInspectList();
                if (null!=deviceTypeReq&&deviceTypeReq.getList().size()>0){
                    for (InspectTypeRequest inspectTypeRequest : deviceTypeReq.getList()){
                        InspectType inspectType = inspectTypeRepository.findOne(inspectTypeRequest.getId());
                        DeviceTypeInspect deviceTypeInspect = deviceTypeInspectRepository.
                                findByDeviceTypeIdAndInspectTypeId(deviceType.getId(),inspectType.getId());
                        if (null!=deviceTypeInspect){
                            deviceTypeInspect.setHighDown(Float.valueOf(inspectTypeRequest.getHighDown()));
                            deviceTypeInspect.setHighUp(Float.valueOf(inspectTypeRequest.getHighUp()));
                            deviceTypeInspect.setStandard(Float.valueOf(inspectTypeRequest.getStandard()));
                            deviceTypeInspect.setLowDown(Float.valueOf(inspectTypeRequest.getLowDown()));
                            deviceTypeInspect.setLowUp(Float.valueOf(inspectTypeRequest.getLowUp()));
                            deviceTypeInspect.setLowAlter(null==inspectTypeRequest.getLowAlter()?10:inspectTypeRequest.getLowAlter());
                            deviceTypeInspectRepository.save(deviceTypeInspect);
                        }
                    }
                }
            }else {
                if (null!=deviceTypeReq&&deviceTypeReq.getList().size()>0){
                    for (InspectTypeRequest inspectTypeRequest : deviceTypeReq.getList()){
                        InspectType inspectType = inspectTypeRepository.findOne(inspectTypeRequest.getId());
                        DeviceTypeInspect deviceTypeInspect = new DeviceTypeInspect();
                        deviceTypeInspect.setHighDown(Float.valueOf(inspectTypeRequest.getHighDown()));
                        deviceTypeInspect.setHighUp(Float.valueOf(inspectTypeRequest.getHighUp()));
                        deviceTypeInspect.setStandard(Float.valueOf(inspectTypeRequest.getStandard()));
                        deviceTypeInspect.setLowDown(Float.valueOf(inspectTypeRequest.getLowDown()));
                        deviceTypeInspect.setLowUp(Float.valueOf(inspectTypeRequest.getLowUp()));
                        deviceTypeInspect.setLowAlter(null==inspectTypeRequest.getLowAlter()?10:inspectTypeRequest.getLowAlter());
                        deviceTypeInspectRepository.save(deviceTypeInspect);
                    }
                }
            }

            if (null!=deviceTypeReq.getName()){
                deviceType.setName(deviceTypeReq.getName());
            }

            try {
                MultipartHttpServletRequest multirequest = (MultipartHttpServletRequest) request;
                MultiValueMap<String, MultipartFile> map = multirequest.getMultiFileMap();
                Set<String> keys = map.keySet();
                for (String key : keys) {
                    JSONObject jobj = new JSONObject();
                    String path = "";

                    path = request.getSession().getServletContext().getRealPath("/") + "photo/company/";
                    File add = new File(path);
                    if (!add.exists() && !add.isDirectory()) {
                        add.mkdir();
                    }

                    List<MultipartFile> files = map.get(key);
                    if (null != files && files.size() > 0) {
                        MultipartFile file = files.get(0);
//                String name  = file.getOriginalFilename();
                        String fileName = UUID.randomUUID().toString() + ".jpg";
                        InputStream is = file.getInputStream();
                        File f = new File(path + fileName);
                        FileOutputStream fos = new FileOutputStream(f);
                        int hasRead = 0;
                        byte[] buf = new byte[1024];
                        while ((hasRead = is.read(buf)) > 0) {
                            fos.write(buf, 0, hasRead);
                        }
                        fos.close();
                        is.close();

                        deviceType.setLogo("/photo/company/" + fileName);
                    }
                }
            }catch (ClassCastException e){
                e.printStackTrace();
//                deviceType.setLogo("/photo/company/" + fileName);
            }
            deviceTypeRepository.save(deviceType);
            restResponse = new RestResponse("操作成功！",new RestDeviceType(deviceType));
        } else {
            restResponse = new RestResponse("权限不足！",1005,null);
        }
        out.print(JSON.toJSONString(restResponse));
        out.flush();
        out.close();

    }
}
