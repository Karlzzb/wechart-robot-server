package com.karl.db.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Player implements Serializable {
    private static final long serialVersionUID = -7909930972652408103L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String weId;

    @Column(nullable = false)
    private String remarkName;

    @Column(nullable = true)
    private Long points;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWeId() {
        return weId;
    }

    public void setWeId(String weId) {
        this.weId = weId;
    }

    public String getRemarkName() {
        return remarkName;
    }

    public void setRemarkName(String remarkName) {
        this.remarkName = remarkName;
    }

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    public Player() {
        super();
    }

    @Override
    public String toString() {
        return "{id:" + id + ",weId:" + weId + ",remarkName:" + remarkName + ",points" + points
                + "}";
    }

}
