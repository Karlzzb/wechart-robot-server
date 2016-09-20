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
    private String playerId;

    @Column(nullable = true)
    private String webchatId;

    @Column(nullable = false)
    private String remarkName;

    @Column(nullable = true)
    private Long points;

    @Column(nullable = true)
    private Long latestBetValue;
    
    @Column(nullable = true)
    private String latestBet;

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

    public String getLatestBet() {
        return latestBet;
    }

    public void setLatestBet(String latestBet) {
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

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public String getWebchatId() {
		return webchatId;
	}

	public void setWebchatId(String webchatId) {
		this.webchatId = webchatId;
	}

	public Long getLatestBetValue() {
		return latestBetValue;
	}

	public void setLatestBetValue(Long latestBetValue) {
		this.latestBetValue = latestBetValue;
	}

}
