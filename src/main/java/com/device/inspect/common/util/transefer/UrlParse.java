package com.device.inspect.common.util.transefer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zyclincoln on 3/25/17.
 */
public class UrlParse {

    public static final String API_TYPE_WEB_QUERY = "web_query";
    public static final String API_TYPE_USER_OPERATION = "user_op";

    // for all url related to a user's operation, map it to operation name.
    public static final Map<String, String> urlUserOperationMap = createUrlUserOperationMap();
    private static Map<String, String> createUrlUserOperationMap()
    {
        Map<String,String> myMap = new HashMap<String,String>();
        myMap.put("/api/rest/login", "login");
        myMap.put("/api/rest/logout", "logout");
        myMap.put("/api/rest/file/change/avatar", "update_user_avatar");
        myMap.put("/api/rest/file/logo/company", "update_company_logo");
        myMap.put("/api/rest/operate/modify/password", "update_password");
        myMap.put("/api/rest/file/upload/deviceType/icon", "update_device_type_icon");

        // TODO: we should separate this to two apis for create and update
        myMap.put("/api/rest/operate/deviceType", "create_or_update_device_type");
        myMap.put("/api/rest/operate/manager/device/type", "delete_device_type");

        myMap.put("/api/rest/firm/devices", "query_device");

        myMap.put("/api/rest/operate/create/user","create_user");  // 创建新用户
        myMap.put("/api/rest/operate/update/user","update_user");  // 修改用户信息
        myMap.put("/api/rest/operate/delete/user","delete_user");  // 删除企业级用户
        myMap.put("/api/rest/firm/employees","query_company_user");  // 查询公司用户

        return myMap;
    }

    public static Map<String, String> parseAzureUrl(String url) {
        if(url == null || !url.startsWith("http")){
	       return null;
	    }
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
