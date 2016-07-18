package com.device.inspect.common.repository.charater;

import com.device.inspect.common.model.charater.Role;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2016/7/8.
 */
public interface RoleRepository extends CrudRepository<Role,Integer> {
    public Role findByUserId(Integer UserId);

//    @Query("select o from Order o left join o.customer where o.customer.id = ?1 order by o.createDate desc")
//    @Query("select r from Role r left join r.user where r.user.company.id = ?1 and r. ")
//    public List<Role> findByUserCompanyIdAndRoleAuthorityId(Integer UserCompanyId,Integer RoleAuthorityId);
}
