package com.karl.service;

import java.text.MessageFormat;
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

import com.karl.db.domain.Player;
import com.karl.domain.RuntimeDomain;
import com.karl.utils.AppUtils;
import com.karl.utils.CookieUtil;
import com.karl.utils.StringUtils;

@Service
public class WebWechat {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(WebWechat.class);

	private RuntimeDomain runtimeDomain;

	private GameService gameService;

	private ExecutorService listenService;

	private ExecutorService messageService;

	private volatile boolean stopRequested;

	@Autowired
	public WebWechat(RuntimeDomain runtimeDomain, GameService gameService)
			throws InterruptedException {
		System.setProperty("jsse.enableSNIExtension", "false");
		System.setProperty("https.protocols", "TLSv1.1");
		this.runtimeDomain = runtimeDomain;
		this.gameService = gameService;
		listenService = Executors.newFixedThreadPool(4);
		messageService = Executors.newFixedThreadPool(10);
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
					runtimeDomain.setUuid(StringUtils.match(
							"window.QRLogin.uuid = \"(.*)\";", res));
					return runtimeDomain.getUuid();
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
				+ runtimeDomain.getUuid();

		HttpRequest.post(url, true, "t", "webwx", "_",
				DateKit.getCurrentUnixTime()).receive(
				runtimeDomain.getQrCodeFile());

		// if (null != output && output.exists() && output.isFile()) {
		// try {
		// //
		// UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		// // qrCodeFrame = new QRCodeFrame(output.getPath());
		//
		// } catch (Exception e) {
		// LOGGER.error("failed:", e);
		// }
		// }
	}

