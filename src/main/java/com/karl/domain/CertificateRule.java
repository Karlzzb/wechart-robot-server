package com.karl.domain;

import java.util.Date;

import com.karl.utils.DateUtils;

public enum CertificateRule {
	USER1("hopeless", "321qwer4321#", "2017-4-30"),
	USER2("kevin", "qazwsx121!", "2017-5-7"),
	USER3("coal", "poilkj22!", "2017-6-14"),
	USER4("global", "qazw3sx!", "2017-7-21"),
	USER5("gold", "qwer34321#", "2017-8-28"),
	USER6("wise", "poilkj2!", "2017-9-2"),
	USER7("unlike", "zx4cvasdf#", "2017-10-9"),
	USER8("luck", "4321r1ewq!", "2017-11-16"),
	USER9("last", "asdf;l6kj", "2017-12-23"),
	USER10("final", "qazw8sx!", "2018-1-1");

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
