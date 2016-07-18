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
    private Integer id;
    private List<RestUser> userList;

    public RestIndexUser(@NotNull Company company){
        this.id=company.getId();
        if (null!=company.getManager()){
            userList = new ArrayList<RestUser>();
            for (User user:company.getManager()){

            }
        }
    }

    public List<RestUser> getUserList() {
        return userList;
    }

    public void setUserList(List<RestUser> userList) {
        this.userList = userList;
    }

    public Integer getId() {

        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