	/**
	 * 等待登录
	 */
	public String waitForLogin() {
		runtimeDomain.setTip(1);
		String url = "https://login.weixin.qq.com/cgi-bin/mmwebwx-bin/login";
		HttpRequest request = HttpRequest.get(url, true, "tip",
				runtimeDomain.getTip(), "uuid", runtimeDomain.getUuid(), "_",
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
				runtimeDomain.setTip(0);
			} else if (code.equals("200")) {
				LOGGER.debug("[*] 正在登录...");
				String pm = StringUtils.match(
						"window.redirect_uri=\"(\\S+?)\";", res);
				AppUtils.redirect_uri = pm + "&fun=new";
				LOGGER.debug("[*] redirect_uri={}", AppUtils.redirect_uri);
				AppUtils.base_uri = AppUtils.redirect_uri.substring(0,
						AppUtils.redirect_uri.lastIndexOf("/"));
				LOGGER.debug("[*] base_uri={}", AppUtils.base_uri);
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

		HttpRequest request = HttpRequest.get(AppUtils.redirect_uri);

		LOGGER.debug("[*] " + request);

		String res = request.body();
		runtimeDomain.setCookie(CookieUtil.getCookie(request));

		request.disconnect();

		if (StringKit.isBlank(res)) {
			return false;
		}

		runtimeDomain.setSkey(StringUtils.match("<skey>(\\S+)</skey>", res));
		runtimeDomain.setWxsid(StringUtils.match("<wxsid>(\\S+)</wxsid>", res));
		runtimeDomain.setWxuin(StringUtils.match("<wxuin>(\\S+)</wxuin>", res));
		runtimeDomain.setPassTicket(StringUtils.match(
				"<pass_ticket>(\\S+)</pass_ticket>", res));

		LOGGER.debug("[*] skey[{}]", runtimeDomain.getSkey());
		LOGGER.debug("[*] wxsid[{}]", runtimeDomain.getWxsid());
		LOGGER.debug("[*] wxuin[{}]", runtimeDomain.getWxuin());
		LOGGER.debug("[*] pass_ticket[{}]", runtimeDomain.getPassTicket());

		runtimeDomain.setBaseRequest(new JSONObject());
		runtimeDomain.getBaseRequest().put("Uin", runtimeDomain.getWxuin());
		runtimeDomain.getBaseRequest().put("Sid", runtimeDomain.getWxsid());
		runtimeDomain.getBaseRequest().put("Skey", runtimeDomain.getSkey());
		runtimeDomain.getBaseRequest().put("DeviceID",
				runtimeDomain.getDeviceId());

		return true;
	}

	/**
	 * 微信初始化
	 */
	public boolean wxInit() {

		String url = AppUtils.base_uri + "/webwxinit?r="
				+ DateKit.getCurrentUnixTime() + "&pass_ticket="
				+ runtimeDomain.getPassTicket() + "&skey="
				+ runtimeDomain.getSkey();

		JSONObject body = new JSONObject();
		body.put("BaseRequest", this.runtimeDomain.getBaseRequest());

		HttpRequest request = HttpRequest.post(url)
				.header("Content-Type", "application/json;charset=utf-8")
				.header("Cookie", runtimeDomain.getCookie())
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
						runtimeDomain.setSyncKeyJNode(jsonObject
								.getJSONObject("SyncKey"));
						runtimeDomain.setUser(jsonObject.getJSONObject("User"));

						StringBuffer synckey = new StringBuffer();

						JSONArray list = runtimeDomain.getSyncKeyJNode()
								.getJSONArray("List");
						for (int i = 0, len = list.size(); i < len; i++) {
							JSONObject item = list.getJSONObject(i);
							synckey.append("|" + item.getInt("Key", 0) + "_"
									+ item.getInt("Val", 0));
						}

						runtimeDomain.setSynckey(synckey.substring(1));
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

		String url = AppUtils.base_uri
				+ "/webwxstatusnotify?lang=zh_CN&pass_ticket="
				+ runtimeDomain.getPassTicket();

		JSONObject body = new JSONObject();
		body.put("BaseRequest", runtimeDomain.getBaseRequest());
		body.put("Code", 3);
		body.put("FromUserName", runtimeDomain.getUser().getString("UserName"));
		body.put("ToUserName", runtimeDomain.getUser().getString("UserName"));
		body.put("ClientMsgId", DateKit.getCurrentUnixTime());

		HttpRequest request = HttpRequest.post(url)
				.header("Content-Type", "application/json;charset=utf-8")
				.header("Cookie", runtimeDomain.getCookie())
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

		String url = AppUtils.base_uri + "/webwxgetcontact?pass_ticket="
				+ runtimeDomain.getPassTicket() + "&skey="
				+ runtimeDomain.getSkey() + "&r="
				+ DateKit.getCurrentUnixTime();

		JSONObject body = new JSONObject();
		body.put("BaseRequest", runtimeDomain.getBaseRequest());

		HttpRequest request = HttpRequest.post(url)
				.header("Content-Type", "application/json;charset=utf-8")
				.header("Cookie", runtimeDomain.getCookie())
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
			runtimeDomain.putAllUsrMap(contact.getString("UserName"), contact);

			gameService.rsynPlayerEntityWechatInfo(runtimeDomain
					.getUserRemarkName(contact.getString("UserName")), contact
					.getString("UserName"), contact.getString("NickName"));
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
				runtimeDomain.putGroupMap(contact.getString("UserName"),
						contact);
				continue;
			}
			// 自己
			if (contact.getString("UserName").equals(
					runtimeDomain.getUser().getString("UserName"))) {
				continue;
			}
		}
		runtimeDomain.putAllUsrMap(runtimeDomain.getUser()
				.getString("UserName"), runtimeDomain.getUser());

