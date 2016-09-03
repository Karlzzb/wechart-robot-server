package com.karl.domain;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import blade.kit.DateKit;
import blade.kit.json.JSONObject;

@Component
public class RuntimeDomain implements Serializable {

    public RuntimeDomain() {
        groupMap = new HashMap<String, JSONObject>();
        allUsrMap = new HashMap<String, JSONObject>();
        groupUsrMap = new HashMap<String, JSONObject>();
        publicUsrMap = new HashMap<String, JSONObject>();
        specialUsrMap = new HashMap<String, JSONObject>();
        latestLuckInfo = new HashMap<String, Double>();
        qrCodeFile = new File("temp.jpg");

    }

    private static final long serialVersionUID = 5720576756640779509L;

    private String currentGroupId;

    private final File qrCodeFile;

    private String uuid;
    private int tip = 0;

    private String cookie;

    private JSONObject syncKeyJNode, User, baseRequest;

    // 群集合
    private Map<String, JSONObject> groupMap;

    // 所有用戶信息
    private Map<String, JSONObject> allUsrMap;

    // 群用户
    private Map<String, JSONObject> groupUsrMap;

    // 公众号／服务号
    private Map<String, JSONObject> publicUsrMap;

    // 特殊账号
    private Map<String, JSONObject> specialUsrMap;

    /**
     * The latest received LUCKPACKAGE info after interpretation
     */
    private Map<String, Double> latestLuckInfo;

    /**
     * The latest bet info
     */
    private Map<String, Double> latestBetInfo;

    private String skey, synckey, wxsid, wxuin, passTicket, deviceId = "e"
            + DateKit.getCurrentUnixTime();

    public Map<String, Double> getLatestLuckInfo() {
        return latestLuckInfo;
    }

    public void clearLatestLuckInfo() {
        latestLuckInfo.clear();
    }

    public void setLatestLuckInfo(Map<String, Double> latestLuckInfo) {
        this.latestLuckInfo = latestLuckInfo;
    }

    public Map<String, Double> getLatestBetInfo() {
        return latestBetInfo;
    }

    public void clearLatestBetInfo() {
        latestBetInfo.clear();
    }

    public void setLatestBetInfo(Map<String, Double> latestBetInfo) {
        this.latestBetInfo = latestBetInfo;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getTip() {
        return tip;
    }

    public void setTip(int tip) {
        this.tip = tip;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public JSONObject getSyncKeyJNode() {
        return syncKeyJNode;
    }

    public void setSyncKeyJNode(JSONObject syncKey) {
        syncKeyJNode = syncKey;
    }

    public JSONObject getUser() {
        return User;
    }

    public void setUser(JSONObject user) {
        User = user;
    }

    public JSONObject getBaseRequest() {
        return baseRequest;
    }

    public void setBaseRequest(JSONObject baseRequest) {
        this.baseRequest = baseRequest;
    }

    public Map<String, JSONObject> getGroupMap() {
        return groupMap;
    }

    public void setGroupMap(Map<String, JSONObject> groupMap) {
        this.groupMap = groupMap;
    }

    public void putGroupMap(String key, JSONObject value) {
        this.groupMap.put(key, value);
    }

    public Map<String, JSONObject> getAllUsrMap() {
        return allUsrMap;
    }

    public void setAllUsrMap(Map<String, JSONObject> allUsrMap) {
        this.allUsrMap = allUsrMap;
    }

    public void putAllUsrMap(String key, JSONObject value) {
        this.allUsrMap.put(key, value);
    }

    public Map<String, JSONObject> getGroupUsrMap() {
        return groupUsrMap;
    }

    public void setGroupUsrMap(Map<String, JSONObject> groupUsrMap) {
        this.groupUsrMap = groupUsrMap;
    }

    public void putGroupUsrMap(String key, JSONObject value) {
        this.groupUsrMap.put(key, value);
    }

    public Map<String, JSONObject> getPublicUsrMap() {
        return publicUsrMap;
    }

    public void setPublicUsrMap(Map<String, JSONObject> publicUsrMap) {
        this.publicUsrMap = publicUsrMap;
    }

    public void putPublicUsrMap(String key, JSONObject value) {
        this.publicUsrMap.put(key, value);
    }

    public Map<String, JSONObject> getSpecialUsrMap() {
        return specialUsrMap;
    }

    public void setSpecialUsrMap(Map<String, JSONObject> specialUsrMap) {
        this.specialUsrMap = specialUsrMap;
    }

    public void putSpecialUsrMap(String key, JSONObject value) {
        this.specialUsrMap.put(key, value);
    }

    public String getCurrentGroupId() {
        return currentGroupId;
    }

    public void setCurrentGroupId(String currentGroupId) {
        this.currentGroupId = currentGroupId;
    }

    public String getSkey() {
        return skey;
    }

    public void setSkey(String skey) {
        this.skey = skey;
    }

    public String getSynckey() {
        return synckey;
    }

    public void setSynckey(String synckey) {
        this.synckey = synckey;
    }

    public String getWxsid() {
        return wxsid;
    }

    public void setWxsid(String wxsid) {
        this.wxsid = wxsid;
    }

    public String getWxuin() {
        return wxuin;
    }

    public void setWxuin(String wxuin) {
        this.wxuin = wxuin;
    }

    public String getPassTicket() {
        return passTicket;
    }

    public void setPassTicket(String passTicket) {
        this.passTicket = passTicket;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public File getQrCodeFile() {
        return qrCodeFile;
    }

}
