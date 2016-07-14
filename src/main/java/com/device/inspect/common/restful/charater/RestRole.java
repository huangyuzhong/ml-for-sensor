package com.device.inspect.common.restful.charater;

import com.device.inspect.common.model.charater.Role;
import com.device.inspect.common.model.charater.RoleAuthority;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;

/**
 * Created by Administrator on 2016/7/12.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestRole {

    private Integer id;
    private String authority;
    private RestRoleAuthority roleAuthority;

    public RestRole(@NotNull Role role){
        this.id = role.getId();
        this.authority = role.getAuthority();
        this.roleAuthority = null==role.getAuthority()?null:new RestRoleAuthority(role.getRoleAuthority());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public RestRoleAuthority getRoleAuthority() {
        return roleAuthority;
    }

    public void setRoleAuthority(RestRoleAuthority roleAuthority) {
        this.roleAuthority = roleAuthority;
    }
}
