package com.device.inspect.common.query.charater;

import com.device.inspect.common.model.device.AlertCount;
import com.device.inspect.common.query.QueryFilter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Created by fgz on 2017/9/20.
 */
public abstract class AlertCountQueryFilter implements QueryFilter<AlertCount> {
    @Override
    public abstract Predicate filterQuery(CriteriaBuilder cb, CriteriaQuery cq, String object, Root<AlertCount> rootObject);
}
