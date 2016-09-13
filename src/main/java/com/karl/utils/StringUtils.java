package com.karl.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    public static Pattern DOUBLE = Pattern.compile("([0-9]*\\.?[0-9]+)");
    public static Pattern LONG = Pattern.compile("([0-9]+)");
    public static String ANGLEINLINE = "</?[^>]+>";
    

    public static String match(String p, String str) {
        Pattern pattern = Pattern.compile(p);
        Matcher m = pattern.matcher(str);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }
    
    public static Boolean matchLong(String str) {
    	if(str == null || str.isEmpty()) {
    		return Boolean.FALSE;
    	}
        Matcher m = LONG.matcher(str);
        return m.find();
    }
    
    public static String replaceHtml(String s) {
    	return s.replaceAll(ANGLEINLINE, "");
    }
    
    public static String getMD5(String key) {
    	 try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(key.getBytes(),0,key.length());
			StringBuffer sb = new StringBuffer();
			for(byte b:md5.digest()) {
				sb.append(String.format("%02x", b & 0xff));
			}
			
		} catch (NoSuchAlgorithmException e) {
		}
    	 
    	return key;
    }

}
