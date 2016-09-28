package com.karl.db.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class PlayerTrace implements Serializable {

	public PlayerTrace() {
		super();
	}

	public PlayerTrace(Long gameSerialNo, String playerId,
			String webchatId, String wechatName, String remarkName,
			String betInfo, Long betPoint, Boolean islowRisk, Integer betIndex, Long betTime) {
		super();
		this.gameSerialNo = gameSerialNo;
		this.playerId = playerId;
		this.webchatId = webchatId;
		this.wechatName = wechatName;
		this.remarkName = remarkName;
		this.betInfo = betInfo;
		this.betPoint = betPoint;
		this.betIndex = betIndex;
		this.betTime = betTime;
		this.islowRisk = islowRisk;
	}

	private static final long serialVersionUID = -7909930972652408103L;
    
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long traceId;

    @Column(nullable = false)
	private Long gameSerialNo;
       
    @Column(nullable = false)
    private String playerId;

    @Column(nullable = true)
    private String webchatId;
    
    @Column(nullable = true)
    private String wechatName;

    @Column(nullable = false)
    private String remarkName;

    @Column(nullable = true)
    private String betInfo;    
    
    @Column(nullable = true)
    private Long betPoint;

    @Column(nullable = true)
    private Integer betIndex;
    
    @Column(nullable = true)
    private Long betTime;
    
    @Column(nullable = true)
    private Boolean islowRisk;

    @Column(nullable = true)
    private Double luckInfo;

    @Column(nullable = true)
    private Long luckTime;

    @Column(nullable = true)
    private String resultRuleName;
    
    @Column(nullable = true)
    private Integer resultTimes;

    @Column(nullable = true)
    private Long resultPoint;

	public Long getGameSerialNo() {
		return gameSerialNo;
	}

	public void setGameSerialNo(Long gameSerialNo) {
		this.gameSerialNo = gameSerialNo;
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

	public String getWechatName() {
		return wechatName;
	}

	public void setWechatName(String wechatName) {
		this.wechatName = wechatName;
	}

	public String getRemarkName() {
		return remarkName;
	}

	public void setRemarkName(String remarkName) {
		this.remarkName = remarkName;
	}

	public String getBetInfo() {
		return betInfo;
	}

	public void setBetInfo(String betInfo) {
		this.betInfo = betInfo;
	}

	public Long getBetPoint() {
		return betPoint;
	}

	public void setBetPoint(Long betPoint) {
		this.betPoint = betPoint;
	}


	public Long getBetTime() {
		return betTime;
	}

	public void setBetTime(Long betTime) {
		this.betTime = betTime;
	}

	public Double getLuckInfo() {
		return luckInfo;
	}

	public void setLuckInfo(Double luckInfo) {
		this.luckInfo = luckInfo;
	}

	public Long getResultPoint() {
		return resultPoint;
	}

	public void setResultPoint(Long resultPoint) {
		this.resultPoint = resultPoint;
	}

	public Long getTraceId() {
		return traceId;
	}

	public void setTraceId(Long traceId) {
		this.traceId = traceId;
	}

	public Integer getBetIndex() {
		return betIndex;
	}

	public void setBetIndex(Integer betIndex) {
		this.betIndex = betIndex;
	}

	public Long getLuckTime() {
		return luckTime;
	}

	public void setLuckTime(Long luckTime) {
		this.luckTime = luckTime;
	}

	public String getResultRuleName() {
		return resultRuleName;
	}

	public void setResultRuleName(String resultRuleName) {
		this.resultRuleName = resultRuleName;
	}

	public Boolean getIslowRisk() {
		return islowRisk;
	}

	public void setIslowRisk(Boolean islowRisk) {
		this.islowRisk = islowRisk;
	}

	public Integer getResultTimes() {
		return resultTimes;
	}

	public void setResultTimes(Integer resultTimes) {
		this.resultTimes = resultTimes;
	}

}
