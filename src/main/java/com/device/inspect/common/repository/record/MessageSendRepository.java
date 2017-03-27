package com.device.inspect.common.repository.record;

import com.device.inspect.common.model.record.MessageSend;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Administrator on 2016/10/18.
 */
public interface MessageSendRepository extends CrudRepository<MessageSend,Integer> {
    public MessageSend findTopByUserIdAndDeviceIdAndEnableOrderByCreateDesc(Integer UserId,Integer DeviceId,Integer Enable);
    public MessageSend findTopByUserIdAndDeviceIdAndEnableAndDeviceInspectIdOrderByCreateDesc(Integer UserId, Integer DeviceId, Integer Enable, Integer DeviceInspectId);
}
