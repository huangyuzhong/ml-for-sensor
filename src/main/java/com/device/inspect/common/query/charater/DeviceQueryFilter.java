package com.device.inspect.common.query.charater;

import com.device.inspect.common.model.device.Device;
import com.device.inspect.common.query.QueryFilter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Created by Administrator on 2016/7/19.
 */
public  abstract class DeviceQueryFilter implements QueryFilter<Device> {
    @Override
    public abstract Predicate filterQuery(CriteriaBuilder cb, CriteriaQuery cq, String object, Root<Device> rootObject) ;


}
