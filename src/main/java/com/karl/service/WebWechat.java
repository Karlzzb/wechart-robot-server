package com.karl.service;

import java.text.MessageFormat;

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

import com.karl.domain.RuntimeDomain;
import com.karl.fx.controller.ConsoleController;
import com.karl.utils.AppUtils;
import com.karl.utils.CookieUtil;
import com.karl.utils.StringUtils;

@Service
public class WebWechat {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(WebWechat.class);

	private RuntimeDomain runtimeDomain;

	private GameService gameService;

	private Thread runThread;

	private volatile boolean stopRequested;

	@Autowired
	public WebWechat(RuntimeDomain runtimeDomain, GameService gameService)
			throws InterruptedException {
		System.setProperty("jsse.enableSNIExtension", "false");
		this.runtimeDomain = runtimeDomain;
		this.gameService = gameService;
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

		LOGGER.info("[*] " + request);

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
					LOGGER.info("[*] 错误的状态码: {}", code);
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

		LOGGER.info("[*] " + request.toString());

		String res = request.body();
		request.disconnect();

		if (null == res) {
			LOGGER.info("[*] 扫描二维码验证失败");
			return "";
		}

		String code = StringUtils.match("window.code=(\\d+);", res);
		if (null == code) {
			LOGGER.info("[*] 扫描二维码验证失败");
			return "";
		} else {
			if (code.equals("201")) {
				LOGGER.info("[*] 成功扫描,请在手机上点击确认以登录");
				runtimeDomain.setTip(0);
			} else if (code.equals("200")) {
				LOGGER.info("[*] 正在登录...");
				String pm = StringUtils.match(
						"window.redirect_uri=\"(\\S+?)\";", res);
				AppUtils.redirect_uri = pm + "&fun=new";
				LOGGER.info("[*] redirect_uri={}", AppUtils.redirect_uri);
				AppUtils.base_uri = AppUtils.redirect_uri.substring(0,
						AppUtils.redirect_uri.lastIndexOf("/"));
				LOGGER.info("[*] base_uri={}", AppUtils.base_uri);
			} else if (code.equals("408")) {
				LOGGER.info("[*] 登录超时");
			} else {
				LOGGER.info("[*] 扫描code={}", code);
			}
		}
		return code;
	}

