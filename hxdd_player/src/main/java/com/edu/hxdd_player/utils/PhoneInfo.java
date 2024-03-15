package com.edu.hxdd_player.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;

public class PhoneInfo {

    public static String getDeviceInfo(Context context) {
        StringBuffer sb = new StringBuffer();
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        sb.append("系统定制商：" + Build.BRAND);
        sb.append("硬件名称：" + Build.HARDWARE);
        sb.append("硬件制造商：" + Build.MANUFACTURER);
        sb.append("版本：" + Build.MODEL);
        sb.append("手机制造商：" + Build.PRODUCT);
        sb.append("TIME: " + Build.TIME);
        sb.append("系统版本: " + Build.VERSION.RELEASE);
        if (activeNetworkInfo != null)
            switch (activeNetworkInfo.getType()) {
                case ConnectivityManager.TYPE_MOBILE:
                    sb.append("网络类型: 移动数据");
                    break;
                case ConnectivityManager.TYPE_WIFI:
                    sb.append("网络类型: WIFI");
                    break;
                default:
                    sb.append("网络类型: -1");
                    break;
            }
        return sb.toString();
    }

    /**
     * 得到软件版本号
     *
     * @param context 上下文
     * @return 当前版本Code
     */
    public static String getVerCode(Context context) {
        int verCode = -1;
        try {
            String packageName = context.getPackageName();
            verCode = context.getPackageManager()
                    .getPackageInfo(packageName, 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verCode + "";
    }

    /**
     * 获得APP名称
     *
     * @param context
     * @return
     */
    public static String getAppName(Context context) {
        String appName = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            appName = (String) packageManager.getApplicationLabel(applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appName;
    }
}
