package com.device.inspect.controller;

import com.device.inspect.common.restful.RestResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialException;
import java.io.IOException;

/**
 * Created by Administrator on 2016/8/16.
 */
@Controller
@RequestMapping(value = "/api/rest/file")
public class FileController {


    @RequestMapping(value = "/create/building")
    public RestResponse createBuilding(HttpServletRequest request,HttpServletResponse response)
            throws ServletException, IOException,SerialException {

    }
}
