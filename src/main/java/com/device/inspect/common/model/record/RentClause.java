package com.device.inspect.common.model.record;

import javax.persistence.*;

/**
 * Created by zyclincoln on 7/15/17.
 */
@Entity
@Table(name = "rent_clause")
public class RentClause {
    private Integer id;
    private String content;

    @Id
    @GeneratedValue()
    public Integer getId(){
        return this.id;
    }

    public void setId(Integer id){
        this.id = id;
    }

    @Column(name = "content")
    public String getContent(){
        return this.content;
    }

    public void setContent(String content){
        this.content = content;
    }

}
