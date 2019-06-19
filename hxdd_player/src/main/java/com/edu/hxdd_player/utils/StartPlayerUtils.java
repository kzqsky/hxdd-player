package com.edu.hxdd_player.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.ColorInt;

import com.edu.hxdd_player.activity.PlayerActivity;
import com.edu.hxdd_player.bean.parameters.GetChapter;

public class StartPlayerUtils {
    private static int COLOR_PRIMARY = Color.parseColor("#B7B7B7");
    private static int COLOR_PRIMARY_DARK = Color.parseColor("#B5B5B5");
    private static int COLOR_ACCENT = Color.parseColor("#BABABA");


    public static void play(Context context, GetChapter getChapter) {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra("data", getChapter);
        context.startActivity(intent);
    }

    public static void play(Context context, GetChapter getChapter, @ColorInt int colorPrimary) {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra("data", getChapter);
        context.startActivity(intent);
        COLOR_PRIMARY = colorPrimary;
    }

    public static void play(Context context, GetChapter getChapter, @ColorInt int colorPrimary, @ColorInt int colorPrimaryDark) {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra("data", getChapter);
        context.startActivity(intent);
        COLOR_PRIMARY = colorPrimary;
        COLOR_PRIMARY_DARK = colorPrimaryDark;
    }

    public static void play(Context context, GetChapter getChapter, @ColorInt int colorPrimary, @ColorInt int colorPrimaryDark, @ColorInt int colorAccent) {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra("data", getChapter);
        context.startActivity(intent);
        COLOR_PRIMARY = colorPrimary;
        COLOR_PRIMARY_DARK = colorPrimaryDark;
        COLOR_ACCENT = colorAccent;
    }

    public static void setColorPrimary(@ColorInt int colorPrimary) {
        COLOR_PRIMARY = colorPrimary;
    }

    public static void setColorPrimaryDark(@ColorInt int colorPrimaryDark) {
        COLOR_PRIMARY_DARK = colorPrimaryDark;
    }

    public static void setColorAccent(@ColorInt int colorAccent) {
        COLOR_ACCENT = colorAccent;
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
}
