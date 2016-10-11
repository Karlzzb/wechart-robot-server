package com.karl.domain;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
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

	private int messageBoardCount;

	public RuntimeDomain() {
		groupMap = new HashMap<String, JSONObject>();
		allUsrMap = new HashMap<String, JSONObject>();
		publicUsrMap = new HashMap<String, JSONObject>();
		specialUsrMap = new HashMap<String, JSONObject>();
		runningPlayeres = new HashMap<String, Player>();
		qrCodeFile = new File("data/temp.jpg");
		imagePath = "file:data/temp.jpg";
		bankerRemarkName = "";
		currentRule = EnumSet.allOf(LotteryRule.class);
		groupList = FXCollections.observableArrayList();
		groupListFiniance = FXCollections.observableArrayList();
		playerList = FXCollections.observableArrayList();
		ruleList = FXCollections.observableArrayList();
		applyList = FXCollections.observableArrayList();
		globalGameSignal = Boolean.FALSE;
		currentGameKey = AppUtils.PLAYLONGSPLIT;
		minimumBet = AppUtils.DEFAULT_MINBET;
		maximumBet = AppUtils.DEFAULT_MAXBET;
		bankerIndex = 1;
		packageNumber = 8;
		betOrder = 0;
		allowPace = Boolean.TRUE;
		allowInvain = Boolean.TRUE;
		allowAllIn = Boolean.TRUE;
		defiendBet = Long.valueOf(50);
		bankerBetPoint = Long.valueOf(0);
		currentTimeOutRule = AppUtils.TIMEOUTPAIDONETIME;
		currentTimeOutRuleBanker = AppUtils.TIMEOUTPAIDONETIME;
		currentLotteryRule = AppUtils.LOTTERYRULE3;
		currentTimeOut = 21;
		messageBoardCount = 0;
	}

	private static final long serialVersionUID = 5720576756640779509L;

	private String readyWechatId;

	private String currentGroupId;

	private String currentMGroupId;

	private String currentGroupName;

	private final File qrCodeFile;
	
	private final String imagePath;

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
	public Integer bankerIndex;

	/**
	 * The current banker defined
	 */
	public Long bankerBetPoint;

	public Long minimumBet;

	public Long maximumBet;

	private EnumSet<LotteryRule> currentRule;

	/**
	 * current groups
	 */
	private ObservableList<ChatGroupModel> groupList;

	/**
	 * current groups
	 */
	private ObservableList<ChatGroupModel> groupListFiniance;

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
	 * game package number defied by banker
	 */
	private Integer packageNumber;

	/**
	 * game key for play
	 */
	private String currentGameKey;

	/**
	 * game serial No. for play
	 */
	public Long currentGameId;

	/**
     * 
     */
	private Integer betOrder;

	/**
	 * if allow banker equal to player
	 */
	private Boolean allowPace;

	/**
	 * if allow banker equal to player
	 */
	private Boolean allowAllIn;

	/**
	 * if allow banker
	 */
	private Boolean allowInvain;

	private String sentOutMessage;

	private String skey, synckey, wxsid, wxuin, passTicket, deviceId = "e"
			+ DateKit.getCurrentUnixTime();

	/**
	 * pre defied fixed bet
	 */
	private Long defiendBet;

	/**
	 * for player
	 */
	private String currentTimeOutRule;

	/**
	 * for banker
	 */
	private String currentTimeOutRuleBanker;

	/**
	 * current timeout limit second
	 */
	private Integer currentTimeOut;

	private Date currentFirstPackegeTime;

	private Date currentLastPackegeTime;

	private String currentLotteryRule;
	

	/**
	 * key=remarkName
	 * 
	 * @return
	 */
	public Map<String, PlayerModel> getCurrentPlayers() {
		Map<String, PlayerModel> playersName = new HashMap<String, PlayerModel>();
		if (getCurrentGroupId() == null || getCurrentGroupId().isEmpty()) {
			return playersName;
		}

		JSONObject groupNode = getGroupMap().get(getCurrentGroupId());
		if (groupNode == null) {
			return playersName;
		}
		JSONArray memberList = groupNode.getJSONArray("MemberList");
		if (memberList == null || memberList.size() < 1) {
			return playersName;
		}
		JSONObject contact = null;
		String remarkName = "";
		String wechatName = "";
		String wechatId = "";
		for (int i = 0, len = memberList.size(); i < len; i++) {
			contact = memberList.getJSONObject(i);
			remarkName = getUserRemarkName(contact.getString("UserName"));
			wechatName = getUserNickName(contact.getString("UserName"));
			wechatId = contact.getString("UserName");
			if (!AppUtils.UNCONTACTUSRNAME.equals(remarkName)) {
				playersName.put(remarkName, new PlayerModel(i, remarkName, 0,
						wechatId, wechatName));
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

	public String getUserNickName(String id) {
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
//		return "e083944759865791";
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

	public String getCurrentMGroupId() {
		return currentMGroupId;
	}

	public void setCurrentMGroupId(String currentMGroupId) {
		this.currentMGroupId = currentMGroupId;
	}

	public String getReadyWechatId() {
		return readyWechatId;
	}

	public void setReadyWechatId(String readyWechatId) {
		this.readyWechatId = readyWechatId;
	}

	public Long getCurrentGameId() {
		return currentGameId;
	}

	public void setCurrentGameId(Long currentGameId) {
		this.currentGameId = currentGameId;
	}

	public Long getBankerBetPoint() {
		return bankerBetPoint;
	}

	public void setBankerBetPoint(Long bankerBetPoint) {
		this.bankerBetPoint = bankerBetPoint;
	}

	public Integer getPackageNumber() {
		return packageNumber;
	}

	public void setPackageNumber(Integer packageNumber) {
		this.packageNumber = packageNumber;
	}

	public Boolean getAllowPace() {
		return allowPace;
	}

	public void setAllowPace(Boolean allowPace) {
		this.allowPace = allowPace;
	}

	public Boolean getAllowInvain() {
		return allowInvain;
	}

	public void setAllowInvain(Boolean allowInvain) {
		this.allowInvain = allowInvain;
	}

	public Boolean getAllowAllIn() {
		return allowAllIn;
	}

	public void setAllowAllIn(Boolean allowAllIn) {
		this.allowAllIn = allowAllIn;
	}

	public Long getDefiendBet() {
		return defiendBet;
	}

	public void setDefiendBet(Long defiendBet) {
		this.defiendBet = defiendBet;
	}

	public String getSentOutMessage() {
		return sentOutMessage;
	}

	public void setSentOutMessage(String sentOutMessage) {
		this.sentOutMessage = sentOutMessage;
	}

	public ObservableList<ChatGroupModel> getGroupListFiniance() {
		return groupListFiniance;
	}

	public void setGroupListFiniance(
			ObservableList<ChatGroupModel> groupListFiniance) {
		this.groupListFiniance = groupListFiniance;
	}

	public void setCurrentTimeOutRule(String currentTimeOutRule) {
		this.currentTimeOutRule = currentTimeOutRule;
	}

	public String getCurrentTimeOutRule() {
		return currentTimeOutRule;
	}

	public String getCurrentTimeOutRuleBanker() {
		return currentTimeOutRuleBanker;
	}

	public void setCurrentTimeOutRuleBanker(String currentTimeOutRuleBanker) {
		this.currentTimeOutRuleBanker = currentTimeOutRuleBanker;
	}

	public Integer getCurrentTimeOut() {
		return currentTimeOut;
	}

	public void setCurrentTimeOut(Integer currentTimeOut) {
		this.currentTimeOut = currentTimeOut;
	}

	public Date getCurrentFirstPackegeTime() {
		return currentFirstPackegeTime;
	}

	public void setcurrentFirstPacageTime(Date currentFirstPackegeTime) {
		if (this.currentFirstPackegeTime == null) {
			this.currentFirstPackegeTime = currentFirstPackegeTime;
		} else {
			this.currentFirstPackegeTime = this.currentFirstPackegeTime
					.compareTo(currentFirstPackegeTime) > 0 ? currentFirstPackegeTime
					: this.currentFirstPackegeTime;
		}
	}
	
	public void removeCurrentFirstPacageTime() {
		this.currentFirstPackegeTime = null;
	}

	public void setcurrentLastPacageTime(Date currentLastPackegeTime) {
		if (this.currentLastPackegeTime == null) {
			this.currentLastPackegeTime = currentLastPackegeTime;
		} else {
			this.currentLastPackegeTime = this.currentLastPackegeTime
					.compareTo(currentLastPackegeTime) < 0 ? currentLastPackegeTime
					: this.currentLastPackegeTime;
		}
	}
	
	public void removeCurrentLastPackegeTime() {
		this.currentLastPackegeTime = null;
	}


	public Date getCurrentLastPackegeTime() {
		return currentLastPackegeTime;
	}

	public int getMessageBoardCount() {
		return messageBoardCount;
	}

	public void setMessageBoardCount(int messageBoardCount) {
		this.messageBoardCount = messageBoardCount;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setCurrentLotteryRule(String currentLotteryRule) {
		this.currentLotteryRule = currentLotteryRule;
	}

	public String getCurrentLotteryRule() {
		return currentLotteryRule;
	}
}
