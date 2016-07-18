package com.device.inspect.common.query;


import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public interface QueryFilter<T> {
    public abstract Predicate filterQuery(CriteriaBuilder cb, CriteriaQuery cq, String object, Root<T> rootObject);
}