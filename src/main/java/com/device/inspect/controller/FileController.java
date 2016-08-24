package com.device.inspect.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.device.inspect.common.model.charater.User;
import com.device.inspect.common.model.device.DeviceType;
import com.device.inspect.common.model.firm.Building;
import com.device.inspect.common.model.firm.Room;
import com.device.inspect.common.model.firm.Storey;
import com.device.inspect.common.repository.charater.RoleAuthorityRepository;
import com.device.inspect.common.repository.charater.RoleRepository;
import com.device.inspect.common.repository.charater.UserRepository;
import com.device.inspect.common.repository.device.DeviceRepository;
import com.device.inspect.common.repository.device.DeviceTypeInspectRepository;
import com.device.inspect.common.repository.device.DeviceTypeRepository;
import com.device.inspect.common.repository.device.InspectTypeRepository;
import com.device.inspect.common.repository.firm.BuildingRepository;
import com.device.inspect.common.repository.firm.RoomRepository;
import com.device.inspect.common.repository.firm.StoreyRepository;
import com.device.inspect.common.restful.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialException;
import java.io.*;
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
                        restResponse = new RestResponse("添加成功！",null);

                    }
                }catch (ClassCastException e){
                    e.printStackTrace();
                }
                buildingRepository.save(building);
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
        if (user.getRole().getRoleAuthority().getName().equals("FIRM_MANAGER")) {

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
                    restResponse = new RestResponse("添加成功！",null);

                }
            }catch (ClassCastException e){
                e.printStackTrace();
            }

            storeyRepository.save(floor);
        } else {
            restResponse = new RestResponse("权限不足！",1005,null);
        }
        out.print(JSON.toJSONString(restResponse));
        out.flush();
        out.close();
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
        if (user.getRole().getRoleAuthority().getName().equals("FIRM_MANAGER")) {

            Room room = new Room();

            if (null!=param.get("type")&&null!=param.get("roomId")&&param.get("type").equals("1")){
                room = roomRepository.findOne(Integer.valueOf(param.get("floorId")));
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
                    restResponse = new RestResponse("添加成功！",null);
                }
            }catch (ClassCastException e){
                e.printStackTrace();
            }

            roomRepository.save(room);
        } else {
            restResponse = new RestResponse("权限不足！",1005,null);
        }
        out.print(JSON.toJSONString(restResponse));
        out.flush();
        out.close();
    }

    @RequestMapping(value = "/create/deviceType/{name}")
    public void createDeviceType(@PathVariable String name,@RequestParam Map<String,String> param,
                                 HttpServletRequest request,HttpServletResponse response)
            throws ServletException, IOException,SerialException{
        User user = userRepository.findByName(name);
        RestResponse restResponse = null;
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        if (null == user)
            restResponse = new RestResponse("手机号出错！", null);
        if (user.getRole().getRoleAuthority().getName().equals("FIRM_MANAGER")) {

            DeviceType deviceType = new DeviceType();
            deviceType.setName(null == param.get("name") ? null : param.get("name"));
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
            restResponse = new RestResponse("添加成功！",null);
        } else {
            restResponse = new RestResponse("权限不足！",1005,null);
        }
        out.print(JSON.toJSONString(restResponse));
        out.flush();
        out.close();

    }
}
