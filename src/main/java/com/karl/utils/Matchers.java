package com.karl.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Matchers {

    public static Pattern DOUBLE = Pattern.compile("([0-9]*\\.?[0-9]+)");

    public static String match(String p, String str) {
        Pattern pattern = Pattern.compile(p);
        Matcher m = pattern.matcher(str);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

}
