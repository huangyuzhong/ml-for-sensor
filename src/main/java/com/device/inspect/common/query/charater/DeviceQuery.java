package com.device.inspect.common.query.charater;

import com.device.inspect.common.model.charater.User;
import com.device.inspect.common.model.device.Device;
import com.device.inspect.common.model.device.DeviceFloor;
import com.device.inspect.common.model.device.ScientistDevice;
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
 * Created by Administrator on 2016/7/19.
 */
public class DeviceQuery extends Querier<Device> {
    @Autowired
    public DeviceQuery(EntityManager entityManager) {
        super(entityManager, Device.class);

        queryFilterMap.put("code", new DeviceQueryFilter() {
            @Override
            public Predicate filterQuery(CriteriaBuilder cb, CriteriaQuery cq, String object, Root<Device> deviceRoot) {
                return cb.equal(deviceRoot.get("code"), object);
            }
        });
        queryFilterMap.put("name", new DeviceQueryFilter() {
            @Override
            public Predicate filterQuery(CriteriaBuilder cb, CriteriaQuery cq, String object, Root<Device> deviceRoot) {
                return cb.equal(deviceRoot.get("name"),object);
            }
        });

        queryFilterMap.put("typeId", new DeviceQueryFilter() {
            @Override
            public Predicate filterQuery(CriteriaBuilder cb, CriteriaQuery cq, String object, Root<Device> deviceRoot) {
                return cb.equal(deviceRoot.get("deviceType").get("id"), object);
            }
        });

        queryFilterMap.put("userId", new DeviceQueryFilter() {
            @Override
            public Predicate filterQuery(CriteriaBuilder cb, CriteriaQuery cq, String object, Root<Device> deviceRoot) {
                Join<Device, ScientistDevice> scientistDeviceJoin = deviceRoot.join("scientistDeviceList", JoinType.INNER);
                Predicate predicate = cb.equal(deviceRoot.get("manager").get("id"),object);
                predicate = cb.or(predicate,cb.equal(scientistDeviceJoin.<User>get("scientist").<String>get("id"),object));
                return predicate;
            }
        });
        queryFilterMap.put("monitorCode", new DeviceQueryFilter() {
            @Override
            public Predicate filterQuery(CriteriaBuilder cb, CriteriaQuery cq, String object, Root<Device> deviceRoot) {
                return cb.equal(deviceRoot.get("monitorDevice").get("number"),object);
            }
        });
        //根据公司名称查询
        queryFilterMap.put("serchCompanyName", new DeviceQueryFilter() {
            @Override
            public Predicate filterQuery(CriteriaBuilder cb, CriteriaQuery cq, String object, Root<Device> deviceRoot) {
                return cb.equal(deviceRoot.get("manager").get("company").get("name"),object);
            }
        });

        queryFilterMap.put("enable", new DeviceQueryFilter() {
            @Override
            public Predicate filterQuery(CriteriaBuilder cb, CriteriaQuery cq, String object, Root<Device> deviceRoot) {
                return cb.equal(deviceRoot.get("enable"),object);
            }
        });

//        queryFilterMap.put("scientistId", new DeviceQueryFilter() {
//            @Override
//            public Predicate filterQuery(CriteriaBuilder cb, CriteriaQuery cq, String object, Root<Device> deviceRoot) {
//                Join<Device, ScientistDevice> scientistDeviceJoin = deviceRoot.join("scientistDeviceList", JoinType.INNER);
//                return cb.equal(scientistDeviceJoin.<User>get("scientist").<String>get("id"),object);
//            }
//        });

    }

    @Override
    public Page<Device> query(Map<String, String> queryParamenter, int start, int limit, Sort sort) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Device> query = cb.createQuery(Device.class);
        Root<Device> objectRoot = setQueryWhere(query, cb, queryParamenter);
        query = setOrderBy(query, sort, cb, objectRoot);
        query.select(objectRoot);
        objectRoot = setFetch(queryParamenter, objectRoot);
        query.groupBy(objectRoot.get("id"));
        TypedQuery<Device> q = entityManager.createQuery(query);
        q = setLimit(q, start, limit);
        List<Device> results = q.getResultList();
        return new PageImpl<>(results, new PageRequest(start/limit,limit), queryCount(cb, queryParamenter));
    }

}
