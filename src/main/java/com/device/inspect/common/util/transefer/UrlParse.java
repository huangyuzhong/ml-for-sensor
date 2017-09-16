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

        // TODO: 很多添加/新增的操作同用一个api, 这样前端获取操作列表的时候很难判断到底是哪个操作, 应该把两个操作分离开.

        // 下面注释掉的内容属于页面加载的时候进行的查询, 暂时不归入用户主动查询范畴

        // TODO:ExportController
        myMap.put("/api/rest/download/file", "download_file");   // 文件下载
        myMap.put("/api/rest/download/device/version","download_device_version");   // 硬件版本下载
        // TODO:FileController
        myMap.put("/api/rest/file/create/building","create_building");  // 管理员创建楼
        myMap.put("/api/rest/file/create/floor","create_floor");  // 管理员创建层
        myMap.put("/api/rest/file/create/device","create_device");  // 管理员添加设备
        myMap.put("/api/rest/file/create/room","create_room");  // 管理员创建室
        myMap.put("/api/rest/file/upload/deviceType/icon", "update_device_type_icon");  // 更新设备类型图标
        myMap.put("/api/rest/file/change/avatar", "update_user_avatar");    // 更新用户头像
        myMap.put("/api/rest/file/change/picture","update_picture");  // 更换设备图片
        myMap.put("/api/rest/file/create/company","create_company");  // 添加企业
        myMap.put("/api/rest/file/upload/device/file","upload_device_file");  // 上传设备文件
        myMap.put("/api/rest/file/logo/company","update_company_logo");  // 更换公司logo
        myMap.put("/api/rest/file/create/device/version","create_device_version");  // 硬件版本更新
        // TODO:OperateController
        myMap.put("/api/rest/operate/device/floor","operate_device_floor");  // 添加/修改设备样品层
        myMap.put("/api/rest/operate/delete/device/floor","delete_device_floor");  // 删除设备样品层
        myMap.put("/api/rest/operate/find/deviceDisableTimeByDeviceId","find_device_disableTime");  // 查看共享设备信息
        myMap.put("/api/rest/operate/add/deviceDisableTime","add_device_disableTime");  // 操作设备不可租赁时间段
        myMap.put("/api/rest/operate/deviceSharing","update_device_sharing");  // 起用停止设备租赁
        myMap.put("/api/rest/operate/device","update_device");  // 修改设备概况
        //myMap.put("/api/rest/operate/get/device/parameter","get_device_parameter");  // 获取设备报警参数
        //myMap.put("/api/rest/operate/findAll/modelData","find_model_data");  // 获取模型信息
        myMap.put("/api/rest/operate/device/parameter","update_device_parameter");  // 修改单个设备参数
        myMap.put("/api/rest/operate/create/user","create_user");  // 创建新用户
        myMap.put("/api/rest/operate/update/user","update_user");  // 修改用户信息
        myMap.put("/api/rest/operate/delete/user","delete_user");  // 删除企业级用户
        myMap.put("/api/rest/operate/create/camera","create_camera");  // 添加摄像头
        myMap.put("/api/rest/operate/deviceType","create_or_update_device_type");  // 修改设备种类及其参数
        myMap.put("/api/rest/operate/delete/file","delete_device_file");  // 删除设备文件
        myMap.put("/api/rest/operate/manager/device","delete_device");  // 删除设备
        myMap.put("/api/rest/operate/manager/room","delete_room");  // 删除室
        myMap.put("/api/rest/operate/manager/build","delete_build");  // 删除楼
        myMap.put("/api/rest/operate/manager/device/type","delete_device_type");  // 删除设备种类
        myMap.put("/api/rest/operate/send/mobile/verify","send_mobile_verify");  // 发送短信验证码
        myMap.put("/api/rest/operate/send/email/verify","send_email_verify");  // 发送邮箱验证码
        myMap.put("/api/rest/operate/update/mobile","update_mobile");  // 更换手机号
        myMap.put("/api/rest/operate/update/email","update_email");  // 绑定邮箱
        myMap.put("/api/rest/operate/reset/password","reset_password");  // 重置密码
        myMap.put("/api/rest/operate/modify/password","update_password");  // 修改密码
        myMap.put("/api/rest/operate/forget/find/password","forget_find_password");  // 找回密码
        myMap.put("/api/rest/operate/device/code","update_device_code");  // 修改终端编号
        //myMap.put("/api/rest/operate/select/device/version","select_device_version");  // 获取所有版本
        myMap.put("/api/rest/operate/update/multi/device/version","update_multi_device_version");  // 多选更新版本接口
        //myMap.put("/api/rest/operate/device/version/explain","explain_device_version");  // 硬件版本说明接口
        myMap.put("/api/rest/operate/set/zero","set_zero");  // 零票设置
        myMap.put("/api/rest/operate/modify/zero","modify_zero");  // 修改零票值
        //myMap.put("/api/rest/operate/is/login","is_login"); // 判断是否登录
        //myMap.put("/api/rest/operate/is/device/sicentist","query_device_unbound_scientist");  // 获取设备未绑定的科学家
        myMap.put("/api/rest/operate/device/upChain","device_upChain");  // 设备上链
        myMap.put("/api/rest/operate/device/updateChainDeviceInfo","update_device_chain_info");  // 更新上链设备的上链相关信息
        myMap.put("/api/rest/operate/device/makeChainDeal","apply_device_chain_deal");  // 申请链上交易
        myMap.put("/api/rest/operate/device/finishChainDeal","finish_device_chain_deal");  // 确认交易完成
        myMap.put("/api/rest/operate/monitor/getFirstNotActActionAndUpdate","update_monitor_first_unfinished_action");  // 获取monitor动作队列中最早的未完成动作，并改变状态
        // TODO:SelectApiController
        //myMap.put("/api/rest/firm/person/info","query_person");  // 查询个人信息
        //myMap.put("/api/rest/firm/person/mine/info","query_person_mine");  // 查询用户个人信息
        //myMap.put("/api/rest/firm/buildings","query_buildings");  // 查询当前园区内所有楼
        //myMap.put("/api/rest/firm/floors","query_floors");  // 查询当前楼里所有层
        //myMap.put("/api/rest/firm/rooms","query_rooms");  // 查询当前层里所有室
        //myMap.put("/api/rest/firm/devices","query_device");  // 查询当前室里所有设备
        //myMap.put("/api/rest/firm/device/types","query_device_types");  // 查询所有设备种类
        myMap.put("/api/rest/firm/device/type/request","query_device_type");  // 查询设备类型
        myMap.put("/api/rest/firm/manager/devices","query_devices");  // 查询设备列表信息(使用各种筛选条件)
        myMap.put("/api/rest/firm/enableSharing/devices","query_sharing_devices");  // 查询分享设备列表信息
        myMap.put("/api/rest/firm/service/device","management_query_devices");  // 管理平台用户查询设备列表
        myMap.put("/api/rest/firm/employees","query_employees");  // 查询当前公司的所有员工
        myMap.put("/api/rest/firm/query/all/company","management_query_all_company");  // 管理平台查询所有业务企业
        //myMap.put("/api/rest/firm/query/login/company","query_login_company");  // 登陆页面根据公司URL获取公司信息
        //myMap.put("/api/rest/firm/query/mine/company","query_mine_company");  // 根据登录人获取所属公司信息
        //myMap.put("/api/rest/firm/colleges/manager","query_colleges_manager");  // 获取当前企业所有员工(仅包含企业管理员和设备管理员)
        //myMap.put("/api/rest/firm/query/inspect/type","query_inspect_type");  // 获取所有的监控参数
        //myMap.put("/api/rest/firm/colleges/scientist","query_colleges_scientist");  // 获取用户所在企业所有的科学家
        myMap.put("/api/rest/firm/take/over/colleges","delete_colleges");  // 删除公司人员
        //myMap.put("/api/rest/firm/service/get/versions","query_service_versions");  // 查询所有版本号接口
        //myMap.put("/api/rest/firm/device/running_status","query_device_running_status");  // 获取所有运行状态
        //myMap.put("/api/rest/firm/device/type/status","query_device_type_status");  // 获取设备种类监控参数对应状态
        //myMap.put("/api/rest/firm/device/inspect/status","query_device_inspect_status");  // 获取设备监控参数对应状态
        myMap.put("/api/rest/firm/device/utilization","query_device_weekly_utilization");  // 获取最近一周的某设备的hourly设备利用率
        //myMap.put("/api/rest/firm/user/operations","query_user_operations");  // 获取用户操作记录
        myMap.put("/api/rest/firm/device/daily/utilization","query_device_daily_utilization");  // 获取昨日设备利用率情况
        myMap.put("/api/rest/firm/device/monitorData","query_device_monitoring_data");  // 查询设备监控数据
        //myMap.put("/api/rest/firm/dealHistory","query_dealHistory");  // 获取交易记录
        //myMap.put("/api/rest/firm/device/runningStatusHistory","query_device_runningStatusHistory");  // 获取设备运行记录
        //myMap.put("/api/rest/firm/device/cameraList","query_device_cameraList");  // 获取设备cameraList
        //myMap.put("/api/rest/firm/device/cameraToken","query_device_cameraToken");  // 获取设备cameraToken
        //myMap.put("/api/rest/firm/monitor/actionList","query_monitor_actionList");  // 获取某个monitor的动作历史
        //myMap.put("/api/rest/firm/dealRecord/alert","query_dealRecord_alert");  // 获取交易的报警列表

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
