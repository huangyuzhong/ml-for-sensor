package com.device.inspect.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.inmycars.service.web.common.model.car.CarBrand;
import com.inmycars.service.web.common.model.character.User;
import com.inmycars.service.web.common.model.dictionary.Photo;
import com.inmycars.service.web.common.repository.car.CarBrandRepository;
import com.inmycars.service.web.common.repository.character.UserRepository;
import com.inmycars.service.web.common.repository.dictionary.PhotoRepository;
import com.inmycars.service.web.controller.entity.responseEntity.RestResponse;
import com.inmycars.service.web.controller.entity.responseEntity.character.RestUser;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.RequestContext;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
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
import java.util.*;

/**
 * Created by Administrator on 2016/1/23.
 */
@Controller
@RequestMapping(value = "/api/rest/upload")
public class PhotoUploadController {

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private CarBrandRepository carBrandRepository;

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value = "/files")
    public void uploadPhoto(HttpServletRequest request,HttpServletResponse response)
            throws ServletException, IOException,SerialException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        MultipartHttpServletRequest multirequest = (MultipartHttpServletRequest)request;

        MultiValueMap<String,MultipartFile> map = multirequest.getMultiFileMap();

        Set<String> keys = map.keySet();
        List<String> result = new ArrayList<String>();
        for (String key:keys){
            Photo photo = photoRepository.findOne(Integer.valueOf(key));
            JSONObject jobj = new JSONObject();
            String path = "";
            if (null == photo){
//                out.print(JSON.toJSONString(new RestResponse("当前图片为空！！",1005,null)));
                result.add(key);
            }else {
                path = request.getSession().getServletContext().getRealPath("/")+"photo/"+photo.getId()+"/";
                File add = new File(path);
                if (!add.exists()&&!add.isDirectory()){
                    add.mkdir();
                }

                List<MultipartFile> files = map.get(key);
                if (null!=files&&files.size()>0){
                    MultipartFile file = files.get(0);
//                    String name  = file.getOriginalFilename();
                    String name = UUID.randomUUID().toString()+".jpg";
                    InputStream is = file.getInputStream();
                    File f = new File(path + name);
                    FileOutputStream fos = new FileOutputStream(f);
                    int hasRead = 0;
                    byte[] buf = new byte[1024];
                    while((hasRead = is.read(buf)) > 0){
                        fos.write(buf, 0, hasRead);
                    }
                    fos.close();
                    is.close();

                    photo.setUrl("/photo/"+photo.getId()+"/" + name);
                    photo.setEnable(1);
                    photoRepository.save(photo);
                }
            }

        }
        out.print(JSON.toJSONString(new RestResponse( "图片上传成功！", 0, result)));
        out.flush();
        out.close();
    }

    @RequestMapping(value = "/file/user/head/{mobile}")
    public void uploadUserHeadIcon(@PathVariable String mobile,HttpServletRequest request,HttpServletResponse response)
            throws ServletException, IOException,SerialException{
        User user = userRepository.findByName(mobile);
        RestResponse restResponse = null;
        if (null==user)
            restResponse = new RestResponse("手机号出错！",null);

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        MultipartHttpServletRequest multirequest = (MultipartHttpServletRequest)request;

        MultiValueMap<String,MultipartFile> map = multirequest.getMultiFileMap();

        Set<String> keys = map.keySet();
//        List<String> result = new ArrayList<String>();
        for (String key:keys){
            JSONObject jobj = new JSONObject();
            String path = "";

            path = request.getSession().getServletContext().getRealPath("/")+"photo/head/";
            File add = new File(path);
            if (!add.exists()&&!add.isDirectory()){
                add.mkdir();
            }

            List<MultipartFile> files = map.get(key);
            if (null!=files&&files.size()>0){
                MultipartFile file = files.get(0);
//                String name  = file.getOriginalFilename();
                String name = UUID.randomUUID().toString()+".jpg";
                InputStream is = file.getInputStream();
                File f = new File(path + name);
                FileOutputStream fos = new FileOutputStream(f);
                int hasRead = 0;
                byte[] buf = new byte[1024];
                while((hasRead = is.read(buf)) > 0){
                    fos.write(buf, 0, hasRead);
                }
                fos.close();
                is.close();

                user.setHeadicon("/photo/head/" + name);
                userRepository.save(user);

            }

        }
        restResponse = new RestResponse("头像上传成功！",new RestUser(user));
        out.print(JSON.toJSONString(restResponse));
        out.flush();
        out.close();

    }

    @RequestMapping(value = "/file/upload/{brandId}")
    public void uploadBrandPhoto(@PathVariable Integer brandId,HttpServletRequest request,HttpServletResponse response)
            throws ServletException, IOException,SerialException{
        RestResponse restResponse = new RestResponse();
        CarBrand carBrand = carBrandRepository.findOne(brandId);
        if (carBrand == null)
            restResponse.setMessage("车品牌信息出错");

        if (null==carBrand.getPhoto()){
            Photo photo = new Photo();
            photo.setType("jpg");
            photo.setEnable(0);
            photo.setCreateDate(new Date());
            photoRepository.save(photo);
            carBrand.setPhoto(photo);
        }
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        MultipartHttpServletRequest multirequest = (MultipartHttpServletRequest)request;

        MultiValueMap<String,MultipartFile> map = multirequest.getMultiFileMap();

        Set<String> keys = map.keySet();
//        List<String> result = new ArrayList<String>();
        for (String key:keys){
            JSONObject jobj = new JSONObject();
            String path = "";

            path = request.getSession().getServletContext().getRealPath("/")+"photo/brand/";
            File add = new File(path);
            if (!add.exists()&&!add.isDirectory()){
                add.mkdir();
            }

            List<MultipartFile> files = map.get(key);
            if (null!=files&&files.size()>0){
                MultipartFile file = files.get(0);
//                String name  = file.getOriginalFilename();
                String name = UUID.randomUUID().toString()+".jpg";
                InputStream is = file.getInputStream();
                File f = new File(path + name);
                FileOutputStream fos = new FileOutputStream(f);
                int hasRead = 0;
                byte[] buf = new byte[1024];
                while((hasRead = is.read(buf)) > 0){
                    fos.write(buf, 0, hasRead);
                }
                fos.close();
                is.close();

                carBrand.getPhoto().setUrl("/photo/brand/" + name);
                carBrand.getPhoto().setEnable(1);
                photoRepository.save(carBrand.getPhoto());

                restResponse.setMessage("图片上传成功！");
            }

        }
        out.print(JSON.toJSONString(restResponse));
        out.flush();
        out.close();
    }

    @RequestMapping(value = "/file/error")
    public void uploadFile(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");

        PrintWriter printWriter = response.getWriter();
        //获得磁盘文件条目工厂。
        DiskFileItemFactory factory = new DiskFileItemFactory();
        //获取文件上传需要保存的路径，upload文件夹需存在。
        String path = request.getSession().getServletContext().getRealPath("/upload");

        //设置暂时存放文件的存储室，这个存储室可以和最终存储文件的文件夹不同。因为当文件很大的话会占用过多内存所以设置存储室。
        factory.setRepository(new File(path));

        //设置缓存的大小，当上传文件的容量超过缓存时，就放到暂时存储室。
        factory.setSizeThreshold(1024*1024);
        //上传处理工具类（高水平API上传处理？）
        ServletFileUpload upload = new ServletFileUpload(factory);

        try{
            //调用 parseRequest（request）方法  获得上传文件 FileItem 的集合list 可实现多文件上传。
            List<FileItem> list = (List<FileItem>)upload.parseRequest((RequestContext)request.getServletContext());
            for(FileItem item:list){
                //获取表单属性名字。
                String name = item.getFieldName();
                //如果获取的表单信息是普通的文本信息。即通过页面表单形式传递来的字符串。
                if(item.isFormField()){
                    //获取用户具体输入的字符串，
                    String value = item.getString();
                    request.setAttribute(name, value);
                }
                //如果传入的是非简单字符串，而是图片，音频，视频等二进制文件。
                else{
                    //获取路径名
                    String value = item.getName();
                    //取到最后一个反斜杠。
                    int start = value.lastIndexOf("\\");
                    //截取上传文件的 字符串名字。+1是去掉反斜杠。
                    String filename = value.substring(start+1);
                    request.setAttribute(name, filename);

                    /*第三方提供的方法直接写到文件中。
                     * item.write(new File(path,filename));*/
                    //收到写到接收的文件中。
                    OutputStream out = new FileOutputStream(new File(path,filename));
                    InputStream in = item.getInputStream();

                    int length = 0;
                    byte[] buf = new byte[1024];
                    System.out.println("获取文件总量的容量:"+ item.getSize());

                    while((length = in.read(buf))!=-1){
                        out.write(buf,0,length);
                    }
                    in.close();
                    out.close();

                    printWriter.print(JSON.toJSONString(new RestResponse( "图片上传成功！",  null)));
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        printWriter.flush();
        printWriter.close();
    }
}
