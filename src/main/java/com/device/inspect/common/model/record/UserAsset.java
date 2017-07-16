package com.device.inspect.common.model.record;

import com.device.inspect.common.model.charater.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by zyclincoln on 7/15/17.
 */
@Entity
@Table(name = "user_asset")
public class UserAsset {
    @Id
    private Integer id;
    private User user;
    private String assetAddress;
    private String assetName;

    @Id
    @GeneratedValue
    public void setId(Integer id){
        this.id = id;
    }

    public Integer getId() {
        return this.id;
    }

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id")
    @NotNull
    public void setUser(User user){
        this.user = user;
    }

    public User getUser(){
        return this.user = user;
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
