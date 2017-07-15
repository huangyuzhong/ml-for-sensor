package com.device.inspect.common.model.record;

import com.device.inspect.common.model.firm.Company;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


@Entity
@Table(name="company_asset")
public class CompanyAsset{
    private Integer id;
    private Company company;
    private String assetAddress;
    private String assetName;

    @Id
    @GeneratedValue()
    public void setId(Integer id){
        this.id = id;
    }

    public Integer getId() {
        return this.id;
    }

    @ManyToOne()
    @JoinColumn(name = "company_id")
    @NotNull
    public void setCompany(Company company){
        this.company = company;
    }

    public Company getCompany(){
        return this.company = company;
    }

    @Column(name = "asset_address")
    @NotNull
    public void setAssetAddress(String assetAddress){
        this.assetAddress = assetAddress;
    }

    public String getAssetAddress(){
        return this.assetAddress;
    }

    @Column(name = "asset_name")
    @NotNull
    public void setAssetName(String assetName){
        this.assetName = assetName;
    }

    public String getAssetName(){
        return this.assetName;
    }
}