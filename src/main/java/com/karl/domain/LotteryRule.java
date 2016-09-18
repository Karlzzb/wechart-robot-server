package com.karl.domain;

import java.util.ResourceBundle;

import com.karl.utils.DigitalUtils;

public enum LotteryRule {
    MOMO_SAME("momo.same",15) {

        @Override
        public Boolean getRuleResult(Double luckInfo) {
            Long d1 = DigitalUtils.getIntFromDouble(luckInfo);
            Long d2 = DigitalUtils.getIntFromDouble(luckInfo * 10 - d1 * 10);
            Long d3 = DigitalUtils.getIntFromDouble(luckInfo * 100 - d1 * 100 - d2 * 10);
            if (Long.compare(d1, d2) == 0 && Long.compare(d1, d3) == 0 && Long.compare(d3, d2) == 0) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }
    },
    MOMO_FULL("momo.full",14) {

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
    MOMO_STRAIGHT("momo.straight",13) {

        @Override
        public Boolean getRuleResult(Double luckInfo) {
            Long d1 = DigitalUtils.getIntFromDouble(luckInfo);
            Long d2 = DigitalUtils.getIntFromDouble(luckInfo * 10 - d1 * 10);
            Long d3 = DigitalUtils.getIntFromDouble(luckInfo * 100 - d1 * 100 - d2 * 10);
            if (Long.compare(d2 - d1, Long.valueOf(1)) == 0
                    && Long.compare(d3 - d2, Long.valueOf(1)) == 0) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }
    },
    MOMO_GOLD("momo.gold",12) {

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
    MOMO_PAIR("momo.pair",11) {

        @Override
        public Boolean getRuleResult(Double luckInfo) {
            Long d1 = DigitalUtils.getIntFromDouble(luckInfo);
            Long d2 = DigitalUtils.getIntFromDouble(luckInfo * 10 - d1 * 10);
            Long d3 = DigitalUtils.getIntFromDouble(luckInfo * 100 - d1 * 100 - d2 * 10);
            if (Long.compare(d1, d2) != 0 && Long.compare(d1, d3) != 0 && Long.compare(d3, d2) == 0) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }

    };

    private final long times;
    
    private final String ruleKey;

    private LotteryRule(String ruleKey, long times) {
        this.times = times;
        this.ruleKey = ruleKey;
    }

   public String getRuleName() {
	   return getStringFromResourceBundle(ruleKey); 
   }
    
    public String getRuleDetail() {
    	return String.valueOf(times)+"倍";
    }

    abstract public Boolean getRuleResult(Double luckInfo);

    String getStringFromResourceBundle(String key) {
        return ResourceBundle.getBundle("PlayRule").getString(key);
    }

    public long getTimes() {
        return times;
    }

}
