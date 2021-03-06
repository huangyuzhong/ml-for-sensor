package com.device.inspect.common.query.charater;

import com.device.inspect.common.model.charater.User;
import com.device.inspect.common.model.device.AlertCount;
import com.device.inspect.common.model.device.Device;
import com.device.inspect.common.query.Querier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by fgz on 2017/9/20.
 */
public class AlertCountQuery extends Querier<AlertCount>{

    @Autowired
    public AlertCountQuery(EntityManager entityManager) {
        super(entityManager, AlertCount.class);

        queryFilterMap.put("userId", new AlertCountQueryFilter() {
            @Override
            public Predicate filterQuery(CriteriaBuilder cb, CriteriaQuery cq, String object, Root<AlertCount> rootObject) {
                Join<AlertCount, Device> deviceJoin = rootObject.join("device",JoinType.LEFT);
                Predicate predicate = cb.equal(rootObject.get("device").get("id"), object);
                predicate = cb.or(predicate, cb.equal(deviceJoin.<User>get("manager").<String>get("id"), object));
                return predicate;
            }
        });

        queryFilterMap.put("deviceId", new AlertCountQueryFilter() {
            @Override
            public Predicate filterQuery(CriteriaBuilder cb, CriteriaQuery cq, String object, Root<AlertCount> rootObject) {
                return cb.equal(rootObject.get("device").get("id"), object);
            }
        });

        queryFilterMap.put("deviceTypeId", new AlertCountQueryFilter() {
            @Override
            public Predicate filterQuery(CriteriaBuilder cb, CriteriaQuery cq, String object, Root<AlertCount> rootObject) {
                return cb.equal(rootObject.get("device").get("deviceType").get("id"), object);
            }
        });

        queryFilterMap.put("inspectTypeId", new AlertCountQueryFilter() {
            @Override
            public Predicate filterQuery(CriteriaBuilder cb, CriteriaQuery cq, String object, Root<AlertCount> rootObject) {
                return cb.equal(rootObject.get("inspectType").get("id"), object);
            }
        });

        queryFilterMap.put("alertType", new AlertCountQueryFilter() {
            @Override
            public Predicate filterQuery(CriteriaBuilder cb, CriteriaQuery cq, String object, Root<AlertCount> rootObject) {
                return cb.equal(rootObject.get("type"), object);
            }
        });

        queryFilterMap.put("startTime", new AlertCountQueryFilter() {
            @Override
            public Predicate filterQuery(CriteriaBuilder cb, CriteriaQuery cq, String object, Root<AlertCount> rootObject) {
                return cb.and(cb.greaterThanOrEqualTo(rootObject.<Date>get("createDate"), new Date(Long.parseLong(object))));
            }
        });

        queryFilterMap.put("endTime", new AlertCountQueryFilter() {
            @Override
            public Predicate filterQuery(CriteriaBuilder cb, CriteriaQuery cq, String object, Root<AlertCount> rootObject) {
                return cb.and(cb.lessThanOrEqualTo(rootObject.<Date>get("finish"), new Date(Long.parseLong(object))));
            }
        });
    }

    @Override
    public Page<AlertCount> query(Map<String, String> queryParameter, int start, int limit, Sort sort) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<AlertCount> query = cb.createQuery(AlertCount.class);
        Root<AlertCount> objectRoot = setQueryWhere(query, cb, queryParameter);
        query = setOrderBy(query, sort, cb, objectRoot);
        query.select(objectRoot);
        objectRoot = setFetch(queryParameter, objectRoot);
        query.groupBy(objectRoot.get("id"));
        TypedQuery<AlertCount> q = entityManager.createQuery(query);
        long total = q.getResultList().size();
        q = setLimit(q, start, limit);
        List<AlertCount> results = q.getResultList();
        return new PageImpl<>(results, new PageRequest(start/limit,limit), total);
    }

}
