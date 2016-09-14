package com.karl.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringUtils {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(StringUtils.class);

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
				sb.append(Integer.toString((b&0xff)+0x100,16).substring(1));
//				sb.append(String.format("%02x", b & 0xff));
			}
			key = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("Generating MD5 ID failed!",e);
		}
    	 
    	return key;
    }

}
