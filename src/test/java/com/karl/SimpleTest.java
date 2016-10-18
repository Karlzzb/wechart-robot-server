package com.karl;

import java.util.Date;
import java.util.regex.Matcher;

import com.karl.utils.StringUtils;

public class SimpleTest {

	public static void main(String[] args) {
		String content = "【包信息】：<br/>1位: insomnia, 0.01, 11:36:58<br/>2位: karl_zzb, 0.18, 留言<br/>3位: 凌霞, 0.04, 11:35:42<br/>4位: 然而并没有, 0.01, 11:35:42<br/>5位: 倦生, 0.11, 11:35:42<br/>6位: AA?玩坏了, 0.04, 11:35:42<br/>7位: 与你一世, 0.01, 11:35:42";
		
		String[] packageArray = content.split(StringUtils.SELFPACKSPLIT);
		if (packageArray == null || packageArray.length < 2) {
			return;
		}
		String line = null;
		
		Integer index;
		String remarkName;
		Double luckInfo;
		Date luckTime;
		for (int i = 1; i < packageArray.length; i++) {
			line = packageArray[i];
			if (line == null || line.isEmpty()) {
				continue;
			}
			Matcher m = StringUtils.SELFPACKLINE.matcher(line);
			if(m.find()) {
				System.out.println(m.group(1));
				System.out.println(m.group(2));
				System.out.println(m.group(3));
				System.out.println(m.group(4));
			}
		}

	}

}
