package com.device.inspect.common.repository.charater;

import com.device.inspect.common.model.charater.RoleAuthority;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Administrator on 2016/7/8.
 */
public interface RoleAuthorityRepository extends CrudRepository<RoleAuthority,Integer> {
    public RoleAuthority findByName(String name);
    public List<RoleAuthority> findByParent(Integer Parent);
}
