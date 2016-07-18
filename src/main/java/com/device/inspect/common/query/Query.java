package com.device.inspect.common.query;

import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 *
 */
public abstract class Query<R,T> {
    protected CriteriaBuilder cb;

    protected CriteriaQuery<T> query;

    protected Root<R> root;

    protected EntityManager entityManager;

    protected Predicate predicate = null;

    protected Pageable pageable;

    public Query(EntityManager entityManager, CriteriaBuilder cb, Class<T> retClass, Class<R> rootClass) {
        this.entityManager = entityManager;
        this.cb = cb;
        this.query = cb.createQuery(retClass);
        this.root = query.from(rootClass);
    }

    public <P> EntityCondtion<P,R,T> condtion(String propertyName) {
        return new EntityCondtion<>(cb,this, root.<P>get(propertyName),true);
    }

    public <P> EntityCondtion<P,R,T> and(String propertyName) {
        return new EntityCondtion<>(cb,this, root.<P>get(propertyName),true);
    }

    public <P> EntityCondtion<P,R,T> or(String propertyName) {
        return new EntityCondtion<>(cb,this, root.<P>get(propertyName),false);
    }

    Query<R,T> setPredicate(Predicate predicate, boolean isAnd) {
        if(this.predicate == null) {
            this.predicate = predicate;
        }else{
            if(isAnd) {
                this.predicate = cb.and(predicate);
            }else{
                this.predicate = cb.or(predicate);
            }
        }
        return this;
    }

    public Query<R,T> fetch(String entityName) {
        root.fetch(entityName);
        return this;
    }

    public abstract TypedQuery<T> execute();

    public Query<R,T> page(Pageable pageable) {
        this.pageable = pageable;
        return this;
    }
}
