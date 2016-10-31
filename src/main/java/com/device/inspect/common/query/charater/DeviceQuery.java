package com.device.inspect.common.query.charater;

import com.device.inspect.common.model.charater.User;
import com.device.inspect.common.model.device.Device;
import com.device.inspect.common.model.device.DeviceFloor;
import com.device.inspect.common.model.device.ScientistDevice;
import com.device.inspect.common.query.Querier;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;

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

}
