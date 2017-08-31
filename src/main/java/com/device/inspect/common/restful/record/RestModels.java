package com.device.inspect.common.restful.record;

import com.device.inspect.common.model.record.Models;

/**
 * Created by fgz on 2017/8/31.
 */
public class RestModels {

    private Integer id;
    private String name;
    private String description;

    public RestModels() {
    }

    public RestModels(Models models) {
        this.id = models.getId();
        this.name = models.getName();
        this.description = models.getDescription();
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
