package com.device.inspect.common.query.charater;

import com.device.inspect.common.model.firm.Company;
import com.device.inspect.common.query.QueryFilter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Created by Administrator on 2016/8/31.
 */
public abstract class CompanyQueryFilter implements QueryFilter<Company>{
    @Override
    public abstract Predicate filterQuery(CriteriaBuilder cb, CriteriaQuery cq, String object, Root<Company> companyRoot) ;
}
