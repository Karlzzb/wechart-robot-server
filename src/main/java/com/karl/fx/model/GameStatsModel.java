package com.karl.fx.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class GameStatsModel {

	public static final String STATSSIGNCOL = "statsSign";

	public static final String STATSTIMECOL = "statsTime";

	public static final String MANAGEFEECOL = "manageFee";

	public static final String PACKAGEFEECOL = "packageFee";

	public static final String FIRSTBANKERFEECOL = "firstBankerFee";

	public static final String BANKERWINCUTCOL = "bankerWinCut";

	public static final String GAMENUMCOL = "gameNum";

	public static final String STATSSUMCOL = "statsSum";

	public GameStatsModel(String statsSign, String statsTime, Long manageFee, Long packageFee,
			Long firstBankerFee, Long bankerWinCut, Integer gameNum) {
		super();
		this.statsSign = new SimpleStringProperty(statsSign);
		this.statsTime = new SimpleStringProperty(statsTime);
		this.manageFee = new SimpleLongProperty(manageFee);
		this.packageFee = new SimpleLongProperty(packageFee);
		this.firstBankerFee = new SimpleLongProperty(firstBankerFee);
		this.bankerWinCut = new SimpleLongProperty(bankerWinCut);
		this.gameNum = new SimpleIntegerProperty(gameNum);
		this.statsSum = new SimpleLongProperty(manageFee+packageFee+firstBankerFee+bankerWinCut);
	}
	
	private SimpleStringProperty statsSign;

	private SimpleStringProperty statsTime;

	private SimpleLongProperty manageFee;

	private SimpleLongProperty packageFee;

	private SimpleLongProperty firstBankerFee;

	private SimpleLongProperty bankerWinCut;

	private SimpleIntegerProperty gameNum;
	
	private SimpleLongProperty statsSum;

	public String getStatsTime() {
		return statsTime.getValueSafe();
	}

	public void setStatsTime(String statsTime) {
		this.statsTime = new SimpleStringProperty(statsTime);
	}

	public Long getManageFee() {
		return manageFee.getValue();
	}

	public void setManageFee(Long manageFee) {
		this.manageFee = new SimpleLongProperty(manageFee);
	}

	public Long getPackageFee() {
		return packageFee.getValue();
	}

	public void setPackageFee(Long packageFee) {
		this.packageFee = new SimpleLongProperty(packageFee);
	}

	public Long getFirstBankerFee() {
		return firstBankerFee.getValue();
	}

	public void setFirstBankerFee(Long firstBankerFee) {
		this.firstBankerFee = new SimpleLongProperty(firstBankerFee);
	}

	public Long getBankerWinCut() {
		return bankerWinCut.getValue();
	}

	public void setBankerWinCut(Long bankerWinCut) {
		this.bankerWinCut = new SimpleLongProperty(bankerWinCut);
	}

	public Integer getGameNum() {
		return gameNum.getValue();
	}

	public void setGameNum(Integer gameNum) {
		this.gameNum = new SimpleIntegerProperty(gameNum);
	}

	public String getStatsSign() {
		return statsSign.getValueSafe();
	}

	public void setStatsSign(String statsSign) {
		this.statsSign = new SimpleStringProperty(statsSign);
	}

	public Long getStatsSum() {
		return statsSum.getValue();
	}

	public void setStatsSum(Long statsSum) {
		this.statsSum = new SimpleLongProperty(statsSum);
	}
}
