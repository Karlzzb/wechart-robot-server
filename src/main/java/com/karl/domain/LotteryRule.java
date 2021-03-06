package com.karl.domain;

import java.util.ResourceBundle;

import com.karl.utils.DigitalUtils;

public enum LotteryRule {
	MOMO_SAME("momo.same", 15) {

		@Override
		public Boolean getRuleResult3(Double luckInfo) {
			Integer d1 = DigitalUtils.getSpecificIndexOfDouble(luckInfo,3);
			Integer d2 = DigitalUtils.getSpecificIndexOfDouble(luckInfo,2);
			Integer d3 = DigitalUtils.getSpecificIndexOfDouble(luckInfo,1);
			if (Integer.compare(d1, d2) == 0 && Integer.compare(d1, d3) == 0
					&& Integer.compare(d3, d2) == 0) {
				return Boolean.TRUE;
			}
			return Boolean.FALSE;
		}

		@Override
		public Boolean getRuleResult2(Double luckInfo) {
			return Boolean.FALSE;
		}
	},
	MOMO_FULL("momo.full", 14) {

		@Override
		public Boolean getRuleResult3(Double luckInfo) {
			for (int i = 0; i < 10; i++) {
				if (Double.compare(luckInfo, Double.valueOf(i)) == 0) {
					return Boolean.TRUE;
				}
			}
			return Boolean.FALSE;
		}
		
		@Override
		public Boolean getRuleResult2(Double luckInfo) {
			return Boolean.FALSE;
		}

	},
	MOMO_STRAIGHT("momo.straight", 13) {

		@Override
		public Boolean getRuleResult3(Double luckInfo) {
			if (luckInfo.compareTo(Double.valueOf(1)) <= 0) {
				return Boolean.FALSE;
			}
			
			Integer d1 = DigitalUtils.getSpecificIndexOfDouble(luckInfo,3);
			Integer d2 = DigitalUtils.getSpecificIndexOfDouble(luckInfo,2);
			Integer d3 = DigitalUtils.getSpecificIndexOfDouble(luckInfo,1);
			if (Integer.compare(d2 - d1, Integer.valueOf(1)) == 0
					&& Integer.compare(d3 - d2, Integer.valueOf(1)) == 0) {
				return Boolean.TRUE;
			}
			return Boolean.FALSE;
		}
		
		@Override
		public Boolean getRuleResult2(Double luckInfo) {
			return Boolean.FALSE;
		}

	},
	MOMO_GOLD("momo.gold", 12) {

		@Override
		public Boolean getRuleResult3(Double luckInfo) {
			for (int i = 1; i < 10; i++) {
				if (Double.compare(luckInfo * 10, Double.valueOf(i)) == 0) {
					return Boolean.TRUE;
				}
			}
			return Boolean.FALSE;
		}
		
		@Override
		public Boolean getRuleResult2(Double luckInfo) {
			return Boolean.FALSE;
		}

	},
	MOMO_PAIR("momo.pair", 11) {

		@Override
		public Boolean getRuleResult3(Double luckInfo) {
			Integer d1 = DigitalUtils.getSpecificIndexOfDouble(luckInfo,3);
			Integer d2 = DigitalUtils.getSpecificIndexOfDouble(luckInfo,2);
			Integer d3 = DigitalUtils.getSpecificIndexOfDouble(luckInfo,1);
			if (Integer.compare(d1, d2) != 0 && Integer.compare(d1, d3) != 0
					&& Integer.compare(d3, d2) == 0) {
				return Boolean.TRUE;
			}
			return Boolean.FALSE;
		}
		
		@Override
		public Boolean getRuleResult2(Double luckInfo) {
			return Boolean.FALSE;
		}
		
	},
	MOMO_NONE("momo.ten", 10) {
		@Override
		public Boolean getRuleResult3(Double luckInfo) {
			return DigitalUtils.getSumFromDoubleThree(luckInfo).compareTo(Integer.valueOf(0)) == 0;
		}
		
		@Override
		public Boolean getRuleResult2(Double luckInfo) {
			for (int i = 1; i < 10; i++) {
				if (Double.compare(luckInfo, Double.valueOf(i)) == 0) {
					return Boolean.TRUE;
				}
			}
			return DigitalUtils.getSumFromDoubleTwo(luckInfo).compareTo(Integer.valueOf(0)) == 0;
		}
	},
	MOMO_MAX("momo.night", 9) {

		@Override
		public Boolean getRuleResult3(Double luckInfo) {
			return DigitalUtils.getSumFromDoubleThree(luckInfo).compareTo(Integer.valueOf(9)) == 0;
		}
		
		@Override
		public Boolean getRuleResult2(Double luckInfo) {
			return DigitalUtils.getSumFromDoubleTwo(luckInfo).compareTo(Integer.valueOf(9)) == 0;
		}


	},MOMO_EIGHT("momo.eight", 8) {

		@Override
		public Boolean getRuleResult3(Double luckInfo) {
			return DigitalUtils.getSumFromDoubleThree(luckInfo).compareTo(Integer.valueOf(8)) == 0;
		}

		@Override
		public Boolean getRuleResult2(Double luckInfo) {
			return DigitalUtils.getSumFromDoubleTwo(luckInfo).compareTo(Integer.valueOf(8)) == 0;
		}
		
	},MOMO_SEVEN("momo.seven", 7) {

		@Override
		public Boolean getRuleResult3(Double luckInfo) {
			return DigitalUtils.getSumFromDoubleThree(luckInfo).compareTo(Integer.valueOf(7)) == 0;
		}
		
		@Override
		public Boolean getRuleResult2(Double luckInfo) {
			return DigitalUtils.getSumFromDoubleTwo(luckInfo).compareTo(Integer.valueOf(7)) == 0;
		}


	},MOMO_SIX("momo.six", 6) {

		@Override
		public Boolean getRuleResult3(Double luckInfo) {
			return DigitalUtils.getSumFromDoubleThree(luckInfo).compareTo(Integer.valueOf(6)) == 0;
		}

		@Override
		public Boolean getRuleResult2(Double luckInfo) {
			return DigitalUtils.getSumFromDoubleTwo(luckInfo).compareTo(Integer.valueOf(6)) == 0;
		}
	},MOMO_FIVE("momo.five", 5) {

		@Override
		public Boolean getRuleResult3(Double luckInfo) {
			return DigitalUtils.getSumFromDoubleThree(luckInfo).compareTo(Integer.valueOf(5)) == 0;
		}

		@Override
		public Boolean getRuleResult2(Double luckInfo) {
			return DigitalUtils.getSumFromDoubleTwo(luckInfo).compareTo(Integer.valueOf(5)) == 0;
		}
	},MOMO_FOUR("momo.four", 4) {

		@Override
		public Boolean getRuleResult3(Double luckInfo) {
			return DigitalUtils.getSumFromDoubleThree(luckInfo).compareTo(Integer.valueOf(4)) == 0;
		}
		
		@Override
		public Boolean getRuleResult2(Double luckInfo) {
			return DigitalUtils.getSumFromDoubleTwo(luckInfo).compareTo(Integer.valueOf(4)) == 0;
		}

	},MOMO_THREE("momo.three", 3) {

		@Override
		public Boolean getRuleResult3(Double luckInfo) {
			return DigitalUtils.getSumFromDoubleThree(luckInfo).compareTo(Integer.valueOf(3)) == 0;
		}
		
		@Override
		public Boolean getRuleResult2(Double luckInfo) {
			return DigitalUtils.getSumFromDoubleTwo(luckInfo).compareTo(Integer.valueOf(3)) == 0;
		}


	},MOMO_TWO("momo.two", 2) {

		@Override
		public Boolean getRuleResult3(Double luckInfo) {
			return DigitalUtils.getSumFromDoubleThree(luckInfo).compareTo(Integer.valueOf(2)) == 0;
		}
		
		@Override
		public Boolean getRuleResult2(Double luckInfo) {
			return DigitalUtils.getSumFromDoubleTwo(luckInfo).compareTo(Integer.valueOf(2)) == 0;
		}


	},MOMO_ONE("momo.one", 1) {
		@Override
		public Boolean getRuleResult3(Double luckInfo) {
			return DigitalUtils.getSumFromDoubleThree(luckInfo).compareTo(Integer.valueOf(1)) == 0;
		}
		
		@Override
		public Boolean getRuleResult2(Double luckInfo) {
			return DigitalUtils.getSumFromDoubleTwo(luckInfo).compareTo(Integer.valueOf(1)) == 0;
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

	abstract public Boolean getRuleResult3(Double luckInfo);
	
	abstract public Boolean getRuleResult2(Double luckInfo);

	String getStringFromResourceBundle(String key) {
		return ResourceBundle.getBundle("PlayRule").getString(key);
	}

	public Integer getTimes() {
		return times;
	}

}
