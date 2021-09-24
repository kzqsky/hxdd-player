package com.edu.hxdd_player.utils;

import android.app.AlertDialog;
import android.content.Context;

import com.blankj.utilcode.util.ThreadUtils;

public class DialogUtils {
    public static void showDialog(Context context, String message) {
        try {
            LiveDataBus.get().with("stop").setValue("stop");
            if (context == null)
                return;
            final AlertDialog.Builder normalDialog =
                    new AlertDialog.Builder(context);
            normalDialog.setTitle("提示");
            normalDialog.setMessage(message);
            normalDialog.setCancelable(false);
            normalDialog.setPositiveButton("确定",
                    (dialog, which) -> {

                    });
            normalDialog.setNegativeButton("",
                    (dialog, which) -> {
                        //...To-do

                    });
            // 显示
            ThreadUtils.runOnUiThread(() -> {
                normalDialog.show();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
