package com.device.inspect.common.service;

import com.device.inspect.common.model.record.OfflineHourUnit;
import org.springframework.stereotype.Component;

import java.util.Vector;

/**
 * Created by zyclincoln on 4/27/17.
 */
@Component
public class OfflineHourQueue {
    public Vector<OfflineHourUnit> recalculateRequest;
}
