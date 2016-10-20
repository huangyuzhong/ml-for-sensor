package com.device.inspect.common.model.device;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Administrator on 2016/8/31.
 */
@Entity
@Table(name = "alert_count")
public class AlertCount {

    private Integer id;
    private Device device;
    private InspectType inspectType;
    private Integer num;
    private Integer type;       //1为低级报警，2为高级报警
    private Date createDate;
    private String unit;
    private Date finish;

    @Id
    @GeneratedValue()
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne()
    @JoinColumn(name = "device_id")
    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    @ManyToOne()
    @JoinColumn(name = "inspect_type_id")
    public InspectType getInspectType() {
        return inspectType;
    }

    public void setInspectType(InspectType inspectType) {
        this.inspectType = inspectType;
    }

    @Column(name = "alert_num")
    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    @Column(name = "alert_type")
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Column(name = "create_date")
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Column(name = "finish_date")
    public Date getFinish() {
        return finish;
    }

    public void setFinish(Date finish) {
        this.finish = finish;
    }
}
