package com.karl.thread;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import blade.kit.DateKit;
import blade.kit.StringKit;
import blade.kit.http.HttpRequest;
import blade.kit.json.JSON;
import blade.kit.json.JSONObject;

import com.karl.domain.MessageDomain;
import com.karl.domain.RuntimeDomain;
import com.karl.domain.SentorDomain;
import com.karl.utils.AppUtils;

public class MessageConsumer implements Runnable {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(MessageConsumer.class);
	
	public MessageConsumer(SentorDomain sentorDomain) {
		super();
		this.sentorDomain = sentorDomain;
		this.msgQueue = sentorDomain.getMsgQueue();
	}
	
	public MessageConsumer(RuntimeDomain runtimeDomain) {
		super();
		this.runtimeDomain = runtimeDomain;
		this.msgQueue = runtimeDomain.getMsgQueue();
	}

	private RuntimeDomain runtimeDomain;
	private SentorDomain sentorDomain;
	private BlockingQueue<MessageDomain> msgQueue;

	/**
	 * Sent message
	 * 
	 * @param content
	 * @param to
	 *            : UserName
	 */
	public void webwxsendmsg2(String content, String to) {
		int retry = AppUtils.MSGSENTRETRY;
		Boolean result = Boolean.FALSE;

		while (!result && retry-- > 0) {
			try {
				if (retry < AppUtils.MSGSENTRETRY - 1) {
					Thread.sleep(2000);
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
	 * Sent message
	 * 
	 * @param content
	 * @param to
	 *            : UserName
	 */
	public void webwxsendmsg(String content, String to) {
		int retry = AppUtils.MSGSENTRETRY;
		Boolean result = Boolean.FALSE;

		while (!result && retry-- > 0) {
			try {
				if (retry < AppUtils.MSGSENTRETRY - 1) {
					Thread.sleep(2000);
				}
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

	@Override
	public void run() {
		while (true) {
			try {
				long start = System.currentTimeMillis();
				MessageDomain msgDomain = msgQueue.take();
				webwxsendmsg(msgDomain.getContent(), msgDomain.getTarget());
				LOGGER.info("message sent  in 【"
						+ (System.currentTimeMillis() - start) + "】ms!");
				Thread.sleep(800);
			} catch (Exception e) {
				LOGGER.error("Quenue wait failed!", e);
			}
		}
	}

	public SentorDomain getSentorDomain() {
		return sentorDomain;
	}

	public void setSentorDomain(SentorDomain sentorDomain) {
		this.sentorDomain = sentorDomain;
	}

	public RuntimeDomain getRuntimeDomain() {
		return runtimeDomain;
	}

	public void setRuntimeDomain(RuntimeDomain runtimeDomain) {
		this.runtimeDomain = runtimeDomain;
	}

}
