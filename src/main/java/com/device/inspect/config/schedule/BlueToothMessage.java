package com.device.inspect.config.schedule;

/**
 * Created by Administrator on 2016/11/14.
 */
public class BlueToothMessage {

    private static final String STEP_ONE = "10001000";
    private static final Integer STEP_ONE_INTERVAL = 5;//第一步距离下一步操作的时间(分钟)
    private static final String STEP_TWO = "01001000";
    private static final Integer STEP_TWO_INTERVAL = 5;//下一步操作的时间(分钟)
    private static final String STEP_THREE = "00101000";
    private static final Integer STEP_THREE_INTERVAL = 5;//下一步操作的时间(分钟)
    private static final String STEP_FOUR = "00011010";
    private static final Integer STEP_FOUR_INTERVAL = 5;//下一步操作的时间(分钟)
    private static final String STEP_FIVE = "00000000";


    public void sendMessage() {
        //    发送的数据是byte[]
        //    for examp ：
        STEP_ONE.getBytes();
    }


}
