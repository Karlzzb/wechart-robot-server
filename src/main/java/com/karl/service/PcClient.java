package com.karl.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import blade.kit.json.JSON;
import blade.kit.json.JSONArray;
import blade.kit.json.JSONObject;

import com.karl.utils.StringUtils;

@Service
public class PcClient {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(PcClient.class);

	private static final String socket_host = "127.0.0.1";
	private static final int socket_port = 12580;

	private Socket socket;
	
	private Boolean isConnected;

	@Autowired
	private GameService gameService;

	public PcClient() {
		Thread OpenConnection = new Thread(new OpenConnection(socket_host,
				socket_port));
		OpenConnection.setDaemon(Boolean.TRUE);
		OpenConnection.start();
		isConnected = Boolean.FALSE;
	}

	private class OpenConnection extends Thread {
		private String host;
		private int port;

		public OpenConnection(String host, int port) {
			this.host = host;
			this.port = port;
		}

		@Override
		public void run() {
			while (Boolean.TRUE) {
				try {
					Thread.sleep(5000);
					Runtime.getRuntime().exec(
							"adb shell am broadcast -a NotifyServiceStop");
					Runtime.getRuntime()
							.exec("adb forward tcp:12580 tcp:62001");
					Runtime.getRuntime().exec(
							"adb shell am broadcast -a NotifyServiceStart");
				} catch (Exception e) {
					LOGGER.error("PcClient forward failed!", e);
				}
				connnectToServer();
			}
		}

		private void startHeartBeatThread() {
			// 启动心跳线程
			Timer heartBeatTimer = new Timer();
			TimerTask heartBeatTask = new TimerTask() {
				@Override
				public void run() {
					sendOrder("\r\n");
				}
			};
			heartBeatTimer.schedule(heartBeatTask, 25000, 25000);
		}

		private void sendOrder(String order) {
			try {
				BufferedOutputStream outputStream = new BufferedOutputStream(
						socket.getOutputStream());
				outputStream.write(order.getBytes("UTF-8"));
				outputStream.flush();
			} catch (Exception e) {
				LOGGER.error("PcClient send heart beat failed!", e);
				isConnected = Boolean.FALSE;
			}
		}

		private Boolean connnectToServer() {
			try {
				InetAddress serveraddr = null;
				serveraddr = InetAddress.getByName(host);
				LOGGER.debug("host【{}】 port【{}】 connecting ", host, port);
				socket = new Socket(serveraddr, port);
				LOGGER.debug("host【{}】 port【{}】 connecting ", host, port);
				startHeartBeatThread();
				LOGGER.debug("host【{}】 port【{}】 start heartchecking ", host,
						port);
				BufferedInputStream in = new BufferedInputStream(
						socket.getInputStream());
				while (isConnected) {
					String strFormsocket = readFromSocket(in);
					if (strFormsocket == null || strFormsocket.isEmpty()) {
						sendOrder("\r\n");
						continue;
					}
					LOGGER.info("host【{}】 port【{}】 receive data: {}", host,
							port + strFormsocket);
					interpretPackage(strFormsocket);
					isConnected = Boolean.TRUE;
				}
			} catch (UnknownHostException e1) {
				LOGGER.error("PcClient failed!", e1);
				isConnected = Boolean.FALSE;
			} catch (Exception e2) {
				LOGGER.error("PcClient failed!", e2);
				isConnected = Boolean.FALSE;
			}
			return isConnected;
		}
	}

	/* 从InputStream流中读数据 */
	public String readFromSocket(InputStream in) {
		int MAX_BUFFER_BYTES = 4000;
		String msg = "";
		byte[] tempbuffer = new byte[MAX_BUFFER_BYTES];
		try {
			int numReadedBytes = in.read(tempbuffer);
			if (numReadedBytes < 0) {
				return null;
			}
			msg = new String(tempbuffer, 0, numReadedBytes, "utf-8");

			tempbuffer = null;
		} catch (Exception e) {
			LOGGER.error("Connection broken!",e);
			isConnected = Boolean.FALSE;
		}
		return msg;
	}

	/**
	 * Interpret LUCKAGEPACAKGE that received from socket connection
	 * 
	 * @param packageInfo
	 *            JSON string
	 */
	private void interpretPackage(String packageInfo) {
		try {
			JSONObject jsonObject = JSON.parse(packageInfo).asObject();
			JSONArray jsonLuckPeople = jsonObject.getJSONArray("LuckPeople");
			if (jsonLuckPeople == null || jsonLuckPeople.size() < 1) {
				LOGGER.warn("Luck package is empty! {}", packageInfo);
				return;
			}

			JSONObject jsonLuckOne = null;
			for (int i = 0; i < jsonLuckPeople.size(); i++) {
				jsonLuckOne = jsonLuckPeople.getJSONObject(i);
				Matcher matcher = StringUtils.DOUBLE.matcher(jsonLuckOne
						.getString("Money"));
				if (matcher.find()) {
					gameService.puttingLuckInfo(i,
							jsonLuckOne.getString("RemarkName"),
							Double.valueOf(matcher.group(1)));
				} else {
					LOGGER.warn(
							"Luck message RemarkUser {} Money{} interpret failed!",
							jsonLuckOne.getString("RemarkName"),
							jsonLuckOne.getString("Money"));
				}
			}
		} catch (Exception e) {
			LOGGER.error("Luck package[{}] interpret failed!", packageInfo, e);
		}
	}
}
