package com.karl.fx.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

import com.karl.utils.AppUtils;

public class PlayerApply {
	public PlayerApply(Boolean applyCheck,
			Long applyId, String playerName,
			Integer applyType, Long applyPoint,
			Integer approvalStatus, String webChatId, String wechatName) {
		super();
		this.applyCheck = new SimpleBooleanProperty(applyCheck);
		this.playerName = new SimpleStringProperty(playerName);
		this.applyType = new SimpleIntegerProperty(applyType);
		this.applyPoint = new SimpleLongProperty(applyPoint);
		this.approvalStatus = new SimpleIntegerProperty(approvalStatus);
		this.applyId = new SimpleLongProperty(applyId);
		String applyInfo = "";
		switch (applyType) {
		case AppUtils.APPLYADDPOINT :
			applyInfo =AppUtils.APPLYADDTEXT+" "+String.valueOf(applyPoint);
			break;
		case AppUtils.APPLYSUBPOINT :
			applyInfo =AppUtils.APPLYSUBTEXT+" "+String.valueOf(applyPoint);
			break;
		default:
			break;
		}
		this.applyInfo = new SimpleStringProperty(applyInfo);
		this.webChatId = new SimpleStringProperty(webChatId);
		this.wechatName = new SimpleStringProperty(wechatName);
	}
	public static final String APPLYCHECKKEY = "applyCheck";
    public static final String PLAYERNAMEKEY = "playerName";
    public static final String APPLYTYPEKEY = "applyType";
    public static final String APPLYPOINTKEY = "applyPoint";
    public static final String APPROVALSTATUSKEY = "applyPoint";
    public static final String APPLYINFOKEY = "applyPoint";
    private  SimpleBooleanProperty applyCheck;
    private  SimpleLongProperty applyId;
    private  SimpleStringProperty playerName;
    private  SimpleStringProperty applyInfo;
    private  SimpleIntegerProperty applyType;
    private  SimpleLongProperty applyPoint;
    private  SimpleIntegerProperty approvalStatus;
    private SimpleStringProperty webChatId;
    private SimpleStringProperty wechatName;
    
    public Boolean getApplyCheck() {
		return applyCheck.getValue();
	}
	public void setApplyCheck(Boolean applyCheck) {
		this.applyCheck.setValue(applyCheck);
	}
	public String getPlayerName() {
		return playerName.getValue();
	}
	public void setPlayerName(String playerName) {
		this.playerName.setValue(playerName);
	}
	public Integer getApplyType() {
		return applyType.getValue();
	}
	public void setApplyType(Integer applyType) {
		this.applyType.setValue(applyType);
	}
	public Long getApplyPoint() {
		return applyPoint.getValue();
	}
	public void setApplyPoint(Long applyPoint) {
		this.applyPoint.setValue(applyPoint);
	}
	public Integer getApprovalStatus() {
		return approvalStatus.getValue();
	}
	public void setApprovalStatus(Integer approvalStatus) {
		this.approvalStatus.setValue(approvalStatus);
	}
	public Long getApplyId() {
		return applyId.getValue();
	}
	public void setApplyId(Long applyId) {
		this.applyId.setValue(applyId);
	}
	public String getApplyInfo() {
		return applyInfo.getValue();
	}
	public void setApplyInfo(String applyInfo) {
		this.applyInfo.setValue(applyInfo);
	}
	public String getWebChatId() {
		return webChatId.getValue();
	}
	public void setWebChatId(String webChatId) {
		this.webChatId.set(webChatId);
	}

	public String getWechatName() {
		return wechatName.getValue();
	}

	public void setWechatName(String wechatName) {
		this.wechatName.set(wechatName);
	}
}
