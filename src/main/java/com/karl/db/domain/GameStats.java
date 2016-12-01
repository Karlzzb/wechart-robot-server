package com.karl.db.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class GameStats implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7828107852881802118L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long gameStatsId;

	@Column(nullable = false)
	private Long statsTime;

	@Column(nullable = false)
	private Long manageFee;

	@Column(nullable = false)
	private Long packageFee;

	@Column(nullable = false)
	private Long firstBankerFee;

	@Column(nullable = false)
	private Long bankerWinCut;
	
	@Column(nullable = false)
	private Long dirtyCut;

	@Column(nullable = false)
	private Integer gameNum;

	public Long getGameStatsId() {
		return gameStatsId;
	}

	public void setGameStatsId(Long gameStatsId) {
		this.gameStatsId = gameStatsId;
	}

	public Long getStatsTime() {
		return statsTime;
	}

	public void setStatsTime(Long statsTime) {
		this.statsTime = statsTime;
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

	public Integer getGameNum() {
		return gameNum;
	}

	public void setGameNum(Integer gameNum) {
		this.gameNum = gameNum;
	}

	public Long getDirtyCut() {
		return dirtyCut;
	}

	public void setDirtyCut(Long dirtyCut) {
		this.dirtyCut = dirtyCut;
	}
}
