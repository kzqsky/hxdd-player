package com.edu.hxdd_player.utils;

import android.content.Context;
import android.util.TypedValue;

/**
 * 常用单位转换的辅助类
 *
 * @author kang
 */
public class DensityUtils {

    /**
     * dp转px
     *
     * @param dpVal
     * @return
     */
    public static int dp2px(Context context,float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

//    /**
//     * sp转px
//     *
//     * @param spVal
//     * @return
//     */
//    public static int sp2px(float spVal) {
//        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
//                spVal, BaseApplication.getInstance().getResources().getDisplayMetrics());
//    }
//
    /**
     * px转dp
     *
     * @param pxVal
     * @return
     */
    public static int px2dp(Context context,float pxVal) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxVal / scale);
    }
//
//    /**
//     * px转sp
//     *
//     * @param pxVal
//     * @return
//     */
//    public static float px2sp(float pxVal) {
//        return (pxVal / BaseApplication.getInstance().getResources().getDisplayMetrics().scaledDensity);
//    }

}
