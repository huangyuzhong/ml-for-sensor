package com.device.inspect.controller;

import com.device.inspect.common.model.device.DeviceVersion;
import com.device.inspect.common.model.device.Files;
import com.device.inspect.common.repository.device.DeviceRepository;
import com.device.inspect.common.repository.device.DeviceVersionRepository;
import com.device.inspect.common.repository.device.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2016/9/12.
 */
@Controller
@RequestMapping(value = "/api/rest/download")
public class ExportController {
    private static final String CONTENT_TYPE = "text/html; charset=GBK";

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DeviceVersionRepository deviceVersionRepository;

    /**
     * 文件下载接口
     * @param fileId  文件id
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/file/{fileId}")
    public void downloadDeviceFile(@PathVariable Integer fileId,
                                   HttpServletRequest request,HttpServletResponse response) throws Exception{
        Files files = fileRepository.findOne(fileId);
        //获取网站部署路径(通过ServletContext对象)，用于确定下载文件位置，从而实现下载
        String path = request.getSession().getServletContext().getRealPath("/");
//        String path = "http://inmycars.ihengtian.top";
        //1.设置文件ContentType类型，这样设置，会自动判断下载文件类型
        response.setContentType("application/octet-stream");
//        response.setContentType(CONTENT_TYPE);
        //2.设置文件头：最后一个参数是设置下载文件名(假如我们叫a.pdf)
//        response.setHeader("Content-Disposition", "attachment;fileName="+files.getName());
        response.setHeader("Content-Disposition","attachment;filename="+new String
                (files.getName().getBytes("gbk"),"iso-8859-1"));
        ServletOutputStream out;
        //通过文件路径获得File对象(假如此路径中有一个download.pdf文件)
        File file = new File(path + files.getUrl());
        try {
            FileInputStream inputStream = new FileInputStream(file);
            //3.通过response获取ServletOutputStream对象(out)
            out = response.getOutputStream();
            int b = 0;
            byte[] buffer = new byte[512];
            while (b != -1){
                b = inputStream.read(buffer);
                //4.写到输出流(out)中
                out.write(buffer,0,b);
            }
            inputStream.close();
            out.close();
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 硬件版本下载
     * @param id 设备版本id
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/device/version/{id}")
    public void downloadDeviceVersionFile(@PathVariable Integer id,
                                   HttpServletRequest request,HttpServletResponse response) throws Exception{
        DeviceVersion deviceVersion = deviceVersionRepository.findOne(id);
        //获取网站部署路径(通过ServletContext对象)，用于确定下载文件位置，从而实现下载
        String path = request.getSession().getServletContext().getRealPath("/");
//        String path = "http://inmycars.ihengtian.top";
        //1.设置文件ContentType类型，这样设置，会自动判断下载文件类型
        response.setContentType("application/octet-stream");
//        response.setContentType(CONTENT_TYPE);
        //2.设置文件头：最后一个参数是设置下载文件名(假如我们叫a.pdf)
//        response.setHeader("Content-Disposition", "attachment;fileName="+files.getName());
        response.setHeader("Content-Disposition","attachment;filename="+new String
                (deviceVersion.getFileName().getBytes("gbk"),"iso-8859-1"));
        ServletOutputStream out;
        //通过文件路径获得File对象(假如此路径中有一个download.pdf文件)
        File file = new File(path + deviceVersion.getUrl());

        try {
            FileInputStream inputStream = new FileInputStream(file);

            //3.通过response获取ServletOutputStream对象(out)
            out = response.getOutputStream();

            int b = 0;
            byte[] buffer = new byte[512];
            while (b != -1){
                b = inputStream.read(buffer);
                //4.写到输出流(out)中
                out.write(buffer,0,b);
            }
            inputStream.close();
            out.close();
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
