package com.device.inspect.controller;

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
import com.device.inspect.common.repository.charater.RoleAuthorityRepository;
import com.device.inspect.common.repository.charater.RoleRepository;
import com.device.inspect.common.repository.charater.UserRepository;
import com.device.inspect.common.repository.device.*;
import com.device.inspect.common.repository.firm.BuildingRepository;
import com.device.inspect.common.repository.firm.CompanyRepository;
import com.device.inspect.common.repository.firm.RoomRepository;
import com.device.inspect.common.repository.firm.StoreyRepository;
import com.device.inspect.common.restful.RestResponse;
import com.device.inspect.common.restful.device.RestDevice;
import com.device.inspect.common.restful.device.RestDeviceType;
import com.device.inspect.common.restful.firm.RestBuilding;
import com.device.inspect.common.restful.firm.RestCompany;
import com.device.inspect.common.restful.firm.RestFloor;
import com.device.inspect.common.restful.firm.RestRoom;
import com.device.inspect.common.util.transefer.ByteAndHex;
import com.device.inspect.common.util.transefer.UserRoleDifferent;
import com.device.inspect.controller.request.CompanyRequest;
import com.device.inspect.controller.request.DeviceTypeRequest;
import com.device.inspect.controller.request.InspectTypeRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.record.PageBreakRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialException;
import java.io.*;
import java.io.File;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.*;

/**
 * Created by Administrator on 2016/8/16.
 */
@Controller
@RequestMapping(value = "/api/rest/file")
public class FileController {

    private static final String SERVICE_PATH = "http://inmycars.ihengtian.top:8998";
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

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private DeviceFileRepository deviceFileRepository;

    @Autowired
    private ScientistDeviceRepository scientistDeviceRepository;

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
     *
     * @param 
     * @param param         type 0位新增，1为修改
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     * @throws SerialException
     */
    @RequestMapping(value = "/create/building")
        public void createBuilding(Principal principal,@RequestParam Map<String,String> param,
                               HttpServletRequest request,HttpServletResponse response)
            throws ServletException, IOException,SerialException {
        User user = judgeByPrincipal(principal);
        RestResponse restResponse = new RestResponse();
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        if (null == user)
            restResponse = new RestResponse("该用户不存在！",1005, null);
        else {
            if (UserRoleDifferent.userFirmManagerConfirm(user)) {
                Building building = new Building();
                //wkj添加  设置开关
                boolean b=false;
                //wkj添加 根据用户获取企业
                Company company=user.getCompany();
                //wkj添加
                List<Building> list=new ArrayList<Building>();
                //wkj添加  根据企业id 获取同一个企业的所有楼
                list=buildingRepository.findByCompanyId(Integer.valueOf(company.getId()));
                if (null!=param.get("type")&&null!=param.get("buildId")&&param.get("type").equals("1")){
                    building = buildingRepository.findOne(Integer.valueOf(param.get("buildId")));
                    //wkj添加 修改楼信息
                    if (!building.getName().equals(param.get("name"))){
                        //修改楼名称
                        if (list!=null&&list.size()>0){
                            for (Building building1:list){
                                if (building1.getName()!=null&&!"".equals(building1.getName())&&building1.getName().equals(param.get("name"))){
                                    b=true;
                                    break;
                                }
                            }
                        }
                    }
                }else {
                    building.setCreateDate(new Date());
                    building.setDeviceNum(0);
                    building.setCompany(user.getCompany());
                    //wkj添加
                    if (list!=null&&list.size()>0){
                        for (Building building2:list){
                            if (building2.getName()!=null&&!"".equals(building2.getName())&&building2.getName().equals(param.get("name"))){
                                b=true;
                                break;
                            }
                        }
                    }
                }
                //wkj添加 b=true  存在相同的楼名称  b=false 不存在相同的楼名称
                if (b){
                    restResponse=new RestResponse("该名称已经存在",1005,null);
                }else {
                    //楼名称不存在重复
                    building.setEnable(1);
                    building.setName(null == param.get("name") ? null : param.get("name"));
                    building.setXpoint(null == param.get("xpoint") ? null : Float.valueOf(param.get("xpoint")));
                    building.setYpoint(null == param.get("ypoint") ? null : Float.valueOf(param.get("ypoint")));
                    buildingRepository.save(building);
                    String pic=param.get("pic");
                    if (pic.equals("0")){
                        System.out.println("上传图片");
                        try {
                            MultipartHttpServletRequest multirequest = (MultipartHttpServletRequest) request;
                            MultiValueMap<String, MultipartFile> map = multirequest.getMultiFileMap();
                            Set<String> keys = map.keySet();
                            for (String key : keys) {
                                JSONObject jobj = new JSONObject();
                                String path = "";

                                path = request.getSession().getServletContext().getRealPath("/") + "photo/company/build/"+building.getId()+"/";
                                File add = new File(path);
                                if (!add.exists() && !add.isDirectory()) {
                                    add.mkdirs();
                                }

                                List<MultipartFile> files = map.get(key);
                                if (null != files && files.size() > 0) {
                                    MultipartFile file = files.get(0);
                                    String fileName  = file.getOriginalFilename();
                                    Date date=new Date();
                                    fileName=String.valueOf(date.getTime());
//                            String fileName = UUID.randomUUID().toString() + ".jpg";
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

                                    building.setBackground("/photo/company/build/"+building.getId()+"/" + fileName);
//                        userRepository.save(user);
                                }


                            }
                        }catch (ClassCastException e){
                            e.printStackTrace();
                        }
                    }else {
                        System.out.println("没有上传图片");
                    }

                    buildingRepository.save(building);
                    restResponse = new RestResponse("操作成功！",new RestBuilding(building));

                }
//                building.setEnable(1);
//                building.setName(null == param.get("name") ? null : param.get("name"));
//                building.setXpoint(null == param.get("xpoint") ? null : Float.valueOf(param.get("xpoint")));
//                building.setYpoint(null == param.get("ypoint") ? null : Float.valueOf(param.get("ypoint")));
//                buildingRepository.save(building);
//                try {
//                    MultipartHttpServletRequest multirequest = (MultipartHttpServletRequest) request;
//                    MultiValueMap<String, MultipartFile> map = multirequest.getMultiFileMap();
//                    Set<String> keys = map.keySet();
//                    for (String key : keys) {
//                        JSONObject jobj = new JSONObject();
//                        String path = "";
//
//                        path = request.getSession().getServletContext().getRealPath("/") + "photo/company/build/"+building.getId()+"/";
//                        File add = new File(path);
//                        if (!add.exists() && !add.isDirectory()) {
//                            add.mkdirs();
//                        }
//
//                        List<MultipartFile> files = map.get(key);
//                        if (null != files && files.size() > 0) {
//                            MultipartFile file = files.get(0);
//                            String fileName  = file.getOriginalFilename();
////                            String fileName = UUID.randomUUID().toString() + ".jpg";
//                            InputStream is = file.getInputStream();
//                            File f = new File(path + fileName);
//                            FileOutputStream fos = new FileOutputStream(f);
//                            int hasRead = 0;
//                            byte[] buf = new byte[1024];
//                            while ((hasRead = is.read(buf)) > 0) {
//                                fos.write(buf, 0, hasRead);
//                            }
//                            fos.close();
//                            is.close();
//
//                            building.setBackground("/photo/company/build/"+building.getId()+"/" + fileName);
////                        userRepository.save(user);
//                        }
//
//
//                    }
//                }catch (ClassCastException e){
//                    e.printStackTrace();
//                }
//                buildingRepository.save(building);
//                restResponse = new RestResponse("操作成功！",new RestBuilding(building));
            } else {
                restResponse = new RestResponse("权限不足！",1005,null);
            }
        }

        out.print(JSON.toJSONString(restResponse));
        out.flush();
        out.close();
    }

