package com.karl.utils;

public class DigitalUtils {

    public static Long getIntFromDouble(Double doubleValue) {
        Long result = Long.valueOf(0);
        if (doubleValue != null && doubleValue.compareTo(Double.valueOf(1)) > 0) {
            result = doubleValue.longValue();
        }
        return result;
    }

    public static Long getSumFromDouble(Double doubleValue) {
        Long resutl = Long.valueOf(0);
        if (doubleValue != null && doubleValue.compareTo(Double.valueOf(1)) > 0) {
            Long d1 = getIntFromDouble(doubleValue);
            Long d2 = getIntFromDouble(doubleValue * 10 - d1 * 10);
            Long d3 = getIntFromDouble(doubleValue * 100 - d1 * 100 - d2 * 10);
            resutl = (d1 + d2 + d3)%10;
        }
        return resutl;
    }

}
