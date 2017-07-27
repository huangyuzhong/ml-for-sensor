package com.device.inspect.common.restful.charater;

import com.device.inspect.common.model.charater.Role;
import com.device.inspect.common.model.charater.User;
import com.device.inspect.common.model.firm.Company;
import com.device.inspect.common.service.OnchainService;
import com.device.inspect.common.util.transefer.ByteAndHex;
import com.device.inspect.common.util.transefer.UserRoleDifferent;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotNull;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

/**
 * Created by Administrator on 2016/7/12.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestUser {

    private Integer id;
    private String name;
    //private String password;
    private String userName;
    private String mobile;
    private String telephone;
    private String headIcon;
    private Date createDate;
    private String gender;
    private String email;
    private String department;
    private String job;
    private String jobNum;
    private RestRole role;
    private String companyName;
    private String verify;
    private Integer bindMobile;
    private Integer bindEmail;
    private String companyLogo;
    private String roleNames;
    private String companyId;
    private String removeAlert;
    private String accountAddress;
    private String assetId;
    private Long latestPasswordUpdateTime;

    public RestUser(@NotNull User user){
        this.id = user.getId();
        if (UserRoleDifferent.userStartWithService(user)){
            this.name = user.getName();
        }else {
            this.name=user.getName().substring(0,user.getName().indexOf("@"));
        }
    //    this.password = user.getPassword();
        this.userName = user.getUserName();
        this.mobile = user.getMobile();
        this.telephone = user.getTelephone();
        this.headIcon = user.getHeadIcon();
        this.createDate = user.getCreateDate();
        this.gender = user.getGender();
        this.email = user.getEmail();
        this.department = user.getDepartment();
        this.job = user.getJob();
        this.jobNum = user.getJobNum();
        if (null!=user.getRoles()&&user.getRoles().size()>0)
            this.role = new RestRole(user.getRoles().get(0));
        this.verify = null==user.getVerify()?null:user.getVerify().toString();
        this.bindMobile = user.getBindMobile();
        this.bindEmail = user.getBindEmail();
        if (null!=user.getCompany()) {
            this.companyLogo = user.getCompany().getLogo();
            this.companyName = user.getCompany().getName();
            try {
                this.companyId = ByteAndHex.convertMD5(URLEncoder.encode(user.getCompany().getId().toString(),"UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if (null!=user.getRoles()){
            roleNames = "";
            for (Role role : user.getRoles()){
                if (null!=role.getRoleAuthority()&&null!=role.getRoleAuthority().getRoleName())
                    roleNames+=role.getRoleAuthority().getRoleName()+" ";
            }
        }
        this.removeAlert=user.getRemoveAlert();
        this.accountAddress=user.getAccountAddress();
        this.latestPasswordUpdateTime = user.getLatestPasswordUpdateTime().getTime();
//        OnchainService onchainService = new OnchainService();
//        if (this.accountAddress != null && !this.accountAddress.isEmpty()){
//            this.assetId=onchainService.getAssetId(accountAddress);
//        }
    }



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //public String getPassword() { return password;}

    //public void setPassword(String password) {this.password = password;}

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getHeadIcon() {
        return headIcon;
    }

    public void setHeadIcon(String headIcon) {
        this.headIcon = headIcon;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getJobNum() {
        return jobNum;
    }

    public void setJobNum(String jobNum) {
        this.jobNum = jobNum;
    }

    public RestRole getRole() {
        return role;
    }

    public void setRole(RestRole role) {
        this.role = role;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getVerify() {
        return verify;
    }

    public void setVerify(String verify) {
        this.verify = verify;
    }

    public Integer getBindMobile() {
        return bindMobile;
    }

    public void setBindMobile(Integer bindMobile) {
        this.bindMobile = bindMobile;
    }

    public Integer getBindEmail() {
        return bindEmail;
    }

    public void setBindEmail(Integer bindEmail) {
        this.bindEmail = bindEmail;
    }

    public String getCompanyLogo() {
        return companyLogo;
    }

    public void setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo;
    }

    public String getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(String roleNames) {
        this.roleNames = roleNames;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getRemoveAlert() {
        return removeAlert;
    }

    public void setRemoveAlert(String removeAlert) {
        this.removeAlert = removeAlert;
    }

    public String getAccountAddress() {
        return accountAddress;
    }

    public void setAccountAddress(String accountAddress) {
        this.accountAddress = accountAddress;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public Long getLatestPasswordUpdateTime(){
        return this.latestPasswordUpdateTime;
    }

    public void setLatestPasswordUpdateTime(Long latestPasswordUpdateTime){
        this.latestPasswordUpdateTime = latestPasswordUpdateTime;
    }
}
