package com.device.inspect.common.model.charater;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Administrator on 2016/7/7.
 */
@Entity
@Table(name = "role_authority")
public class RoleAuthority {
    private Integer id;
    private String name;

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
}
