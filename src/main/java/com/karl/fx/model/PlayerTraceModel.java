package com.karl.fx.model;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

import com.karl.utils.StringUtils;

public class PlayerTraceModel {
	
    public PlayerTraceModel(String playerId,String playerName,Long playerPoint,String betInfo,String resultRuleName,
    		String resultInfo) {
		super();
		this.playerId = new SimpleStringProperty(playerId);
		this.playerName = new SimpleStringProperty(playerName);
		this.resultRuleName = new SimpleStringProperty(resultRuleName);
		this.playerPoint = new SimpleLongProperty(playerPoint);
		this.betInfo = new SimpleStringProperty(betInfo);
		this.resultInfo = new SimpleStringProperty(resultInfo);
	}

	public static final String PLAYERIDCOLKEY = "playerId";
    public static final String PLAYERNAMECOLKEY = "playerName";
    public static final String PLAYERPOINTCOLKEY = "playerPoint";
	public static final String BETINFOKEY = "betInfo";
	public static final String RESULTRULENAMEKEY = "resultRuleName";
	public static final String RESULTINFOKEY= "resultInfo";
	
	private SimpleStringProperty resultRuleName;
	private SimpleStringProperty resultInfo;
    private  SimpleStringProperty playerId;
    private  SimpleStringProperty wechatId;
    private  SimpleStringProperty playerName;
    private  SimpleLongProperty playerPoint;
    private SimpleStringProperty betInfo;
    private SimpleStringProperty wechatName;

    /**
     * very import for cell data auto fresh
     * @return
     */
    public SimpleLongProperty playerPointProperty() {
    	return playerPoint;
    }

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
}
