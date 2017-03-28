package com.device.inspect.common.util.transefer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zyclincoln on 3/25/17.
 */
public class UrlParse {

    public static Map<String, String> parseAzureUrl(String url) {
        int firstSlash = url.indexOf(new String("//"), 0);
        if (firstSlash == -1) {
            return null;
        }
        int secondSlash = url.indexOf('/', firstSlash + 2);
        if (secondSlash == -1) {
            return null;
        }
        int thirdSlash = url.indexOf('/', secondSlash + 1);
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("containerName", url.substring(secondSlash + 1, thirdSlash));
        resultMap.put("blobName", url.substring(thirdSlash + 1));
        System.out.println("second: " + secondSlash + ", third: " + thirdSlash + ", url: " + url);
        return resultMap;
    }
}