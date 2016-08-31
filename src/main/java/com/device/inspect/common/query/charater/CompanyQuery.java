package com.device.inspect.common.query.charater;

import com.device.inspect.common.model.firm.Company;
import com.device.inspect.common.query.Querier;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Created by Administrator on 2016/8/31.
 */
public class CompanyQuery extends Querier<Company> {
    @Autowired
    public CompanyQuery(EntityManager entityManager){
        super(entityManager, Company.class);

        queryFilterMap.put("name", new CompanyQueryFilter() {
            @Override
            public Predicate filterQuery(CriteriaBuilder cb, CriteriaQuery cq, String object, Root<Company> companyRoot) {
                return cb.like(companyRoot.<String>get("name"), '%' + (String) object + '%');
            }
        });
        queryFilterMap.put("address", new CompanyQueryFilter() {
            @Override
            public Predicate filterQuery(CriteriaBuilder cb, CriteriaQuery cq, String object, Root<Company> companyRoot) {
                return cb.like(companyRoot.<String>get("address"), '%' + (String) object + '%');
            }
        });

        queryFilterMap.put("businessId", new CompanyQueryFilter() {
            @Override
            public Predicate filterQuery(CriteriaBuilder cb, CriteriaQuery cq, String object, Root<Company> companyRoot) {
                return cb.equal(companyRoot.get("businessMan").get("id"),object);
            }
        });
    }
}
