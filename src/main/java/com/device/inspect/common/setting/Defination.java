package com.device.inspect.common.setting;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by zyclincoln on 7/17/17.
 */
public class Defination {
    public static final Integer ONCHAIN_DEAL_STATUS_DEAL = 0;
    public static final Integer ONCHAIN_DEAL_STATUS_CANCELLED = 1;
    public static final Integer ONCHAIN_DEAL_STATUS_EXECUTING = 2;
    public static final Integer ONCHAIN_DEAL_STATUS_WAITING_MUTUAL_CONFIRM = 3;
    public static final Integer ONCHAIN_DEAL_STATUS_WAITING_LESSOR_CONFIRM = 4;
    public static final Integer ONCHAIN_DEAL_STATUS_WAITING_LESSEE_CONFIRM = 5;
    public static final Integer ONCHAIN_DEAL_STATUS_FINISH = 6;

    public static final String ONCHAIN_DEAL_OPERATION_CONFIRM = "confirm";
    public static final Set<String> ONCHAIN_DEAL_OPERATION_SET = new HashSet<String>(){{
        add(ONCHAIN_DEAL_OPERATION_CONFIRM);
    }};

    public static final Map<Integer, String> DEAL_STATUS_TRANSFER_MAP = new HashMap<Integer, String>(){{
        put(ONCHAIN_DEAL_STATUS_DEAL, "Device booked");
        put(ONCHAIN_DEAL_STATUS_CANCELLED, "Cancelled before start");
        put(ONCHAIN_DEAL_STATUS_EXECUTING, "Using Device");
        put(ONCHAIN_DEAL_STATUS_WAITING_MUTUAL_CONFIRM, "Stop use, waiting mutual confirm");
        put(ONCHAIN_DEAL_STATUS_WAITING_LESSOR_CONFIRM, "Waiting lessor confirm");
        put(ONCHAIN_DEAL_STATUS_WAITING_LESSEE_CONFIRM, "Waiting lessee confirm");
        put(ONCHAIN_DEAL_STATUS_FINISH, "Deal finished");
    }};
}
