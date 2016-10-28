package com.device.inspect.common.query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.*;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/17.
 */
public abstract class Querier<T> {

        public static final String FETCH_NAME = "FETCH_NAME";

        private static final Logger LOGGER = LogManager.getLogger(Querier.class);

        protected EntityManager entityManager;

        protected Map<String, QueryFilter<T>> queryFilterMap = new HashMap<>();

        private Class<T> typeClass;

        public Querier(EntityManager entityManager, Class<T> tClass) {
                this.entityManager = entityManager;
                this.typeClass = tClass;
        }


        public Page<T> query(Map<String, String> queryParamenter, int start, int limit, Sort sort) {
                CriteriaBuilder cb = entityManager.getCriteriaBuilder();
                CriteriaQuery<T> query = cb.createQuery(typeClass);
                Root<T> objectRoot = setQueryWhere(query, cb, queryParamenter);
                query = setOrderBy(query, sort, cb, objectRoot);
                query.select(objectRoot);
                objectRoot = setFetch(queryParamenter, objectRoot);
                TypedQuery<T> q = entityManager.createQuery(query);
                q = setLimit(q, start, limit);
                List<T> results = q.getResultList();
                return new PageImpl<>(results, new PageRequest(start/limit,limit), queryCount(cb, queryParamenter));
        }

        protected TypedQuery<T> setLimit(TypedQuery<T> q, int start, int limit) {
                return q.setFirstResult(start).setMaxResults(limit);
        }

        public Page<T> query(Map<String, String> queryParamenter, Pageable pageable) {
                if (pageable != null) {
                        return this.query(queryParamenter, pageable.getOffset(), pageable.getPageSize(), pageable.getSort());
                }
                return this.query(queryParamenter, 0, 100, null);
        }

        public Page<T> query(Map<String, String> queryParamenter) {
                return this.query(queryParamenter, 0, 100, null);
        }

        protected Long queryCount(CriteriaBuilder cb, Map<String, String> queryParamenter) {
                CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
                Root<T> shopRoot = setQueryWhere(countQuery, cb, queryParamenter);
                countQuery.select(cb.count(shopRoot));
                TypedQuery<Long> q = entityManager.createQuery(countQuery);
                return q.getSingleResult();
        }

        protected <T1> Root<T> setQueryWhere(CriteriaQuery<T1> cq, CriteriaBuilder cb, Map<String, String> queryParamenter) {
                Root<T> objectRoot = cq.from(typeClass);
                Predicate predicate = null;
                for (String parameter : queryParamenter.keySet()) {
                        String parCotent = queryParamenter.get(parameter);
                        if (parCotent == null || parCotent.equals("")) continue;
                        QueryFilter<T> queryFilter = queryFilterMap.get(parameter);

                        if (queryFilter == null) {
                                if (parameter.equals(FETCH_NAME)) continue;
                                LOGGER.error("key:" + parameter + " not have support filter");
                                throw new RuntimeException("no support query filter");
                        }
                        Predicate newPredicate = queryFilter.filterQuery(cb, cq, parCotent, objectRoot);
                        if (predicate == null) {
                                predicate = newPredicate;
                        } else {
                                predicate = cb.and(predicate, newPredicate);
                        }
                }
                if (predicate != null)
                        cq.where(predicate);
                return objectRoot;
        }

        protected CriteriaQuery<T> setOrderBy(CriteriaQuery<T> query, Sort sort, CriteriaBuilder cb, Root<T> root) {
                if (sort == null) return query;
                List<Order> orders = new LinkedList<>();

                for (Sort.Order order : sort) {
                        Order certiaOrder = null;
                        if (order.isAscending() || order.isIgnoreCase()) {
                                certiaOrder = cb.asc(root.get(order.getProperty()));
                        } else {
                                certiaOrder = cb.desc(root.get(order.getProperty()));
                        }
                        orders.add(certiaOrder);
                }
                query.orderBy(orders);
                return query;
        }

        protected Root<T> setFetch(Map<String, String> queryParamenter, Root<T> root) {
                String propertyName = queryParamenter.get(FETCH_NAME);
                if (propertyName == null) return root;
                for (String str : propertyName.split(",")) {
                        root.fetch(str);
                }
                queryParamenter.remove(FETCH_NAME);
                return root;
        }

        protected EntityQuery<T> query(Class<T> tClass) {
                return new EntityQuery<>(entityManager, tClass);
        }
}
