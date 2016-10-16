package com.karl.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;
import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import blade.kit.json.JSON;
import blade.kit.json.JSONArray;
import blade.kit.json.JSONObject;
import blade.kit.json.ParseException;

import com.android.ddmlib.IDevice;
import com.karl.domain.RuntimeDomain;
import com.karl.utils.DateUtils;
import com.karl.utils.StringUtils;

@Service
public class PcClient {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(PcClient.class);

	private static final String socket_host = "127.0.0.1";
	private static final int PC_LOCAL_PORT = 12580;
	private static final int PHONE_PORT = 62001;

	private Socket socket;

	private Boolean isConnected;

	private Boolean destory;

	private Thread openConnection;

	@Autowired
	private GameService gameService;

	@Autowired
	private RuntimeDomain runtimeDomain;

	private ADB mADB;

	public PcClient() {
		destory = Boolean.FALSE;
		openConnection = new Thread(new OpenConnection(socket_host,
				PC_LOCAL_PORT));
		openConnection.setDaemon(Boolean.TRUE);
		openConnection.start();
		isConnected = Boolean.FALSE;
		mADB = new ADB();
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
			while (!destory) {
				try {
					Thread.sleep(5000);
					mADB.initialize();
					IDevice[] mDevices = mADB.getDevices();
					if (mDevices == null || mDevices.length < 1) {
						LOGGER.error("No devices found!");
						mADB = null;
						mADB = new ADB();
						continue;
					}
					IDevice mDevice = mDevices[0];
					mDevice.createForward(PC_LOCAL_PORT, PHONE_PORT);
					// String adbPath =
					// getClass().getResource("/adbtools/adb.exe").toString();
					// LOGGER.info("adbPath【"+adbPath+"】");
					// Runtime.getRuntime().exec(
					// adbPath+" shell am broadcast -a NotifyServiceStop");
					// Runtime.getRuntime()
					// .exec(adbPath+" forward tcp:12580 tcp:62001");
					// Runtime.getRuntime().exec(
					// adbPath+" shell am broadcast -a NotifyServiceStart");
				} catch (Exception e) {
					LOGGER.error("PcClient forward failed!", e);
				}
				connnectToServer();
			}
		}

		private void sendOrder(String order) {
			try {
				BufferedOutputStream outputStream = new BufferedOutputStream(
						socket.getOutputStream());
				outputStream.write(order.getBytes("UTF-8"));
				outputStream.flush();
				isConnected = Boolean.TRUE;
				LOGGER.info("host【{}】 port【{}】 sent heartbeat! ", host, port);
			} catch (Exception e) {
				LOGGER.error("PcClient send heart beat failed!");
				isConnected = Boolean.FALSE;
			}
		}

		private Boolean connnectToServer() {
			try {
				InetAddress serveraddr = null;
				serveraddr = InetAddress.getByName(host);
				socket = new Socket(serveraddr, port);
				LOGGER.info("host【{}】 port【{}】 connecting...", host, port);
				sendOrder("\r\n");
				LOGGER.info("host【{}】 port【{}】 connected! ", host, port);
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
			} catch (Exception e) {
				LOGGER.error("PcClient failed!");
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
			LOGGER.error("PC Connection broken!");
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
			if (packageInfo == null || packageInfo.isEmpty()
					|| packageInfo.equals("{}")) {
				return;
			}

			JSONObject jsonObject = JSON.parse(packageInfo).asObject();
			JSONArray jsonLuckPeople = jsonObject.getJSONArray("LuckPeople");
			if (jsonLuckPeople == null || jsonLuckPeople.size() < 1) {
				LOGGER.warn("Luck package is empty! {}", packageInfo);
				return;
			}
			JSONObject jsonLuckOne = null;
			BigDecimal sumPackage = new BigDecimal(0);
			for (int i = 0; i < jsonLuckPeople.size(); i++) {
				jsonLuckOne = jsonLuckPeople.getJSONObject(i);
				try {
					Matcher matcher = StringUtils.DOUBLE.matcher(jsonLuckOne
							.getString("Money"));

					Date time = DateUtils.parsePageDateTime(jsonLuckOne
							.getString("Time"));

					if (matcher.find()) {
						gameService.puttingLuckInfo(i + 1,
								jsonLuckOne.getString("RemarkName"),
								Double.valueOf(matcher.group(1)), time);
					}
					runtimeDomain.setcurrentFirstPacageTime(time);
					runtimeDomain.setcurrentLastPacageTime(time);
					sumPackage = sumPackage.add(new BigDecimal(Double
							.valueOf(matcher.group(1))));
				} catch (Exception e) {
					LOGGER.error(
							"Luck message RemarkUser {} Money{} interpret failed!",
							jsonLuckOne.getString("RemarkName"),
							jsonLuckOne.getString("Money"));
					continue;
				}
			}
			runtimeDomain.setCurrentRealPackageFee(sumPackage.longValue());
			LOGGER.info("Luck package is {}", packageInfo);
		} catch (ParseException pe) {
			LOGGER.error("Luck package[" + packageInfo + "] interpret failed!");
			return;

		} catch (Exception e) {
			LOGGER.error("Luck package[{}] interpret failed!", packageInfo, e);
			return;
		}
	}

	public Boolean getIsConnected() {
		return isConnected;
	}

	public void destory() {
		destory = Boolean.TRUE;
		openConnection.interrupt();
		openConnection = null;
	}

	public void setIsConnected(Boolean isConnected) {
		this.isConnected = isConnected;
	}

	public Boolean getDestory() {
		return destory;
	}

	public void setDestory(Boolean destory) {
		this.destory = destory;
	}
}
