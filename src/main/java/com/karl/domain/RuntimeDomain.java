package com.karl.domain;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.springframework.stereotype.Component;

import blade.kit.DateKit;
import blade.kit.StringKit;
import blade.kit.json.JSONObject;

import com.karl.db.domain.GameInfo;
import com.karl.db.domain.Player;
import com.karl.db.domain.PlayerTrace;
import com.karl.fx.model.ChatGroupModel;
import com.karl.fx.model.LuckInfoModel;
import com.karl.fx.model.PlayRule;
import com.karl.fx.model.PlayerApply;
import com.karl.fx.model.PlayerModel;
import com.karl.utils.AppUtils;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateNotFoundException;

@Component
public class RuntimeDomain implements Serializable {

	private int messageBoardCount;
	
	private BlockingQueue<MessageDomain> msgQueue;

	public RuntimeDomain() {
		groupMap = new HashMap<String, JSONObject>();
		allUsrMap = new HashMap<String, JSONObject>();
		// publicUsrMap = new HashMap<String, JSONObject>();
		// specialUsrMap = new HashMap<String, JSONObject>();
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
		currentGameKey = AppUtils.PLAYLUCKWAY;
		minimumBet = AppUtils.DEFAULT_MINBET;
		maximumBet = AppUtils.DEFAULT_MAXBET;
		bankerIndex = 1;
		packageNumber = 8;
		betOrder = 0;
		allowPace = Boolean.TRUE;
		allowAllIn = Boolean.TRUE;
		allowInvainBanker = Boolean.FALSE;
		allowInvainPlayer = Boolean.FALSE;
		defiendBet = Long.valueOf(50);
		bankerBetPoint = Long.valueOf(0);
		currentTimeOutRule = AppUtils.TIMEOUTPAIDONETIME;
		currentTimeOutRuleBanker = AppUtils.TIMEOUTPAIDONETIME;
		currentLotteryRule = AppUtils.LOTTERYRULE3;
		currentTimeOut = 3;
		messageBoardCount = 0;
		manageFee = 130L;
		showManageFee = Boolean.TRUE;
		packageFeeModel = AppUtils.FIXEDPACKAGEFEEMODEL;
		fixedPackageFee = 10L;
		mathPackageFeeB = 1L;
		mathPackageFeeC = 1L;
		bankerWinCutRate = 5L;
		currentRealPackageFee = 0L;
		firstBankerFee = 0L;
		luckInfoModeList = FXCollections.observableArrayList();
		definedStartInfo = "3秒玩法，新鲜上市!";
		buildFtl();
		illegalPlayer = new ArrayList<String>();
		msgQueue = new ArrayBlockingQueue<MessageDomain>(100);
		paceLotteryRule = AppUtils.PACELARGEWIN;
	}

	private static final long serialVersionUID = 5720576756640779509L;

	private Configuration ftlCfg;

	private String readyWechatId;

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

	// 所有用戶信息
	private Map<String, JSONObject> allUsrMap;

	// // 公众号／服务号
	// private Map<String, JSONObject> publicUsrMap;

	// // 特殊账号
	// private Map<String, JSONObject> specialUsrMap;

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
	 * game entity. for play(before current)
	 */
	public GameInfo beforeGameInfo;

	/**
     * 
     */
	private Integer betOrder;

	/**
	 * if allow banker equal to player
	 */
	private Boolean allowPace;

	/**
	 * if allow player explict soha
	 */
	private Boolean allowAllIn;

	/**
	 * if allow banker betpoint < 0
	 */
	private Boolean allowInvainBanker;

	/**
	 * if allow player point < 0
	 */
	private Boolean allowInvainPlayer;

	private String[] sentOutMessage;

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

	private Long manageFee;

	private Boolean showManageFee;

	private String packageFeeModel;

	private Long fixedPackageFee;

	private Long mathPackageFeeB;

	private Long mathPackageFeeC;

	private Long bankerWinCutRate;

	private Long currentRealPackageFee;

	private ObservableList<LuckInfoModel> luckInfoModeList;

	private Long firstBankerFee;

	private String definedStartInfo;
	
	private List<String> illegalPlayer;

