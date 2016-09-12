package com.device.inspect.common.restful.device;

import com.device.inspect.common.model.device.Files;
import com.device.inspect.common.model.device.Files;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by Administrator on 2016/7/20.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestFile {
    private Integer id;
    private String url;
    private String type;
    private String description;
    private Integer enable;
    private Date createDate;

    public RestFile(@NotNull Files file) {
        this.id = file.getId();
        this.url = file.getUrl();
        this.type = file.getType();
        this.description = file.getDescription();
        this.createDate = file.getCreateDate();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
