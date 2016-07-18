package com.device.inspect.common.model.charater;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

/**
 * Created by Administrator on 2016/7/7.
 */
@Entity
@Table(name = "roles")
public class Role implements GrantedAuthority {

    private Integer id;
    private User user;
    private String authority;
    private RoleAuthority roleAuthority;

    @Id
    @GeneratedValue()
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne()
    @JoinColumn(name = "user_id")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    @OneToOne()
    @JoinColumn(name = "role_auth_id")
    public RoleAuthority getRoleAuthority() {
        return roleAuthority;
    }

    public void setRoleAuthority(RoleAuthority roleAuthority) {
        this.roleAuthority = roleAuthority;
    }
}
