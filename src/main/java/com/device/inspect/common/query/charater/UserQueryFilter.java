package com.device.inspect.common.query.charater;

import com.device.inspect.common.model.charater.User;
import com.device.inspect.common.query.QueryFilter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Created by Administrator on 2016/7/18.
 */
public abstract class UserQueryFilter   implements QueryFilter<User> {
    public abstract Predicate filterQuery(CriteriaBuilder cb,CriteriaQuery cq,String object,Root<User> userRoot);
}
