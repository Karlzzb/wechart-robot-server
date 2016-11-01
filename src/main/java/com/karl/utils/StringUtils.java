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

	public static final String SELFPACKSPLIT = "<br/>";

    public static Pattern DOUBLE = Pattern.compile("^([0-9]*\\.?[0-9]+)$");
    public static Pattern LONGSPLIT = Pattern.compile("^([1-9]+[0-9]*/[1-9]+[0-9]*)$");
    public static Pattern SUOHAPERF = Pattern.compile("^(梭哈)+([1-9]+[0-9]*)(梭哈)+$");
    public static Pattern LONG = Pattern.compile("^([1-9]+[0-9]*)$");
    public static Pattern ADDPOINT = Pattern.compile("^查\\s*([1-9]+[0-9]*)$");
    public static Pattern SUBPOINT = Pattern.compile("^回\\s*([1-9]+[0-9]*)$");
    public static Pattern PUTPOINT = Pattern.compile("^上\\s*([1-9]+[0-9]*)$");
    public static Pattern DRAWPOINT = Pattern.compile("^下\\s*([1-9]+[0-9]*)$");
    
    public static Pattern SELFPACKHEAD = Pattern.compile("^【包信息】.*");
    
	public static Pattern RECOMMENDMSG = Pattern.compile(".*现在可以开始聊天了.*");

    
    public static Pattern SELFPACKLINE = Pattern.compile("([1-9]+[0-9]*)位: (.*), ([0-9]*\\.?[0-9]+), (.*)");
    
    public static String ANGLEINLINE = "</?[^>]+>";
    
    public static String BETSPLIT = "/";
    

    public static String match(String p, String str) {
        Pattern pattern = Pattern.compile(p);
        Matcher m = pattern.matcher(str);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }
    
    public static Boolean matchSelfPackageHead(String str) {
    	if(str == null || str.isEmpty()) {
    		return Boolean.FALSE;
    	}
        Matcher m = SELFPACKHEAD.matcher(str);
        return m.find();
    }
    
    public static Boolean matchLongSplit(String str) {
    	if(str == null || str.isEmpty()) {
    		return Boolean.FALSE;
    	}
        Matcher m = LONGSPLIT.matcher(str);
        return m.find();
    }
    
    public static Boolean matchLong(String str) {
    	if(str == null || str.isEmpty()) {
    		return Boolean.FALSE;
    	}
        Matcher m = LONG.matcher(str);
        return m.find();
    }
    
    public static Boolean matchDouble(String str) {
    	if(str == null || str.isEmpty()) {
    		return Boolean.FALSE;
    	}
        Matcher m = DOUBLE.matcher(str);
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
    
    /**
     * unicode 转字符串
     */
    public static String unicode2String(String unicode) {
        StringBuffer string = new StringBuffer();
        String[] hex = unicode.split("U");
        for (int i = 1; i < hex.length; i++) {
            // 转换出每一个代码点
            int data = Integer.parseInt(hex[i], 16);
     
            // 追加成string
            string.append((char) data);
        }
     
        return string.toString();
    }
    
    /**
     * unicode 转字符串
     */
    public static String labelR() {
    	return StringUtils.unicode2String("U+00AE");
    }
    
    /**
     * unicode 转字符串
     */
    public static String labelFace() {
    	return StringUtils.unicode2String("U+E105");
    }
    
    /**
     * unicode 转字符串
     */
    public static String labelFaceEM() {
    	return StringUtils.unicode2String("U+E107");
    }
    
    /**
     * unicode 转字符串
     */
    public static String labelRUN() {
    	return StringUtils.unicode2String("U+E115");
    }
    
    /**
     * unicode 转字符串
     */
    public static String labelEXPIRE() {
    	return StringUtils.unicode2String("U+E02D");
    }
    
    /**
     * unicode 转字符串
     */
    public static String labelTOP() {
    	return StringUtils.unicode2String("U+E24C");
    }
    
	
}
