package com.device.inspect.common.query.charater;

import com.alibaba.fastjson.JSON;
import com.device.inspect.common.model.charater.Role;
import com.device.inspect.common.model.charater.User;
import com.device.inspect.common.query.Querier;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.List;

/**
 * Created by Administrator on 2016/7/18.
 */
public class UserQuery extends Querier<User> {
    @Autowired
    public UserQuery(EntityManager entityManager) {
        super(entityManager, User.class);

        queryFilterMap.put("userName", new UserQueryFilter() {
            @Override
            public Predicate filterQuery(CriteriaBuilder cb, CriteriaQuery cq, String object, Root<User> userRoot) {
                return cb.like(userRoot.<String>get("userName"), '%' + (String) object + '%');
            }
        });

        queryFilterMap.put("mobile", new UserQueryFilter() {
            @Override
            public Predicate filterQuery(CriteriaBuilder cb, CriteriaQuery cq, String object, Root<User> userRoot) {
                return cb.equal(userRoot.get("mobile"),object);
            }
        });

        queryFilterMap.put("jobNum", new UserQueryFilter() {
            @Override
            public Predicate filterQuery(CriteriaBuilder cb, CriteriaQuery cq, String object, Root<User> userRoot) {
                return cb.equal(userRoot.get("jobNum"),object);
            }
        });

        queryFilterMap.put("authorityId", new UserQueryFilter() {
            @Override
            public Predicate filterQuery(CriteriaBuilder cb, CriteriaQuery cq, String object, Root<User> userRoot) {
                List<Integer> list = JSON.parseObject(object,List.class);
                Join<User, Role> userRoleJoin = userRoot.join("roles", JoinType.RIGHT);
                if (null!=list&&list.size()<=0)
                    return null;
                Predicate predicate = cb.equal(userRoleJoin.get("roleAuthority").get("id"),list.get(0).toString());
                for (int i = 1; i < list.size(); i++) {
                    predicate = cb.or(predicate,cb.equal(userRoleJoin.get("roleAuthority").get("id"),list.get(i).toString()));
                }
                return predicate;
            }
        });

        queryFilterMap.put("companyId", new UserQueryFilter() {
            @Override
            public Predicate filterQuery(CriteriaBuilder cb, CriteriaQuery cq, String object, Root<User> userRoot) {
                return cb.equal(userRoot.get("company").get("id"),object);
            }
        });




    }
}
