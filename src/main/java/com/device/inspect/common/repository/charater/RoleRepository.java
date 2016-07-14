package com.device.inspect.common.repository.charater;

import com.device.inspect.common.model.charater.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2016/7/8.
 */
public interface RoleRepository extends CrudRepository<Role,Integer> {
    public Set<Role> findByUserId(Integer UserId);
}
