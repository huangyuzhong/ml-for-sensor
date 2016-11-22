package com.device.inspect.common.util.transefer;

import com.device.inspect.common.model.charater.Role;
import com.device.inspect.common.model.charater.User;

/**
 * Created by Administrator on 2016/10/27.
 */
public class UserRoleDifferent {

    private static final String SERVICEMANAGER = "SERVICE_MANAGER";
    private static final String SERVICEWORKER = "SERVICE_BUSINESS";
    private static final String FIRMMANAGER = "FIRM_MANAGER";
    private static final String FIRMWORKER = "FIRM_WORKER";
    private static final String FIRMSCIENTIST = "FIRM_SCIENTIST";

    /**
     * 判断用户是不是平台用户
     * @param user
     * @return
     */
    public static boolean userStartWithService(User user){
        if (null!=user.getRoles())
            for (Role role:user.getRoles()){
                if (role.getRoleAuthority().getName()!=null&&role.getRoleAuthority().getName().startsWith("SERVICE"))
                    return true;
            }
        return false;
    }

    /**
     * 判断用户是不是企业级用户
     * @param user
     * @return
     */
    public static boolean userStartWithFirm(User user){
        if (null!=user.getRoles())
            for (Role role:user.getRoles()){
                if (role.getRoleAuthority().getName()!=null&&role.getRoleAuthority().getName().startsWith("FIRM"))
                    return true;
            }
        return false;
    }

    /**
     *  判断用户是不是平台管理员
     * @param user
     * @return
     */
    public static boolean userServiceManagerConfirm(User user){
        if (null!=user.getRoles())
            for (Role role:user.getRoles()){
                if (role.getRoleAuthority().getName()!=null&&role.getRoleAuthority().getName().startsWith(SERVICEMANAGER))
                    return true;
            }
        return false;
    }

    /**
     * 判断用户是不是平台业务员
     * @param user
     * @return
     */
    public static boolean userServiceWorkerConfirm(User user){
        if (null!=user.getRoles())
            for (Role role:user.getRoles()){
                if (role.getRoleAuthority().getName()!=null&&role.getRoleAuthority().getName().startsWith(SERVICEWORKER))
                    return true;
            }
        return false;
    }

    /**
     * 判断用户是不是企业管理员
     * @param user
     * @return
     */
    public static boolean userFirmManagerConfirm(User user){
        if (null!=user.getRoles())
            for (Role role:user.getRoles()){
                if (role.getRoleAuthority().getName()!=null&&role.getRoleAuthority().getName().startsWith(FIRMMANAGER))
                    return true;
            }
        return false;
    }

    /**
     * 判断用户是不是企业业务员
     * @param user
     * @return
     */
    public static boolean userFirmWorkerConfirm(User user){
        if (null!=user.getRoles())
            for (Role role:user.getRoles()){
                if (role.getRoleAuthority().getName()!=null&&role.getRoleAuthority().getName().startsWith(FIRMWORKER))
                    return true;
            }
        return false;
    }

    /**
     * 判断用户是不是实验品业务员
     * @param user
     * @return
     */
    public static boolean userScientistConfirm(User user){
        if (null!=user.getRoles())
            for (Role role:user.getRoles()){
                if (role.getRoleAuthority().getName()!=null&&role.getRoleAuthority().getName().startsWith(FIRMSCIENTIST))
                    return true;
            }
        return false;
    }
}
