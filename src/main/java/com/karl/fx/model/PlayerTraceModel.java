package com.karl.fx.model;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

import com.karl.utils.StringUtils;

public class PlayerTraceModel {

	/**
	 * 
	 * @param traceId
	 * @param playerId
	 * @param playerName
	 * @param playerPoint
	 * @param betInfo
	 * @param luckInfo
	 * @param resultRuleName
	 * @param resultInfo
	 */
	public PlayerTraceModel(Long traceId, String playerId, String playerName,
			Long playerPoint, String betInfo, String luckInfo,
			String resultRuleName, String resultInfo) {
		super();
		this.playerId = new SimpleStringProperty(playerId);
		this.playerName = new SimpleStringProperty(playerName);
		this.resultRuleName = new SimpleStringProperty(resultRuleName);
		this.playerPoint = new SimpleLongProperty(playerPoint);
		this.betInfo = new SimpleStringProperty(betInfo);
		this.resultInfo = new SimpleStringProperty(resultInfo);
		this.luckInfo = new SimpleStringProperty(luckInfo);
		this.traceId = traceId;
	}

	public static final String PLAYERIDCOLKEY = "playerId";
	public static final String PLAYERNAMECOLKEY = "playerName";
	public static final String PLAYERPOINTCOLKEY = "playerPoint";
	public static final String BETINFOKEY = "betInfo";
	public static final String LUCKINFOKEY = "luckInfo";
	public static final String RESULTRULENAMEKEY = "resultRuleName";
	public static final String RESULTINFOKEY = "resultInfo";

	private SimpleStringProperty resultRuleName;
	private SimpleStringProperty resultInfo;
	private SimpleStringProperty playerId;
	private SimpleStringProperty wechatId;
	private SimpleStringProperty playerName;
	private SimpleLongProperty playerPoint;
	private SimpleStringProperty betInfo;
	private SimpleStringProperty wechatName;
	private SimpleStringProperty luckInfo;

	// unvisualable properties
	private Long traceId;

	/**
	 * very import for cell data auto fresh
	 * 
	 * @return
	 */
	public SimpleStringProperty resultRuleNameProperty() {
		return resultRuleName;
	}

	/**
	 * very import for cell data auto fresh
	 * 
	 * @return
	 */
	public SimpleStringProperty resultInfoProperty() {
		return resultInfo;
	}

	/**
	 * very import for cell data auto fresh
	 * 
	 * @return
	 */
	public SimpleStringProperty playerIdProperty() {
		return playerId;
	}

	/**
	 * very import for cell data auto fresh
	 * 
	 * @return
	 */
	public SimpleStringProperty playerNameProperty() {
		return playerName;
	}

	/**
	 * very import for cell data auto fresh
	 * 
	 * @return
	 */
	public SimpleStringProperty wechatIdProperty() {
		return wechatId;
	}

	/**
	 * very import for cell data auto fresh
	 * 
	 * @return
	 */
	public SimpleLongProperty playerPointProperty() {
		return playerPoint;
	}

	/**
	 * very import for cell data auto fresh
	 * 
	 * @return
	 */
	public SimpleStringProperty betInfoProperty() {
		return betInfo;
	}

	/**
	 * very import for cell data auto fresh
	 * 
	 * @return
	 */
	public SimpleStringProperty wechatNameProperty() {
		return wechatName;
	}

	/* Get Set method */

	public String getPlayerId() {
		return playerId.getValue();
	}

	public String getPlayerName() {
		return StringUtils.replaceHtml(playerName.getValue());
	}

	public String getPlayerNameRaw() {
		return playerName.getValue();
	}

	public Long getPlayerPoint() {
		return playerPoint.getValue();
	}

	public void setPlayerId(String playerId) {
		this.playerId.set(playerId);
	}

	public void setPlayerName(String playerName) {
		this.playerName.set(playerName);
	}

	public void setPlayerPoint(Long playerPoint) {
		this.playerPoint.set(playerPoint);
	}

	public String getWechatId() {
		return wechatId.getValue();
	}

	public void setWechatId(String wechatId) {
		this.wechatId.set(wechatId);
	}

	public String getWechatName() {
		return wechatName.getValue();
	}

	public void setWechatName(String wechatName) {
		this.wechatName.set(wechatName);
	}

	public String getBetInfo() {
		return betInfo.getValue();
	}

	public void setBetInfo(String betInfo) {
		this.betInfo.set(betInfo);
	}

	public String getResultRuleName() {
		return resultRuleName.getValue();
	}

	public void setResultRuleName(String resultRuleName) {
		this.resultRuleName.set(resultRuleName);
	}

	public String getResultInfo() {
		return resultInfo.getValue();
	}

	public void setResultInfo(String resultInfo) {
		this.resultInfo.set(resultInfo);
	}

	public String getLuckInfo() {
		return this.luckInfo.getValue();
	}

	public void setLuckInfo(String luckInfo) {
		this.luckInfo.set(luckInfo);
	}

	public Long getTraceId() {
		return traceId;
	}

	public void setTraceId(Long traceId) {
		this.traceId = traceId;
	}
}
