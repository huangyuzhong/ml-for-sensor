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

        queryFilterMap.put("enableSharing", new DeviceQueryFilter() {
            @Override
            public Predicate filterQuery(CriteriaBuilder cb, CriteriaQuery cq, String object, Root<Device> deviceRoot) {
                return cb.equal(deviceRoot.get("enableSharing"),object);
            }
        });


//        queryFilterMap.put("typeId", new DeviceQueryFilter() {
//            @Override
//            public Predicate filterQuery(CriteriaBuilder cb, CriteriaQuery cq, String object, Root<Device> deviceRoot) {
//                return cb.equal(deviceRoot.get("deviceType").get("id"), object);
//            }
//        });

        queryFilterMap.put("userId", new DeviceQueryFilter() {
            @Override
            public Predicate filterQuery(CriteriaBuilder cb, CriteriaQuery cq, String object, Root<Device> deviceRoot) {
                Join<Device, ScientistDevice> scientistDeviceJoin = deviceRoot.join("scientistDeviceList", JoinType.LEFT);
                Predicate predicate = cb.equal(deviceRoot.get("manager").get("id"),object);
                predicate = cb.or(predicate,cb.equal(scientistDeviceJoin.<User>get("scientist").<String>get("id"),object));
                return predicate;
            }
        });

        //查询出Device所属的Room
        queryFilterMap.put("roomId", new DeviceQueryFilter() {
            @Override
            public Predicate filterQuery(CriteriaBuilder cb, CriteriaQuery cq, String object, Root<Device> deviceRoot) {
//                Join<Device, ScientistDevice> scientistDeviceJoin = deviceRoot.join("scientistDeviceList", JoinType.LEFT);
                Predicate predicate = cb.equal(deviceRoot.get("room").get("id"),object);
//                predicate = cb.or(predicate,cb.equal(scientistDeviceJoin.<User>get("scientist").<String>get("id"),object));
                return predicate;
            }
        });

        //根据Device的MonitorDevice的参数online是否在线进行筛选
        queryFilterMap.put("onlineStatus", new DeviceQueryFilter() {
            @Override
            public Predicate filterQuery(CriteriaBuilder cb, CriteriaQuery cq, String object, Root<Device> deviceRoot) {
                return cb.equal(deviceRoot.get("monitorDevice").get("online"),object);
            }
        });

        //查询出Device所属的Floor
        queryFilterMap.put("floorId", new DeviceQueryFilter() {
            @Override
            public Predicate filterQuery(CriteriaBuilder cb, CriteriaQuery cq, String object, Root<Device> deviceRoot) {
                return cb.equal(deviceRoot.get("room").get("floor").get("id"),object);
            }
        });

        //查询出Device所属的Building
        queryFilterMap.put("buildingId", new DeviceQueryFilter() {
            @Override
            public Predicate filterQuery(CriteriaBuilder cb, CriteriaQuery cq, String object, Root<Device> deviceRoot) {
                return cb.equal(deviceRoot.get("room").get("floor").get("build").get("id"),object);
            }
        });

        //查询Device的alertType
        queryFilterMap.put("alertType", new DeviceQueryFilter() {
            @Override
            public Predicate filterQuery(CriteriaBuilder cb, CriteriaQuery cq, String object, Root<Device> deviceRoot) {
                return cb.equal(deviceRoot.get("status"),object);
            }
        });

        queryFilterMap.put("monitorCode", new DeviceQueryFilter() {
            @Override
            public Predicate filterQuery(CriteriaBuilder cb, CriteriaQuery cq, String object, Root<Device> deviceRoot) {
                return cb.equal(deviceRoot.get("monitorDevice").get("number"),object);
            }
        });
        //根据公司名称模糊查询
        queryFilterMap.put("serchCompanyName", new DeviceQueryFilter() {
            @Override
            public Predicate filterQuery(CriteriaBuilder cb, CriteriaQuery cq, String object, Root<Device> deviceRoot) {
                return cb.like(deviceRoot.get("manager").get("company").<String>get("name"),'%' + (String) object + '%');
            }
        });
//        根据设备种类查询
        queryFilterMap.put("unitType", new DeviceQueryFilter() {
            @Override
            public Predicate filterQuery(CriteriaBuilder cb, CriteriaQuery cq, String object, Root<Device> deviceRoot) {
                return cb.equal(deviceRoot.get("deviceType").get("id"),object);
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
        long total = q.getResultList().size();
        q = setLimit(q, start, limit);
        List<Device> results = q.getResultList();
        return new PageImpl<>(results, new PageRequest(start/limit,limit), total);
    }

}
