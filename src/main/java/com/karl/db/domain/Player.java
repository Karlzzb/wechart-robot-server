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
    private String remarkName;

    @Column(nullable = true)
    private Long points;

    @Column(nullable = true)
    private Long latestBet;

    @Column(nullable = true)
    private Double latestLuck;

    @Column(nullable = true)
    private Long latestResult;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        return "{id:" + id + "remarkName:" + remarkName + ",points" + points + "}";
    }

    public Long getLatestBet() {
        return latestBet;
    }

    public void setLatestBet(Long latestBet) {
        this.latestBet = latestBet;
    }

    public Double getLatestLuck() {
        return latestLuck;
    }

    public void setLatestLuck(Double latestLuck) {
        this.latestLuck = latestLuck;
    }

    public Long getLatestResult() {
        return latestResult;
    }

    public void setLatestResult(Long latestResult) {
        this.latestResult = latestResult;
    }

}
