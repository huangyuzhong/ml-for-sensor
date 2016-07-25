package com.device.inspect.common.restful.page;

import com.device.inspect.common.model.device.InspectData;
import com.device.inspect.common.restful.device.RestInspectData;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/25.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestIndexInspctData {
    private List<RestInspectData> list;

    public RestIndexInspctData(List<InspectData> inspectDataList){
        if (null!=inspectDataList){
            list = new ArrayList<RestInspectData>();
            for (InspectData inspectData:inspectDataList){
                list.add(new RestInspectData(inspectData));
            }
        }
    }

    public List<RestInspectData> getList() {
        return list;
    }

    public void setList(List<RestInspectData> list) {
        this.list = list;
    }
}
