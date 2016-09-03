package com.karl.utils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

}
