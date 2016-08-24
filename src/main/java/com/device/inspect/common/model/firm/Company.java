package com.device.inspect.common.model.firm;

import com.device.inspect.common.model.charater.User;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/7/7.
 */
@Entity
@Table(name = "")
public class Company {

    private Integer id;
    private String name;
    private String address;
    private User manager;
    private User businessMan;
    private String email;
    private String telephone;
    private String contractNum;
    private Date signDate;
    private Date contractEndDate;
    private String background;
    private Date createDate;
    private List<Building> buildings;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @ManyToOne()
    @JoinColumn(name = "manager_user_id")
    public User getManager() {
        return manager;
    }


    public void setManager(User manager) {
        this.manager = manager;
    }

    @ManyToOne()
    @JoinColumn(name = "business_user_id")
    public User getBusinessMan() {
        return businessMan;
    }

    public void setBusinessMan(User businessMan) {
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

    @Column(name = "contract_no")
    public String getContractNum() {
        return contractNum;
    }

    public void setContractNum(String contractNum) {
        this.contractNum = contractNum;
    }


    @Column(name = "sign_date")
    public Date getSignDate() {
        return signDate;
    }

    public void setSignDate(Date signDate) {
        this.signDate = signDate;
    }

    @Column(name = "contract_end_date")
    public Date getContractEndDate() {
        return contractEndDate;
    }

    public void setContractEndDate(Date contractEndDate) {
        this.contractEndDate = contractEndDate;
    }

    @Column(name = "background_url")
    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    @Column(name = "create_date")
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @OneToMany(mappedBy = "company")
    public List<Building> getBuildings() {
        return buildings;
    }

    public void setBuildings(List<Building> buildings) {
        this.buildings = buildings;
    }
}
