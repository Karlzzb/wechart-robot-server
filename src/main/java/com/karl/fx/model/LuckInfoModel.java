package com.karl.fx.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;



public class LuckInfoModel {
    public static final String  PACKAGEORDERCOL = "packageOrder";
    public static final String  PLAYERNAMECOL = "playerName";
    public static final String  PACKAGEINFOCOL = "packageInfo";
    public static final String  PACKAGETIMECOL = "packageTime";
    public static final String PLAYERROLECOL = "playerRole";
    
    public static final String  PLAYERROLEBANKER= "庄家";
    public static final String  PLAYERROLENOMAL = "玩家";
    public static final String  PLAYERROLENOPOINT = "无分";
    public static final String  PLAYERROLENONE = "非好友";
    public static final String  PLAYERROLENOBET = "未下注";

	
    public LuckInfoModel(Integer packageOrder, String playerName,
			String packageInfo, String packageTime, String playerRole) {
		super();
		this.packageOrder = new SimpleIntegerProperty(packageOrder);
		this.playerName = new SimpleStringProperty(playerName);
		this.packageInfo = new SimpleStringProperty(packageInfo);
		this.packageTime = new SimpleStringProperty(packageTime);
		this.playerRole = new SimpleStringProperty(playerRole);
	}
	
    private  SimpleIntegerProperty packageOrder;
    private  SimpleStringProperty playerName;
    private  SimpleStringProperty packageInfo;
    private  SimpleStringProperty packageTime;
    private SimpleStringProperty playerRole;
    
    /**
     * very import for cell data auto fresh
     * @return
     */
    public SimpleIntegerProperty packageOrderProperty() {
    	return packageOrder;
    }
    
    /**
     * very import for cell data auto fresh
     * @return
     */
    public SimpleStringProperty packageInfoProperty() {
    	return packageInfo;
    }
    
    /**
     * very import for cell data auto fresh
     * @return
     */
    public SimpleStringProperty packageTimeProperty() {
    	return packageTime;
    }
    
    
    /**
     * very import for cell data auto fresh
     * @return
     */
    public SimpleStringProperty playerRoleProperty() {
    	return playerRole;
    }
    
    
	public Integer getPackageOrder() {
		return packageOrder.getValue();
	}
	public void setPackageOrder(Integer packageOrder) {
		this.packageOrder = new SimpleIntegerProperty(packageOrder);
	}
	public String getPlayerName() {
		return playerName.getValue();
	}
	public void setPlayerName(String playerName) {
		this.playerName = new SimpleStringProperty(playerName);
	}
	public String getPackageInfo() {
		return packageInfo.getValue();
	}
	public void setPackageInfo(String packageInfo) {
		this.packageInfo = new SimpleStringProperty(packageInfo);
	}
	public String getPackageTime() {
		return packageTime.getValue();
	}
	public void setPackageTime(String packageTime) {
		this.packageTime = new SimpleStringProperty(packageTime);
	}
	public String getPlayerRole() {
		return playerRole.getValue();
	}
	public void setPlayerRole(String playerRole) {
		this.playerRole = new SimpleStringProperty(playerRole);
	}

}
