package com.device.inspect.common.model.charater;

import javax.persistence.*;

/**
 * Created by Administrator on 2016/7/7.
 */
@Entity
@Table(name = "roles")
public class Role {

    private Integer id;
    private String userName;
    private String roleName;
    private RoleAuthority authority;

    @Id
    @GeneratedValue()
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "user_name")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Column(name = "role_name")
    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @ManyToOne()
    @JoinColumn(name = "role_auth_id")
    public RoleAuthority getAuthority() {
        return authority;
    }

    public void setAuthority(RoleAuthority authority) {
        this.authority = authority;
    }
}
