package com.device.inspect.common.repository.firm;

import com.device.inspect.common.model.firm.Company;
import org.hibernate.validator.constraints.CreditCardNumber;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Administrator on 2016/7/8.
 */
public interface CompanyRepository extends CrudRepository<Company,Integer> {
    List<Company> findAll();
}