    /**
     *  type 0新增   1是修改
     * @param principal
     * @param param
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     * @throws SerialException
     */
    @RequestMapping(value = "/create/floor")
    public void createFloor(Principal principal,@RequestParam Map<String,String> param,
                            HttpServletRequest request,HttpServletResponse response)
            throws ServletException, IOException,SerialException{
        User user = judgeByPrincipal(principal);
        RestResponse restResponse = null;
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        if (null == user)
            restResponse = new RestResponse("用户未登录！",1005, null);
        else if(null==param.get("buildId")){
            restResponse = new RestResponse("楼建筑信息出错！",1005, null);
        }
        else if (UserRoleDifferent.userFirmManagerConfirm(user)) {

            Storey floor = new Storey();

            //wkj添加
            boolean b=false;
            List<Storey> list=new ArrayList<Storey>();
            //wkj添加  获取词楼的所有层
            list=storeyRepository.findByBuildId(Integer.valueOf(param.get("buildId")));
            if (null!=param.get("type")&&null!=param.get("floorId")&&param.get("type").equals("1")){
                floor = storeyRepository.findOne(Integer.valueOf(param.get("floorId")));
                //wkj添加 修改层信息 层的名称也修改
                if (!floor.getName().equals(param.get("name"))){
                    if (list!=null&&list.size()>0){
                        for (Storey storey:list){
                            if (storey.getName()!=null&&!"".equals(storey.getName())&&storey.getName().equals(param.get("name"))){
                                b=true;
                                break;
                            }
                        }
                    }
                }

            }else {
                floor.setCreateDate(new Date());
                floor.setDeviceNum(0);
                if (list!=null&&list.size()>0){
                    for (Storey storey:list){
                        if (storey.getName()!=null&&!"".equals(storey.getName())&&storey.getName().equals(param.get("name"))){
                            b=true;
                            break;
                        }
                    }
                }
            }
            if (b){
                restResponse=new RestResponse("该层名称已经存在",1005,null);
            }else {
                floor.setBuild(param.get("buildId") == null ? null : buildingRepository.findOne(Integer.valueOf(param.get("buildId"))));
                floor.setName(null == param.get("name") ? null : param.get("name"));
                floor.setXpoint(null == param.get("xpoint") ? null : Float.valueOf(param.get("xpoint")));
                floor.setYpoint(null==param.get("ypoint")?null:Float.valueOf(param.get("ypoint")));
                floor.setEnable(1);
                storeyRepository.save(floor);
                //用来判断是否上传图片  0是上传图片  1是没有上传图片
                String pic=param.get("pic");
                System.out.println("pic："+pic);
                if (pic.equals("0")){
                    System.out.println("上传图片："+pic);
                    try {
                        MultipartHttpServletRequest multirequest = (MultipartHttpServletRequest) request;
                        MultiValueMap<String, MultipartFile> map = multirequest.getMultiFileMap();
                        Set<String> keys = map.keySet();
                        for (String key : keys) {
                            JSONObject jobj = new JSONObject();
                            String path = "";

                            path = request.getSession().getServletContext().getRealPath("/") + "photo/company/floor/"+floor.getId()+"/";
                            File add = new File(path);
                            if (!add.exists() && !add.isDirectory()) {
                                add.mkdirs();
                            }

                            List<MultipartFile> files = map.get(key);
                            if (null != files && files.size() > 0) {
                                MultipartFile file = files.get(0);
                                String fileName  = file.getOriginalFilename();
                                Date date=new Date();
                                fileName=String.valueOf(date.getTime());
//                        String fileName = UUID.randomUUID().toString() + ".jpg";
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

                                floor.setBackground("/photo/company/floor/" +floor.getId()+"/"+ fileName);
//                    userRepository.save(user);
                            }
                        }
                    }catch (ClassCastException e){
                        e.printStackTrace();
                    }
                }else {
                    System.out.println("没有上传图片："+pic);
                }

                storeyRepository.save(floor);
                restResponse = new RestResponse("操作成功！",new RestFloor(floor));
            }

//            floor.setBuild(param.get("buildId") == null ? null : buildingRepository.findOne(Integer.valueOf(param.get("buildId"))));
//            floor.setName(null == param.get("name") ? null : param.get("name"));
//            floor.setXpoint(null == param.get("xpoint") ? null : Float.valueOf(param.get("xpoint")));
//            floor.setYpoint(null==param.get("ypoint")?null:Float.valueOf(param.get("ypoint")));
//            floor.setEnable(1);
//            storeyRepository.save(floor);
//            try {
//                MultipartHttpServletRequest multirequest = (MultipartHttpServletRequest) request;
//                MultiValueMap<String, MultipartFile> map = multirequest.getMultiFileMap();
//                Set<String> keys = map.keySet();
//                for (String key : keys) {
//                    JSONObject jobj = new JSONObject();
//                    String path = "";
//
//                    path = request.getSession().getServletContext().getRealPath("/") + "photo/company/floor/"+floor.getId()+"/";
//                    File add = new File(path);
//                    if (!add.exists() && !add.isDirectory()) {
//                        add.mkdirs();
//                    }
//
//                    List<MultipartFile> files = map.get(key);
//                    if (null != files && files.size() > 0) {
//                        MultipartFile file = files.get(0);
//                        String fileName  = file.getOriginalFilename();
////                        String fileName = UUID.randomUUID().toString() + ".jpg";
//                        InputStream is = file.getInputStream();
//                        File f = new File(path + fileName);
//                        FileOutputStream fos = new FileOutputStream(f);
//                        int hasRead = 0;
//                        byte[] buf = new byte[1024];
//                        while ((hasRead = is.read(buf)) > 0) {
//                            fos.write(buf, 0, hasRead);
//                        }
//                        fos.close();
//                        is.close();
//
//                        floor.setBackground("/photo/company/floor/" +floor.getId()+"/"+ fileName);
////                    userRepository.save(user);
//                    }
//                }
//            }catch (ClassCastException e){
//                e.printStackTrace();
//            }
//            storeyRepository.save(floor);
//            restResponse = new RestResponse("操作成功！",new RestFloor(floor));
        } else {
            restResponse = new RestResponse("权限不足！",1005,null);
        }
        out.print(JSON.toJSONString(restResponse));
        out.flush();
        out.close();
    }

