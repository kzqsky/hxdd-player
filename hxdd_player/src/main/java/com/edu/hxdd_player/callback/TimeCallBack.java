package com.edu.hxdd_player.callback;

import com.edu.hxdd_player.activity.PlayerActivity;

public interface TimeCallBack {
    /**
     * 时间到的回调
     */
    default void onTime() {
    }

    /**
     * 时间到的回调
     *
     * @param currentCatalogID 章节id
     * @param coursewareCode   课件code
     */
    default void onTime(String currentCatalogID, String coursewareCode) {
    }

    /**
     * 不同模式回调（如时间递增、按次数等）
     *
     * @param activity         播放页面
     * @param studyTime        打开后累计学习时长
     * @param currentTime      进度条位置
     * @param duration         视频总时长
     * @param currentCatalogID 章节id
     * @param coursewareCode   课件code
     */
    default void oneSecondCallback(PlayerActivity activity, long studyTime, long currentTime, long duration, String currentCatalogID, String coursewareCode) {
    }
//
//    /**
//     * 识别结束的回调
//     */
//    void continuePlaying();
}
