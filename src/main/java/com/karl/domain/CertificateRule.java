package com.karl.domain;

import java.util.Date;

import com.karl.utils.DateUtils;

public enum CertificateRule {
	USER1("hopeless", "qwer4321#", "2016-10-31"),
	USER2("kevin", "qazwsx!", "2016-11-7"),
	USER3("coal", "poilkj!", "2016-11-14"),
	USER4("global", "qazwsx!", "2016-11-21"),
	USER5("gold", "qwer4321#", "2016-11-28"),
	USER6("wise", "poilkj!", "2016-12-2"),
	USER7("unlike", "zxcvasdf#", "2016-12-9"),
	USER8("luck", "4321rewq!", "2016-12-16"),
	USER9("last", "asdf;lkj", "2016-12-23"),
	USER10("final", "qazwsx!", "2017-1-1");

	private final String user;

	private final String userKey;

	private Date expire;

	private CertificateRule(String user, String userKey, String expire) {
		this.user = user;
		this.userKey = userKey;
		this.expire = null;
		try {
			this.expire = DateUtils.stringToDate(expire, DateUtils.DATE_FORMAT);
		} catch (Exception e) {
		}
	}

	public Boolean check(String user, String userKey) {
		if (user == null || userKey == null || userKey.isEmpty() || user.isEmpty()) {
			return Boolean.FALSE;
		}
		if (user.equals(this.user) && userKey.equals(this.userKey)
				&& expire != null && expire.getTime() >= (new Date()).getTime()) {
			return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}
}
