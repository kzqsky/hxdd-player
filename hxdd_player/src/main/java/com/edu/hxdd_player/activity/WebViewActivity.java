package com.edu.hxdd_player.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.edu.hxdd_player.R;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.DefaultWebClient;

public class WebViewActivity extends AppCompatActivity {

    TextView tv_title;
    AgentWeb mAgentWeb;
    LinearLayout layout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hxdd_player_webview);
        layout =findViewById(R.id.layout);
        tv_title = findViewById(R.id.tv_title);

        findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_title.setText(getIntent().getStringExtra("title"));
        //加载网页
        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(layout, new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)
                .createAgentWeb()
                .ready().go( getIntent().getStringExtra("url"));

    }


    @Override
    public void onBackPressed() {
        if (mAgentWeb.getWebCreator().getWebView().canGoBack()) {
            mAgentWeb.getWebCreator().getWebView().goBack();
        } else {
            finish();
        }
    }


}
