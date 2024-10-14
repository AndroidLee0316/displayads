package com.pasc.lib.displayads.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AdsDateUtil {

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_AND_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_NO_SS = "yyyy-MM-dd HH:mm";

    public static boolean isToday(Long mills) {
        if (mills != null && mills != 0L && mills != -1L) {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
            return fmt.format(new Date(mills)).equals(fmt.format(new Date()));
        } else {
            return false;
        }
    }

}
