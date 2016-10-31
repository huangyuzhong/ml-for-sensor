package com.device.inspect.controller.wkj1;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.device.inspect.common.model.charater.Role;
import com.device.inspect.common.model.charater.User;
import com.device.inspect.common.model.firm.Company;
import com.device.inspect.common.repository.charater.UserRepository;
import com.device.inspect.common.repository.firm.CompanyRepository;
import com.device.inspect.common.restful.RestResponse;
import com.device.inspect.common.restful.firm.RestCompany;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialException;
import java.io.*;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Straight on 2016/10/31.
 */
@Controller
@RequestMapping("/api/rest/test")
public class LogoCompany {
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private UserRepository userRepository;
    @RequestMapping("/logo/company")
    public void uploadLogo(Principal principal, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException,SerialException {
        User user=userRepository.findByName(principal.getName());
        response.setContentType("text/html");
        PrintWriter out=response.getWriter();
        RestResponse restResponse=null;
        if (user==null){
            new RestResponse("用户信息不存在！",1005,null);
        }else {
            if (user.getRole().getRoleAuthority().getName().equals("FIRM_MANAGER")){
                Company company=companyRepository.findOne(user.getCompany().getId());
                if (company==null){
                    restResponse=new RestResponse("公司不存在",1005,null);
                }else {
                    MultipartHttpServletRequest multipartHttpServletRequest=(MultipartHttpServletRequest ) request;
                    MultiValueMap<String,MultipartFile> map=multipartHttpServletRequest.getMultiFileMap();
                    Set<String> keys = map.keySet();
                    List<String> result=new ArrayList<String>();
                    for (String key:keys){
                            JSONObject jsonObject=new JSONObject();
                            String path="";
                            path=request.getSession().getServletContext().getRealPath("/")+"logo/company"+company.getId()+"/";
                            File add=new File(path);
                            if (!add.exists()&&!add.isDirectory()){
                               add.mkdir();
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
                            company.setLogo("/logo/company/"+company.getId()+"/"+fileName);
                            companyRepository.save(company);
                        }

                    }
                    out.print(JSON.toJSONString(new RestResponse("图片上传成功！", 0, new RestCompany(company))));
                }
            }else {
                new RestResponse("用户权限不足，无法上传公司logo!",1005,null);
            }
        }
        out.flush();
        out.close();
    }
}
