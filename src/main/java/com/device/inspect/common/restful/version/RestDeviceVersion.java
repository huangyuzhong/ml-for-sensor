package com.device.inspect.common.restful.version;

import com.device.inspect.common.model.device.DeviceVersion;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by Straight on 2016/11/4.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestDeviceVersion {
    private Integer id;
    private String name;
    private String url;
    private String firstCode;        //版本号
    private String secondCode;
    private String thirdCode;
    private String forthCode;
    private String type;
    private Date createDate;
    public RestDeviceVersion(@NotNull DeviceVersion deviceVersion){
        this.id=deviceVersion.getId();
        this.name=deviceVersion.getName();
        this.url=deviceVersion.getUrl();
        this.firstCode=deviceVersion.getFirstCode();
        this.secondCode=deviceVersion.getSecondCode();
        this.thirdCode=deviceVersion.getThirdCode();
        this.forthCode=deviceVersion.getForthCode();
        this.type=deviceVersion.getType();
        this.createDate=deviceVersion.getCreateDate();
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setFirstCode(String firstCode) {
        this.firstCode = firstCode;
    }

    public String getFirstCode() {
        return firstCode;
    }

    public void setSecondCode(String secondCode) {
        this.secondCode = secondCode;
    }

    public String getSecondCode() {
        return secondCode;
    }

    public void setThirdCode(String thirdCode) {
        this.thirdCode = thirdCode;
    }

    public String getThirdCode() {
        return thirdCode;
    }

    public void setForthCode(String forthCode) {
        this.forthCode = forthCode;
    }

    public String getForthCode() {
        return forthCode;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getCreateDate() {
        return createDate;
    }
}
