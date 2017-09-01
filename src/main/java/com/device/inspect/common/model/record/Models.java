package com.device.inspect.common.model.record;

import javax.persistence.*;

/**
 * Created by fgz on 2017/8/28.
 */
@Entity
@Table(name = "models")
public class Models {

    private Integer id;
    private String name;
    private String url;
    private String api;
    private String description;

    @Id
    @GeneratedValue()
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "url")
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Column(name = "api")
    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
