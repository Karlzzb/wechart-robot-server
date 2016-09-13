package com.karl.domain;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.springframework.stereotype.Component;

import blade.kit.DateKit;
import blade.kit.StringKit;
import blade.kit.json.JSONArray;
import blade.kit.json.JSONObject;

import com.karl.db.domain.Player;
import com.karl.fx.model.ChatGroupModel;
import com.karl.fx.model.PlayerModel;
import com.karl.utils.AppUtils;

@Component
public class RuntimeDomain implements Serializable {

    public RuntimeDomain() {
        groupMap = new HashMap<String, JSONObject>();
        allUsrMap = new HashMap<String, JSONObject>();
        groupUsrMap = new HashMap<String, JSONObject>();
        publicUsrMap = new HashMap<String, JSONObject>();
        specialUsrMap = new HashMap<String, JSONObject>();
        runningPlayeres = new HashMap<String, Player>();
        qrCodeFile = new File("temp.jpg");
        bankerRemarkName = "";
        currentRule = EnumSet.allOf(LotteryRule.class);
        groupList = FXCollections.observableArrayList();
        playerList = FXCollections.observableArrayList();
    }

    private static final long serialVersionUID = 5720576756640779509L;

    private String currentGroupId;

    private final File qrCodeFile;

    private String uuid;
    private int tip = 0;

    private String cookie;

    private JSONObject syncKeyJNode, User, baseRequest;

    // 群集合
    private Map<String, JSONObject> groupMap;

    // 所有用戶信息
    private Map<String, JSONObject> allUsrMap;

    // 群用户
    private Map<String, JSONObject> groupUsrMap;

    // 公众号／服务号
    private Map<String, JSONObject> publicUsrMap;

    // 特殊账号
    private Map<String, JSONObject> specialUsrMap;

    /**
     * The latest player info
     */
    public Map<String, Player> runningPlayeres;

    /**
     * The current banker
     */
    public String bankerRemarkName;

    private EnumSet<LotteryRule> currentRule;

    /**
     * current groups
     */
    private ObservableList<ChatGroupModel> groupList;
    
    /**
     * current player
     */
    private ObservableList<PlayerModel> playerList;

    private String skey, synckey, wxsid, wxuin, passTicket, deviceId = "e"
            + DateKit.getCurrentUnixTime();
    
    
    
    public List<String> getCurrentPlayersName() {
    	List<String> playersName = new ArrayList<String>();
    	if (getCurrentGroupId() == null ||getCurrentGroupId().isEmpty()) {
    		return playersName;
    	}
    	
    	JSONObject groupNode = getGroupMap().get(getCurrentGroupId());
    	if (groupNode == null) {
    		return playersName;
    	}
        JSONArray memberList = groupNode.getJSONArray("MemberList");
        if (memberList == null || memberList.size() <1) {
    		return playersName;
        }
        JSONObject contact = null;
        String remarkName = "";
        for (int i = 0, len = memberList.size(); i < len; i++) {
            contact = memberList.getJSONObject(i);
            remarkName = getUserRemarkName(contact.getString("UserName"));
            if (!AppUtils.UNCONTACTUSRNAME.equals(remarkName)) {
            	playersName.add(remarkName);
            }
        }
    	return playersName;
    }
    
    public String getUserRemarkName(String id) {
        String name = AppUtils.UNCONTACTUSRNAME;
        JSONObject member = getAllUsrMap().get(id);
        if (member != null && member.getString("UserName").equals(id)) {
            if (StringKit.isNotBlank(member.getString("RemarkName"))) {
                name = member.getString("RemarkName");
            } else {
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

    public Map<String, JSONObject> getAllUsrMap() {
        return allUsrMap;
    }

    public void setAllUsrMap(Map<String, JSONObject> allUsrMap) {
        this.allUsrMap = allUsrMap;
    }

    public void putAllUsrMap(String key, JSONObject value) {
        this.allUsrMap.put(key, value);
    }

    public Map<String, JSONObject> getGroupUsrMap() {
        return groupUsrMap;
    }

    public void setGroupUsrMap(Map<String, JSONObject> groupUsrMap) {
        this.groupUsrMap = groupUsrMap;
    }

    public void putGroupUsrMap(String key, JSONObject value) {
        this.groupUsrMap.put(key, value);
    }

    public Map<String, JSONObject> getPublicUsrMap() {
        return publicUsrMap;
    }

    public void setPublicUsrMap(Map<String, JSONObject> publicUsrMap) {
        this.publicUsrMap = publicUsrMap;
    }

    public void putPublicUsrMap(String key, JSONObject value) {
        this.publicUsrMap.put(key, value);
    }

    public Map<String, JSONObject> getSpecialUsrMap() {
        return specialUsrMap;
    }

    public void setSpecialUsrMap(Map<String, JSONObject> specialUsrMap) {
        this.specialUsrMap = specialUsrMap;
    }

    public void putSpecialUsrMap(String key, JSONObject value) {
        this.specialUsrMap.put(key, value);
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
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public File getQrCodeFile() {
        return qrCodeFile;
    }

    public Map<String, Player> getRunningPlayeres() {
        return runningPlayeres;
    }

    public void setRunningPlayeres(Map<String, Player> runningPlayeres) {
        this.runningPlayeres = runningPlayeres;
    }

    public String getBankerRemarkName() {
        return bankerRemarkName;
    }

    public void setBankerRemarkName(String bankerRemarkName) {
        this.bankerRemarkName = bankerRemarkName;
    }

    public EnumSet<LotteryRule> getCurrentRule() {
        return currentRule;
    }

    public void setCurrentRule(EnumSet<LotteryRule> currentRule) {
        this.currentRule = currentRule;
    }

    public ObservableList<ChatGroupModel> getGroupList() {
        return groupList;
    }

    public void setGroupList(ObservableList<ChatGroupModel> groupList) {
        this.groupList = groupList;
    }

	public ObservableList<PlayerModel> getPlayerList() {
		return playerList;
	}

	public void setPlayerList(ObservableList<PlayerModel> playerList) {
		this.playerList = playerList;
	}
}
