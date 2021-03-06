package com.karl.db.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class GameInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long gameSerialNo;

	@Column(nullable = false)
	private String bankerRemarkName;

	@Column(nullable = false)
	private Long bankerPoint;

	@Column(nullable = false)
	private Integer betIndex;

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

	@Column(columnDefinition = "tinyint(1) default 0")
	private Boolean isUndo;

	@Column(nullable = true)
	private Long manageFee;

	@Column(nullable = true)
	private Long packageFee;

	@Column(nullable = true)
	private Long firstBankerFee;

	@Column(nullable = true)
	private Long bankerWinCut;
	
	public Long getGameSerialNo() {
		return gameSerialNo;
	}

	public void setGameSerialNo(Long gameSerialNo) {
		this.gameSerialNo = gameSerialNo;
	}

	public String getBankerRemarkName() {
		return bankerRemarkName;
	}

	public void setBankerRemarkName(String bankerRemarkName) {
		this.bankerRemarkName = bankerRemarkName;
	}

	public Long getBankerPoint() {
		return bankerPoint;
	}

	public void setBankerPoint(Long bankerPoint) {
		this.bankerPoint = bankerPoint;
	}

	public Integer getBetIndex() {
		return betIndex;
	}

	public void setBetIndex(Integer betIndex) {
		this.betIndex = betIndex;
	}

	public Double getLuckInfo() {
		return luckInfo;
	}

	public void setLuckInfo(Double luckInfo) {
		this.luckInfo = luckInfo;
	}

	public Integer getResultTimes() {
		return resultTimes;
	}

	public void setResultTimes(Integer resultTimes) {
		this.resultTimes = resultTimes;
	}

	public Long getResultPoint() {
		return resultPoint;
	}

	public void setResultPoint(Long resultPoint) {
		this.resultPoint = resultPoint;
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

	public Boolean getIsUndo() {
		return isUndo == null ? Boolean.FALSE : isUndo;
	}

	public void setIsUndo(Boolean isUndo) {
		this.isUndo = isUndo;
	}

	public Long getManageFee() {
		return manageFee;
	}

	public void setManageFee(Long manageFee) {
		this.manageFee = manageFee;
	}

	public Long getPackageFee() {
		return packageFee;
	}

	public void setPackageFee(Long packageFee) {
		this.packageFee = packageFee;
	}

	public Long getFirstBankerFee() {
		return firstBankerFee;
	}

	public void setFirstBankerFee(Long firstBankerFee) {
		this.firstBankerFee = firstBankerFee;
	}

	public Long getBankerWinCut() {
		return bankerWinCut;
	}

	public void setBankerWinCut(Long bankerWinCut) {
		this.bankerWinCut = bankerWinCut;
	}

}