	private Integer paceLotteryRule;

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
			if (StringKit.isNotBlank(member.getString("NickName"))) {
				name = member.getString("NickName");
			}
		}
		return name;
	}
	
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

	public Map<String, JSONObject> getAllUsrMap() {
		return allUsrMap;
	}
	
	public JSONObject getSingleUsrMap(String remarkName) {
		return allUsrMap.get(remarkName);
	}

	public void setAllUsrMap(Map<String, JSONObject> allUsrMap) {
		this.allUsrMap = allUsrMap;
	}

	public void putAllUsrMap(String key, JSONObject value) {
		this.allUsrMap.put(key, value);
	}
	
	public void clearAllUsrMap() {
		this.allUsrMap.clear();
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

	public String[] getSentOutMessage() {
		return sentOutMessage;
	}

	public void setSentOutMessage(String[] content) {
		this.sentOutMessage = content;
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

	public Boolean getAllowInvainPlayer() {
		return allowInvainPlayer;
	}

	public void setAllowInvainPlayer(Boolean allowInvainPlayer) {
		this.allowInvainPlayer = allowInvainPlayer;
	}

	public Boolean getAllowInvainBanker() {
		return allowInvainBanker;
	}

	public void setAllowInvainBanker(Boolean allowInvainBanker) {
		this.allowInvainBanker = allowInvainBanker;
	}

	public Long getManageFee() {
		return this.manageFee;
	}

	public void setManageFee(Long manageFee) {
		this.manageFee = manageFee;
	}

	public boolean getShowManageFee() {
		return this.showManageFee;
	}

	public void setShowManageFee(boolean showManageFee) {
		this.showManageFee = showManageFee;
	}

	public void setPackageFeeModel(String packageFeeModel) {
		this.packageFeeModel = packageFeeModel;
	}

	public Object getPackageFeeModel() {
		return packageFeeModel;
	}

	public Long getCurrentPackageFee(List<PlayerTrace> traceList,
			GameInfo gameInfo) {
		if (packageFeeModel.equals(AppUtils.FIXEDPACKAGEFEEMODEL)) {
			return fixedPackageFee;
		} else if (packageFeeModel.equals(AppUtils.MATHPACKAGEFEEMODEL)) {
			return (traceList.size() + 1) * mathPackageFeeB + mathPackageFeeC;
		} else if (packageFeeModel.equals(AppUtils.REALPACKAGEFEEMODEL)) {
			return currentRealPackageFee;
		}
		return Long.valueOf(0);
	}

	public Long getFixedPackageFee() {
		return fixedPackageFee;
	}

	public void setFixedPackageFee(Long fixedPackageFee) {
		this.fixedPackageFee = fixedPackageFee;
	}

	public void setMathPackageFeeB(Long mathPackageFeeB) {
		this.mathPackageFeeB = mathPackageFeeB;
	}

	public void setMathPackageFeeC(Long mathPackageFeeC) {
		this.mathPackageFeeC = mathPackageFeeC;
	}

	public Long getMathPackageFeeC() {
		return mathPackageFeeC;
	}

	public Long getMathPackageFeeB() {
		return mathPackageFeeB;
	}

	public void setBankerWinCutRate(Long bankerWinCutRate) {
		this.bankerWinCutRate = bankerWinCutRate;
	}

	public Long getBankerWinCutRate() {
		return bankerWinCutRate;
	}

	public Long getCurrentRealPackageFee() {
		return currentRealPackageFee;
	}

	public void setCurrentRealPackageFee(Long currentRealPackageFee) {
		this.currentRealPackageFee = currentRealPackageFee;
	}

	public ObservableList<LuckInfoModel> getLuckInfoModeList() {
		return this.luckInfoModeList;
	}

	public void setLuckInfoModeList(
			ObservableList<LuckInfoModel> luckInfoModeList) {
		this.luckInfoModeList = luckInfoModeList;
	}

	public GameInfo getBeforeGameInfo() {
		return beforeGameInfo;
	}

	public void setBeforeGameInfo(GameInfo beforeGameInfo) {
		this.beforeGameInfo = beforeGameInfo;
	}

	public Long getFirstBankerFee() {
		return this.firstBankerFee;
	}

	public void setFirstBankerFee(Long firstBankerFee) {
		this.firstBankerFee = firstBankerFee;
	}

	public String getBestSyncCheckChannel() {
		return bestSyncCheckChannel;
	}

	public void setBestSyncCheckChannel(String bestSyncCheckChannel) {
		this.bestSyncCheckChannel = bestSyncCheckChannel;
	}

	public String getDefinedStartInfo() {
		return this.definedStartInfo;
	}

	public void setDefinedStartInfo(String definedStartInfo) {
		this.definedStartInfo = definedStartInfo;
	}

	private void buildFtl() {
		ftlCfg = new Configuration(Configuration.VERSION_2_3_23);
		ftlCfg.setClassForTemplateLoading(getClass(), "/ftl");
		ftlCfg.setDefaultEncoding("UTF-8");
		ftlCfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		ftlCfg.setLogTemplateExceptions(false);
		ftlCfg.setWhitespaceStripping(Boolean.TRUE);
	}

	public Template getRankTemplate() {
		try {
			return ftlCfg.getTemplate("ranking.ftlh");
		} catch (TemplateNotFoundException e) {
			e.printStackTrace();
		} catch (MalformedTemplateNameException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Template getBillTemplate() {
		try {
			return ftlCfg.getTemplate("bill.ftlh");
		} catch (TemplateNotFoundException e) {
			e.printStackTrace();
		} catch (MalformedTemplateNameException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<String> getIllegalPlayer() {
		return illegalPlayer;
	}

	public void setIllegalPlayer(List<String> illegalPlayer) {
		this.illegalPlayer = illegalPlayer;
	}
	
	public void addIllegalPlayer(String playerName) {
		illegalPlayer.add(playerName);
	}
	
	public void clearIllegalPlayer() {
		illegalPlayer.clear();
	}

	public BlockingQueue<MessageDomain> getMsgQueue() {
		return msgQueue;
	}

	public void setMsgQueue(BlockingQueue<MessageDomain> msgQueue) {
		this.msgQueue = msgQueue;
	}

	public void setPaceLotteryRule(Integer paceLotteryRule) {
		this.paceLotteryRule = paceLotteryRule;
	}

	public Integer getPaceLotteryRule() {
		return paceLotteryRule;
	}
}
