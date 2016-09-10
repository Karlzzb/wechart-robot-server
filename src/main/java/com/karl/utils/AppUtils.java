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

    public static String base_uri, redirect_uri = "https://wx.qq.com/cgi-bin/mmwebwx-bin";

    public static String webpush_url = "https://webpush.weixin.qq.com/cgi-bin/mmwebwx-bin/synccheck";

    public static String skey, synckey, wxsid, wxuin, pass_ticket, deviceId = "e"
            + (new Date()).getTime();

    // 微信特殊账号
    public static List<String> specialUsers = Arrays.asList("newsapp", "fmessage", "filehelper",
            "weibo", "qqmail", "fmessage", "tmessage", "qmessage", "qqsync", "floatbottle",
            "lbsapp", "shakeapp", "medianote", "qqfriend", "readerapp", "blogapp", "facebookapp",
            "masssendapp", "meishiapp", "feedsapp", "voip", "blogappweixin", "weixin",
            "brandsessionholder", "weixinreminder", "wxid_novlwrv3lqwv11", "gh_22b87fa7cb3c",
            "officialaccounts", "notification_messages", "wxid_novlwrv3lqwv11", "gh_22b87fa7cb3c",
            "wxitil", "userexperience_alarm", "notification_messages");

    public static final long LOGIN_WAITING_TIME = 2000;

    public static final long WECHAT_LISTEN_INTERVAL = 500;

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
            public int compare(Map.Entry<String, Player> o1, Map.Entry<String, Player> o2) {
                return (o1.getValue().getLatestBetTime()).compareTo(o2.getValue()
                        .getLatestBetTime());
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
