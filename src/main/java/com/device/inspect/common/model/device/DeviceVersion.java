package com.device.inspect.common.model.device;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Administrator on 2016/10/9.
 */
@Entity
@Table(name = "device_version")
public class DeviceVersion {
    private Integer id;
    private String name;
    private String url;
    private String firstCode;        //版本号
    private String secondCode;
    private String thirdCode;
    private String fourthCode;
    private String type;
    private Date createDate;
    private String fileName;
    private String message;

    @Id
    @GeneratedValue()
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Column(name = "code_first")
    public String getFirstCode() {
        if(firstCode != null) {
            return firstCode;
        }
        else{
            return "00";
        }
    }

    public void setFirstCode(String firstCode) {
        this.firstCode = firstCode;
    }
    @Column(name = "code_second")
    public String getSecondCode() {
        if(secondCode != null) {
            return secondCode;
        }else{
            return "00";
        }
    }

    public void setSecondCode(String secondCode) {
        this.secondCode = secondCode;
    }
    @Column(name = "code_third")
    public String getThirdCode() {
        if(thirdCode!=null) {
            return thirdCode;
        }else{
            return "00";
        }
    }

    public void setThirdCode(String thirdCode) {
        this.thirdCode = thirdCode;
    }
    @Column(name = "code_forth")
    public String getFourthCode() {
        if(fourthCode !=null) {
            return fourthCode;
        }else{
            return "00";
        }
    }

    public void setFourthCode(String fourthCode) {
        this.fourthCode = fourthCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "create_date")
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Column(name = "file_name")
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String toString() {
        String str = "";
        if (this.firstCode != null){
            str += this.firstCode;

        }
        if(this.secondCode != null){
            str += " " + this.secondCode;
        }
        if(this.thirdCode != null){
            str += " " + this.thirdCode;
        }
        if(this.fourthCode != null){
            str += " " + this.fourthCode;
        }

        return str;
    }
}
