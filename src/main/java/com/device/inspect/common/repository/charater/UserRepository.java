package com.device.inspect.common.repository.charater;

import com.device.inspect.common.model.charater.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Administrator on 2016/7/8.
 */
public interface UserRepository extends CrudRepository<User,Integer> {
    public User findByName(String name);


    public List<User> findByCompanyId(Integer CompanyId);
}
