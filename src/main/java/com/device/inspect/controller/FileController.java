package com.device.inspect.controller;

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
import com.device.inspect.common.model.record.DeviceDisableTime;
import com.device.inspect.common.repository.charater.RoleAuthorityRepository;
import com.device.inspect.common.repository.charater.RoleRepository;
import com.device.inspect.common.repository.charater.UserRepository;
import com.device.inspect.common.repository.device.*;
import com.device.inspect.common.repository.firm.BuildingRepository;
import com.device.inspect.common.repository.firm.CompanyRepository;
import com.device.inspect.common.repository.firm.RoomRepository;
import com.device.inspect.common.repository.firm.StoreyRepository;
import com.device.inspect.common.repository.record.DeviceDisableTimeRepository;
import com.device.inspect.common.restful.RestResponse;
import com.device.inspect.common.restful.charater.RestUser;
import com.device.inspect.common.restful.device.RestDevice;
import com.device.inspect.common.restful.device.RestDeviceType;
import com.device.inspect.common.restful.firm.RestBuilding;
import com.device.inspect.common.restful.firm.RestCompany;
import com.device.inspect.common.restful.firm.RestFloor;
import com.device.inspect.common.restful.firm.RestRoom;
import com.device.inspect.common.restful.record.BlockChainDevice;
import com.device.inspect.common.restful.record.BlockChainDeviceRecord;
import com.device.inspect.common.service.InitWallet;
import com.device.inspect.common.service.OnchainService;
import com.device.inspect.common.util.transefer.ByteAndHex;
import com.device.inspect.common.util.transefer.UserRoleDifferent;
import com.device.inspect.Application;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final String SERVICE_PATH = "http://ilabservice.chinaeast.cloudapp.chinacloudapi.cn";

    private static final Logger LOGGER1 = LogManager.getLogger(OperateController.class);

    protected static Logger logger = LogManager.getLogger();

    @Autowired
    private OnchainService onchainService;

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

    @Autowired
    private  DeviceDisableTimeRepository deviceDisableTimeRepository;

    private User judgeByPrincipal(Principal principal){
        if (null == principal||null==principal.getName())
            throw new UsernameNotFoundException("You are not login!");
        User user = userRepository.findByName(principal.getName());
        if (null==user)
            throw new UsernameNotFoundException("user not found!");
        return user;
    }

    /**
     * 企业管理员创建楼
     * @param 
     * @param param         type 0位新增，1为修改
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     * @throws SerialException
     */
    @RequestMapping(value = "/create/building"  )
        public RestResponse createBuilding(Principal principal,@RequestParam Map<String,String> param,
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
                    logger.info(String.format("Updating building, name=%s, lng=%s, lat=%s",
                            param.get("name"), param.get("xpoint"), param.get("ypoint")));

                    String pic=param.get("pic");

                    boolean fileUploadSuccess = true;
                    if (pic.equals("0")){
                        logger.info("Uploading building picture");

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

                                    String companyContainerName = String.format("company%s", company.getId());
                                    String blobName = String.format("buildings/%s/%s", building.getId(), UUID.randomUUID().toString());
                                    String photoUrl = Application.intelabStorageManager.uploadFile(file, companyContainerName, blobName, building.getBackground());
                                    if (photoUrl != null) {
                                        building.setBackground(photoUrl);
                                    } else {
                                        logger.error(String.format("Storage return null for file %s", fileName));
                                    }
                                }


                            }
                        }catch (ClassCastException e){
                            e.printStackTrace();

                            fileUploadSuccess = false;
                        }
                    }else {
                        System.out.println("没有上传图片");
                    }

                    if(fileUploadSuccess){
                        buildingRepository.save(building);
                        restResponse = new RestResponse("操作成功！",new RestBuilding(building));
                    }else{
                        restResponse = new RestResponse("Upload picture failed.", RestResponse.ERROR_STORAGE, null);

                    }
                }

            } else {
                restResponse = new RestResponse("权限不足！",1005,null);
            }
        }

        out.print(JSON.toJSONString(restResponse));
        out.flush();
        out.close();

        return restResponse;

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
    public RestResponse createFloor(Principal principal,@RequestParam Map<String,String> param,
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
                //用来判断是否上传图片  0是上传图片  1是没有上传图片
                String pic=param.get("pic");
                System.out.println("pic："+pic);
                boolean fileUploadSuccess = true;
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

                                String companyContainerName = String.format("company%s", user.getCompany().getId());
                                String blobName = String.format("floors/%s/%s", floor.getId(), UUID.randomUUID().toString());
                                String photoUrl = Application.intelabStorageManager.uploadFile(file, companyContainerName, blobName, floor.getBackground());
                                if (photoUrl != null) {
                                    floor.setBackground(photoUrl);
                                } else {
                                    logger.error(String.format("Storage return null for file %s", fileName));
                                }

                            }
                        }
                    }catch (ClassCastException e){
                        e.printStackTrace();
                        fileUploadSuccess = false;

                    }
                }else {
                   logger.info("没有上传图片："+pic);
                }

                if(fileUploadSuccess) {
                    storeyRepository.save(floor);
                    restResponse = new RestResponse("操作成功！", new RestFloor(floor));
                }else{
                    restResponse = new RestResponse("Upload picture failed.", RestResponse.ERROR_STORAGE, null);
                }
            }

        } else {
            restResponse = new RestResponse("权限不足！",1005,null);
        }
        out.print(JSON.toJSONString(restResponse));
        out.flush();
        out.close();

        return restResponse;
    }

    /**
     * 企业管理员添加设备
     * @param principal
     * @param param     设备的信息
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     * @throws SerialException
     */
    @RequestMapping(value = "/create/device")
    public RestResponse createDevice(Principal principal,@RequestParam Map<String,String> param,
                             HttpServletRequest request,HttpServletResponse response)
            throws ServletException, IOException,SerialException{

        User user = judgeByPrincipal(principal);
        RestResponse restResponse = null;
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        param.put("code", java.net.URLDecoder.decode(param.get("code"),"UTF-8"));
        param.put("name", java.net.URLDecoder.decode(param.get("name"),"UTF-8"));
        param.put("monitorCode", java.net.URLDecoder.decode(param.get("monitorCode"),"UTF-8"));
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
	    logger.info("Create device: monitor code " + param.get("monitorCode"));
            monitorDevice = monitorDeviceRepository.findByNumber(param.get("monitorCode"));
            logger.info("Create device: monitor code " + param.get("monitorCode"));
	    if (null!=monitorDevice){
                throw new RuntimeException("终端编号已存在，无法添加！");
	    }
	    logger.info("Create device: begin setting basic info.");
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

            String pic=param.get("pic");
            System.out.println("pic："+pic);
            boolean fileUploadSuccess = true;
            if (pic.equals("0")){
                System.out.println("上传图片pic："+pic);
                try {
                    MultipartHttpServletRequest multirequest = (MultipartHttpServletRequest) request;
                    MultiValueMap<String, MultipartFile> map = multirequest.getMultiFileMap();
                    Set<String> keys = map.keySet();
                    for (String key : keys) {
                        List<MultipartFile> files = map.get(key);
                        if (null != files && files.size() > 0) {
                            MultipartFile file = files.get(0);
                            String fileName  = file.getOriginalFilename();

                            logger.info(String.format("uploading file %s", fileName));

                            String containerName = String.format("company%s", user.getCompany().getId());

                            String blobName = String.format("devices/%s/%s", device.getId(), UUID.randomUUID().toString());

                            String photoUrl = Application.intelabStorageManager.uploadFile(file, containerName, blobName, device.getPhoto());

                            if(photoUrl != null){
                                device.setPhoto(photoUrl);
                                logger.info(String.format("successfully upload file %s to blob storage at %s", fileName, photoUrl));
                            }else{
                                logger.error(String.format("Storage return null for file %s", fileName));
                            }

                        }
                    }
                }catch (ClassCastException e){
                    e.printStackTrace();
                    fileUploadSuccess = false;
                }
            }else {
                logger.info("没有上传图片pic："+pic);
            }

            if(!fileUploadSuccess) {
                restResponse = new RestResponse( "File upload failed", RestResponse.ERROR_STORAGE, null);

            }else{
                if(param.get("serialNo") == null){
                    device.setSerialNo("ilabservice");
                }
                else{
                    device.setSerialNo(param.get("serialNo"));
                }
                if (param.get("model") == null || param.get("model").equals("")) {
                    device.setModel("iLabService");
                } else {
                    device.setModel(param.get("model"));
                }
                if(param.get("purchase") != null){
                    device.setPurchase(new Date(Long.parseLong(param.get("purchase"))));
                }
                if(param.get("maintainDate") != null){
                    device.setMaintainDate(new Date(Long.parseLong(param.get("maintainDate"))));
                }
                device.setMaintain(param.get("maintain"));
                device.setxPoint(null == param.get("xPoint") ? 0 : Float.valueOf(param.get("xPoint")));
                device.setyPoint(null == param.get("yPoint") ? 0 : Float.valueOf(param.get("yPoint")));
                device.setName(param.get("name"));
                device.setRoom(room);
                device.setPushInterval(null == param.get("pushInterval")?30:Integer.valueOf(param.get("pushInterval")));
                device.setEnable(1);
                device.setEnableSharing(Integer.parseInt(param.get("enableSharing")));
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

                if (null!=param.get("enableSharing")) {
                    Integer enableSharing = Integer.parseInt(param.get("enableSharing"));
                    device.setEnableSharing(enableSharing);
                }
                if (null!=param.get("rentClause"))
                    device.setRentClause(param.get("rentClause"));
                if (null!=param.get("rentPrice"))
                    device.setRentPrice(Double.parseDouble(param.get("rentPrice")));

                deviceRepository.save(device);
                monitorDevice = new MonitorDevice();
                monitorDevice.setBattery("100");
                monitorDevice.setDevice(device);
                monitorDevice.setNumber(param.get("monitorCode"));
                monitorDevice.setOnline(1);
                monitorDeviceRepository.save(monitorDevice);
                logger.info("Create Device: finish adding basic infomation.");
                if (null!=param.get("scientist")) {
                    String[] scientist = param.get("scientist").split(",");
                    for (String id:scientist){
                        if (null!=id&&!"".equals(id)){
                            ScientistDevice scientistDevice = null;
                            User keeper = userRepository.findOne(Integer.valueOf(id));
                            if (null==keeper)
                                continue;
                            scientistDevice = scientistDeviceRepository.findTopByScientistIdAndDeviceId(keeper.getId(),device.getId());
                            if (null!=scientistDevice)
                                continue;
                            scientistDevice = new ScientistDevice();
                            scientistDevice.setDevice(device);
                            scientistDevice.setScientist(keeper);
                            scientistDeviceRepository.save(scientistDevice);
                        }
                    }
                }
                logger.info("Create Device: finish adding scientist infomation.");

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
                        deviceInspect.setInspectPurpose(deviceTypeInspect.getInspectPurpose());
                        deviceInspect.setZero(0f);
                        deviceInspect.setOriginalValue(0f);
                        deviceInspect.setCorrectionValue(0f);
                        deviceInspectRepository.save(deviceInspect);
                    }
                }
                logger.info("Create Device: finish adding device type inspect information.");

                device = deviceRepository.findByCode(device.getCode());
                device.setDeviceChainKey(""+device.getId());

                BlockChainDevice data = new BlockChainDevice(device, null);
                BlockChainDeviceRecord value = new BlockChainDeviceRecord("Device Register", data);
                try {
                    onchainService.sendStateUpdateTx("device", device.getDeviceChainKey(), "", JSON.toJSONString(value));
                }catch(Exception e){
                    logger.error(e.getMessage());
                }

                deviceRepository.save(device);

                if (null!=param.get("enableSharing")) {
                    Integer enableSharing = Integer.parseInt(param.get("enableSharing"));
                    if (enableSharing == 1) {
                        DeviceDisableTime deviceDisableTime = new DeviceDisableTime();
                        deviceDisableTime.setDevice(device);
                        deviceDisableTime.setStrategyType("yearly");
                        deviceDisableTime.setContent("");

                        BlockChainDevice data1 = new BlockChainDevice(deviceDisableTime.getDevice(), deviceDisableTime);
                        data1.setTimeStamp(new Date().getTime());
                        BlockChainDeviceRecord value1 = new BlockChainDeviceRecord("Update Device Rent Time", data1);
                        try {
                            onchainService.sendStateUpdateTx("device", String.valueOf(deviceDisableTime.getDevice()
                                    .getId()), "", JSON.toJSONString(value1));
                        }catch(Exception e){
                            LOGGER1.error(e.getMessage());
                        }

                        deviceDisableTimeRepository.save(deviceDisableTime);
                    }
                }
                device.getRoom().setDeviceNum(device.getRoom().getDeviceNum()+1);
                roomRepository.save(device.getRoom());
                device.getRoom().getFloor().setDeviceNum(device.getRoom().getFloor().getDeviceNum()+1);
                storeyRepository.save(device.getRoom().getFloor());
                device.getRoom().getFloor().getBuild().setDeviceNum(device.getRoom().getFloor().getBuild().getDeviceNum()+1);
                buildingRepository.save(device.getRoom().getFloor().getBuild());
                restResponse = new RestResponse("操作成功！", new RestDevice(device));
            }

        }else {
            restResponse = new RestResponse("权限不足！",1005,null);
        }
        out.print(JSON.toJSONString(restResponse));
        out.flush();
        out.close();
        return restResponse;
    }

    /**
     * 平台管理员去添加室及其信息
     * @param principal
     * @param param  type  0是添加室  1是修改室
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     * @throws SerialException
     */
    @RequestMapping(value = "/create/room")
    public RestResponse createRoom(Principal principal,@RequestParam Map<String,String> param,
                           HttpServletRequest request,HttpServletResponse response)
            throws ServletException, IOException,SerialException{
        User user = judgeByPrincipal(principal);
        RestResponse restResponse = null;
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        if (null == user)
            restResponse = new RestResponse("用户未登陆！", 1005,null);
        else if(null==param.get("floorId")){
            restResponse = new RestResponse("楼层信息出错！", 1005,null);
        }
        else if (!UserRoleDifferent.userFirmManagerConfirm(user)) {
            restResponse = new RestResponse("权限不足！",1005,null);
        }
        else {

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
                String pic=param.get("pic");
                logger.info("pic："+pic);
                boolean fileUploadSuccess = true;
                if (pic.equals("0")){
                    logger.info("上传图片pic："+pic);
                    try {
                        MultipartHttpServletRequest multirequest = (MultipartHttpServletRequest) request;
                        MultiValueMap<String, MultipartFile> map = multirequest.getMultiFileMap();
                        Set<String> keys = map.keySet();
                        for (String key : keys) {

                            List<MultipartFile> files = map.get(key);
                            if (null != files && files.size() > 0) {
                                MultipartFile file = files.get(0);
                                String fileName  = file.getOriginalFilename();

                                logger.info(String.format("uploading file %s", fileName));

                                String containerName = String.format("company%s", user.getCompany().getId());

                                String blobName = String.format("rooms/%s/%s", room.getId(), UUID.randomUUID().toString());

                                String photoUrl = Application.intelabStorageManager.uploadFile(file, containerName, blobName, room.getBackground());

                                if(photoUrl != null){
                                    room.setBackground(photoUrl);
                                    logger.info(String.format("successfully upload file to blob storage at %s", photoUrl));
                                }else{
                                    logger.error(String.format("Storage return null for file %s", fileName));
                                }
                            }

                        }
                    }catch (ClassCastException e){
                        e.printStackTrace();
                        fileUploadSuccess = false;
                    }
                }else {
                    logger.info("没有上传图片pic："+pic);
                }


                if(fileUploadSuccess) {
                    roomRepository.save(room);
                    restResponse = new RestResponse("操作成功！", new RestRoom(room));
                }else{
                    restResponse = new RestResponse("File upload failed", RestResponse.ERROR_STORAGE, null);
                }
            }

        }
        out.print(JSON.toJSONString(restResponse));
        out.flush();
        out.close();
        return restResponse;
    }

    /**
     *  根据设备类型的Id ,找到相应的设备类型，上传图片，更换设备类型的图标
     * @param deviceTypeId  设备类型的Id
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     * @throws SerialException
     */
    @RequestMapping(value = "/upload/deviceType/icon/{deviceTypeId}")
    public RestResponse createDeviceType(Principal principal, @PathVariable Integer deviceTypeId,
                                 HttpServletRequest request,HttpServletResponse response)
            throws ServletException, IOException,SerialException{
        RestResponse restResponse = null;
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        User user = judgeByPrincipal(principal);
        if (null == user) {
            // TODO: add user role validation
            restResponse  = new RestResponse("用户未登录！", 1005, null);
        }else {

            DeviceType deviceType = deviceTypeRepository.findOne(deviceTypeId);
            if (null == deviceType)
                restResponse = new RestResponse("设备类型不存在！", 1005, null);
            else {
                boolean fileUploadSuccess = true;
                try {
                    MultipartHttpServletRequest multirequest = (MultipartHttpServletRequest) request;
                    MultiValueMap<String, MultipartFile> map = multirequest.getMultiFileMap();
                    Set<String> keys = map.keySet();
                    for (String key : keys) {
                        List<MultipartFile> files = map.get(key);
                        if (null != files && files.size() > 0) {
                            MultipartFile file = files.get(0);
                            String fileName = file.getOriginalFilename();

                            logger.info("original file name is " + fileName);

                            String deviceTypeContainerName = "devicetypes";
                            String blobName = String.format("%s/%s", deviceTypeId, UUID.randomUUID().toString());
                            String photoUrl = Application.intelabStorageManager.uploadFile(file, deviceTypeContainerName, blobName, deviceType.getLogo());
                            if (photoUrl != null) {
                                deviceType.setLogo(photoUrl);
                                restResponse = new RestResponse("操作成功！", new RestDeviceType(deviceType));
                            } else {
                                logger.error(String.format("Storage return null for file %s", fileName));
                                restResponse = new RestResponse("Failed to upload file", RestResponse.ERROR_STORAGE, new RestDeviceType(deviceType));
                                fileUploadSuccess = false;
                            }


                        }
                    }

                } catch (ClassCastException e) {
                    e.printStackTrace();
                    restResponse = new RestResponse("Exception happens when upload file", RestResponse.ERROR_STORAGE, new RestDeviceType(deviceType));
                    fileUploadSuccess = false;
                }

                if(fileUploadSuccess){
                    deviceTypeRepository.save(deviceType);
                }

            }
        }
        out.print(JSON.toJSONString(restResponse));
        out.flush();
        out.close();

        return restResponse;
    }

    /**
     * 根据参数用户Id,去更换用户的头像
     * @param userId
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     * @throws SerialException
     */
    @RequestMapping(value = "/change/avatar/{userId}")
    public RestResponse updateUserAvatar(Principal principal, @PathVariable Integer userId, HttpServletRequest request,HttpServletResponse response)
            throws ServletException, IOException,SerialException {
        User user = judgeByPrincipal(principal);
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        RestResponse restResponse = null;
        String changePicURL = null;
        if (null == user) {
            restResponse  = new RestResponse("用户未登录！", 1005, null);
        }
        else {

            Integer companyId = user.getCompany().getId();
            Application.LOGGER.info(String.format("update user %d of company %d", userId, companyId));

            User updateUser = userRepository.findOne(userId);
            if (null == updateUser) {
                restResponse = new RestResponse("当前用户有误！", 1005, null);
            } else {

                boolean fileUploadSuccess = true;
                try {
                    MultipartHttpServletRequest multirequest = (MultipartHttpServletRequest) request;

                    MultiValueMap<String, MultipartFile> map = multirequest.getMultiFileMap();
                    Set<String> keys = map.keySet();
                    for (String key : keys) {
                        List<MultipartFile> files = map.get(key);
                        if (null != files && files.size() > 0) {
                            MultipartFile file = files.get(0);
                            String fileName = file.getOriginalFilename();
                            logger.info("original file name is " + fileName);

                            String companyContainerName = String.format("company%s", companyId);
                            String blobName = String.format("users/%s/%s", userId, UUID.randomUUID().toString());
                            String photoUrl = Application.intelabStorageManager.uploadFile(file, companyContainerName, blobName, updateUser.getHeadIcon());
                            if (photoUrl != null) {
                                updateUser.setHeadIcon(photoUrl);


                            } else {
                                logger.error(String.format("Failed to save file %s to blob", fileName));
                                fileUploadSuccess = false;
                            }

                        }
                    }
                }catch (ClassCastException e){
                    e.printStackTrace();
                    fileUploadSuccess = false;
                }

                if(fileUploadSuccess){
                    userRepository.save(updateUser);
                    restResponse = new RestResponse("图片上传成功！", 0, new RestUser(updateUser));

                }else{
                    restResponse = new RestResponse("Failed to upload picture", RestResponse.ERROR_STORAGE, new RestUser(updateUser));
                }

            }
        }
        out.print(JSON.toJSONString(restResponse));
        out.flush();
        out.close();
        return restResponse;
    }

    /**
     * 根据参数设备Id,去更换设备的图片
     * @param deviceId  设备Id
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     * @throws SerialException
     */
    @RequestMapping(value = "/change/picture/{deviceId}")
    public RestResponse uploadPhoto(Principal principal, @PathVariable Integer deviceId, HttpServletRequest request,HttpServletResponse response)
            throws ServletException, IOException,SerialException {
        User user = judgeByPrincipal(principal);
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        RestResponse restResponse = null;
        if (null == user) {
            restResponse  = new RestResponse("用户未登录！", 1005, null);
        }
        else {

            Integer companyId = user.getCompany().getId();
            Application.LOGGER.info(String.format("update device %d of company %d", deviceId, companyId));

            Device device = deviceRepository.findOne(deviceId);
            if (null == device) {
                restResponse = new RestResponse("当前设备有误！", 1005, null);
            } else {
                boolean fileUploadSuccess = true;
                try {
                    MultipartHttpServletRequest multirequest = (MultipartHttpServletRequest) request;

                    MultiValueMap<String, MultipartFile> map = multirequest.getMultiFileMap();
                    Set<String> keys = map.keySet();
                    for (String key : keys) {
                        List<MultipartFile> files = map.get(key);
                        if (null != files && files.size() > 0) {
                            MultipartFile file = files.get(0);
                            String fileName = file.getOriginalFilename();
                            logger.info("original file name is " + fileName);

                            String companyContainerName = String.format("company%s", companyId);
                            String blobName = String.format("devices/%s/%s", deviceId, UUID.randomUUID().toString());
                            String photoUrl = Application.intelabStorageManager.uploadFile(file, companyContainerName, blobName, device.getPhoto());
                            if (photoUrl != null) {
                                device.setPhoto(photoUrl);


                            } else {
                                logger.error(String.format("Failed to save file %s to blob", fileName));
                                fileUploadSuccess = false;
                            }

                        }
                    }
                }catch (ClassCastException e){
                    e.printStackTrace();
                    fileUploadSuccess = false;
                }

                if(fileUploadSuccess){
                    deviceRepository.save(device);
                    restResponse = new RestResponse("图片上传成功！", 0, new RestDevice(device));

                }else{
                    restResponse = new RestResponse("Failed to upload picture", RestResponse.ERROR_STORAGE, new RestDevice(device));
                }

            }
        }
        out.print(JSON.toJSONString(restResponse));
        out.flush();
        out.close();
        return restResponse;

    }

    /**
     * 平台管理员和平台业务员新增企业并添加管理员信息
     * 或者是修改企业信息并修改管理员信息
     * @param principal
     * @param param    id  为null或者是""则是新增企业   不为null或者""则是修改企业信息
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     * @throws SerialException
     */
    @RequestMapping(value = "/create/company")
    public RestResponse createCompany(Principal principal,@RequestParam Map<String,String> param,
                                 HttpServletRequest request,HttpServletResponse response)
            throws ServletException, IOException,SerialException{
        User user = judgeByPrincipal(principal);
        RestResponse restResponse = null;
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        if (null == user)
            restResponse = new RestResponse("用户未登陆！",1005, null);

        Company company = null;
        List<DeviceTypeInspect> deviceTypeInspects = new ArrayList<DeviceTypeInspect>();
        if (UserRoleDifferent.userServiceWorkerConfirm(user)||
                UserRoleDifferent.userServiceManagerConfirm(user) ) {
            User firmManager = null;

            logger.info(String.format("create company parameters: %s", param));
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

                if(null!=param.get("features")){
                    company.setFeatures(param.get("features"));
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

                //设置公司的companyId
                // company url is <company domain name>.ilabservice.cloud,
                // in which the domain name is passed in via API
                // domain name maps to company_id in db, it must be unique
                String domain_name = param.get("domain");

                if(domain_name == null){
                    //向前兼容 web1.0，新增api的参数里没有domain， 生成2字母公司id， 并用其登录
                    //给公司添加url
                    company.setLogin(SERVICE_PATH+"/Lab_login.html?company="+
                            ByteAndHex.convertMD5(URLEncoder.encode(company.getId().toString(),"UTF-8")));
                    company.setCompanyId(company.getLogin().substring(company.getLogin().indexOf("=")+1));
                }
                else{
                    //web2.0 新增api的参数里domain为公司登录域名
                    if(companyRepository.findByCompanyId(domain_name) != null){
                        throw new RuntimeException("create failure, 公司域名已经存在");
                    }

                    if(domain_name.contains(" ")){
                        throw new RuntimeException("创建公司失败， 域名非法， 不能有空格");
                    }

                    company.setCompanyId(domain_name);

                    //给公司添加url

                    company.setLogin(String.format("%s.ilabservice.cloud", domain_name));
                }

                if ("true".equals(param.get("companyOnChain").toString())) {
                    UserWalletManager wallet = InitWallet.getWallet();
                    String address = wallet.createAccount();
                    company.setAccountAddress(address);

                    // 将所属该公司的user全部上链
                    List<User> users = userRepository.findByCompanyId(company.getId());
                    for (User userOfCompany : users) {
                        // 先判断user本身是否已经上链
                        if (userOfCompany.getAccountAddress() == null) {
                            String userAddress = wallet.createAccount();
                            userOfCompany.setAccountAddress(userAddress);
                            userRepository.save(userOfCompany);
                        }
                    }
                }

                company=companyRepository.save(company);


                // 创建公司管理员账号
                firmManager = new User();
                firmManager.setName(param.get("account"));
                firmManager.setPassword(null==param.get("password")?"ilabservice":param.get("password"));
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


                //给管理员账号加密
                firmManager.setName(param.get("account")+"@"+company.getCompanyId());

                userRepository.save(firmManager);
            }else {
                //修改公司信息
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

                if(param.get("features") != null){
                    company.setFeatures(param.get("features"));
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

                if ((param.get("companyOnChain").toString()).equals("true")) {
                    UserWalletManager wallet = InitWallet.getWallet();
                    if (company.getAccountAddress() == null){
                        String address = wallet.createAccount();
                        company.setAccountAddress(address);
                    }

                    // 将所属该公司的user全部上链
                    List<User> users = userRepository.findByCompanyId(company.getId());
                    for (User userOfCompany : users) {
                        // 先判断user本身是否已经上链
                        if (userOfCompany.getAccountAddress() == null) {
                            String userAddress = wallet.createAccount();
                            userOfCompany.setAccountAddress(userAddress);
                            userRepository.save(userOfCompany);
                        }
                    }
                }

                company=companyRepository.save(company);
                firmManager = company.getManager();
//                firmManager.setName(param.get("account"));
//                firmManager.setPassword(null==param.get("password")?"123":param.get("password"));
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

                        List<MultipartFile> files = map.get(key);
                        if (null != files && files.size() > 0) {
                            MultipartFile file = files.get(0);
                            String fileName  = file.getOriginalFilename();
                            if (null==fileName||fileName.equals(""))
                                break;

                            logger.info("original file name is " + fileName);

                            String companyContainerName = String.format("company%s", company.getId());
                            String blobName = String.format("company/%s", UUID.randomUUID().toString());

                            String photoUrl = Application.intelabStorageManager.uploadFile(file, companyContainerName, blobName, company.getBackground());
                            if (photoUrl != null) {
                                logger.info(String.format("file %s saved to blob, and update path to db %s", fileName, photoUrl));
                                company.setBackground(photoUrl);
                            } else {
                                logger.error(String.format("Failed to save file %s to blob", fileName));
                            }

                        }
                    }
                }catch (ClassCastException e){
                    e.printStackTrace();

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

        return restResponse;
    }

    /**
     * 上传设备文件
     * @param deviceId   设备Id
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     * @throws SerialException
     */
    @RequestMapping(value = "/upload/device/file/{deviceId}")
    public void uploadDeviceFile(Principal principal, @PathVariable Integer deviceId, HttpServletRequest request,HttpServletResponse response)
            throws ServletException, IOException,SerialException {

        User user = judgeByPrincipal(principal);
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        RestResponse restResponse = null;
        if (null == user) {
            restResponse = new RestResponse("用户未登录！", 1005, null);
            return;
        }

        logger.info(String.format("update device %d of company %d", deviceId, user.getCompany().getId()));

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
            for (String key : keys) {

                List<MultipartFile> files = map.get(key);
                if (null != files && files.size() > 0) {
                    MultipartFile file = files.get(0);
                    String fileName  = file.getOriginalFilename();

                    if (null==fileName||fileName.equals(""))
                        break;

                    logger.info("original file name is " + fileName);

                    String companyContainerName = String.format("company%s", user.getCompany().getId());
                    // we don't use uuid, because we want to keep its file type info
                    String blobName = String.format("devices/%s", fileName);

                    String photoUrl = Application.intelabStorageManager.uploadFile(file, companyContainerName, blobName, null);
                    if (photoUrl != null) {
                        logger.info(String.format("file %s saved to blob, and update path to db %s", fileName, photoUrl));
                        createFile.setName(fileName);
                        createFile.setUrl(photoUrl);
                        fileRepository.save(createFile);
                        DeviceFile deviceFile = new DeviceFile();
                        deviceFile.setDevice(device);
                        deviceFile.setFile(createFile);
                        deviceFileRepository.save(deviceFile);

                    } else {
                        logger.error(String.format("Failed to save file %s to blob", fileName));
                    }
                }
            }
            out.print(JSON.toJSONString(new RestResponse("文件上传成功！", 0, new RestDevice(device))));
            out.flush();
            out.close();
        }
    }

    /**
     * 企业管理员更换公司logo
     * @param principal
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     * @throws SerialException
     */
    @RequestMapping(value="/logo/company")
    public RestResponse uploadLogo(Principal principal,HttpServletRequest request,HttpServletResponse response)
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
                boolean fileUploadSuccess = true;
                try {
                    MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
                    MultiValueMap<String, MultipartFile> map = multipartHttpServletRequest.getMultiFileMap();
                    Set<String> keys = map.keySet();
                    for (String key : keys) {
                        List<MultipartFile> files = map.get(key);
                        if (null != files && files.size() > 0) {
                            MultipartFile file = files.get(0);
                            String fileName = file.getOriginalFilename();

                            logger.info("original file name is " + fileName);

                            String companyContainerName = String.format("company%s", company.getId());
                            // we don't use uuid, because we want to keep its file type info
                            String blobName = String.format("company/%s", UUID.randomUUID().toString());

                            String photoUrl = Application.intelabStorageManager.uploadFile(file, companyContainerName, blobName, company.getLogo());
                            if (photoUrl != null) {
                                logger.info(String.format("file %s saved to blob, and update path to db %s", fileName, photoUrl));
                                company.setLogo(photoUrl);


                            } else {
                                logger.error(String.format("Failed to save file %s to blob", fileName));
                                fileUploadSuccess = false;
                            }

                        }
                    }
                }catch(ClassCastException e){
                    e.printStackTrace();
                    fileUploadSuccess =false;
                }

                if(fileUploadSuccess){
                    companyRepository.save(company);
                    restResponse = new RestResponse("图片上传成功！", 0, new RestCompany(company));
                }else{
                    restResponse = new RestResponse("Failed to upload file", RestResponse.ERROR_STORAGE, null);
                }

            }
        }else {
            restResponse = new RestResponse("用户权限不足，无法上传公司logo!",1005,null);
        }
        out.print(JSON.toJSONString(restResponse));
        out.flush();
        out.close();
        return restResponse;
    }

    /**
     * 硬件版本更新
     * @param principal
     * @param param  type 01是立即更新  02硬件自己判断更新
     * @param request
     * @param response
     * @throws IOException
     */
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
                        deviceVersion.setFourthCode(param.get("code_forth"));
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
            List<MultipartFile> files=map.get(key);
            if (null!=files&&files.size()>0){
                MultipartFile file=files.get(0);
                String fileName=file.getOriginalFilename();

                logger.info("original file name is " + fileName);

                String containerName = "version";
                // we don't use uuid, because we want to keep its file type info
                String blobName = String.format("%s/%s", deviceVersion.getId(), fileName);

                String fileUrl = Application.intelabStorageManager.uploadFile(file, containerName, blobName, deviceVersion.getUrl());
                if (fileUrl != null) {
                    logger.info(String.format("file %s saved to blob, and update path to db %s", fileName, fileUrl));
                    deviceVersion.setUrl(fileUrl);
                    deviceVersion.setFileName(fileName);
                } else {
                    logger.error(String.format("Failed to save file %s to blob", fileName));
                }

            }
        }
    }
}
