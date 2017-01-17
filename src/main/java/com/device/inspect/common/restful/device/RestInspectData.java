package com.device.inspect.common.restful.device;

import com.device.inspect.common.model.device.Device;
import com.device.inspect.common.model.device.DeviceInspect;
import com.device.inspect.common.model.device.InspectData;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by Administrator on 2016/7/25.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestInspectData {
    private Integer id;
    private RestDeviceInspect deviceInspect;
    private String result;
    private Date createDate;
    private Integer judge;
    private String picResult;
    private String realValue;

    public RestInspectData() {
    }

    public RestInspectData(@NotNull InspectData inspectData){
        this.id = inspectData.getId();
        this.deviceInspect = null==inspectData.getDeviceInspect()?null:new RestDeviceInspect(inspectData.getDeviceInspect());
        if (inspectData.getDeviceInspect().getInspectType().getCode()!=null&&
                (inspectData.getDeviceInspect().getInspectType().getCode().equals("05")||
                        inspectData.getDeviceInspect().getInspectType().getCode().equals("08"))){
            if (inspectData.getResult().equals("1")){
                this.result = "开";
            }else {
                this.result = "关";
            }
        } else
            this.result = inspectData.getResult()+inspectData.getDeviceInspect().getInspectType().getUnit();

        this.picResult = inspectData.getResult();
        this.createDate = inspectData.getCreateDate();
        judge = 0;
        if (null!=inspectData.getDeviceInspect()&&null!=inspectData.getDeviceInspect().getLowUp()&&null!=
                inspectData.getDeviceInspect().getLowDown()){
            if (Float.valueOf(inspectData.getResult())<=inspectData.getDeviceInspect().getLowUp()&&
                    Float.valueOf(inspectData.getResult())>=inspectData.getDeviceInspect().getLowDown())
                judge = 0;
            else if ((Float.valueOf(inspectData.getResult())<=inspectData.getDeviceInspect().getHighUp()&&
                    Float.valueOf(inspectData.getResult())>=inspectData.getDeviceInspect().getLowUp())||
                    (Float.valueOf(inspectData.getResult())<=inspectData.getDeviceInspect().getLowDown()&&
                    Float.valueOf(inspectData.getResult())>=inspectData.getDeviceInspect().getHighDown()))
                judge = 1;
            else
                judge = 2;

        }
        this.realValue=inspectData.getRealValue();
//        this.realValue=inspectData.getResult()-inspectData.getDeviceInspect().getZero();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RestDeviceInspect getDeviceInspect() {
        return deviceInspect;
    }

    public void setDeviceInspect(RestDeviceInspect deviceInspect) {
        this.deviceInspect = deviceInspect;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Integer getJudge() {
        return judge;
    }

    public void setJudge(Integer judge) {
        this.judge = judge;
    }

    public String getPicResult() {
        return picResult;
    }

    public void setPicResult(String picResult) {
        this.picResult = picResult;
    }

    public String getRealValue() {
        return realValue;
    }

    public void setRealValue(String realValue) {
        this.realValue = realValue;
    }
}
