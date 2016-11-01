package com.karl;

import java.util.EnumSet;
import java.util.Iterator;

import com.karl.domain.LotteryRule;

public class StaticTest {
	public static void testRule(double d) {
		LotteryRule lotteryRule = null;
		EnumSet<LotteryRule> theRule = EnumSet.allOf(LotteryRule.class);
		for (Iterator<LotteryRule> iterator = theRule.iterator(); iterator
				.hasNext();) {
			lotteryRule = (LotteryRule) iterator.next();
			if (lotteryRule.getRuleResult3(d)) {
				break;
			}
		}
		System.out.print(lotteryRule.getRuleName()+"|"+lotteryRule.getTimes());
	}
	
	public static void main(String[] args) {
//		StaticTest.testRule(1.73);
		
//		System.out.println(StringUtils.unicode2String("U+00AE"));
		
	}
}


