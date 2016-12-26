package com.device.inspect.common.restful.device;

import com.device.inspect.common.model.device.DeviceVersion;
import com.fasterxml.jackson.annotation.JsonInclude;
import javax.validation.constraints.NotNull;

import java.util.Date;

/**
 * Created by Straight on 2016/12/20.
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
    private String fileName;

    public RestDeviceVersion(@NotNull DeviceVersion deviceVersion) {
        this.id=deviceVersion.getId();
        this.name=deviceVersion.getName();
        this.url=deviceVersion.getUrl();
        this.firstCode=deviceVersion.getFirstCode();
        this.secondCode=deviceVersion.getSecondCode();
        this.thirdCode=deviceVersion.getThirdCode();
        this.forthCode=deviceVersion.getForthCode();
        this.type=deviceVersion.getType();
        this.createDate=deviceVersion.getCreateDate();
        this.fileName=deviceVersion.getFileName();
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFirstCode() {
        return firstCode;
    }

    public void setFirstCode(String firstCode) {
        this.firstCode = firstCode;
    }

    public String getSecondCode() {
        return secondCode;
    }

    public void setSecondCode(String secondCode) {
        this.secondCode = secondCode;
    }

    public String getThirdCode() {
        return thirdCode;
    }

    public void setThirdCode(String thirdCode) {
        this.thirdCode = thirdCode;
    }

    public String getForthCode() {
        return forthCode;
    }

    public void setForthCode(String forthCode) {
        this.forthCode = forthCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
