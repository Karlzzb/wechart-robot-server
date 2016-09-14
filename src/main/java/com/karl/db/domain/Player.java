package com.karl.db.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Player implements Serializable {
    private static final long serialVersionUID = -7909930972652408103L;

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

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

    @Column(nullable = true)
    private Long latestBetTime;


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

    public Long getLatestBetTime() {
        return latestBetTime;
    }

    public void setLatestBetTime(Long latestBetTime) {
        this.latestBetTime = latestBetTime;
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
