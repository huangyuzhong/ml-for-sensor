package com.device.inspect.common.model.record;

import com.device.inspect.common.model.firm.Company;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


@Entity
@Table(name="company_asset")
public class CompanyAsset{
    @Id
    @GeneratedValue()
    private Integer id;

    @ManyToOne(targetEntity = Company.class)
    @JoinColumn(name = "company_id")
    @NotNull
    private Company company;

    @Column(name = "asset_address")
    @NotNull
    private String assetAddress;

    @Column(name = "asset_name")
    @NotNull
    private String assetName;

    public void setId(Integer id){
        this.id = id;
    }

    public Integer getId() {
        return this.id;
    }

    public void setCompany(Company company){
        this.company = company;
    }

    public Company getCompany(){
        return this.company = company;
    }

    public void setAssetAddress(String assetAddress){
        this.assetAddress = assetAddress;
    }

    public String getAssetAddress(){
        return this.assetAddress;
    }

    public void setAssetName(String assetName){
        this.assetName = assetName;
    }

    public String getAssetName(){
        return this.assetName;
    }
}
