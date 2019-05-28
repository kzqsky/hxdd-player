package com.edu.hxdd_player.utils;

public class TimeFormatUtils {
    private static String getString(long t) {
        String m = "";
        if (t > 0) {
            if (t < 10) {
                m = "0" + t;
            } else {
                m = t + "";
            }
        } else {
            m = "00";
        }
        return m;
    }

    /**
     * @param t 秒
     * @return
     * @author Peter（张春玲）
     */
    public static String format(long t) {
        t = t * 1000;
        if (t < 60) {
            return (t % 60000) / 1000 + "秒";
        } else if ((t >= 60000) && (t < 3600000)) {
            return getString((t % 3600000) / 60000) + ":" + getString((t % 60000) / 1000);
        } else {
            return getString(t / 3600000) + ":" + getString((t % 3600000) / 60000) + ":" + getString((t % 60000) / 1000);
        }
    }

}
