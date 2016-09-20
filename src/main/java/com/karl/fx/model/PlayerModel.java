package com.karl.fx.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import com.karl.utils.StringUtils;

public class PlayerModel {
    public static final String AUDOIDCOLKEY = "autoID";
    public static final String PLAYERIDCOLKEY = "playerId";
    public static final String PLAYERNAMECOLKEY = "playerName";
    public static final String PLAYERPOINTCOLKEY = "playerPoint";
	public static final String PLAYERBETCOLKEY = "playerLatestBet";
    private  SimpleIntegerProperty autoID;
    private  SimpleBooleanProperty isBanker;
    private  SimpleStringProperty playerId;
    private  SimpleStringProperty wechatId;
    private  SimpleStringProperty playerName;
    private  SimpleStringProperty playerPoint;
    private SimpleStringProperty playerLatestBet;

    public PlayerModel(Integer autoID, String playerName, Integer playerPoint, String wechatId) {
        this.autoID =  new SimpleIntegerProperty(autoID);;
        this.playerId = new SimpleStringProperty(StringUtils.getMD5(playerName));
        this.playerName = new SimpleStringProperty(playerName);
        this.playerPoint = new SimpleStringProperty(String.valueOf(playerPoint));
        this.wechatId = new SimpleStringProperty(wechatId);
        this.isBanker = new SimpleBooleanProperty(Boolean.FALSE);
        this.playerLatestBet = new SimpleStringProperty("");
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

    public String getPlayerPoint() {
        return playerPoint.getValue();
    }

	public Integer getAutoID() {
		return autoID.getValue();
	}


	public void setPlayerId(String playerId) {
		this.playerId.set(playerId);
	}

	public void setPlayerName(String playerName) {
		this.playerName.set(playerName);
	}

	public void setPlayerPoint(String playerPoint) {
		this.playerPoint.set(playerPoint);
	}

	public void setAutoID(Integer autoID) {
		this.autoID.set(autoID);
	}

	public String getWechatId() {
		return wechatId.getValue();
	}

	public void setWechatId(String wechatId) {
		this.wechatId.set(wechatId);
	}

	public Boolean getIsBanker() {
		return isBanker.getValue();
	}

	public void setIsBanker(Boolean isBanker) {
		this.isBanker.set(isBanker);
	}

	public String getPlayerLatestBet() {
		return playerLatestBet.getValue();
	}

	public void setPlayerLatestBet(String playerLatestBet) {
		this.playerLatestBet.set(playerLatestBet==null?"":playerLatestBet);
	}

}