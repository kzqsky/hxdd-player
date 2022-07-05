package com.aliyun.vodplayerview.view.gesturedialog;

import android.app.Activity;
import android.provider.Settings;

import com.edu.hxdd_player.R;
import com.edu.hxdd_player.utils.ComputeUtils;

/*
 * Copyright (C) 2010-2018 Alibaba Group Holding Limited.
 */

/**
 * 手势滑动的时的亮度提示框
 */
public class BrightnessDialog extends BaseGestureDialog {


    private static final String TAG = BrightnessDialog.class.getSimpleName();

    // 当前亮度。0~100
    private int mCurrentBrightness = 0;

    public BrightnessDialog(Activity activity, int percent) {
        super(activity);

        mCurrentBrightness = percent;

        //设置亮度图片
        mImageView.setImageResource(R.drawable.alivc_brightness);
        updateBrightness(percent);
    }

    /**
     * 更新对话框上的亮度百分比
     *
     * @param percent 亮度百分比
     */
    public void updateBrightness(int percent) {
        mTextView.setText(percent + "%");
    }


    /**
     * 获取当前亮度百分比
     *
     * @param activity 活动
     * @return 当前亮度百分比
     */
    public static int getActivityBrightness(Activity activity) {
        if (activity != null) {
            int systemBrightness = 0;
            try {
                systemBrightness = Settings.System.getInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            return (int) (ComputeUtils.div(systemBrightness, 255, 2) * 100);
        }
        return 0;
    }

    /**
     * 计算最终的亮度百分比
     *
     * @param changePercent 变化的百分比
     * @return 最终的亮度百分比
     */
    public int getTargetBrightnessPercent(int changePercent) {

        int newBrightness = mCurrentBrightness - changePercent;
        if (newBrightness > 100) {
            newBrightness = 100;
        } else if (newBrightness < 0) {
            newBrightness = 0;
        }
        return newBrightness;
    }

}
