package com.device.inspect.common.model.record;

import com.device.inspect.common.model.charater.User;

import javax.persistence.*;

/**
 * Created by Administrator on 2016/10/18.
 */
@Entity
@Table(name = "message_send")
public class MessageSend {
    private Integer id;
    private String reason;
    private String type;
    private User user;
    private String error;

    @Id
    @GeneratedValue()
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "send_reason")
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
    @Column(name = "send_type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    @ManyToOne()
    @JoinColumn(name = "user_id")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column(name = "error_reason")
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
