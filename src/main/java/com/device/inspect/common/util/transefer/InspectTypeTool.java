package com.device.inspect.common.util.transefer;

/**
 * Created by gxu on 5/30/17.
 */

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class InspectTypeTool {

    private static final Map<String, List<String>> inspectTypes = createMap();
    private static Map<String, List<String>> createMap(){
        Map<String, List<String>> map = new HashMap<String, List<String>>();

        map.put("00", Arrays.asList("temperature_PT100", "degree"));
        map.put("01", Arrays.asList("humidity", "%"));
        map.put("02", Arrays.asList("CO2", "ppm"));
        map.put("04", Arrays.asList("temperature", "degree"));
        map.put("06", Arrays.asList("PSID", "pa"));
        map.put("07", Arrays.asList("methane", "LEL%"));
        map.put("05", Arrays.asList("door", "on_off"));
        map.put("03", Arrays.asList("battery", "%"));
        map.put("08", Arrays.asList("active_energy", "KWH"));
        map.put("09", Arrays.asList("passive_energy", "KWH"));
        map.put("0a", Arrays.asList("voltage", "V"));
        map.put("0b", Arrays.asList("current", "A"));
        map.put("0c", Arrays.asList("active_power", "W"));
        map.put("0d", Arrays.asList("passive_power", "Q"));
        map.put("0e", Arrays.asList("temperature_PT100", "degree"));
        map.put("0f", Arrays.asList("humidity", "%"));
        map.put("10", Arrays.asList("TVOC", "mg/m3"));
        map.put("11", Arrays.asList("Smoke", "normal/abnormal"));
        map.put("18", Arrays.asList("temperature_PT100", "degree"));
        map.put("19", Arrays.asList("temperature_PT100", "degree"));
        map.put("1a", Arrays.asList("temperature_PT100", "degree"));
        map.put("1b", Arrays.asList("current", "A"));

        return map;
    }

    public static String getMeasurementByCode(String code){
        if(inspectTypes.containsKey(code)){
            return inspectTypes.get(code).get(0);
        }else{
            return code;
        }
    }

    public static String getMeasurementUnitByCode(String code){
        if (inspectTypes.containsKey(code)) {

            return inspectTypes.get(code).get(1);
        }else{
            return code;
        }
    }
}
