package com.device.inspect.common.repository.record;

import com.device.inspect.common.model.record.MessageReceive;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by hwd on 2017/10/1.
 */
public interface MessageReceiveRepository extends CrudRepository<MessageReceive, Integer> {

    List<MessageReceive> findTopByUserIdAndContentAndStatusOrderByCreateDateDesc(Integer UserId, String content,
                                                                                 Integer status);
}
