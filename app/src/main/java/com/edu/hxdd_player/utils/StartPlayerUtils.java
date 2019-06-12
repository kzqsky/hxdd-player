package com.edu.hxdd_player.utils;

import android.content.Context;
import android.content.Intent;

import com.edu.hxdd_player.activity.PlayerActivity;
import com.edu.hxdd_player.bean.parameters.GetChapter;

public class StartPlayerUtils {
    public static void play(Context context, GetChapter getChapter) {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra("data", getChapter);
        context.startActivity(intent);
    }
}