	/**
	 * 登录
	 */
	public boolean login() {

		HttpRequest request = HttpRequest.get(AppUtils.redirect_uri);

		LOGGER.info("[*] " + request);

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

		LOGGER.info("[*] skey[{}]", runtimeDomain.getSkey());
		LOGGER.info("[*] wxsid[{}]", runtimeDomain.getWxsid());
		LOGGER.info("[*] wxuin[{}]", runtimeDomain.getWxuin());
		LOGGER.info("[*] pass_ticket[{}]", runtimeDomain.getPassTicket());

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

		LOGGER.debug("[*] " + request);
		String res = request.body();
		request.disconnect();

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
			// 公众号/服务号
			if (contact.getInt("VerifyFlag", 0) == 8) {
				runtimeDomain.putPublicUsrMap(contact.getString("UserName"),
						contact);
				continue;
			}
			// 特殊联系人
			if (AppUtils.specialUsers.contains(contact.getString("UserName"))) {
				runtimeDomain.putSpecialUsrMap(contact.getString("UserName"),
						contact);
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
	public boolean changeRemarkName(String wechatId, String remarkName) {
		String url = AppUtils.base_uri
				+ "/webwxoplog?lang=zh_CN&pass_ticket="
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
	 * 消息检查
	 */
	public int[] syncCheck() {

		int[] arr = new int[2];

		String url = AppUtils.webpush_url;

		JSONObject body = new JSONObject();
		body.put("BaseRequest", runtimeDomain.getBaseRequest());

		HttpRequest request = HttpRequest.get(url, true, "r",
				DateKit.getCurrentUnixTime(), "skey", runtimeDomain.getSkey(),
				"uin", runtimeDomain.getWxuin(), "sid",
				runtimeDomain.getWxsid(), "deviceid",
				runtimeDomain.getDeviceId(), "synckey",
				runtimeDomain.getSynckey(), "_", System.currentTimeMillis())
				.header("Cookie", runtimeDomain.getCookie());

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
			return arr;
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

		String url = AppUtils.base_uri
				+ "/webwxsendmsg?lang=zh_CN&pass_ticket="
				+ runtimeDomain.getPassTicket();

		JSONObject body = new JSONObject();

		String clientMsgId = DateKit.getCurrentUnixTime()
				+ StringKit.getRandomNumber(5);
		JSONObject Msg = new JSONObject();
		Msg.put("Type", 1);
		Msg.put("Content", content);
		Msg.put("FromUserName", runtimeDomain.getUser().getString("UserName"));
		Msg.put("ToUserName", to);
		Msg.put("LocalID", clientMsgId);
		Msg.put("ClientMsgId", clientMsgId);
		body.put("BaseRequest", this.runtimeDomain.getBaseRequest());
		body.put("Msg", Msg);

		HttpRequest request = HttpRequest.post(url)
				.header("Content-Type", "application/json;charset=utf-8")
				.header("Cookie", runtimeDomain.getCookie())
				.send(body.toString());

		LOGGER.debug("Message sent to runtimeDomain.getUser()[{}] reuest {}",
				to, request);

		LOGGER.debug("Message sent to runtimeDomain.getUser()[{}] response {}",
				to, request.body());

		request.disconnect();

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

	/**
	 * 获取最新消息
	 * 
	 * @param console
	 */
	public void handleMsg(JSONObject data, ConsoleController console) {
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
				handleTextMsg(msg, console);
				break;
			case 3:
				// webwxsendmsg("二蛋还不支持图片呢", msg.getString("FromUserName"));
				break;
			case 34:
				// webwxsendmsg("二蛋还不支持语音呢", msg.getString("FromUserName"));
				break;
			case 42:
				handleRecomendMsg(msg, console);
				break;
			default:
				break;
			}
			LOGGER.debug("Message Detail： {}" + msg.toString());
		}
		LOGGER.debug("Message Package： {}", data.toString());
	}

	private void handleRecomendMsg(JSONObject jsonMsg, ConsoleController console) {
		String recommendWechatId = "";
		String recommendWechatName = "";
		JSONObject recommendInfo = null;

		if (jsonMsg.getString("FromUserName").equals(
				runtimeDomain.getCurrentMGroupId())||runtimeDomain.getUser().getString("UserName")
				.equals(jsonMsg.getString("FromUserName"))) {
			LOGGER.debug("FromUserName{} message",
					jsonMsg.getString("FromUserName"));
			recommendInfo = jsonMsg.getJSONObject("RecommendInfo");
			if (recommendInfo !=null) {
				recommendWechatId = recommendInfo.getString("UserName");
				recommendWechatName = recommendInfo.getString("NickName");

			} else {
				LOGGER.debug("FromUserName{} message's recommendInfo is empty!",
						jsonMsg.getString("FromUserName"));
			}
		} else {
			LOGGER.debug(
					"FromUserName[{}] message is not come from specific manage group[{}]!",
					jsonMsg.getString("FromUserName"),
					runtimeDomain.getCurrentMGroupId());
		}
		
		if (recommendWechatId.isEmpty() || recommendWechatName.isEmpty()) {
			LOGGER.debug(
					"FromUserName[{}] recommendInfo cann't be interpet{}",
					jsonMsg.getString("FromUserName"),
					recommendInfo==null?"":recommendInfo.toString());
			return;
		}
		
		runtimeDomain.setReadyWechatId(recommendWechatId);
		String content = MessageFormat.format(AppUtils.ASKRECOMMEND, recommendWechatName);
		webwxsendmsgM(content);
	}

	/**
	 * handle text message
	 * 
	 * @param jsonMsg
	 * @param console
	 */
	private void handleTextMsg(JSONObject jsonMsg, ConsoleController console) {

		String remarkName = "";
		String content = "";
		String webChatId = "";

		// Message from others
		if (jsonMsg.getString("FromUserName").equals(
				runtimeDomain.getCurrentGroupId())||jsonMsg.getString("FromUserName").equals(
						runtimeDomain.getCurrentMGroupId())) {
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
			console.writeLog("【" + remarkName + "】       说：        【" + content
					+ "】");
		} else {
			LOGGER.debug(
					"FromUserName[{}] message is not come from specific group[{}]!",
					jsonMsg.getString("FromUserName"),
					runtimeDomain.getCurrentGroupId());
		}

		// Message from myself
		if (runtimeDomain.getUser().getString("UserName")
				.equals(jsonMsg.getString("FromUserName"))) {
			remarkName = runtimeDomain.getUser().getString("NickName");
			content = jsonMsg.getString("Content");
			webChatId = runtimeDomain.getUser().getString("UserName");
		}

		if (webChatId != null && remarkName != null && content != null
				&& !webChatId.isEmpty() && !remarkName.isEmpty()
				&& !content.isEmpty()) {
			gameService.mainMessageHandle(webChatId, remarkName, content);
		}

		// switch (content) {
		// case "bet":
		// this.webwxsendBetInfo();
		// break;
		// case "luck":
		// this.webwxsendLuckInfo();
		// break;
		//
		// default:
		// break;
		// }
	}

	public void listenMsgMode(final ConsoleController console) {
		new Thread(new Runnable() {
			public void run() {
				console.writeLog("[*] 获取联系人成功");
				console.writeLog("[*] 共有 "
						+ runtimeDomain.getAllUsrMap().size() + " 位联系人");
				console.writeLog("[*] 共有 "
						+ runtimeDomain.getSpecialUsrMap().size() + " 位特殊联系人");
				console.writeLog("[*] 共有 " + runtimeDomain.getGroupMap().size()
						+ " 个群");
				console.writeLog("[*] 进入消息监听模式 ...");
				while (!stopRequested) {

					try {
						Thread.sleep(AppUtils.WECHAT_LISTEN_INTERVAL);
					} catch (InterruptedException e) {
						LOGGER.error("sleeping failed:", e);
					}

					int[] arr = syncCheck();

					LOGGER.debug("[*] retcode={},selector={}", arr[0], arr[1]);

					if (arr[0] == 1100) {
						// console.writeLog("[*] 你在手机上登出了微信，债见");
						break;
					}

					if (arr[0] == 0) {
						JSONObject data = null;

						switch (arr[1]) {
						case 2:// 新的消息
							data = webwxsync();
							handleMsg(data, console);
							break;
						case 6:// 红包
							data = webwxsync();
							handleMsg(data, console);
							break;
						case 7:// 进入/离开聊天界面
							data = webwxsync();
							break;
						default:
							break;
						}
					}
				}
			}
		}, "listenMsgMode").start();
	}

	public void loginWechat() throws InterruptedException {
		String uuid = getUUID();
		if (null == uuid || uuid.isEmpty()) {
			LOGGER.info("[*] uuid获取失败");
		} else {
			LOGGER.info("[*] 获取到uuid为 [{}]", runtimeDomain.getUuid());
			showQrCode();
			while (!"200".equals(waitForLogin())) {
				Thread.sleep(AppUtils.LOGIN_WAITING_TIME);
			}
			// closeQrWindow();

			if (!login()) {
				LOGGER.info("微信登录失败");
				return;
			}
			LOGGER.info("[*] 微信登录成功");
		}
	}

	public void buildWechat() {

		if (!wxInit()) {
			LOGGER.info("[*] 微信初始化失败");
			return;
		}

		LOGGER.info("[*] 微信初始化成功");

		if (!wxStatusNotify()) {
			LOGGER.info("[*] 开启状态通知失败");
			return;
		}

		LOGGER.info("[*] 开启状态通知成功");

		if (!getContact()) {
			LOGGER.info("[*] 获取联系人失败");
			return;
		}
		// if (!getGroupMembers()) {
		// LOGGER.info("[*] 获取群成员失败");
		// return;
		// }
	}

	public void stopListen() {
		stopRequested = true;
		if (runThread != null) {
			runThread.interrupt();
		}
	}

	public static void main(String[] args) throws InterruptedException {

	}

	public RuntimeDomain getRuntimeDomain() {
		return runtimeDomain;
	}

	public void setRuntimeDomain(RuntimeDomain runtimeDomain) {
		this.runtimeDomain = runtimeDomain;
	}
}