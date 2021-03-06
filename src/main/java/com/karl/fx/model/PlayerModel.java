package com.karl.fx.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class PlayerModel{
    public static final String AUDOIDCOLKEY = "autoID";
    public static final String PLAYERNAMECOLKEY = "playerName";
    public static final String PLAYERPOINTCOLKEY = "playerPoint";
	public static final String PLAYERBETCOLKEY = "playerLatestBet";
	public static final String ISBANKERCOLKEY = "isBanker";
	
    private  SimpleIntegerProperty autoID;
    private  SimpleBooleanProperty isBanker;
    private  SimpleStringProperty wechatId;
    private  SimpleStringProperty playerName;
    private  SimpleLongProperty playerPoint;
    private SimpleStringProperty playerLatestBet;
    private SimpleStringProperty wechatName;

    public PlayerModel(Integer autoID, String playerName, Long playerPoint, String wechatId, String wechatName) {
        this.autoID =  new SimpleIntegerProperty(autoID);;
        this.playerName = new SimpleStringProperty(playerName);
        this.playerPoint = new SimpleLongProperty(playerPoint);
        this.wechatId = new SimpleStringProperty(wechatId);
        this.isBanker = new SimpleBooleanProperty(Boolean.FALSE);
        this.playerLatestBet = new SimpleStringProperty("");
		this.wechatName = new SimpleStringProperty(wechatName);
    }
    
    /**
     * very import for cell data auto fresh
     * @return
     */
    public SimpleStringProperty playerLatestBetProperty() {
    	return playerLatestBet;
    }
    
    /**
     * very import for cell data auto fresh
     * @return
     */
    public SimpleStringProperty playerNameProperty() {
    	return playerName;
    }
    
    
    /**
     * very import for cell data auto fresh
     * @return
     */
    public SimpleLongProperty playerPointProperty() {
    	return playerPoint;
    }

    public String getPlayerName() {
        return playerName.getValue();
    }

    public Long getPlayerPoint() {
        return playerPoint.getValue();
    }

	public Integer getAutoID() {
		return autoID.getValue();
	}

	public void setPlayerName(String playerName) {
		this.playerName.set(playerName);
	}

	public void setPlayerPoint(Long playerPoint) {
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

	public String getWechatName() {
		return wechatName.getValue();
	}

	public void setWechatName(String wechatName) {
		this.wechatName.set(wechatName);
	}

}