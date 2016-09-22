package com.karl.domain;

import java.io.File;
import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
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
import com.karl.fx.model.PlayRule;
import com.karl.fx.model.PlayerApply;
import com.karl.fx.model.PlayerModel;
import com.karl.utils.AppUtils;

@Component
public class RuntimeDomain implements Serializable {

    public RuntimeDomain() {
        groupMap = new HashMap<String, JSONObject>();
        allUsrMap = new HashMap<String, JSONObject>();
        publicUsrMap = new HashMap<String, JSONObject>();
        specialUsrMap = new HashMap<String, JSONObject>();
        runningPlayeres = new HashMap<String, Player>();
        qrCodeFile = new File("temp.jpg");
        bankerRemarkName = "";
        currentRule = EnumSet.allOf(LotteryRule.class);
        groupList = FXCollections.observableArrayList();
        playerList = FXCollections.observableArrayList();
        ruleList = FXCollections.observableArrayList();
        globalGameSignal = Boolean.FALSE;
        currentGameKey = AppUtils.PLAYLONGSPLIT;
        minimumBet = AppUtils.DEFAULT_MINBET;
        maximumBet = AppUtils.DEFAULT_MAXBET;
        bankerPackageNum = AppUtils.DEFAULT_PACKAGE_NUM;
        bankerIndex = 1;
        betOrder = 0;
    }

    private static final long serialVersionUID = 5720576756640779509L;

    private String currentGroupId;
    
    private String currentGroupName;

    private final File qrCodeFile;

    private String uuid;
    private int tip = 0;

    private String cookie;

    private JSONObject syncKeyJNode, User, baseRequest;

    // 群集合
    private Map<String, JSONObject> groupMap;

    // 所有用戶信息
    private Map<String, JSONObject> allUsrMap;

    // 公众号／服务号
    private Map<String, JSONObject> publicUsrMap;

    // 特殊账号
    private Map<String, JSONObject> specialUsrMap;

    /**
     * The latest player info(key= remarkName)
     */
    private Map<String, Player> runningPlayeres;

    /**
     * The current banker
     */
    public String bankerRemarkName;
    
    /**
     * The current banker defined 
     */
    public Integer bankerPackageNum;
    
    /**
     * The current banker defined 
     */
    public Integer bankerIndex;
    
    public Long minimumBet;
    
    public Long maximumBet;

    private EnumSet<LotteryRule> currentRule;

    /**
     * current groups
     */
    private ObservableList<ChatGroupModel> groupList;
    
    /**
     * current player
     */
    private ObservableList<PlayerModel> playerList;
    
    /**
     * current rule List
     */
    private ObservableList<PlayRule> ruleList;
    
    /**
     * current rule List
     */
    private ObservableList<PlayerApply> applyList;

    /**
     * game start/end signal
     */
    private Boolean globalGameSignal;
    
    /**
     * game key for play
     */
    private String currentGameKey;
    
    /**
     * 
     */
    private Integer betOrder;
    

    private String skey, synckey, wxsid, wxuin, passTicket, deviceId = "e"
            + DateKit.getCurrentUnixTime();
    
    /**
     * key=wechatId
     * @return
     */
    public Map<String, String> getCurrentPlayersName() {
    	Map<String, String> playersName = new HashMap<String,String>();
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
            	playersName.put(contact.getString("UserName"), remarkName);
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
    
    public void putRunningPlayeres(String remarkName, Player runningPlayer) {
        this.runningPlayeres.put(remarkName, runningPlayer);
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

	public ObservableList<PlayRule> getRuleList() {
		return ruleList;
	}

	public void setRuleList(ObservableList<PlayRule> ruleList) {
		this.ruleList = ruleList;
	}

	public Boolean getGlobalGameSignal() {
		return globalGameSignal;
	}

	public void setGlobalGameSignal(Boolean globalGameSignal) {
		this.globalGameSignal = globalGameSignal;
	}

	public String getCurrentGameKey() {
		return currentGameKey;
	}

	public void setCurrentGameKey(String currentGameKey) {
		this.currentGameKey = currentGameKey;
	}

	public String getCurrentGroupName() {
		return currentGroupName;
	}

	public void setCurrentGroupName(String currentGroupName) {
		this.currentGroupName = currentGroupName;
	}

	public Long getMinimumBet() {
		return minimumBet;
	}

	public void setMinimumBet(Long minimumBet) {
		this.minimumBet = minimumBet;
	}

	public Long getMaximumBet() {
		return maximumBet;
	}

	public void setMaximumBet(Long maximumBet) {
		this.maximumBet = maximumBet;
	}

	public Integer getBankerPackageNum() {
		return bankerPackageNum;
	}

	public void setBankerPackageNum(Integer bankerPackageNum) {
		this.bankerPackageNum = bankerPackageNum;
	}

	public Integer getBankerIndex() {
		return bankerIndex;
	}

	public void setBankerIndex(Integer bankerIndex) {
		this.bankerIndex = bankerIndex;
	}

	public Integer getBetOrder() {
		return betOrder;
	}

	public void setBetOrder(Integer betOrder) {
		this.betOrder = betOrder;
	}

	public ObservableList<PlayerApply> getApplyList() {
		return applyList;
	}

	public void setApplyList(ObservableList<PlayerApply> applyList) {
		this.applyList = applyList;
	}
}
