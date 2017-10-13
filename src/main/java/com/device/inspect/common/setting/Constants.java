package com.device.inspect.common.setting;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by zyclincoln on 7/17/17.
 */
public class Constants {
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

    public static final String DEVICE_INSPECT_STATUS_NORMAL = "normal";
    public static final String DEVICE_INSPECT_STATUS_YELLOW = "low";
    public static final String DEVICE_INSPECT_STATUS_RED = "high";
    public static final String UNDEFINED = "undefined";

    public static final Integer ALERT_CODE_NO_ALERT = 0;
    public static final Integer ALERT_CODE_YELLOW = 1;
    public static final Integer ALERT_CODE_RED = 2;

    public static final Map<Integer, String> ALERT_CODE_STATUS_MAP = new HashMap<Integer, String>(){{
        put(ALERT_CODE_NO_ALERT, DEVICE_INSPECT_STATUS_NORMAL);
        put(ALERT_CODE_YELLOW, DEVICE_INSPECT_STATUS_YELLOW);
        put(ALERT_CODE_RED, DEVICE_INSPECT_STATUS_RED);
    }

    };

    public static final String SMS_MEDIA_TYPE_ALIYUN = "aliyun";
    public static final String SMS_MEDIA_TYPE_MODULE = "module";

    public static final Integer INSPECT_PURPOSE_ALERT = 0;
    public static final Integer INSPECT_PURPOSE_OPERATING_STATUS_BY_THRESHOLDS = 1;
    public static final Integer INSPECT_PURPOSE_OPERATING_STATUS_BY_LEARNING_MODEL = 2;

    public static final Integer DEVICE_ACTION_NOACT = 0;
    public static final Integer DEVICE_ACTION_FINISH = 1;
    public static final Integer DEVICE_ACTION_ERROR = 2;

    public static final Integer REPLY_MESSAGE_TYPE_UNKNOWN = -1;
    public static final Integer REPLY_MESSAGE_TYPE_CANCEL_PUSH = 1;

    public static final Integer DEVICE_OPERATING_STATUS_STOPPED = 0;
    public static final Integer DEVICE_OPERATING_STATUS_IDLE = 10;
    public static final Integer DEVICE_OPERATING_STATUS_RUNNING = 20;

    public static final String ACTION_POWER_OFF = "0000";
    public static final String ACTION_POWER_ON = "0001";
    public static final String ACTION_NO_ACTION = "FFFF";

    public static final String ONCHAIN_DEAL_OPERATION_CONFIRM = "confirm";
    public static final Set<String> ONCHAIN_DEAL_OPERATION_SET = new HashSet<String>(){{
        add(ONCHAIN_DEAL_OPERATION_CONFIRM);
    }};

    public static final Map<String, String> DEVICE_ACTION_TRANSFER_MAP = new HashMap<String, String>(){{
        put("power on", ACTION_POWER_ON);
        put("power off", ACTION_POWER_OFF);
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

    public static final String HTTP_REQUEST_CUSTOM_ATTRIBUTE_POST_BODY = "postBody";

    public static final String SYSTEM_DEPLOYMENT_CLOUD = "cloud";
    public static final String SYSTEM_DEPLOYMENT_LOCAL = "local";
}
