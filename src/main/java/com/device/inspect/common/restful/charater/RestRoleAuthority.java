package com.device.inspect.common.restful.charater;

import com.device.inspect.common.model.charater.RoleAuthority;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;

/**
 * Created by Administrator on 2016/7/12.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestRoleAuthority {
    private Integer id;
    private String name;


    public RestRoleAuthority(@NotNull RoleAuthority roleAuthority){
        this.id = roleAuthority.getId();
        this.name = roleAuthority.getName();
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
}
