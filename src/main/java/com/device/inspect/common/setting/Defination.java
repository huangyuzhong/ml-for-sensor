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
    public static final Integer ONCHAIN_DEAL_STATUS_EXECUTING_WITH_ALERT = 7;
    public static final Integer ONCHAIN_DEAL_STATUS_WAITING_MUTUAL_CONFIRM_WITH_ALERT = 8;
    public static final Integer ONCHAIN_DEAL_STATUS_WAITING_LESSOR_CONFIRM_WITH_ALERT = 9;
    public static final Integer ONCHAIN_DEAL_STATUS_WAITING_LESSEE_CONFIRM_WITH_ALERT = 10;
    public static final Integer ONCHAIN_DEAL_STATUS_FINISH_WITH_ALERT = 11;

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

        put(ONCHAIN_DEAL_STATUS_EXECUTING_WITH_ALERT, "Device executing with alert");
        put(ONCHAIN_DEAL_STATUS_WAITING_MUTUAL_CONFIRM_WITH_ALERT, "Stop use with alerting device, waiting mutual confirm");
        put(ONCHAIN_DEAL_STATUS_WAITING_LESSOR_CONFIRM_WITH_ALERT, "Device alert, waiting lessor confirm");
        put(ONCHAIN_DEAL_STATUS_WAITING_LESSEE_CONFIRM_WITH_ALERT, "Device alert, waiting lessee confirm");
        put(ONCHAIN_DEAL_STATUS_FINISH_WITH_ALERT, "Device alert, deal finished");
    }};
}
