package com.edu.hxdd_player.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;

import androidx.annotation.ColorInt;

import com.edu.hxdd_player.activity.PlayerActivity;
import com.edu.hxdd_player.bean.parameters.GetChapter;
import com.edu.hxdd_player.callback.TimeCallBack;

public class StartPlayerUtils {
    /**
     * 主色
     */
    private static int COLOR_PRIMARY = Color.parseColor("#B7B7B7");
    /**
     * 已学习的颜色
     */
    private static int COLOR_LEARNED = Color.parseColor("#06c26c");
    /**
     * 预留色
     */
    private static int COLOR_PRIMARY_DARK = Color.parseColor("#B5B5B5");
    /**
     * 预留色
     */
    private static int COLOR_ACCENT = Color.parseColor("#BABABA");
    /**
     * 视频保存路径
     */
    private static String VIDEO_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/edu_video/";
    /**
     * 是否有下载视频功能
     */
    private static boolean HAS_DOWNLOAD = true;

    /**
     * 是否是缓存模式
     */
    private static boolean CACHE_MODE = false;

    /**
     * 回调时间，目前用于隔多少秒进行人脸识别
     */
    private static int CALL_BACK_TIME = 0;

    /**
     * 是否有讲义页面
     */
    private static boolean HAS_HANDOUT = true;

    public static TimeCallBack timeCallBack;
    Context context;
    public static GetChapter getChapter;
    /**
     * 是否在视频录制中
     */
    public static boolean isVideoRecord = false;

    public StartPlayerUtils(Context context, GetChapter getChapter) {
        this.context = context;
        this.getChapter = getChapter;
    }

    /**
     * 配色
     *
     * @param colorPrimary
     * @return
     */
    public StartPlayerUtils colorPrimary(@ColorInt int colorPrimary) {
        COLOR_PRIMARY = colorPrimary;
        return this;
    }

    /**
     * 已学习的颜色
     *
     * @param colorPrimary
     * @return
     */
    public StartPlayerUtils colorLearned(@ColorInt int colorPrimary) {
        COLOR_LEARNED = colorPrimary;
        return this;
    }

    /**
     * 预留色
     */
    public StartPlayerUtils colorPrimaryDark(@ColorInt int colorPrimaryDark) {
        COLOR_PRIMARY_DARK = colorPrimaryDark;
        return this;
    }

    /**
     * 预留色
     */
    public StartPlayerUtils colorAccent(@ColorInt int colorAccent) {
        COLOR_ACCENT = colorAccent;
        return this;
    }

    /**
     * 视频保存路径
     *
     * @param videoPath
     * @return
     */
    public StartPlayerUtils videoPath(String videoPath) {
        VIDEO_PATH = videoPath;
        return this;
    }

    /**
     * 是否有下载视频功能（默认是）
     *
     * @param downLoad
     * @return
     */
    public StartPlayerUtils downLoad(boolean downLoad) {
        HAS_DOWNLOAD = downLoad;
        return this;
    }

    /**
     * 是否是缓存模式（默认否）
     *
     * @param cacheMode
     * @return
     */
    public StartPlayerUtils cacheMode(boolean cacheMode) {
        CACHE_MODE = cacheMode;
        return this;
    }

    /**
     * 定时回调任务
     *
     * @param time
     * @return
     */
    public StartPlayerUtils callBackTime(int time, TimeCallBack callBack) {
        CALL_BACK_TIME = time;
        timeCallBack = callBack;
        return this;
    }

    /**
     * 是否显示讲义页面（默认是）
     *
     * @param handout
     * @return
     */
    public StartPlayerUtils handout(boolean handout) {
        HAS_HANDOUT = handout;
        return this;
    }


    /**
     * 播放
     */
    public void play() {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra("data", getChapter);
        context.startActivity(intent);
    }


    public static int getColorPrimary() {
        return COLOR_PRIMARY;
    }

    public static int getColorLearned() {
        return COLOR_LEARNED;
    }

    public static int getColorPrimaryDark() {
        return COLOR_PRIMARY_DARK;
    }

    public static int getColorAccent() {
        return COLOR_ACCENT;
    }

    public static String getVideoPath() {
        return VIDEO_PATH;
    }

    public static boolean getHasDownload() {
        return HAS_DOWNLOAD;
    }

    public static boolean getCacheMode() {
        return CACHE_MODE;
    }

    public static boolean isShowPoint() {
        return getChapter.hintPoint == 1;
    }

    public static boolean canSeek() {
        return getChapter.drag == 0;
    }

    /**
     * 是否启用顺序学习，顺序学习只能按顺序切换章节
     *
     * @return
     */
    public static boolean nextLearning() {
        return getChapter.playByOrder;
    }

    public static int getCallBackTime() {
        return CALL_BACK_TIME;
    }

    public static boolean getHandOut() {
        return HAS_HANDOUT;
    }

    public static void clear() {
        timeCallBack = null;
    }
}
