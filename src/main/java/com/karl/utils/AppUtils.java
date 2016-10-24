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

	public static String[] webpush_url = {
			"https://webpush.wx2.qq.com/cgi-bin/mmwebwx-bin/synccheck",
			"https://webpush.weixin.qq.com/cgi-bin/mmwebwx-bin/synccheck",
			"https://webpush2.weixin.qq.com/cgi-bin/mmwebwx-bin/synccheck",
			"https://webpush1.weixin.qq.com/cgi-bin/mmwebwx-bin/synccheck" };
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

	public static final long LOGIN_WAITING_TIME = 3000;

	public static final long PLAYER_TAB_FLSH_TERVAL = 3000;

	public static final long TRACE_TAB_FLSH_TERVAL = 2000;

	public static final long WECHAT_LISTEN_INTERVAL = 1000;

	public static final String UNCONTACTUSRNAME = "非好友故无名";

	public static final String PLAYLONG = "数字下注玩法";

	public static final String PLAYLONGSPLIT = "分数下注玩法";

	public static final String PLAYLUCKWAY = "3秒抢包";

	public static final String BETRESULTHEAD = "--------[停止下注]--------\n";

	public static final String BETRESULTLINE = "{0}余:{1}  下:{2}\n";

	public static final String BETRESULTTAIL = "--------------------------\n"
			+ "有效下注：{0}\n" + "应发包数：{1}\n" + "应发金额：{2}\n"
			+ "--------------------------\n" + "总下注额：{3,number,#}\n" + "普通下注：{4}\n"
			+ "自定义梭哈下注：{5}\n" + "--------------------------\n"
			+ "请发包手核对无误再发包\n" + "机器统计    对错勿怪\n";

	public static final String GAMESTART = "--------[开局信息]--------\n"+"{0}\n"
			+ "◆◆◆◆◆[第{1,number,#}期]◆◆◆◆◆\n"+ "--------[庄家列表]--------\n"
			+ "本局庄家：{2}\n" + "庄家积分：{3,number,#}\n"
			+ "包数量：{4}\n" + "每点：{5}\n" + "当前玩法：{6}\n" + "超时时间: {7,number,#}秒";

	public static final String GAMERESULT = "◆◆◆◆◆[第{0}期]◆◆◆◆◆\n" + "{1}！{2}\n"
			+ "-----------[赢]-----------\n" + "{3}"
			+ "-----------[输]-----------\n" + "{4}"
			+ "----------[梭哈]----------\n" + "{5}" + "{21}"
			+ "--------------------------\n" + "尾包时间：{6}\n" + "超时时间：{7}\n"
			+ "首包时间：{8}\n" + "--------------------------\n" + "本局庄家：{9}\n"
			+ "庄家抢包：{10} {11} 赔率：{12}\n" + "庄家输赢：输{13}家/赢{14}家 /和{15}家\n"
			+ "{24}" + "庄上积分：{16,number,#}\n" + "本局下注：{17,number,#}\n" + "{22}" + "发包费用：{18,number,#}\n"
			+ "本局基金：{19,number,#}\n" + "本局盈亏：{23,number,#}\n" + "庄总积分：{20,number,#}\n"
			+ "--------[翱翔出品]--------";

	public static final String RANKINGLAYOUT = "---------[富豪榜]---------\n{0}\n可用积分：{1} 锁定：{2}\n玩家:{3} 总分:{4}\n"
			+ "--------------------------\n"
			+ "{5}--------------------------\n"
			+ " 富豪榜总积分：{4} \n积分列表仅供参考,如有误请私聊管理员";
	public static final String RANKINGLINE = "第{0}　 {1}　　 积分：{2,number,#}\n";

	public static final String GAMERESULTWIN = "{0} {1} {2} 赢 {3,number,#}";
	public static final String GAMERESULTINVAIN = "{0} {1} {2} 喝水";
	public static final String GAMERESULTLOSE = "{0} {1} {2} 输 {3,number,#}";
	public static final String GAMERESULTTIMEOUT = "{0} {1} 超时 输 {2,number,#}\n";
	public static final String GAMERESULTESAME = "{0} {1} {2} 和 {3}";

	public static final String REPLYPOINTAPPLYPUT = "给{0} 已上[{1}] 剩余积分： {2,number,#}";
	public static final String REPLYPOINTAPPLYDRAW = "给{0} 已下[{1}] 剩余积分： {2,number,#}";
	public static final String REPLYPOINTAPPLYERROR = "{0}剩余积分： {1}, 不足以完成下{2}积分操作!!";
	public static final String REPLYPOINTAPPLYERROR2 = "@{3} {0}剩余积分： {1}, 不足以完成下{2}积分操作!!";
	public static final String REPLYPOINTAPPLYADD = "@{0} 已上[{1}] 剩余积分： {2,number,#}";
	public static final String REPLYPOINTAPPLYSUB = "@{0} 已下[{1}] 剩余积分： {2,number,#}";
	
	public static final String SINGLEPLAYERINFO = "@{0} 剩余积分： {1}";


	public static final String ASKRECOMMEND = "玩家{0}目前积分{1,number,#}，您要修改吗？";
	
	public static final String ASKRECOMMENDUNKNOWN = "{0}不是好友，请先添加！";


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

	public static final String TIMEOUTPAIDALL = "通赔";
	public static final String TIMEOUTPAIDONETIME = "平赔";
	public static final String TIMEOUTPAIDNONE = "无效";

	public static final String PUBLICPOINTRANKHEAD = "---------[富豪榜]---------\n"
			+ "玩家:{0} 总分:{1,number,#}\n" + "--------------------------\n";

	public static final String PUBLICPOINTRANKLINE = "第{0} {1} 积分：{2,number,#}\n";

	public static final String PUBLICPOINTRANKTAIL = "--------------------------\n"
			+ "富豪榜总积分：{0,number,#}\n" + "积分列表仅供参考,如有误请私聊管理员\n";

	public static final String LOTTERYRULE3 = "后三位有效";
	public static final String LOTTERYRULE2 = "后二位有效";

	public static final String FIXEDPACKAGEFEEMODEL = "固定包费";
	public static final String MATHPACKAGEFEEMODEL = "公式计算包费";
	public static final String REALPACKAGEFEEMODEL = "实际包费";

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
				// return (o1.getValue().getLatestBetTime()).compareTo(o2
				// .getValue().getLatestBetTime());
				return 0;
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
