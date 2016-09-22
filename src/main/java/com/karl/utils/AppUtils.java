package com.karl.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.karl.db.domain.Player;

public class AppUtils {

	public static String base_uri,
			redirect_uri = "https://wx.qq.com/cgi-bin/mmwebwx-bin";

	public static String webpush_url = "https://webpush.weixin.qq.com/cgi-bin/mmwebwx-bin/synccheck";

	public static String skey, synckey, wxsid, wxuin, pass_ticket,
			deviceId = "e" + (new Date()).getTime();

	// 微信特殊账号
	public static List<String> specialUsers = Arrays.asList("newsapp",
			"fmessage", "filehelper", "weibo", "qqmail", "fmessage",
			"tmessage", "qmessage", "qqsync", "floatbottle", "lbsapp",
			"shakeapp", "medianote", "qqfriend", "readerapp", "blogapp",
			"facebookapp", "masssendapp", "meishiapp", "feedsapp", "voip",
			"blogappweixin", "weixin", "brandsessionholder", "weixinreminder",
			"wxid_novlwrv3lqwv11", "gh_22b87fa7cb3c", "officialaccounts",
			"notification_messages", "wxid_novlwrv3lqwv11", "gh_22b87fa7cb3c",
			"wxitil", "userexperience_alarm", "notification_messages");

	public static final long LOGIN_WAITING_TIME = 2000;

	public static final long PLAYER_TAB_FLSH_TERVAL = 1000;

	public static final long WECHAT_LISTEN_INTERVAL = 500;

	public static final String UNCONTACTUSRNAME = "非好友故无名";

	public static final String PLAYLONG = "数字下注玩法";

	public static final String PLAYLONGSPLIT = "分数下注玩法";

	public static final String BETRESULTHEAD = "--------[停止下注]--------\n";

	public static final String BETRESULTLINE = "{0}余:{1}  下:{2}\n";

	public static final String BETRESULTTAIL = "--------------------------\n"
			+ "有效下注：{0}\n" + "应发包数：{1}\n" + "应发金额：{2}\n"
			+ "--------------------------\n" + "总下注额：{3}\n" + "普通下注：{4}\n"
			+ "自定义梭哈下注：{5}\n" + "--------------------------\n"
			+ "请发包手核对无误再发包\n" + "机器统计    对错勿怪\n";

	public static final String GAMESTART = "--------[开始下注]--------\n" + "{0}\n"
			+ "--------[庄家列表]--------\n" + "本局庄家：{1}\n" + "庄家积分：{2}\n"
			+ "自定义金额梭哈最高下：{3}\n" + "最低下注：{4}\n" + "包数量：{5}\n" + "庄家位置：{6}\n"
			+ "当前玩法：八门玩法+梭哈玩法\n";

	public static final Long DEFAULT_MAXBET = Long.valueOf("1000");
	public static final Long DEFAULT_MINBET = Long.valueOf("10");
	public static final Integer DEFAULT_PACKAGE_NUM = 8;

	public static final String NONEBET = "未下注";

	public static final int APPLYADDPOINT = 1;
	public static final int APPLYSUBPOINT = 2;
	public static final String APPLYADDTEXT = "加";
	public static final String APPLYSUBTEXT = "减";

	public static final Integer APPROVALNONE = 0;
	public static final Integer APPROVALYES = 1;
	public static final Integer APPROVALNO = 2;

	/**
	 * sort by bet time
	 * 
	 * @param unsortMap
	 * @return
	 */
	public static Map<String, Player> sortByValue(Map<String, Player> unsortMap) {

		// 1. Convert Map to List of Map
		List<Map.Entry<String, Player>> list = new LinkedList<Map.Entry<String, Player>>(
				unsortMap.entrySet());

		// 2. Sort list with Collections.sort(), provide a custom Comparator
		// Try switch the o1 o2 position for a different order
		Collections.sort(list, new Comparator<Map.Entry<String, Player>>() {
			public int compare(Map.Entry<String, Player> o1,
					Map.Entry<String, Player> o2) {
				return (o1.getValue().getLatestBetTime()).compareTo(o2
						.getValue().getLatestBetTime());
			}
		});

		// 3. Loop the sorted list and put it into a new insertion order Map
		Map<String, Player> sortedMap = new LinkedHashMap<String, Player>();
		for (Map.Entry<String, Player> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		list.clear();
		list = null;

		return sortedMap;
	}

}
