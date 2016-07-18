package com.device.inspect.common.query;

import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

public class EntityQuery<T> extends Query<T,T> {

    public EntityQuery(EntityManager entityManager,Class<T> tClass) {
        super(entityManager, entityManager.getCriteriaBuilder(), tClass, tClass);
    }

    public TypedQuery<T> execute() {
        query.select(root);
        if(predicate != null) query.where(predicate);
        TypedQuery<T> q = entityManager.createQuery(query);
        q = setPageLimit(q, pageable);
        return q;
    }

    protected TypedQuery<T> setPageLimit(TypedQuery<T> q, Pageable pageable) {
        if (pageable == null) return q;
        return q.setFirstResult(pageable.getOffset()).setMaxResults(pageable.getPageSize());
    }



}