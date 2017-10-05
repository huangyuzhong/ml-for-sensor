package com.device.inspect.common.model.record;

import com.device.inspect.common.model.charater.User;

import javax.persistence.*;
import java.util.Date;

/**
 * 接收短信、邮件回复
 * Created by hwd on 2017/10/1.
 */
@Entity
@Table(name = "message_receive")
public class MessageReceive {
    private Integer id;
    private String reason;
    private String type;    // 短信或邮件
    private User user;
    private String content; // 回复内容
    private Date createDate;    // 创建时间
    private Integer status; // 0 已处理;1 未处理
    private String mobile;
    private String email;

    @Id
    @GeneratedValue
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "receive_reason")
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Column(name = "receive_type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column(name = "content")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Column(name = "create_date")
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Column(name = "status")
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Column(name = "mobile")
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
