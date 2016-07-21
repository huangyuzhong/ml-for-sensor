package com.device.inspect.common.query.charater;

import com.device.inspect.common.model.charater.User;
import com.device.inspect.common.model.device.Device;
import com.device.inspect.common.query.Querier;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

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
                return cb.equal(deviceRoot.get("manager").get("id"),object);
            }
        });

    }

}
