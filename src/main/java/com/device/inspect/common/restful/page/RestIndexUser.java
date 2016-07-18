package com.device.inspect.common.restful.page;

import com.device.inspect.common.model.charater.User;
import com.device.inspect.common.model.firm.Company;
import com.device.inspect.common.restful.charater.RestUser;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/18.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestIndexUser {
    private Integer userId;
    private List<RestUser> userList;

    public RestIndexUser(User user, List<User> users){
        this.userId=user.getId();
        if (null!=users&&users.size()>0) {
            userList = new ArrayList<RestUser>();
            for (User userForEach : users) {
                userList.add(new RestUser(userForEach));
            }
        }
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<RestUser> getUserList() {
        return userList;
    }

    public void setUserList(List<RestUser> userList) {
        this.userList = userList;
    }
}
