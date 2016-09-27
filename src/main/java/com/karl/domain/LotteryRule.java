package com.karl.domain;

import java.util.ResourceBundle;

import com.karl.utils.DigitalUtils;

public enum LotteryRule {
	MOMO_SAME("momo.same", 15) {

		@Override
		public Boolean getRuleResult(Double luckInfo) {
			Long d1 = DigitalUtils.getIntFromDouble(luckInfo);
			Long d2 = DigitalUtils.getIntFromDouble(luckInfo * 10 - d1 * 10);
			Long d3 = DigitalUtils.getIntFromDouble(luckInfo * 100 - d1 * 100
					- d2 * 10);
			if (Long.compare(d1, d2) == 0 && Long.compare(d1, d3) == 0
					&& Long.compare(d3, d2) == 0) {
				return Boolean.TRUE;
			}
			return Boolean.FALSE;
		}
	},
	MOMO_FULL("momo.full", 14) {

		@Override
		public Boolean getRuleResult(Double luckInfo) {
			for (int i = 0; i < 10; i++) {
				if (Double.compare(luckInfo, Double.valueOf(i)) == 0) {
					return Boolean.TRUE;
				}
			}
			return Boolean.FALSE;
		}
	},
	MOMO_STRAIGHT("momo.straight", 13) {

		@Override
		public Boolean getRuleResult(Double luckInfo) {
			Long d1 = DigitalUtils.getIntFromDouble(luckInfo);
			Long d2 = DigitalUtils.getIntFromDouble(luckInfo * 10 - d1 * 10);
			Long d3 = DigitalUtils.getIntFromDouble(luckInfo * 100 - d1 * 100
					- d2 * 10);
			if (Long.compare(d2 - d1, Long.valueOf(1)) == 0
					&& Long.compare(d3 - d2, Long.valueOf(1)) == 0) {
				return Boolean.TRUE;
			}
			return Boolean.FALSE;
		}
	},
	MOMO_GOLD("momo.gold", 12) {

		@Override
		public Boolean getRuleResult(Double luckInfo) {
			for (int i = 0; i < 10; i++) {
				if (Double.compare(luckInfo * 10, Double.valueOf(i)) == 0) {
					return Boolean.TRUE;
				}
			}
			return Boolean.FALSE;
		}
	},
	MOMO_PAIR("momo.pair", 11) {

		@Override
		public Boolean getRuleResult(Double luckInfo) {
			Long d1 = DigitalUtils.getIntFromDouble(luckInfo);
			Long d2 = DigitalUtils.getIntFromDouble(luckInfo * 10 - d1 * 10);
			Long d3 = DigitalUtils.getIntFromDouble(luckInfo * 100 - d1 * 100
					- d2 * 10);
			if (Long.compare(d1, d2) != 0 && Long.compare(d1, d3) != 0
					&& Long.compare(d3, d2) == 0) {
				return Boolean.TRUE;
			}
			return Boolean.FALSE;
		}
	},
	MOMO_NONE("momo.ten", 10) {
		@Override
		public Boolean getRuleResult(Double luckInfo) {
			return DigitalUtils.getSumFromDouble(luckInfo).compareTo(Long.valueOf(0)) == 0;
		}
	},
	MOMO_MAX("momo.night", 9) {

		@Override
		public Boolean getRuleResult(Double luckInfo) {
			return DigitalUtils.getSumFromDouble(luckInfo).compareTo(Long.valueOf(9)) == 0;
		}

	},MOMO_EIGHT("momo.eight", 8) {

		@Override
		public Boolean getRuleResult(Double luckInfo) {
			return DigitalUtils.getSumFromDouble(luckInfo).compareTo(Long.valueOf(8)) == 0;
		}

	},MOMO_SEVEN("momo.seven", 7) {

		@Override
		public Boolean getRuleResult(Double luckInfo) {
			return DigitalUtils.getSumFromDouble(luckInfo).compareTo(Long.valueOf(7)) == 0;
		}

	},MOMO_SIX("momo.six", 6) {

		@Override
		public Boolean getRuleResult(Double luckInfo) {
			return DigitalUtils.getSumFromDouble(luckInfo).compareTo(Long.valueOf(6)) == 0;
		}

	},MOMO_FIVE("momo.five", 5) {

		@Override
		public Boolean getRuleResult(Double luckInfo) {
			return DigitalUtils.getSumFromDouble(luckInfo).compareTo(Long.valueOf(5)) == 0;
		}

	},MOMO_FOUR("momo.four", 4) {

		@Override
		public Boolean getRuleResult(Double luckInfo) {
			return DigitalUtils.getSumFromDouble(luckInfo).compareTo(Long.valueOf(4)) == 0;
		}

	},MOMO_THREE("momo.three", 3) {

		@Override
		public Boolean getRuleResult(Double luckInfo) {
			return DigitalUtils.getSumFromDouble(luckInfo).compareTo(Long.valueOf(3)) == 0;
		}

	},MOMO_TWO("momo.two", 2) {

		@Override
		public Boolean getRuleResult(Double luckInfo) {
			return DigitalUtils.getSumFromDouble(luckInfo).compareTo(Long.valueOf(2)) == 0;
		}

	},MOMO_ONE("momo.one", 1) {
		@Override
		public Boolean getRuleResult(Double luckInfo) {
			return DigitalUtils.getSumFromDouble(luckInfo).compareTo(Long.valueOf(1)) == 0;
		}
	};

	private final Integer times;

	private final String ruleKey;

	private LotteryRule(String ruleKey, Integer times) {
		this.times = times;
		this.ruleKey = ruleKey;
	}

	public String getRuleName() {
		return getStringFromResourceBundle(ruleKey);
	}

	public String getRuleDetail() {
		return String.valueOf(times) + "倍";
	}

	abstract public Boolean getRuleResult(Double luckInfo);

	String getStringFromResourceBundle(String key) {
		return ResourceBundle.getBundle("PlayRule").getString(key);
	}

	public long getTimes() {
		return times;
	}

}
