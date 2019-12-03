package com.edu.hxdd_player.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.support.annotation.ColorInt;

import com.edu.hxdd_player.activity.PlayerActivity;
import com.edu.hxdd_player.bean.parameters.GetChapter;

public class StartPlayerUtils {
    /**
     * 主色
     */
    private static int COLOR_PRIMARY = Color.parseColor("#B7B7B7");
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
     * 是否显示弹题点
     */
    private static boolean HAS_POINT = true;

    Context context;
    GetChapter getChapter;

    public StartPlayerUtils(Context context, GetChapter getChapter) {
        this.context = context;
        this.getChapter = getChapter;
    }

    /**
     * 配色
     * @param colorPrimary
     * @return
     */
    public StartPlayerUtils colorPrimary(@ColorInt int colorPrimary) {
        COLOR_PRIMARY = colorPrimary;
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
     * @param videoPath
     * @return
     */
    public StartPlayerUtils videoPath(String videoPath) {
        VIDEO_PATH = videoPath;
        return this;
    }

    /**
     * 是否有下载视频功能（默认是）
     * @param downLoad
     * @return
     */
    public StartPlayerUtils downLoad(boolean downLoad) {
        HAS_DOWNLOAD = downLoad;
        return this;
    }

    /**
     * 是否显示弹题点(默认显示)
     * @param point
     * @return
     */
    public StartPlayerUtils point(boolean point) {
        HAS_POINT = point;
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

    public static boolean getPoint() {
        return HAS_POINT;
    }
}
