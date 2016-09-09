package com.karl.domain;

import java.util.ResourceBundle;

public enum LotteryRule {
    MOMO_ONE {

        @Override
        public String getRuleName() {
            return getStringFromResourceBundle("momo.one");
        }

    },

    MOMO_GOLD {
        @Override
        public String getRuleName() {
            return getStringFromResourceBundle("momo.gold");
        }

    };

    abstract public String getRuleName();

    String getStringFromResourceBundle(String key) {
        return ResourceBundle.getBundle("PlayRule").getString(key);
    }

}
