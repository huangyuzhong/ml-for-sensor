package com.device.inspect.common.repository.record;

import com.device.inspect.common.model.record.MLResults;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by fgz on 2017/9/1.
 */
public interface MLResultsRepository extends CrudRepository<MLResults, String> {
    @Query("select u from MLResults u where u.id.deviceId = ?1 and u.id.inspectPara = ?2")
    public MLResults findByDeviceIdAndInspectPara(String deviceId, String inspectPara);
}
