package com.karl.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import blade.kit.DateKit;
import blade.kit.StringKit;
import blade.kit.http.HttpRequest;
import blade.kit.json.JSON;
import blade.kit.json.JSONArray;
import blade.kit.json.JSONObject;

import com.karl.domain.MessageDomain;
import com.karl.domain.SentorDomain;
import com.karl.thread.MessageConsumer;
import com.karl.utils.AppUtils;
import com.karl.utils.CookieUtil;
import com.karl.utils.StringUtils;

@Service
public class WebWechatSentor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(WebWechatSentor.class);

	private SentorDomain sentorDomain;
	
	private GameService gameService;

	private volatile boolean stopRequested;
	
	private ExecutorService messageService;

	@Autowired
	public WebWechatSentor(SentorDomain sentorDomain, GameService gameService)
			throws InterruptedException {
		System.setProperty("jsse.enableSNIExtension", "false");
		System.setProperty("https.protocols", "TLSv1.1");
		this.sentorDomain = sentorDomain;
		new Thread(new MessageConsumer(sentorDomain)).start();
		this.gameService = gameService;
		messageService = Executors.newFixedThreadPool(1);
	}

	/**
	 * 获取UUID
	 * 
	 * @return
	 */
	public String getUUID() {
		String url = "https://login.weixin.qq.com/jslogin";
		HttpRequest request = HttpRequest.get(url, true, "appid",
				"wx782c26e4c19acffb", "fun", "new", "lang", "zh_CN", "_",
				DateKit.getCurrentUnixTime());

		LOGGER.debug("[*] " + request);

		String res = request.body();
		request.disconnect();

		if (StringKit.isNotBlank(res)) {
			String code = StringUtils.match("window.QRLogin.code = (\\d+);",
					res);
			if (null != code) {
				if (code.equals("200")) {
					sentorDomain.setUuid(StringUtils.match(
							"window.QRLogin.uuid = \"(.*)\";", res));
					return sentorDomain.getUuid();
				} else {
					LOGGER.error("[*] 错误的状态码: {}", code);
				}
			}
		}
		return null;
	}

	/**
	 * 显示二维码
	 * 
	 * @return
	 */
	public void showQrCode() {

		String url = "https://login.weixin.qq.com/qrcode/"
				+ sentorDomain.getUuid();

		HttpRequest.post(url, true, "t", "webwx", "_",
				DateKit.getCurrentUnixTime()).receive(
				sentorDomain.getQrCodeFile());
	}

	/**
	 * 等待登录
	 */
	public String waitForLogin() {
		sentorDomain.setTip(1);
		String url = "https://login.weixin.qq.com/cgi-bin/mmwebwx-bin/login";
		HttpRequest request = HttpRequest.get(url, true, "tip",
				sentorDomain.getTip(), "uuid", sentorDomain.getUuid(), "_",
				DateKit.getCurrentUnixTime());

		LOGGER.debug("[*] " + request.toString());

		String res = request.body();
		request.disconnect();

		if (null == res) {
			LOGGER.error("[*] 扫描二维码验证失败");
			return "";
		}

		String code = StringUtils.match("window.code=(\\d+);", res);
		if (null == code) {
			LOGGER.error("[*] 扫描二维码验证失败");
			return "";
		} else {
			if (code.equals("201")) {
				LOGGER.debug("[*] 成功扫描,请在手机上点击确认以登录");
				sentorDomain.setTip(0);
			} else if (code.equals("200")) {
				LOGGER.debug("[*] 正在登录...");
				String pm = StringUtils.match(
						"window.redirect_uri=\"(\\S+?)\";", res);
				AppUtils.redirect_uri2 = pm + "&fun=new";
				LOGGER.info("[*] redirect_uri={}", AppUtils.redirect_uri2);
				AppUtils.base_uri2 = AppUtils.redirect_uri2.substring(0,
						AppUtils.redirect_uri2.lastIndexOf("/"));
				LOGGER.info("[*] base_uri={}", AppUtils.base_uri2);

				LOGGER.info("Login response={}", res);
			} else if (code.equals("408")) {
				LOGGER.debug("[*] 登录超时");
			} else {
				LOGGER.debug("[*] 扫描code={}", code);
			}
		}
		return code;
	}

	/**
	 * 登录
	 */
	public boolean login() {

		HttpRequest request = HttpRequest.get(AppUtils.redirect_uri2);

		LOGGER.debug("[*] " + request);

		String res = request.body();
		sentorDomain.setCookie(CookieUtil.getCookie(request));

		request.disconnect();

		if (StringKit.isBlank(res)) {
			return false;
		}

		sentorDomain.setSkey(StringUtils.match("<skey>(\\S+)</skey>", res));
		sentorDomain.setWxsid(StringUtils.match("<wxsid>(\\S+)</wxsid>", res));
		sentorDomain.setWxuin(StringUtils.match("<wxuin>(\\S+)</wxuin>", res));
		sentorDomain.setPassTicket(StringUtils.match(
				"<pass_ticket>(\\S+)</pass_ticket>", res));

		LOGGER.debug("[*] skey[{}]", sentorDomain.getSkey());
		LOGGER.debug("[*] wxsid[{}]", sentorDomain.getWxsid());
		LOGGER.debug("[*] wxuin[{}]", sentorDomain.getWxuin());
		LOGGER.debug("[*] pass_ticket[{}]", sentorDomain.getPassTicket());

		sentorDomain.setBaseRequest(new JSONObject());
		sentorDomain.getBaseRequest().put("Uin", sentorDomain.getWxuin());
		sentorDomain.getBaseRequest().put("Sid", sentorDomain.getWxsid());
		sentorDomain.getBaseRequest().put("Skey", sentorDomain.getSkey());
		sentorDomain.getBaseRequest().put("DeviceID",
				sentorDomain.getDeviceId());

		return true;
	}

	/**
	 * 微信初始化
	 */
	public boolean wxInit() {

		String url = AppUtils.base_uri2 + "/webwxinit?r="
				+ DateKit.getCurrentUnixTime() + "&pass_ticket="
				+ sentorDomain.getPassTicket() + "&skey="
				+ sentorDomain.getSkey();

		JSONObject body = new JSONObject();
		body.put("BaseRequest", this.sentorDomain.getBaseRequest());

		HttpRequest request = HttpRequest.post(url)
				.header("Content-Type", "application/json;charset=utf-8")
				.header("Cookie", sentorDomain.getCookie())
				.send(body.toString());

		LOGGER.debug("[wechat init request] " + request);
		String res = request.body();
		request.disconnect();

		LOGGER.debug("[wechat init response] " + res);
		if (StringKit.isBlank(res)) {
			return false;
		}

		try {
			JSONObject jsonObject = JSON.parse(res).asObject();
			if (null != jsonObject) {
				JSONObject BaseResponse = jsonObject
						.getJSONObject("BaseResponse");

				if (null != BaseResponse) {
					int ret = BaseResponse.getInt("Ret", -1);
					if (ret == 0) {
						sentorDomain.setSyncKeyJNode(jsonObject
								.getJSONObject("SyncKey"));
						sentorDomain.setUser(jsonObject.getJSONObject("User"));

						StringBuffer synckey = new StringBuffer();

						JSONArray list = sentorDomain.getSyncKeyJNode()
								.getJSONArray("List");
						for (int i = 0, len = list.size(); i < len; i++) {
							JSONObject item = list.getJSONObject(i);
							synckey.append("|" + item.getInt("Key", 0) + "_"
									+ item.getInt("Val", 0));
						}

						sentorDomain.setSynckey(synckey.substring(1));
						if (!assemableContactors(
								jsonObject.getJSONArray("ContactList"),
								Boolean.FALSE)) {
							return false;
						}
						return true;
					}
				}

			}
		} catch (Exception e) {
			LOGGER.error("wechat initial failed!", e);
		}
		return false;
	}

	/**
	 * 微信状态通知
	 */
	public boolean wxStatusNotify() {

		String url = AppUtils.base_uri2
				+ "/webwxstatusnotify?lang=zh_CN&pass_ticket="
				+ sentorDomain.getPassTicket();

		JSONObject body = new JSONObject();
		body.put("BaseRequest", sentorDomain.getBaseRequest());
		body.put("Code", 3);
		body.put("FromUserName", sentorDomain.getUser().getString("UserName"));
		body.put("ToUserName", sentorDomain.getUser().getString("UserName"));
		body.put("ClientMsgId", DateKit.getCurrentUnixTime());

		HttpRequest request = HttpRequest.post(url)
				.header("Content-Type", "application/json;charset=utf-8")
				.header("Cookie", sentorDomain.getCookie())
				.send(body.toString());

		String res = request.body();
		request.disconnect();

		if (StringKit.isBlank(res)) {
			return false;
		}

		try {
			JSONObject jsonObject = JSON.parse(res).asObject();
			JSONObject BaseResponse = jsonObject.getJSONObject("BaseResponse");
			if (null != BaseResponse) {
				int ret = BaseResponse.getInt("Ret", -1);
				return ret == 0;
			}
		} catch (Exception e) {
			LOGGER.error("wxStatusNotify failed due to:", e);
		}
		return false;
	}

	/**
	 * 获取联系人
	 */
	public boolean getContact() {

		String url = AppUtils.base_uri2 + "/webwxgetcontact?pass_ticket="
				+ sentorDomain.getPassTicket() + "&skey="
				+ sentorDomain.getSkey() + "&r="
				+ DateKit.getCurrentUnixTime();

		JSONObject body = new JSONObject();
		body.put("BaseRequest", sentorDomain.getBaseRequest());

		HttpRequest request = HttpRequest.post(url)
				.header("Content-Type", "application/json;charset=utf-8")
				.header("Cookie", sentorDomain.getCookie())
				.send(body.toString());

		String res = request.body();
		request.disconnect();

		if (StringKit.isBlank(res)) {
			return false;
		}

		try {
			JSONObject jsonObject = JSON.parse(res).asObject();
			JSONObject BaseResponse = jsonObject.getJSONObject("BaseResponse");
			if (null != BaseResponse) {
				int ret = BaseResponse.getInt("Ret", -1);
				if (ret == 0) {
					JSONArray memberList = jsonObject
							.getJSONArray("MemberList");
					if (null != memberList) {
						assemableContactors(memberList, Boolean.TRUE);
						return true;
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("fetching contactors failed due to:", e);
		}
		return false;
	}

	private boolean assemableContactors(JSONArray contactList,
			Boolean ignoreGroup) {
		JSONObject contact = null;
		for (int i = 0, len = contactList.size(); i < len; i++) {
			contact = contactList.getJSONObject(i);
			// 所有用戶
//			sentorDomain.putAllUsrMap(contact.getString("UserName"), contact);
			
			// 公众号/服务号
			if (contact.getInt("VerifyFlag", 0) == 8) {
				continue;
			}
			// 特殊联系人
			if (AppUtils.SPECIALUSERS.contains(contact.getString("UserName"))) {
				continue;
			}
			// 群
			if (!ignoreGroup
					&& contact.getString("UserName").indexOf("@@") != -1) {
				sentorDomain.putGroupMap(contact.getString("UserName"),
						contact);
				continue;
			}
			// 自己
			if (contact.getString("UserName").equals(
					sentorDomain.getUser().getString("UserName"))) {
				continue;
			}
		}

		return true;
	}

	/**
	 * 消息检查
	 */
	public int[] syncCheck() {
		long start = System.currentTimeMillis();
		int[] arr = new int[2];
		if (sentorDomain.getBestSyncCheckChannel() != null
				&& !sentorDomain.getBestSyncCheckChannel().isEmpty()) {
			arr = syncCheckSingle(sentorDomain.getBestSyncCheckChannel());
			if (arr[0] == 0) {
				LOGGER.debug("Choisen syncCheck channel【"
						+ sentorDomain.getBestSyncCheckChannel()
						+ "】  time consumption 【"
						+ (System.currentTimeMillis() - start) + "】!");
				return arr;
			}
		}

		for (int i = 0; i < AppUtils.WEBPUSH_URL.length; i++) {
			String url = AppUtils.WEBPUSH_URL[i];
			arr = syncCheckSingle(url);
			if (arr[0] == 0) {
				sentorDomain.setBestSyncCheckChannel(url);
				break;
			}
			LOGGER.debug("Message syncCheck channel【" + url
					+ "】 is unvailable!");
		}

		return arr;
	}

	private int[] syncCheckSingle(String url) {
		int[] arr = new int[2];
		try {
			JSONObject body = new JSONObject();
			body.put("BaseRequest", sentorDomain.getBaseRequest());

			HttpRequest request = HttpRequest
					.get(url, true, "r", DateKit.getCurrentUnixTime(), "skey",
							sentorDomain.getSkey(), "uin",
							sentorDomain.getWxuin(), "sid",
							sentorDomain.getWxsid(), "deviceid",
							sentorDomain.getDeviceId(), "synckey",
							sentorDomain.getSynckey(), "_",
							System.currentTimeMillis()).header("Cookie",
							sentorDomain.getCookie());

			LOGGER.debug("[syncCheck request ] " + request);
			String res = request.body();
			request.disconnect();

			if (StringKit.isBlank(res)) {
				return arr;
			}
			LOGGER.debug("[syncCheck response ] " + res);
			String retcode = StringUtils.match("retcode:\"(\\d+)\",", res);
			String selector = StringUtils.match("selector:\"(\\d+)\"}", res);
			if (null != retcode && null != selector) {
				arr[0] = Integer.parseInt(retcode);
				arr[1] = Integer.parseInt(selector);
			}
		} catch (Exception e) {
			LOGGER.error("Message syncCheck channel【" + url
					+ "】 is sync failed!", e);
		}
		return arr;
	}

	/**
	 * Sent message to specific group
	 * 
	 * @param content
	 */
	public void webwxsendmsg(String content) {
		if (sentorDomain.getCurrentGroupId() == null
				|| sentorDomain.getCurrentGroupId().isEmpty()) {
			LOGGER.warn("message({}) not send due to no group selected!",
					content);
			return;
		}
		webwxsendmsg(content, sentorDomain.getCurrentGroupId());
	}

	/**
	 * Sent message to manage group
	 * 
	 * @param content
	 */
	public void webwxsendmsgM(String content) {
		if (sentorDomain.getCurrentMGroupId() == null
				|| sentorDomain.getCurrentMGroupId().isEmpty()) {
			LOGGER.warn("message({}) not send due to no group selected!",
					content);
			return;
		}
		webwxsendmsg(content, sentorDomain.getCurrentMGroupId());
	}

	/**
	 * Sent message
	 * 
	 * @param content
	 * @param to
	 *            : UserName
	 */
	public void webwxsendmsg(String content, String to) {
		try {
			sentorDomain.getMsgQueue().put(new MessageDomain(content, to));
			LOGGER.info("message queue size 【"
					+ sentorDomain.getMsgQueue().size() + "】!");
		} catch (Exception e) {
			LOGGER.error("message queue put failed!", e);
		}
	}

	/**
	 * Sent message
	 * 
	 * @param content
	 * @param to
	 *            : UserName
	 */
	public void webwxsendmsg2(String content, String to) {
		int retry = 8;
		Boolean result = Boolean.FALSE;

		while (!result && retry-- > 0) {
			try {
				Thread.sleep(1500);
				if (retry < 4) {
					Thread.sleep(3000);
				}
				String url = AppUtils.base_uri2
						+ "/webwxsendmsg?lang=zh_CN&pass_ticket="
						+ sentorDomain.getPassTicket();

				JSONObject body = new JSONObject();

				String clientMsgId = DateKit.getCurrentUnixTime()
						+ StringKit.getRandomNumber(5);
				JSONObject Msg = new JSONObject();
				Msg.put("Type", 1);
				Msg.put("Content", content);
				Msg.put("FromUserName",
						sentorDomain.getUser().getString("UserName"));
				Msg.put("ToUserName", to);
				Msg.put("LocalID", clientMsgId);
				Msg.put("ClientMsgId", clientMsgId);
				body.put("BaseRequest", this.sentorDomain.getBaseRequest());
				body.put("Msg", Msg);

				HttpRequest request = HttpRequest
						.post(url)
						.header("Content-Type",
								"application/json;charset=utf-8")
						.header("Cookie", sentorDomain.getCookie())
						.send(body.toString());
				String res = request.body();
				if (StringKit.isBlank(res)) {
					LOGGER.error("message send failed once! repsoncse blank");
					continue;
				}
				JSONObject jsonObject = JSON.parse(res).asObject();
				JSONObject response = jsonObject.getJSONObject("BaseResponse");
				if (null != response && !response.isEmpty()) {
					int ret = response.getInt("Ret", -1);
					LOGGER.debug("message send result{}!", res);
					if (ret == 0) {
						request.disconnect();
						result = true;
					} else {
						LOGGER.error("message send failed once! ret=" + ret);
					}
				} else {
					LOGGER.error("message send failed once! response empty");
				}
			} catch (Exception e) {
				LOGGER.error("message send failed!", e);
			}
		}
	}

	/**
	 * 获取最新消息
	 */
	public JSONObject webwxsync() {

		String url = AppUtils.base_uri2 + "/webwxsync?lang=zh_CN&pass_ticket="
				+ sentorDomain.getPassTicket() + "&skey="
				+ sentorDomain.getSkey() + "&sid=" + sentorDomain.getWxsid()
				+ "&r=" + DateKit.getCurrentUnixTime();

		JSONObject body = new JSONObject();
		body.put("BaseRequest", sentorDomain.getBaseRequest());
		body.put("SyncKey", sentorDomain.getSyncKeyJNode());
		body.put("rr", ~DateKit.getCurrentUnixTime());

		HttpRequest request = HttpRequest.post(url)
				.header("Content-Type", "application/json;charset=utf-8")
				.header("Cookie", sentorDomain.getCookie())
				.send(body.toString());

		// LOGGER.debug("[webwxsync request:] " + request);
		String res = request.body();
		request.disconnect();
		// LOGGER.debug("[webwxsync response:]" + res);
		if (StringKit.isBlank(res)) {
			return null;
		}

		JSONObject jsonObject = JSON.parse(res).asObject();
		JSONObject BaseResponse = jsonObject.getJSONObject("BaseResponse");
		if (null != BaseResponse) {
			int ret = BaseResponse.getInt("Ret", -1);
			if (ret == 0) {
				sentorDomain.setSyncKeyJNode(jsonObject
						.getJSONObject("SyncKey"));

				StringBuffer synckey = new StringBuffer();
				JSONArray list = sentorDomain.getSyncKeyJNode().getJSONArray(
						"List");
				for (int i = 0, len = list.size(); i < len; i++) {
					JSONObject item = list.getJSONObject(i);
					synckey.append("|" + item.getInt("Key", 0) + "_"
							+ item.getInt("Val", 0));
				}
				sentorDomain.setSynckey(synckey.substring(1));
			}
		}
		return jsonObject;
	}


	public void listenMsgMode() {
		new Thread(new Runnable() {
			public void run() {
				LOGGER.warn("wechat sync start!");
				LOGGER.debug("[*] 获取联系人成功");
				LOGGER.debug("[*] 共有 " + sentorDomain.getGroupMap().size()
						+ " 个群");
				LOGGER.debug("[*] 进入消息监听模式 ...");
				while (!stopRequested) {
					try {
						Thread.sleep(AppUtils.WECHAT_LISTEN_INTERVAL);
						int[] arr = syncCheck();
						if (arr[0] == 1100) {
							// runtimeDomain.setBestSyncCheckChannel(null);
						}
						if (arr[0] == 0) {
							switch (arr[1]) {
							case 2:// 新的消息
								final JSONObject data = webwxsync();
//								messageService.submit(() -> {
//									try {
//										handleMsg(data);
//										LOGGER.debug("Listen Thread1 finish once!");
//									} catch (Exception e) {
//										LOGGER.error("wechat sentor sync newMessageThread1 failed!", e);
//									}
//								});
								break;
							case 3:// 新的消息
								break;
							case 6:// 红包 && 加好友
								break;
							case 7:// 进入/离开聊天界面
								break;
							default:
								break;
							}
						}
						LOGGER.info("wechat sync repsonce{},{}", arr[0], arr[1]);
					} catch (Exception e) {
						LOGGER.error("wechat sync failed!", e);
					}

				}
				LOGGER.warn("wechat sync stop!");
				setStopRequested(Boolean.FALSE);
				listenMsgMode();
			}
		}, "listenMsgMode").start();
	}
	
	/**
	 * 获取最新消息
	 * 
	 * @param console
	 */
	public void handleMsg(JSONObject data) {
		if (null == data) {
			return;
		}

		JSONArray addMsgList = data.getJSONArray("AddMsgList");

		for (int i = 0, len = addMsgList.size(); i < len; i++) {
			JSONObject msg = addMsgList.getJSONObject(i);
			int msgType = msg.getInt("MsgType", 0);

			switch (msgType) {
			case 51:
				break;
			case 1:
				handleTextMsg(msg);
				LOGGER.debug("Text Message Thread finish once!");
				break;
			case 3:
				break;
			case 34:
				break;
			case 42:
				LOGGER.debug("Recomend Message Thread finish once!");
				break;
			default:
				break;
			}
			LOGGER.debug("Message Detail： {}" + msg.toString());
		}
		LOGGER.debug("Message Package： {}", data.toString());
	}
	
	/**
	 * handle text message
	 * 
	 * @param jsonMsg
	 * @param console
	 */
	private void handleTextMsg(JSONObject jsonMsg) {
		try {
			String content = "";
			String messageFrom = jsonMsg.getString("FromUserName");

			// Message from myself
			if (sentorDomain.getUser().getString("UserName")
					.equals(messageFrom)) {
				content = jsonMsg.getString("Content");
				gameService.mainSelfMessageHandle(content);
				return;
			}
		} catch (Exception e) {
			LOGGER.error("handleTextMsg failed!", e);
		}
	}


	public void loginWechat() throws InterruptedException {
		String uuid = getUUID();
		if (null == uuid || uuid.isEmpty()) {
			LOGGER.error("[*] uuid获取失败");
		} else {
			LOGGER.debug("[*] 获取到uuid为 [{}]", sentorDomain.getUuid());
			showQrCode();
			while (!"200".equals(waitForLogin())) {
				Thread.sleep(AppUtils.LOGIN_WAITING_TIME);
			}
			// closeQrWindow();

			if (!login()) {
				LOGGER.error("微信登录失败");
				return;
			}
			LOGGER.error("[*] 微信登录成功");
		}
	}

	public void buildWechat() {

		if (!wxInit()) {
			LOGGER.error("[*] 微信初始化失败");
			return;
		}

		LOGGER.debug("[*] 微信初始化成功");

		if (!wxStatusNotify()) {
			LOGGER.error("[*] 开启状态通知失败");
			return;
		}

		LOGGER.debug("[*] 开启状态通知成功");

		if (!getContact()) {
			LOGGER.error("[*] 获取联系人失败");
			return;
		}
		this.listenMsgMode();
	}

	public void stopListen() {
		stopRequested = true;
	}

	public boolean isStopRequested() {
		return stopRequested;
	}

	public void setStopRequested(boolean stopRequested) {
		this.stopRequested = stopRequested;
	}
}