    @RequestMapping(value = "/create/device")
    public void createDevice(Principal principal,@RequestParam Map<String,String> param,
                             HttpServletRequest request,HttpServletResponse response)
            throws ServletException, IOException,SerialException{

        User user = judgeByPrincipal(principal);
        RestResponse restResponse = null;
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        Device device = new Device();
        if (null == user)
            restResponse = new RestResponse("用户信息出错！",1005, null);
        else if(null==param.get("roomId"))
            restResponse = new RestResponse("房间信息信息出错！",1005, null);
        else if (null==param.get("typeId"))
            restResponse = new RestResponse("设备种类信息出错！", 1005,null);
        if (UserRoleDifferent.userFirmManagerConfirm(user)){
            Room room = roomRepository.findOne(Integer.valueOf(param.get("roomId")));
            DeviceType deviceType = deviceTypeRepository.findOne(Integer.valueOf(param.get("typeId")));
            if (null==room||null==deviceType)
                throw new RuntimeException("信息有误！");
            if(null == param.get("monitorCode")||"".equals(param.get("monitorCode")))
                throw new RuntimeException("终端编号为空！");
            MonitorDevice monitorDevice = null;
            monitorDevice = monitorDeviceRepository.findByNumber(param.get("monitorCode"));
            if (null!=monitorDevice)
                throw new RuntimeException("终端编号已存在，无法添加！");

            device.setCreateDate(new Date());
            device.setCode(param.get("code"));
            device.setAlterNum(null == param.get("alterNum") ? 0 : Integer.valueOf(param.get("alterNum")));
            device.setDeviceType(deviceType);
            if (null!=param.get("managerId")&&!"".equals(param.get("managerId"))&&!"undefined".equals(param.get("managerId"))){
                User deviceManager = userRepository.findOne(Integer.valueOf(param.get("managerId")));
                if (UserRoleDifferent.userFirmManagerConfirm(deviceManager)||UserRoleDifferent.userFirmWorkerConfirm(deviceManager)) {
                    device.setManager(deviceManager);
                    if (deviceManager.getRemoveAlert()!=null&&!"".equals(deviceManager.getRemoveAlert())&&deviceManager.getRemoveAlert().equals("0")){
                        device.setPushType("短信");
                    }
                    if (deviceManager.getRemoveAlert()!=null&&!"".equals(deviceManager.getRemoveAlert())&&deviceManager.getRemoveAlert().equals("1")){
                        device.setPushType("邮箱");
                    }
                    if (deviceManager.getRemoveAlert()!=null&&!"".equals(deviceManager.getRemoveAlert())&&deviceManager.getRemoveAlert().equals("2")){
                        device.setPushType("禁止推送");
                    }
                }
                else {
                    device.setManager(user);
                    if (user.getRemoveAlert()!=null&&!"".equals(user.getRemoveAlert())&&user.getRemoveAlert().equals("0")){
                        device.setPushType("短信");
                    }
                    if (user.getRemoveAlert()!=null&&!"".equals(user.getRemoveAlert())&&user.getRemoveAlert().equals("1")){
                        device.setPushType("邮箱");
                    }
                    if (user.getRemoveAlert()!=null&&!"".equals(user.getRemoveAlert())&&user.getRemoveAlert().equals("2")){
                        device.setPushType("禁止推送");
                    }
                }
            }else {
                device.setManager(user);
                if (user.getRemoveAlert()!=null&&!"".equals(user.getRemoveAlert())&&user.getRemoveAlert().equals("0")){
                    device.setPushType("短信");
                }
                if (user.getRemoveAlert()!=null&&!"".equals(user.getRemoveAlert())&&user.getRemoveAlert().equals("1")){
                    device.setPushType("邮箱");
                }
                if (user.getRemoveAlert()!=null&&!"".equals(user.getRemoveAlert())&&user.getRemoveAlert().equals("2")){
                    device.setPushType("禁止推送");
                }
            }
            device.setxPoint(null == param.get("xPoint") ? 0 : Float.valueOf(param.get("xPoint")));
            device.setyPoint(null == param.get("yPoint") ? 0 : Float.valueOf(param.get("yPoint")));
            device.setName(param.get("name"));
            device.setRoom(room);
            device.setPushInterval(null == param.get("pushInterval")?30:Integer.valueOf(param.get("pushInterval")));
            device.setEnable(1);
            Room room1=device.getRoom();
            room1.setTotal(room1.getTotal()+1);
            room1.setOffline(room1.getOffline()+1);
            roomRepository.save(room1);
            Storey storey=device.getRoom().getFloor();
            storey.setTotal(storey.getTotal()+1);
            storey.setOffline(storey.getOffline()+1);
            storeyRepository.save(storey);
            Building building=device.getRoom().getFloor().getBuild();
            building.setTotal(building.getTotal()+1);
            building.setOffline(building.getOffline()+1);
            buildingRepository.save(building);
            Company company=user.getCompany();
            company.setTotal(company.getTotal()+1);
            company.setOffline(company.getOffline()+1);
            companyRepository.save(company);
            deviceRepository.save(device);
            monitorDevice = new MonitorDevice();
            monitorDevice.setBattery("100");
            monitorDevice.setDevice(device);
            monitorDevice.setNumber(param.get("monitorCode"));
            monitorDevice.setOnline(1);
            monitorDeviceRepository.save(monitorDevice);
            if (null!=param.get("scientist")) {
                String[] scientist = param.get("scientist").split(",");
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
                    deviceInspect.setName(deviceTypeInspect.getInspectType().getName());
                    deviceInspect.setZero(0f);
                    deviceInspect.setOriginalValue(0f);
                    deviceInspect.setCorrectionValue(0f);
                    deviceInspectRepository.save(deviceInspect);
                }
            }
            String pic=param.get("pic");
            System.out.println("pic："+pic);
            if (pic.equals("0")){
                System.out.println("上传图片pic："+pic);
                try {
                    MultipartHttpServletRequest multirequest = (MultipartHttpServletRequest) request;
                    MultiValueMap<String, MultipartFile> map = multirequest.getMultiFileMap();
                    Set<String> keys = map.keySet();
                    for (String key : keys) {
                        JSONObject jobj = new JSONObject();
                        String path = "";

                        path = request.getSession().getServletContext().getRealPath("/") + "photo/device/"+device.getId()+"/";
                        File add = new File(path);
                        if (!add.exists() && !add.isDirectory()) {
                            add.mkdirs();
                        }

                        List<MultipartFile> files = map.get(key);
                        if (null != files && files.size() > 0) {
                            MultipartFile file = files.get(0);
                            String fileName  = file.getOriginalFilename();
                            Date date=new Date();
                            fileName=String.valueOf(date.getTime());
//                        String fileName = UUID.randomUUID().toString() + ".jpg";
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

                            device.setPhoto("/photo/device/" +device.getId()+"/"+ fileName);
//                    userRepository.save(user);
                        }
                    }
                }catch (ClassCastException e){
                    e.printStackTrace();
                }
            }else {
                System.out.println("没有上传图片pic："+pic);
            }

            deviceRepository.save(device);
            device.getRoom().setDeviceNum(device.getRoom().getDeviceNum()+1);
            roomRepository.save(device.getRoom());
            device.getRoom().getFloor().setDeviceNum(device.getRoom().getFloor().getDeviceNum()+1);
            storeyRepository.save(device.getRoom().getFloor());
            device.getRoom().getFloor().getBuild().setDeviceNum(device.getRoom().getFloor().getBuild().getDeviceNum()+1);
            buildingRepository.save(device.getRoom().getFloor().getBuild());
            restResponse = new RestResponse("操作成功！",new RestDevice(device));
        }else {
            restResponse = new RestResponse("权限不足！",1005,null);
        }
        out.print(JSON.toJSONString(restResponse));
        out.flush();
        out.close();
    }

    @RequestMapping(value = "/create/room")
    public void createRoom(Principal principal,@RequestParam Map<String,String> param,
                           HttpServletRequest request,HttpServletResponse response)
            throws ServletException, IOException,SerialException{
        User user = judgeByPrincipal(principal);
        RestResponse restResponse = null;
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        if (null == user)
            restResponse = new RestResponse("手机号出错！", 1005,null);
        else if(null==param.get("floorId")){
            restResponse = new RestResponse("楼层信息出错！", 1005,null);
        }
        else if (UserRoleDifferent.userFirmManagerConfirm(user)) {

            Room room = new Room();

            //wkj添加
            boolean b=false;
            List<Room> list=new ArrayList<Room>();
            //wkj添加  获取词楼的所有层
            list=roomRepository.findByFloorId(Integer.valueOf(param.get("floorId")));
            if (null!=param.get("type")&&null!=param.get("floorId")&&param.get("type").equals("1")){
                room = roomRepository.findOne(Integer.valueOf(param.get("roomId")));
                //wkj添加 修改层信息 层的名称也修改
                if (!room.getName().equals(param.get("name"))){
                    if (list!=null&&list.size()>0){
                        for (Room room1:list){
                            if (room1.getName()!=null&&!"".equals(room1.getName())&&room1.getName().equals(param.get("name"))){
                                b=true;
                                break;
                            }
                        }
                    }
                }
            }else {
                room.setCreateDate(new Date());
                room.setDeviceNum(0);
                if (list!=null&&list.size()>0){
                    for (Room room2:list){
                        if (room2.getName()!=null&&!"".equals(room2.getName())&&room2.getName().equals(param.get("name"))){
                            b=true;
                            break;
                        }
                    }
                }
            }

            if (b){
                restResponse=new RestResponse("该室名称已经存在",1005,null);
            }else {
                room.setFloor(null == param.get("floorId") ? null : storeyRepository.findOne(Integer.valueOf(param.get("floorId"))));
                room.setName(null == param.get("name") ? null : param.get("name"));
                room.setxPoint(null == param.get("xpoint") ? null : Float.valueOf(param.get("xpoint")));
                room.setyPoint(null == param.get("ypoint") ? null : Float.valueOf(param.get("ypoint")));
                room.setEnable(1);
                roomRepository.save(room);
                String pic=param.get("pic");
                System.out.println("pic："+pic);
                if (pic.equals("0")){
                    System.out.println("上传图片pic："+pic);
                    try {
                        MultipartHttpServletRequest multirequest = (MultipartHttpServletRequest) request;
                        MultiValueMap<String, MultipartFile> map = multirequest.getMultiFileMap();
                        Set<String> keys = map.keySet();
                        for (String key : keys) {
                            JSONObject jobj = new JSONObject();
                            String path = "";

                            path = request.getSession().getServletContext().getRealPath("/") + "photo/company/room/"+room.getId()+"/";
                            File add = new File(path);
                            if (!add.exists() && !add.isDirectory()) {
                                add.mkdirs();
                            }

                            List<MultipartFile> files = map.get(key);
                            if (null != files && files.size() > 0) {
                                MultipartFile file = files.get(0);
                                String fileName  = file.getOriginalFilename();
                                Date date=new Date();
                                fileName=String.valueOf(date.getTime());
//                        String fileName = UUID.randomUUID().toString() + ".jpg";
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

                                room.setBackground("/photo/company/room/"+room.getId() +"/"+ fileName);
                            }
//                    restResponse = new RestResponse("添加成功！",null);
                        }
                    }catch (ClassCastException e){
                        e.printStackTrace();
                    }
                }else {
                    System.out.println("没有上传图片pic："+pic);
                }


                roomRepository.save(room);
                restResponse = new RestResponse("操作成功！",new RestRoom(room));
            }
//            room.setFloor(null == param.get("floorId") ? null : storeyRepository.findOne(Integer.valueOf(param.get("floorId"))));
//            room.setName(null == param.get("name") ? null : param.get("name"));
//            room.setxPoint(null == param.get("xpoint") ? null : Float.valueOf(param.get("xpoint")));
//            room.setyPoint(null == param.get("ypoint") ? null : Float.valueOf(param.get("ypoint")));
//            room.setEnable(1);
//            roomRepository.save(room);
//            try {
//                MultipartHttpServletRequest multirequest = (MultipartHttpServletRequest) request;
//                MultiValueMap<String, MultipartFile> map = multirequest.getMultiFileMap();
//                Set<String> keys = map.keySet();
//                for (String key : keys) {
//                    JSONObject jobj = new JSONObject();
//                    String path = "";
//
//                    path = request.getSession().getServletContext().getRealPath("/") + "photo/company/room/"+room.getId()+"/";
//                    File add = new File(path);
//                    if (!add.exists() && !add.isDirectory()) {
//                        add.mkdirs();
//                    }
//
//                    List<MultipartFile> files = map.get(key);
//                    if (null != files && files.size() > 0) {
//                        MultipartFile file = files.get(0);
//                        String fileName  = file.getOriginalFilename();
////                        String fileName = UUID.randomUUID().toString() + ".jpg";
//                        InputStream is = file.getInputStream();
//                        File f = new File(path + fileName);
//                        FileOutputStream fos = new FileOutputStream(f);
//                        int hasRead = 0;
//                        byte[] buf = new byte[1024];
//                        while ((hasRead = is.read(buf)) > 0) {
//                            fos.write(buf, 0, hasRead);
//                        }
//                        fos.close();
//                        is.close();
//
//                        room.setBackground("/photo/company/room/"+room.getId() +"/"+ fileName);
//                    }
////                    restResponse = new RestResponse("添加成功！",null);
//                }
//            }catch (ClassCastException e){
//                e.printStackTrace();
//            }
//
//            roomRepository.save(room);
//            restResponse = new RestResponse("操作成功！",new RestRoom(room));
        } else {
            restResponse = new RestResponse("权限不足！",1005,null);
        }
        out.print(JSON.toJSONString(restResponse));
        out.flush();
        out.close();
    }

    @RequestMapping(value = "/upload/deviceType/icon/{deviceTypeId}")
    public void createDeviceType(@PathVariable Integer deviceTypeId,
                                 HttpServletRequest request,HttpServletResponse response)
            throws ServletException, IOException,SerialException{
        RestResponse restResponse = null;
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        DeviceType deviceType = deviceTypeRepository.findOne(deviceTypeId);
        if (null==deviceType)
            restResponse = new RestResponse("设备类型不存在！",1005,null);
        else {
            try {
                MultipartHttpServletRequest multirequest = (MultipartHttpServletRequest) request;
                MultiValueMap<String, MultipartFile> map = multirequest.getMultiFileMap();
                Set<String> keys = map.keySet();
                for (String key : keys) {
                    JSONObject jobj = new JSONObject();
                    String path = "";

                    path = request.getSession().getServletContext().getRealPath("/") + "photo/company/deviceType/"+deviceType.getId()+"/";
                    File add = new File(path);
                    if (!add.exists() && !add.isDirectory()) {
                        add.mkdirs();
                    }

                    List<MultipartFile> files = map.get(key);
                    if (null != files && files.size() > 0) {
                        MultipartFile file = files.get(0);
                        String fileName  = file.getOriginalFilename();
                        Date date=new Date();
                        fileName=String.valueOf(date.getTime());
//                        String fileName = UUID.randomUUID().toString() + ".jpg";
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

                        deviceType.setLogo("/photo/company/deviceType/"+deviceType.getId() +"/"+ fileName);
                    }
                }
                deviceTypeRepository.save(deviceType);
            }catch (ClassCastException e){
                e.printStackTrace();
//                deviceType.setLogo("/photo/company/" + fileName);
            }

            restResponse = new RestResponse("操作成功！",new RestDeviceType(deviceType));
        }

        out.print(JSON.toJSONString(restResponse));
        out.flush();
        out.close();
    }

    @RequestMapping(value = "/change/picture/{deviceId}")
    public void uploadPhoto(@PathVariable Integer deviceId, HttpServletRequest request,HttpServletResponse response)
            throws ServletException, IOException,SerialException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        RestResponse restResponse = null;

        Device device = deviceRepository.findOne(deviceId);
        if (null==device){
            restResponse = new RestResponse("当前设备有误！",1005,null);
        }else {
            MultipartHttpServletRequest multirequest = (MultipartHttpServletRequest) request;

            MultiValueMap<String, MultipartFile> map = multirequest.getMultiFileMap();
            Set<String> keys = map.keySet();
            List<String> result = new ArrayList<String>();
            for (String key : keys) {
                JSONObject jobj = new JSONObject();
                String path = "";
                path = request.getSession().getServletContext().getRealPath("/") + "photo/device/"+device.getId()+"/";
                File add = new File(path);
                if (!add.exists() && !add.isDirectory()) {
                    add.mkdirs();
                }

                List<MultipartFile> files = map.get(key);
                if (null != files && files.size() > 0) {
                    MultipartFile file = files.get(0);
                    String fileName  = file.getOriginalFilename();
                    Date date=new Date();
                    fileName=String.valueOf(date.getTime());
//                    String fileName = UUID.randomUUID().toString() + ".jpg";
                    if (null==fileName||fileName.equals(""))
                        break;
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
                    device.setPhoto("/photo/device/"+device.getId()+"/"+fileName);
                    deviceRepository.save(device);
                }
            }
            out.print(JSON.toJSONString(new RestResponse("图片上传成功！", 0, new RestDevice(device))));
            out.flush();
            out.close();
        }
    }

    @RequestMapping(value = "/create/company")
    public void createCompany(Principal principal,@RequestParam Map<String,String> param,
                                 HttpServletRequest request,HttpServletResponse response)
            throws ServletException, IOException,SerialException{
        User user = judgeByPrincipal(principal);
        RestResponse restResponse = null;
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        if (null == user)
            restResponse = new RestResponse("手机号出错！",1005, null);

        Company company = null;
        List<DeviceTypeInspect> deviceTypeInspects = new ArrayList<DeviceTypeInspect>();
        if (UserRoleDifferent.userServiceWorkerConfirm(user)||
                UserRoleDifferent.userServiceManagerConfirm(user) ) {
            User firmManager = null;
            //新增
            if (null==param.get("id")||param.get("id").equals("")){
                company = new Company();
                company.setCreateDate(new Date());
                company.setBusinessMan(user);
                if (null==param.get("name")||"".equals(param.get("name"))||null==param.get("account")||"".equals(param.get("account"))) {
                    throw new RuntimeException("企业名不能为空");
//                    restResponse=new RestResponse("企业名不能为空",1005,null);
//                    out.print(JSON.toJSONString(restResponse));
//                    out.flush();
//                    out.close();
//                    return;
                }
                //企业名称不能相同
                List<Company> list=companyRepository.findAll();
                if (list!=null&&list.size()>0){
                    for (Company company1:list){
                        if (company1.getName()!=null&&!"".equals(company1.getName())&&param.get("name").equals(company1.getName()))
                            throw new RuntimeException("企业名称不能相同");
                    }
                }
                company.setName(param.get("name"));

                company.setAddress(param.get("address"));
                company.setEmail(param.get("email"));
                company.setTelephone(param.get("telephone"));
                if (null!=param.get("location")){
                    String[] location = param.get("location").split(",");
                    if (location.length==2){
                        company.setLng(Float.valueOf(location[0]));
                        company.setLat(Float.valueOf(location[1]));
                    }
                }
                company.setEnable(1);
                firmManager = userRepository.findByName(param.get("account"));
                if (null!=firmManager) {
                    throw new RuntimeException("创建失败，管理员账号已存在！");
//                    restResponse=new RestResponse("创建失败，管理员账号已存在！",1005,null);
//                    out.print(JSON.toJSONString(restResponse));
//                    out.flush();
//                    out.close();
//                    return;
                }

                company=companyRepository.save(company);
                firmManager = new User();
                firmManager.setName(param.get("account"));
                firmManager.setPassword(null==param.get("password")?"123":param.get("password"));
                firmManager.setUserName(param.get("userName"));
                firmManager.setCompany(company);
                firmManager.setCreateDate(new Date());
                firmManager.setRemoveAlert("0");
                userRepository.save(firmManager);
                RoleAuthority roleAuthority = roleAuthorityRepository.findByName("FIRM_MANAGER");
                Role role = new Role();
                role.setAuthority(roleAuthority.getName());
                role.setRoleAuthority(roleAuthority);
                role.setUser(firmManager);
                roleRepository.save(role);
                company.setManager(firmManager);

                //给公司添加url
                company.setLogin(SERVICE_PATH+"/inspect/Lab_login.html?company="+
                        ByteAndHex.convertMD5(URLEncoder.encode(company.getId().toString(),"UTF-8")));
                //设置公司的companyId
                company.setCompanyId(company.getLogin().substring(company.getLogin().indexOf("=")+1));
                //给管理员账号加密
                firmManager.setName(param.get("account")+"@"+company.getCompanyId());

                userRepository.save(firmManager);
            }else {
                company = companyRepository.findOne(Integer.valueOf(param.get("id")));
                if (null==param.get("name")||"".equals(param.get("name"))||null==param.get("account")||"".equals(param.get("account"))) {
                    throw new RuntimeException("企业名不能为空");
//                    restResponse=new RestResponse("企业名不能为空",1005,null);
//                    out.print(JSON.toJSONString(restResponse));
//                    out.flush();
//                    out.close();
//                    return;
                }
                //企业名称不能相同
                List<Company> list=companyRepository.findAll();
                if (list!=null&&list.size()>0){
                    for (Company company1:list){
                        if (company1.getName()!=null&&!"".equals(company1.getName())&&!company1.getId().equals(company.getId())&&param.get("name").equals(company1.getName())) {
                            throw new RuntimeException("企业名称不能相同");
//                            restResponse=new RestResponse("企业名称不能相同",1005,null);
//                            out.print(JSON.toJSONString(restResponse));
//                            out.flush();
//                            out.close();
//                            return;
                        }
                    }
                }
                company.setName(param.get("name"));

                company.setAddress(param.get("address"));
                company.setEmail(param.get("email"));
                company.setTelephone(param.get("telephone"));
                if (null!=param.get("location")){
                    String[] location = param.get("location").split(",");
                    if (location.length==2){
                        company.setLng(Float.valueOf(location[0]));
                        company.setLat(Float.valueOf(location[1]));
                    }
                }
                firmManager = userRepository.findByName(param.get("account")+"@"+company.getCompanyId());
                if (null == firmManager) {
                    throw new RuntimeException("修改失败，管理员账号不存在！");
//                    restResponse=new RestResponse("修改失败，管理员账号不存在！",1005,null);
//                    out.print(JSON.toJSONString(restResponse));
//                    out.flush();
//                    out.close();
//                    return;
                }
                company=companyRepository.save(company);
                firmManager = company.getManager();
//                firmManager.setName(param.get("account"));
                firmManager.setPassword(null==param.get("password")?"123":param.get("password"));
                firmManager.setUserName(param.get("userName"));
                firmManager.setCompany(company);
                firmManager.setCreateDate(new Date());
                userRepository.save(firmManager);
            }

            String pic=param.get("pic");
            System.out.println("pic："+pic);
            if (pic.equals("0")){
                System.out.println("上传图片");
                try {
                    MultipartHttpServletRequest multirequest = (MultipartHttpServletRequest) request;
                    MultiValueMap<String, MultipartFile> map = multirequest.getMultiFileMap();
                    Set<String> keys = map.keySet();
                    for (String key : keys) {
                        JSONObject jobj = new JSONObject();
                        String path = "";

                        path = request.getSession().getServletContext().getRealPath("/") + "photo/company/"+company.getId()+"/";
                        File add = new File(path);
                        if (!add.exists() && !add.isDirectory()) {
                            add.mkdirs();
                        }

                        List<MultipartFile> files = map.get(key);
                        if (null != files && files.size() > 0) {
                            MultipartFile file = files.get(0);
                            String fileName  = file.getOriginalFilename();
                            if (null==fileName||fileName.equals(""))
                                break;
//                        String fileName = UUID.randomUUID().toString() + ".jpg";
                            Date date=new Date();
                            fileName=String.valueOf(date.getTime());
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

                            company.setBackground("/photo/company/" +company.getId()+"/"+ fileName);
                        }
                    }
                }catch (ClassCastException e){
                    e.printStackTrace();
//                deviceType.setLogo("/photo/company/" + fileName);
                }
            }else {
                System.out.println("没有上传图片");
            }

            companyRepository.save(company);
            restResponse = new RestResponse("操作成功！",new RestCompany(company));
        } else {
            restResponse = new RestResponse("权限不足！",1005,null);
        }
        out.print(JSON.toJSONString(restResponse));
        out.flush();
        out.close();
    }

    @RequestMapping(value = "/upload/device/file/{deviceId}")
    public void uploadDeviceFile(@PathVariable Integer deviceId, HttpServletRequest request,HttpServletResponse response)
            throws ServletException, IOException,SerialException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        RestResponse restResponse = null;

        Device device = deviceRepository.findOne(deviceId);
        if (null==device){
            restResponse = new RestResponse("当前设备有误！",1005,null);
        }else {
            Files createFile = new Files();
            createFile.setCreateDate(new Date());
            createFile.setEnable(1);

            MultipartHttpServletRequest multirequest = (MultipartHttpServletRequest) request;

            MultiValueMap<String, MultipartFile> map = multirequest.getMultiFileMap();
            Set<String> keys = map.keySet();
            List<String> result = new ArrayList<String>();
            for (String key : keys) {
                JSONObject jobj = new JSONObject();
                String path = "";
                path = request.getSession().getServletContext().getRealPath("/") + "photo/file/"+device.getId()+"/";
                File add = new File(path);
                if (!add.exists() && !add.isDirectory()) {
                    add.mkdirs();
                }

                List<MultipartFile> files = map.get(key);
                if (null != files && files.size() > 0) {
                    MultipartFile file = files.get(0);
                    String fileName  = file.getOriginalFilename();
//                    String fileName = UUID.randomUUID().toString() + ".jpg";
                    if (null==fileName||fileName.equals(""))
                        break;
//                    Date date=new Date();
//                    fileName=String.valueOf(date.getTime());
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
                    createFile.setName(fileName);
                    createFile.setUrl("/photo/file/" + device.getId() + "/" + fileName);
                    fileRepository.save(createFile);
                    DeviceFile deviceFile = new DeviceFile();
                    deviceFile.setDevice(device);
                    deviceFile.setFile(createFile);
                    deviceFileRepository.save(deviceFile);
                }
            }
            out.print(JSON.toJSONString(new RestResponse("文件上传成功！", 0, new RestDevice(device))));
            out.flush();
            out.close();
        }
    }


    @RequestMapping(value="/logo/company")
    public void uploadLogo(Principal principal,HttpServletRequest request,HttpServletResponse response)
            throws ServletException, IOException,SerialException {
        User user = judgeByPrincipal(principal);
        response.setContentType("text/html");
        PrintWriter out=response.getWriter();
        RestResponse restResponse=null;
        if(UserRoleDifferent.userFirmManagerConfirm(user)){
            Company company=user.getCompany();
            if (company==null) {
                restResponse=new RestResponse("公司不存在",1005,null);
            }else {
                MultipartHttpServletRequest multipartHttpServletRequest=(MultipartHttpServletRequest ) request;
                MultiValueMap<String,MultipartFile> map=multipartHttpServletRequest.getMultiFileMap();
                Set<String> keys = map.keySet();
                List<String> result=new ArrayList<String>();
                for (String key:keys){
                    JSONObject jsonObject=new JSONObject();
                    String path="";
                    path=request.getSession().getServletContext().getRealPath("/")+"photo/company/"+company.getId()+"/";
                    File add=new File(path);
                    if (!add.exists()&&!add.isDirectory()){
                        add.mkdirs();
                    }
                    List<MultipartFile> files=map.get(key);
                    if (null!=files&&files.size()>0){
                        MultipartFile file=files.get(0);
                        String fileName=file.getOriginalFilename();
                        Date date=new Date();
                        fileName=String.valueOf(date.getTime());
                        InputStream is=file.getInputStream();
                        File f=new File(path+fileName);
                        FileOutputStream fos=new FileOutputStream(f);
                        int hasRead = 0;
                        byte[] buf = new byte[1024];
                        while ((hasRead=is.read(buf))>0){
                            fos.write(buf, 0, hasRead);
                        }
                        fos.close();
                        is.close();
                        company.setLogo("/photo/company/"+company.getId()+"/"+fileName);
                        companyRepository.save(company);
                    }
                }
                out.print(JSON.toJSONString(new RestResponse("图片上传成功！", 0, new RestCompany(company))));
            }
        }else {
            new RestResponse("用户权限不足，无法上传公司logo!",1005,null);
        }
        out.flush();
        out.close();
    }

    //硬件版本更新
    @RequestMapping(value="/create/device/version")
    public void updateVersion(Principal principal,@RequestParam Map<String ,String> param,
                                      HttpServletRequest request,HttpServletResponse response) throws IOException {
        //判断是否登陆
        User user=judgeByPrincipal(principal);
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        try {
            //平台管理员的判定
            if (UserRoleDifferent.userServiceManagerConfirm(user)){
                DeviceVersion deviceVersion=new DeviceVersion();
                //判定param是否是空
                if (param!=null){
                    //分别判断每个参数是否是空
                    if (param.get("name")!=null){
                        deviceVersion.setName(param.get("name"));
                    }else {
                        out.print(JSON.toJSONString(new RestResponse("设备版本的名称为空", 1005,null)));
                        return;
                    }
                    if (param.get("code_first")!=null){
                        deviceVersion.setFirstCode(param.get("code_first"));
                    }else {
                        out.print(JSON.toJSONString(new RestResponse("版本号1为空", 1005,null)));
                        return;
                    }
                    if (param.get("code_second")!=null){
                        deviceVersion.setSecondCode(param.get("code_second"));
                    }else {
                        out.print(JSON.toJSONString(new RestResponse("版本号2为空", 1005,null)));
                        return;
                    }
                    if (param.get("code_third")!=null){
                        deviceVersion.setThirdCode(param.get("code_third"));
                    }else {
                        out.print(JSON.toJSONString(new RestResponse("版本号3为空", 1005,null)));
                        return;
                    }
                    if (param.get("code_forth")!=null){
                        deviceVersion.setForthCode(param.get("code_forth"));
                    }else {
                        out.print(JSON.toJSONString(new RestResponse("版本号4为空", 1005,null)));
                        return;
                    }
                    if (param.get("type")!=null){
                        if (param.get("type").equals("01")){
                            deviceVersion.setType(param.get("type"));
                            //立即更新
                        }else if (param.get("type").equals("02")){
                            deviceVersion.setType(param.get("type"));
                            //硬件自己判定更新
                        }else {
                            out.print(JSON.toJSONString(new RestResponse("更新机制不正确！", 1005,null)));
                            return;
                        }
                    }else {
                        out.print(JSON.toJSONString(new RestResponse("版本更新机制为空", 1005,null)));
                        return;
                    }
                    deviceVersion.setCreateDate(new Date());
                    uploadVersionFile(deviceVersion, request, response);
                    String  suffix= deviceVersion.getUrl();
                    System.out.println("suffix："+suffix);
                    if (!suffix.endsWith(".tar.gz")){
                        out.print(JSON.toJSONString(new RestResponse("文件格式不正确，请上传以.tar.gz为后缀的文件",1005,null )));
                        out.flush();
                        out.close();
                        return;
                    }
                    if (param.get("message")!=null&&!"".equals(param.get("message")))
                        deviceVersion.setMessage(param.get("message"));
                    //保存到数据库
                    deviceVersion=deviceVersionRepository.save(deviceVersion);
                    //上传文件
//                    uploadVersionFile(deviceVersion, request, response);
//                    //设置路径
//                    String  suffix= deviceVersion.getUrl();
//                    System.out.println();
//                    //更新数据库
//                    deviceVersionRepository.save(deviceVersion);
                    out.print(JSON.toJSONString(new RestResponse("版本更新文件上传成功!", 0)));
                }else {
                    out.print(JSON.toJSONString(new RestResponse("硬件版本参数为空", 0)));
                }
            }else {
                out.print(JSON.toJSONString(new RestResponse("用户权限不足，无法上传文件!", 1005,null)));
            }

        }catch (Exception e){
            e.printStackTrace();
            out.print(JSON.toJSONString(new RestResponse("文件上传失败，无法更新", 1005,null)));
        }
        out.flush();
        out.close();
    }


    public void uploadVersionFile(DeviceVersion deviceVersion,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        PrintWriter out=response.getWriter();
        RestResponse restResponse=null;
        MultipartHttpServletRequest multipartHttpServletRequest=(MultipartHttpServletRequest ) request;
        MultiValueMap<String,MultipartFile> map=multipartHttpServletRequest.getMultiFileMap();
        Set<String> keys = map.keySet();
        List<String> result=new ArrayList<String>();
        for (String key:keys){
            JSONObject jsonObject=new JSONObject();
            String path="";
            path=request.getSession().getServletContext().getRealPath("/")+"photo/version/"+deviceVersion.getId()+"/";
            File add=new File(path);
            if (!add.exists()&&!add.isDirectory()){
                add.mkdirs();
            }
            List<MultipartFile> files=map.get(key);
            if (null!=files&&files.size()>0){
                MultipartFile file=files.get(0);
                String fileName=file.getOriginalFilename();
                InputStream is=file.getInputStream();
                File f=new File(path+fileName);
                FileOutputStream fos=new FileOutputStream(f);
                int hasRead = 0;
                byte[] buf = new byte[1024];
                while ((hasRead=is.read(buf))>0){
                    fos.write(buf, 0, hasRead);
                }
                fos.close();
                is.close();
                deviceVersion.setUrl("/photo/version/"+deviceVersion.getId()+"/"+fileName);
                deviceVersion.setFileName(fileName);
            }
        }
     //        out.print(JSON.toJSONString(new RestResponse("版本文件上传成功！", 0)));
    }


}
