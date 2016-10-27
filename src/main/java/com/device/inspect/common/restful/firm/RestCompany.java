package com.device.inspect.common.restful.firm;

import com.device.inspect.common.model.firm.Company;
import com.device.inspect.common.restful.charater.RestUser;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by Administrator on 2016/8/31.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestCompany {
    private Integer id;
    private String name;
    private String address;
    private RestUser manager;
    private RestUser businessMan;
    private String email;
    private String telephone;
    private String contractNum;
    private Date signDate;
    private Date contractEndDate;
    private String background;
    private Date createDate;

    private Integer lowAlert;
    private Integer highAlert;
    private Integer online;
    private Integer offline;
    private Integer total;
    private Float score;
    private Integer enable;
    private String logo;
    private String login;
    private Float lat;
    private Float lng;
    private String location;

    public RestCompany(@NotNull Company company){
        this.id = company.getId();
        this.name = company.getName();
        this.address = company.getAddress();
        this.manager = null==company.getManager()?null:new RestUser(company.getManager());
        this.businessMan = null==company.getBusinessMan()?null:new RestUser(company.getBusinessMan());
        this.email = company.getEmail();
        this.telephone = company.getTelephone();
        this.contractNum = company.getContractNum();
        this.signDate = company.getSignDate();
        this.contractEndDate = company.getContractEndDate();
        this.background = company.getBackground();
        this.createDate = company.getCreateDate();

        this.lowAlert = company.getLowAlert();
        this.highAlert = company.getHighAlert();
        this.online = company.getOnline();
        this.offline = company.getOffline();
        this.total = company.getTotal();
        this.score = company.getScore();
        this.enable = company.getEnable();
        this.login = company.getLogin();
        this.logo = company.getLogo();
        this.lat = company.getLat();
        this.lng = company.getLng();
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public RestUser getManager() {
        return manager;
    }

    public void setManager(RestUser manager) {
        this.manager = manager;
    }

    public RestUser getBusinessMan() {
        return businessMan;
    }

    public void setBusinessMan(RestUser businessMan) {
        this.businessMan = businessMan;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getContractNum() {
        return contractNum;
    }

    public void setContractNum(String contractNum) {
        this.contractNum = contractNum;
    }

    public Date getSignDate() {
        return signDate;
    }

    public void setSignDate(Date signDate) {
        this.signDate = signDate;
    }

    public Date getContractEndDate() {
        return contractEndDate;
    }

    public void setContractEndDate(Date contractEndDate) {
        this.contractEndDate = contractEndDate;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Integer getLowAlert() {
        return lowAlert;
    }

    public void setLowAlert(Integer lowAlert) {
        this.lowAlert = lowAlert;
    }

    public Integer getHighAlert() {
        return highAlert;
    }

    public void setHighAlert(Integer highAlert) {
        this.highAlert = highAlert;
    }

    public Integer getOnline() {
        return online;
    }

    public void setOnline(Integer online) {
        this.online = online;
    }

    public Integer getOffline() {
        return offline;
    }

    public void setOffline(Integer offline) {
        this.offline = offline;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public Float getLng() {
        return lng;
    }

    public void setLng(Float lng) {
        this.lng = lng;
    }
}
