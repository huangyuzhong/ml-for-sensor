package com.device.inspect.common.model.charater;

import com.device.inspect.common.model.firm.Company;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/7/7.
 */
@Entity()
@Table(name = "users")
public class User {

    private Integer id;
    private String name;
    private String password;
    private String userName;
    private String mobile;
    private String telephone;
    private String headIcon;
    private Date createDate;
    private String gender;
    private String email;
    private String department;
    private String job;
    private Company company;
    private List<Role> roles;
    private String jobNum;
    private Integer bindMobile;
    private Integer bindEmail;
    private Integer verify;
    private String removeAlert;
    private String accountAddress;
    private Date lastPasswordErrorDate;
    private Integer passwordErrorRetryTimes;
    private Date latestPasswordUpdateTime;

    @Id
    @GeneratedValue()
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "user_name")
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

    @Column(name = "head_icon")
    public String getHeadIcon() {
        return headIcon;
    }

    public void setHeadIcon(String headIcon) {
        this.headIcon = headIcon;
    }

    @Column(name = "create_date")
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

    @ManyToOne()
    @JoinColumn(name = "company_id")
    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    @OneToMany(mappedBy = "user")
    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    @Column(name = "job_number")
    public String getJobNum() {
        return jobNum;
    }

    public void setJobNum(String jobNum) {
        this.jobNum = jobNum;
    }

    @Column(name = "bind_mobile")
    public Integer getBindMobile() {
        return bindMobile;
    }

    public void setBindMobile(Integer bindMobile) {
        this.bindMobile = bindMobile;
    }

    @Column(name = "bind_email")
    public Integer getBindEmail() {
        return bindEmail;
    }

    public void setBindEmail(Integer bindEmail) {
        this.bindEmail = bindEmail;
    }

    public Integer getVerify() {
        return verify;
    }

    public void setVerify(Integer verify) {
        this.verify = verify;
    }

    @Column(name = "remove_alert")
    public String getRemoveAlert() {
        return removeAlert;
    }

    public void setRemoveAlert(String removeAlert) {
        this.removeAlert = removeAlert;
    }

    @Column(name = "account_address")
    public String getAccountAddress() {
        return accountAddress;
    }

    public void setAccountAddress(String accountAddress) {
        this.accountAddress = accountAddress;
    }

    @Column(name = "last_password_error_date")
    public Date getLastPasswordErrorDate(){
        return this.lastPasswordErrorDate;
    }

    public void setLastPasswordErrorDate(Date lastPasswordErrorDate){
        this.lastPasswordErrorDate = lastPasswordErrorDate;
    }

    @Column(name = "password_error_retry_times")
    public Integer getPasswordErrorRetryTimes(){
        return this.passwordErrorRetryTimes;
    }

    public void setPasswordErrorRetryTimes(Integer passwordErrorRetryTimes){
        this.passwordErrorRetryTimes = passwordErrorRetryTimes;
    }

    @Column(name = "latest_password_update_time")
    public Date getLatestPasswordUpdateTime(){
        return this.latestPasswordUpdateTime;
    }

    public void setLatestPasswordUpdateTime(Date latestPasswordUpdateTime){
        this.latestPasswordUpdateTime = latestPasswordUpdateTime;
    }
}
