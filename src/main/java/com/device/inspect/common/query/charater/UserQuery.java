package com.device.inspect.common.query.charater;

import com.alibaba.fastjson.JSON;
import com.device.inspect.common.model.charater.Role;
import com.device.inspect.common.model.charater.User;
import com.device.inspect.common.query.Querier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;
import java.util.Map;

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
                Join<User, Role> userRoleJoin = userRoot.join("roles", JoinType.LEFT);
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
//                return cq.groupBy(userRoot.get("id")).getRestriction();
                return cb.equal(userRoot.get("company").get("id"),object);
            }
        });
    }

    @Override
    public Page<User> query(Map<String, String> queryParamenter, int start, int limit, Sort sort) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> objectRoot = setQueryWhere(query, cb, queryParamenter);
        query = setOrderBy(query, sort, cb, objectRoot);
        query.select(objectRoot);
        objectRoot = setFetch(queryParamenter, objectRoot);

        query.groupBy(objectRoot.get("id"));
        TypedQuery<User> q = entityManager.createQuery(query);
        q = setLimit(q, start, limit);
        List<User> results = q.getResultList();
        return new PageImpl<>(results, new PageRequest(start/limit,limit), queryCount(cb, queryParamenter));
    }

}
