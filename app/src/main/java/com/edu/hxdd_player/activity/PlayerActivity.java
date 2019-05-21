package com.edu.hxdd_player.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.aliyun.vodplayerview.activity.AliyunPlayerSkinActivity;
import com.edu.hxdd_player.R;

public class PlayerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        findViewById(R.id.text).setOnClickListener((View v) -> {
            // 视频播放
            Intent intent = new Intent(PlayerActivity.this, AliyunPlayerSkinActivity.class);
            startActivity(intent);
        });
    }
}
