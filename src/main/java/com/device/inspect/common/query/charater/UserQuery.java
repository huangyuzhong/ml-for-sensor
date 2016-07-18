package com.device.inspect.common.query.charater;

import com.device.inspect.common.model.charater.User;
import com.device.inspect.common.query.Querier;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

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
                return cb.equal(userRoot.get("userName"), object);
            }
        });

        queryFilterMap.put("mobile", new UserQueryFilter() {
            @Override
            public Predicate filterQuery(CriteriaBuilder cb, CriteriaQuery cq, String object, Root<User> userRoot) {
                return cb.equal(userRoot.get("mobile"),object);
            }
        });

        queryFilterMap.put("authorityId", new UserQueryFilter() {
            @Override
            public Predicate filterQuery(CriteriaBuilder cb, CriteriaQuery cq, String object, Root<User> userRoot) {
                return cb.equal(userRoot.get("role").get("roleAuthority").get("id"),object);
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