		return true;
	}

	/**
	 * 获取群组联系人
	 */
	public boolean getGroupMembers() {

		String url = AppUtils.base_uri
				+ "/webwxbatchgetcontact?type=ex&pass_ticket="
				+ runtimeDomain.getPassTicket() + "&r="
				+ DateKit.getCurrentUnixTime();

		JSONObject body = new JSONObject();
		body.put("BaseRequest", runtimeDomain.getBaseRequest());
		body.put("Count", runtimeDomain.getGroupMap().size());
		String jsonStr = "[";
		String[] keyStr = (String[]) runtimeDomain.getGroupMap().keySet()
				.toArray();
		for (int i = 0; i < keyStr.length; i++) {
			jsonStr += "{ UserName: " + keyStr[i] + ", EncryChatRoomId: \"\" }";
			jsonStr += i == keyStr.length - 1 ? "" : ",";
		}
		jsonStr += "]";
		body.put("List", jsonStr);

		HttpRequest request = HttpRequest.post(url)
				.header("Content-Type", "application/json;charset=utf-8")
				.header("Cookie", runtimeDomain.getCookie())
				.send(body.toString());

		String res = request.body();
		request.disconnect();

		if (StringKit.isBlank(res)) {
			return false;
		}

		LOGGER.debug("[webwxbatchgetcontact response]: " + res);

		try {
			JSONObject jsonObject = JSON.parse(res).asObject();
			JSONObject BaseResponse = jsonObject.getJSONObject("BaseResponse");
			if (null != BaseResponse) {
				int ret = BaseResponse.getInt("Ret", -1);
				if (ret == 0) {
					JSONArray groupCollection = jsonObject
							.getJSONArray("ContactList");
					if (groupCollection == null) {
						return false;
					}
					JSONArray memberList = null;
					String groupId = null;
					for (int x = 0, xlen = groupCollection.size(); x < xlen; x++) {
						memberList = groupCollection.getJSONObject(x)
								.getJSONArray("MemberList");
						groupId = groupCollection.getJSONObject(x).getString(
								"UserName");
						if (null != memberList && memberList.size() > 0) {
							// JSONObject contact = null;
							// for (int i = 0, len = memberList.size(); i < len;
							// i++) {
							// contact = memberList.getJSONObject(i);
							// }
							// 群组成员
							runtimeDomain.putGroupMap(groupId,
									groupCollection.getJSONObject(x));
						}
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("fetching contactors failed due to:", e);
			return false;
		}
		return true;
	}

	/**
	 * 微信修改备注名
	 */
	public boolean changeRemarkName(String nickName, String wechatId,
			String remarkName) {
		Boolean result = false;

		String oldRemarkName = runtimeDomain.getUserRemarkName(wechatId);

		String url = AppUtils.base_uri + "/webwxoplog?lang=zh_CN&pass_ticket="
				+ runtimeDomain.getPassTicket();

		JSONObject body = new JSONObject();
		body.put("BaseRequest", runtimeDomain.getBaseRequest());
		body.put("CmdId", 2);
		body.put("RemarkName", remarkName);
		body.put("UserName", wechatId);
		body.put("ClientMsgId", DateKit.getCurrentUnixTime());

		HttpRequest request = HttpRequest.post(url)
				.header("Content-Type", "application/json;charset=utf-8")
				.header("Cookie", runtimeDomain.getCookie())
				.send(body.toString());

		String res = request.body();
		request.disconnect();

		if (StringKit.isBlank(res)) {
			if (oldRemarkName != null && oldRemarkName.equals(remarkName)) {
				result = true;
			}
			LOGGER.error("User{} remarkName{} result{} remarkName Failed!",
					nickName, remarkName, res);

			return result;
		}

		try {
			JSONObject jsonObject = JSON.parse(res).asObject();
			JSONObject BaseResponse = jsonObject.getJSONObject("BaseResponse");
			if (null != BaseResponse) {
				int ret = BaseResponse.getInt("Ret", -1);
				LOGGER.debug("User{} remarkName{} refult{}", nickName,
						remarkName, ret);
				result = ret == 0;
			}

			if (!result && oldRemarkName != null
					&& oldRemarkName.equals(remarkName)) {
				result = true;
			}
		} catch (Exception e) {
			LOGGER.error("User{} remarkName{} result{} remarkName Failed!",
					nickName, remarkName, res, e);
		}

		LOGGER.debug("User{} remarkName{} result{} remarkName info!", nickName,
				remarkName, res);
		return result;
	}

	/**
	 * 消息检查
	 */
	public int[] syncCheck() {
		int[] arr = new int[2];
		if (runtimeDomain.getBestSyncCheckChannel() != null
				&& !runtimeDomain.getBestSyncCheckChannel().isEmpty()) {
			arr = syncCheckSingle(runtimeDomain.getBestSyncCheckChannel());
			if (arr[0] == 0) {
				return arr;
			}
		}
		for (int i = 0; i < AppUtils.WEBPUSH_URL.length; i++) {
			String url = AppUtils.WEBPUSH_URL[i];
			arr = syncCheckSingle(url);
			if (arr[0] == 0) {
				runtimeDomain.setBestSyncCheckChannel(url);
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
			body.put("BaseRequest", runtimeDomain.getBaseRequest());

			HttpRequest request = HttpRequest
					.get(url, true, "r", DateKit.getCurrentUnixTime(), "skey",
							runtimeDomain.getSkey(), "uin",
							runtimeDomain.getWxuin(), "sid",
							runtimeDomain.getWxsid(), "deviceid",
							runtimeDomain.getDeviceId(), "synckey",
							runtimeDomain.getSynckey(), "_",
							System.currentTimeMillis()).header("Cookie",
							runtimeDomain.getCookie());

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
		if (runtimeDomain.getCurrentGroupId() == null
				|| runtimeDomain.getCurrentGroupId().isEmpty()) {
			LOGGER.warn("message({}) not send due to no group selected!",
					content);
			return;
		}
		webwxsendmsg(content, runtimeDomain.getCurrentGroupId());
	}

	/**
	 * Sent message to manage group
	 * 
	 * @param content
	 */
	public void webwxsendmsgM(String content) {
		if (runtimeDomain.getCurrentMGroupId() == null
				|| runtimeDomain.getCurrentMGroupId().isEmpty()) {
			LOGGER.warn("message({}) not send due to no group selected!",
					content);
			return;
		}
		webwxsendmsg(content, runtimeDomain.getCurrentMGroupId());
	}

	/**
	 * Sent message
	 * 
	 * @param content
	 * @param to
	 *            : UserName
	 */
	public void webwxsendmsg(String content, String to) {
		int retry = 3;
		Boolean result = Boolean.FALSE;

		while (!result && retry-- > 0) {
			try {
				String url = AppUtils.base_uri
						+ "/webwxsendmsg?lang=zh_CN&pass_ticket="
						+ runtimeDomain.getPassTicket();

				JSONObject body = new JSONObject();

				String clientMsgId = DateKit.getCurrentUnixTime()
						+ StringKit.getRandomNumber(5);
				JSONObject Msg = new JSONObject();
				Msg.put("Type", 1);
				Msg.put("Content", content);
				Msg.put("FromUserName",
						runtimeDomain.getUser().getString("UserName"));
				Msg.put("ToUserName", to);
				Msg.put("LocalID", clientMsgId);
				Msg.put("ClientMsgId", clientMsgId);
				body.put("BaseRequest", this.runtimeDomain.getBaseRequest());
				body.put("Msg", Msg);

				HttpRequest request = HttpRequest
						.post(url)
						.header("Content-Type",
								"application/json;charset=utf-8")
						.header("Cookie", runtimeDomain.getCookie())
						.send(body.toString());

				String res = request.body();
				if (StringKit.isBlank(res)) {
					continue;
				}
				JSONObject jsonObject = JSON.parse(res).asObject();
				JSONObject response = jsonObject.getJSONObject("BaseResponse");
				if (null != response && !response.isEmpty()) {
					int ret = response.getInt("Ret", -1);
					if (ret == 0) {
						LOGGER.debug("message send result{}!", res);
						request.disconnect();
						result = true;
					}
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

		String url = AppUtils.base_uri + "/webwxsync?lang=zh_CN&pass_ticket="
				+ runtimeDomain.getPassTicket() + "&skey="
				+ runtimeDomain.getSkey() + "&sid=" + runtimeDomain.getWxsid()
				+ "&r=" + DateKit.getCurrentUnixTime();

		JSONObject body = new JSONObject();
		body.put("BaseRequest", runtimeDomain.getBaseRequest());
		body.put("SyncKey", runtimeDomain.getSyncKeyJNode());
		body.put("rr", ~DateKit.getCurrentUnixTime());

		HttpRequest request = HttpRequest.post(url)
				.header("Content-Type", "application/json;charset=utf-8")
				.header("Cookie", runtimeDomain.getCookie())
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
				runtimeDomain.setSyncKeyJNode(jsonObject
						.getJSONObject("SyncKey"));

				StringBuffer synckey = new StringBuffer();
				JSONArray list = runtimeDomain.getSyncKeyJNode().getJSONArray(
						"List");
				for (int i = 0, len = list.size(); i < len; i++) {
					JSONObject item = list.getJSONObject(i);
					synckey.append("|" + item.getInt("Key", 0) + "_"
							+ item.getInt("Val", 0));
				}
				runtimeDomain.setSynckey(synckey.substring(1));
			}
		}
		return jsonObject;
	}

	protected void handleMsgSystem(JSONObject data) {
		if (null == data) {
			return;
		}

		JSONArray addMsgList = data.getJSONArray("AddMsgList");
		JSONArray modContactList = data.getJSONArray("ModContactList");
		if (addMsgList == null || modContactList == null) {
			return;
		}

		for (int i = 0, len = addMsgList.size(); i < len; i++) {
			JSONObject msg = addMsgList.getJSONObject(i);
			int msgType = msg.getInt("MsgType", 0);

			switch (msgType) {
			case 51:
				break;
			case 1:
				messageService.submit(() -> {
					handleTextMsgSystem(msg, modContactList);
				});
				break;
			case 3:
				break;
			case 34:
				break;
			case 42:
				break;
			default:
				break;
			}
		}
		LOGGER.debug("System Message: {}", data.toString());
	}

	private void handleTextMsgSystem(JSONObject jsonMsg,
			JSONArray modContactList) {

		try {
			if (modContactList == null || modContactList.size() < 0) {
				return;
			}

			String content = jsonMsg.getString("Content");
			String messageFrom = jsonMsg.getString("FromUserName");
			// int msgType = jsonMsg.getInt("MsgType", 0);

			if (content == null || content.isEmpty() || messageFrom == null) {
				return;
			}

			if (!StringUtils.RECOMMENDMSG.matcher(content).find()) {
				return;
			}

			for (int i = 0; i < modContactList.size(); i++) {
				JSONObject userInfoJson = modContactList.getJSONObject(i);
				if (userInfoJson != null
						&& userInfoJson.getString("UserName") != null
						&& userInfoJson.getString("UserName").equals(
								messageFrom)) {
					if (runtimeDomain.getSingleUsrMap(messageFrom) == null) {
						webwxsendmsgM("/::P 机器人新加好友: "
								+ runtimeDomain.getUserNickName(userInfoJson));
					}
					runtimeDomain.putAllUsrMap(messageFrom, userInfoJson);
					LOGGER.info("New User Json info{} add!",
							userInfoJson.toString());
					break;
				}
			}
		} catch (Exception e) {
			LOGGER.error("handleSystemMsg failed!", e);
		}
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
				messageService.submit(() -> {
					handleTextMsg(msg);
					LOGGER.debug("Text Message Thread finish once!");
				});
				break;
			case 3:
				// webwxsendmsg("二蛋还不支持图片呢", msg.getString("FromUserName"));
				break;
			case 34:
				// webwxsendmsg("二蛋还不支持语音呢", msg.getString("FromUserName"));
				break;
			case 42:
				messageService.submit(() -> {
					handleRecomendMsg(msg);
					LOGGER.debug("Recomend Message Thread finish once!");
				});
				break;
			default:
				break;
			}
			LOGGER.debug("Message Detail： {}" + msg.toString());
		}
		LOGGER.debug("Message Package： {}", data.toString());
	}

	private void handleRecomendMsg(JSONObject jsonMsg) {
		try {
			String recommendWechatId = "";
			String recommendWechatName = "";
			JSONObject recommendInfo = null;

			if (jsonMsg.getString("FromUserName").equals(
					runtimeDomain.getCurrentMGroupId())
					|| runtimeDomain.getUser().getString("UserName")
							.equals(jsonMsg.getString("FromUserName"))) {
				LOGGER.debug("FromUserName{} message",
						jsonMsg.getString("FromUserName"));
				recommendInfo = jsonMsg.getJSONObject("RecommendInfo");
				if (recommendInfo != null) {
					recommendWechatId = recommendInfo.getString("UserName");
					recommendWechatName = recommendInfo.getString("NickName");

				} else {
					LOGGER.debug(
							"FromUserName{} message's recommendInfo is empty!",
							jsonMsg.getString("FromUserName"));
				}
			} else {
				LOGGER.debug(
						"FromUserName[{}] message is not come from specific manage group[{}]!",
						jsonMsg.getString("FromUserName"),
						runtimeDomain.getCurrentMGroupId());
				return;
			}

			if (runtimeDomain.getAllUsrMap().get(recommendWechatId) == null) {
				String content = MessageFormat.format(
						AppUtils.ASKRECOMMENDUNKNOWN, recommendWechatName);
				webwxsendmsgM(content);
				return;
			}

			if (recommendWechatId.isEmpty() || recommendWechatName.isEmpty()) {
				LOGGER.debug(
						"FromUserName[{}] recommendInfo cann't be interpet{}",
						jsonMsg.getString("FromUserName"),
						recommendInfo == null ? "" : recommendInfo.toString());
				return;
			}

			runtimeDomain.setReadyWechatId(recommendWechatId);
			String remarkName = runtimeDomain
					.getUserRemarkName(recommendWechatId);
			Long nowPoint = 0L;
			if (!AppUtils.UNCONTACTUSRNAME.equals(remarkName)) {
				Player pEntity = runtimeDomain.getRunningPlayeres().get(
						remarkName);
				if (pEntity != null) {
					nowPoint = pEntity.getPoints();
				}
			}
			String content = MessageFormat.format(AppUtils.ASKRECOMMEND,
					recommendWechatName, nowPoint);
			webwxsendmsgM(content);
		} catch (Exception e) {
			LOGGER.error("handleTextMsg failed!", e);
		}
	}

	/**
	 * handle text message
	 * 
	 * @param jsonMsg
	 * @param console
	 */
	private void handleTextMsg(JSONObject jsonMsg) {
		try {
			String remarkName = "";
			String content = "";
			String webChatId = "";
			String messageFrom = jsonMsg.getString("FromUserName");

			// Message from others
			if (messageFrom.equals(runtimeDomain.getCurrentGroupId())
					|| messageFrom.equals(runtimeDomain.getCurrentMGroupId())) {
				LOGGER.debug("FromUserName{} message",
						jsonMsg.getString("FromUserName"));
				String contentStr = jsonMsg.getString("Content");
				if (contentStr != null && !contentStr.isEmpty()) {
					String[] contentArray = contentStr.split(":<br/>");
					if (contentArray.length > 1) {
						content = contentArray[1];
						String fromUsrId = contentArray[0];
						webChatId = fromUsrId;
						remarkName = runtimeDomain.getUserRemarkName(fromUsrId);
					} else {
						LOGGER.warn(
								"FromUserName{} message's message can't be interpret! {}",
								jsonMsg.getString("FromUserName"), contentStr);

					}
				} else {
					LOGGER.debug("FromUserName{} message's content is empty!",
							jsonMsg.getString("FromUserName"));

				}
				LOGGER.debug("【" + remarkName + "】       说：        【" + content
						+ "】");
			} else {
				LOGGER.debug(
						"FromUserName[{}] message is not come from specific group[{}]!",
						jsonMsg.getString("FromUserName"),
						runtimeDomain.getCurrentGroupId());
			}

			// Message from myself
			if (runtimeDomain.getUser().getString("UserName")
					.equals(messageFrom)) {
				remarkName = runtimeDomain.getUser().getString("NickName");
				content = jsonMsg.getString("Content");
				webChatId = runtimeDomain.getUser().getString("UserName");
				gameService.mainSelfMessageHandle(content);
				return;
			}

			if (webChatId != null && remarkName != null && content != null
					&& !webChatId.isEmpty() && !remarkName.isEmpty()
					&& !content.isEmpty()) {
				gameService.mainMessageHandle(messageFrom, webChatId,
						remarkName, content);
			}
		} catch (Exception e) {
			LOGGER.error("handleTextMsg failed!", e);
		}
	}

	public void listenMsgMode() {
		new Thread(new Runnable() {
			public void run() {
				LOGGER.debug("[*] 获取联系人成功");
				LOGGER.debug("[*] 共有 " + runtimeDomain.getAllUsrMap().size()
						+ " 位联系人");
				LOGGER.debug("[*] 共有 " + runtimeDomain.getGroupMap().size()
						+ " 个群");
				LOGGER.debug("[*] 进入消息监听模式 ...");
				while (!stopRequested) {
					try {
						Thread.sleep(AppUtils.WECHAT_LISTEN_INTERVAL);

						int[] arr = syncCheck();

						if (arr[0] == 1100) {
						}
						if (arr[0] == 0) {
							switch (arr[1]) {
							case 2:// 新的消息
								newMessageThread1();
								break;
							case 3:// 新的消息
								newMessageThread3();
								break;
							case 6:// 红包 && 加好友
								newMessageThread6();
								break;
							case 7:// 进入/离开聊天界面
								break;
							default:
								break;
							}
						}
					} catch (Exception e) {
						LOGGER.error("wechat sync failed!", e);
					}

				}
			}
		}, "listenMsgMode").start();
	}

	private void newMessageThread1() {
		listenService.submit(() -> {
			try {
				JSONObject data = webwxsync();
				handleMsg(data);
				LOGGER.debug("Listen Thread1 finish once!");
			} catch (Exception e) {
				LOGGER.error("wechat sync newMessageThread1 failed!", e);
			}
		});
	}

	private void newMessageThread3() {
		listenService.submit(() -> {
			try {
				JSONObject data = webwxsync();
				handleMsg(data);
				LOGGER.debug("Listen Thread3 finish once!");
			} catch (Exception e) {
				LOGGER.error("wechat sync newMessageThread1 failed!", e);
			}
		});
	}

	private void newMessageThread6() {
		listenService.submit(() -> {
			try {
				JSONObject data = webwxsync();
				handleMsg(data);
				handleMsgSystem(data);
				LOGGER.debug("Listen Thread6 finish once!");
			} catch (Exception e) {
				LOGGER.error("wechat sync newMessageThread1 failed!", e);
			}
		});
	}

	public void loginWechat() throws InterruptedException {
		String uuid = getUUID();
		if (null == uuid || uuid.isEmpty()) {
			LOGGER.error("[*] uuid获取失败");
		} else {
			LOGGER.debug("[*] 获取到uuid为 [{}]", runtimeDomain.getUuid());
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
		// if (!getGroupMembers()) {
		// LOGGER.debug("[*] 获取群成员失败");
		// return;
		// }
	}

	public void stopListen() {
		stopRequested = true;
	}

	public RuntimeDomain getRuntimeDomain() {
		return runtimeDomain;
	}

	public void setRuntimeDomain(RuntimeDomain runtimeDomain) {
		this.runtimeDomain = runtimeDomain;
	}

	public boolean isStopRequested() {
		return stopRequested;
	}

	public void setStopRequested(boolean stopRequested) {
		this.stopRequested = stopRequested;
	}
}