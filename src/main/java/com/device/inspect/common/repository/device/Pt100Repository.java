package com.device.inspect.common.repository.device;

import com.device.inspect.common.model.device.Pt100;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by Straight on 2016/12/5.
 */
public interface Pt100Repository extends CrudRepository<Pt100,Float> {
    @Query(value = "SELECT p FROM Pt100 p WHERE p.resistance=:resistance")
    Pt100 findByResistance(@Param("resistance")Float Resistance);
//    Pt100 findByDeviceTypeIdAndResistance(Integer DeviceTypeId,Float Resistance);
    //使用默认表进行查询从大到小排列
    @Query(value = "SELECT p FROM Pt100 p WHERE p.resistance <:resistance ORDER BY resistance DESC")
    List<Pt100> findByResistanceAfterOrderByResistanceDESC(@Param("resistance")Float Resistance);
    //使用默认表进行查询从小到大排列
    @Query(value = "SELECT p FROM Pt100 p WHERE p.resistance >:resistance ORDER BY resistance ASC")
    List<Pt100> findByResistanceBeforeOrderByResistanceASC(@Param("resistance")Float Resistance);
//    //查询出比传来电阻大的pt100然后按正序排列从小到大排列
//    List<Pt100> findByDeviceTypeIdAndResistanceAfterOrderByASC(Integer DeviceTypeId,Float Resistance);
//    //倒叙排列从大到小排列
//    List<Pt100> findByDeviceTypeIdAndResistanceBeforeOrderByDESC(Integer DeviceTypeId,Float Resistance);
}
