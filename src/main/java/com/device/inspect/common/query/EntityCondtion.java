package com.device.inspect.common.query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.Date;


public class EntityCondtion<Y,R,T> {

    CriteriaBuilder cb;

    Query<R,T> entityQuery;

    Path<Y> entityPath;

    boolean isAnd;

    public EntityCondtion(CriteriaBuilder cb, Query<R, T> entityQuery, Path<Y> entityPath, boolean isAnd) {
        this.cb = cb;
        this.entityQuery = entityQuery;
        this.entityPath = entityPath;
        this.isAnd = isAnd;
    }

    public <P> EntityCondtion<P,R,T> get(String propertyName) {
        return new EntityCondtion<>(cb,entityQuery,entityPath.<P>get(propertyName),isAnd);
    }

    public Query<R,T> equal(Y object) {
        Predicate predicate = cb.equal(entityPath,object);
        return entityQuery.setPredicate(predicate, isAnd);
    }

    public Query<R,T> prefixlike(String object) {
        Predicate predicate = cb.like(entityPath.as(String.class), "%" + object);
        return entityQuery.setPredicate(predicate, isAnd);
    }

    public Query<R,T> like(String object) {
        Predicate predicate = cb.like(entityPath.as(String.class), "%" + object + "%");
        return entityQuery.setPredicate(predicate, isAnd);
    }

    public Query<R,T> greateThan(Integer object) {
        Predicate predicate = cb.greaterThan(entityPath.as(object.getClass()), object);
        return entityQuery.setPredicate(predicate, isAnd);
    }

    public Query<R,T> greateThan(Float object) {
        Predicate predicate = cb.greaterThan(entityPath.as(object.getClass()), object);
        return entityQuery.setPredicate(predicate, isAnd);
    }

    public Query<R,T> greateThan(Date object) {
        Predicate predicate = cb.greaterThan(entityPath.as(object.getClass()), object);
        return entityQuery.setPredicate(predicate, isAnd);
    }

    public Query<R,T> lessThan(Integer object) {
        Predicate predicate = cb.lessThan(entityPath.as(object.getClass()), object);
        return entityQuery.setPredicate(predicate, isAnd);
    }

    public Query<R,T> lessThan(Float object) {
        Predicate predicate = cb.lessThan(entityPath.as(object.getClass()), object);
        return entityQuery.setPredicate(predicate, isAnd);
    }

    public Query<R,T> lessThan(Date object) {
        Predicate predicate = cb.lessThan(entityPath.as(object.getClass()), object);
        return entityQuery.setPredicate(predicate, isAnd);
    }


}
