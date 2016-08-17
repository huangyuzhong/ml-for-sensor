package com.device.inspect.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.device.inspect.common.model.charater.User;
import com.device.inspect.common.model.firm.Building;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialException;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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

    @RequestMapping(value = "/create/building/{name}")
    public void createBuilding(@PathVariable String name,HttpServletRequest request,HttpServletResponse response)
            throws ServletException, IOException,SerialException {
        User user = userRepository.findByName(name);
        RestResponse restResponse = null;
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        if (null == user)
            restResponse = new RestResponse("手机号出错！", null);
        if (user.getRole().getRoleAuthority().getName().equals("FIRM_MANAGER")) {

            MultipartHttpServletRequest multirequest = (MultipartHttpServletRequest) request;

            MultiValueMap<String, MultipartFile> map = multirequest.getMultiFileMap();

            Set<String> keys = map.keySet();
//        List<String> result = new ArrayList<String>();
            Building building = new Building();
            building.setCreateDate(new Date());
            building.setDeviceNum(0);
            building.setCompany(user.getCompany());
            for (String key : keys) {
                JSONObject jobj = new JSONObject();
                if (!key.equals("file")) {
                    if (key.equals("name")){
                        building.setName(map.get(key).toString());
                    }
                    if (key.equals("xpoint")){
                        building.setXpoint(Float.valueOf(map.get(key).toString()));
                    }
                    if (key.equals("ypoint")){
                        building.setYpoint(Float.valueOf(map.get(key).toString()));
                    }
                } else {
                    String path = "";

                    path = request.getSession().getServletContext().getRealPath("/") + "photo/building/";
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

                        building.setBackground("/photo/building/" + fileName);
                        userRepository.save(user);


                    }

                }
                restResponse = new RestResponse("添加成功！",null);

            }
        } else {
            restResponse = new RestResponse("权限不足！",1005,null);
        }

        out.print(JSON.toJSONString(restResponse));
        out.flush();
        out.close();

    }
}
