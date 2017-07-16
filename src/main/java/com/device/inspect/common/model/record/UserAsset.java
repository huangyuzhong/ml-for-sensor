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
    @GeneratedValue
    private Integer id;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

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

    public void setUser(User user){
        this.user = user;
    }

    public User getUser(){
        return this.user = user;
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
