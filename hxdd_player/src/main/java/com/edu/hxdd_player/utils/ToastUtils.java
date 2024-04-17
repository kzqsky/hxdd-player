package com.edu.hxdd_player.utils;

import android.content.Context;
import android.os.Handler;

public class ToastUtils {
    public static void showLong(Context context, String text) {
        if (context != null){
            new Handler(context.getMainLooper()).post(() -> com.aliyun.svideo.common.utils.ToastUtils.showInCenter(context, text));
        }
    }
    public static void clean(){
        com.aliyun.svideo.common.utils.ToastUtils.clean();
    }
}
