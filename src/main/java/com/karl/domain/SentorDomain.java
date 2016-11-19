package com.karl.domain;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import blade.kit.DateKit;
import blade.kit.StringKit;
import blade.kit.json.JSONObject;

import com.karl.utils.AppUtils;

//@Component
public class SentorDomain implements Serializable {

	private BlockingQueue<MessageDomain> msgQueue;

	public SentorDomain() {
		groupMap = new HashMap<String, JSONObject>();
		// publicUsrMap = new HashMap<String, JSONObject>();
		// specialUsrMap = new HashMap<String, JSONObject>();
		qrCodeFile = new File("data/temp2.jpg");
		imagePath = "file:data/temp2.jpg";
		msgQueue = new ArrayBlockingQueue<MessageDomain>(100);
	}

	private static final long serialVersionUID = 5720576756640779509L;

	private String currentGroupId;

	private String currentMGroupId;

	private String currentGroupName;

	private final File qrCodeFile;

	private final String imagePath;

	private String uuid;
	private int tip = 0;

	private String cookie;

	private String bestSyncCheckChannel;

	private JSONObject syncKeyJNode, User, baseRequest;

	// 群集合
	private Map<String, JSONObject> groupMap;

	private String skey, synckey, wxsid, wxuin, passTicket, deviceId = "e"
			+ DateKit.getCurrentUnixTime();


	
	public String getUserNickName(JSONObject member) {
		String name = AppUtils.UNCONTACTUSRNAME;
		if (member != null) {
			if (StringKit.isNotBlank(member.getString("NickName"))) {
				name = member.getString("NickName");
			}
		}
		return name;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public int getTip() {
		return tip;
	}

	public void setTip(int tip) {
		this.tip = tip;
	}

	public String getCookie() {
		return cookie;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	public JSONObject getSyncKeyJNode() {
		return syncKeyJNode;
	}

	public void setSyncKeyJNode(JSONObject syncKey) {
		syncKeyJNode = syncKey;
	}

	public JSONObject getUser() {
		return User;
	}

	public void setUser(JSONObject user) {
		User = user;
	}

	public JSONObject getBaseRequest() {
		return baseRequest;
	}

	public void setBaseRequest(JSONObject baseRequest) {
		this.baseRequest = baseRequest;
	}

	public Map<String, JSONObject> getGroupMap() {
		return groupMap;
	}

	public void setGroupMap(Map<String, JSONObject> groupMap) {
		this.groupMap = groupMap;
	}

	public void putGroupMap(String key, JSONObject value) {
		this.groupMap.put(key, value);
	}
	
	public void clearGroupMap() {
		this.groupMap.clear();
	}

	public String getCurrentGroupId() {
		return currentGroupId;
	}

	public void setCurrentGroupId(String currentGroupId) {
		this.currentGroupId = currentGroupId;
	}

	public String getSkey() {
		return skey;
	}

	public void setSkey(String skey) {
		this.skey = skey;
	}

	public String getSynckey() {
		return synckey;
	}

	public void setSynckey(String synckey) {
		this.synckey = synckey;
	}

	public String getWxsid() {
		return wxsid;
	}

	public void setWxsid(String wxsid) {
		this.wxsid = wxsid;
	}

	public String getWxuin() {
		return wxuin;
	}

	public void setWxuin(String wxuin) {
		this.wxuin = wxuin;
	}

	public String getPassTicket() {
		return passTicket;
	}

	public void setPassTicket(String passTicket) {
		this.passTicket = passTicket;
	}

	public String getDeviceId() {
		// return "e083944759865791";
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public File getQrCodeFile() {
		return qrCodeFile;
	}

	public String getCurrentGroupName() {
		return currentGroupName;
	}

	public void setCurrentGroupName(String currentGroupName) {
		this.currentGroupName = currentGroupName;
	}

	public String getCurrentMGroupId() {
		return currentMGroupId;
	}

	public void setCurrentMGroupId(String currentMGroupId) {
		this.currentMGroupId = currentMGroupId;
	}

	public String getImagePath() {
		return imagePath;
	}

	public String getBestSyncCheckChannel() {
		return bestSyncCheckChannel;
	}

	public void setBestSyncCheckChannel(String bestSyncCheckChannel) {
		this.bestSyncCheckChannel = bestSyncCheckChannel;
	}

	public BlockingQueue<MessageDomain> getMsgQueue() {
		return msgQueue;
	}

	public void setMsgQueue(BlockingQueue<MessageDomain> msgQueue) {
		this.msgQueue = msgQueue;
	}
}
