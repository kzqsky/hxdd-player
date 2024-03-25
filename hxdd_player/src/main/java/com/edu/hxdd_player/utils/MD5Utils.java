package com.edu.hxdd_player.utils;

import java.security.MessageDigest;

/**
 * Created by Admin on 2016/9/14.
 * MD5编码
 */
public class MD5Utils {
    /**
     * 对字符串MD5加密
     *
     * @param str
     * @return
     */
    public static String encodeMD5String(String str) {
        return encode(str, "MD5");
    }

    /**
     * 对字符串进行编码
     *
     * @param str
     * @param method
     * @return
     */
    private static String encode(String str, String method) {
        MessageDigest md = null;
        String dstr = null;
        try {
            md = MessageDigest.getInstance(method);
            md.update(str.getBytes());
            dstr = byte2hex(md.digest());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dstr;
    }

    /**
     * 转换成16进制字符串
     * @param b
     * @return
     */
    private static String byte2hex(byte[] b) {
        if (b == null) {
            return null;
        }
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString((b[n] & 0xFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs.toLowerCase();
    }
}
