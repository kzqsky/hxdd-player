package com.edu.hxdd_player.activity;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.alivc.player.VcPlayerLog;
import com.aliyun.vodplayer.media.AliyunLocalSource;
import com.aliyun.vodplayerview.utils.FixedToastUtils;
import com.aliyun.vodplayerview.utils.ScreenUtils;
import com.aliyun.vodplayerview.view.choice.AlivcShowMoreDialog;
import com.aliyun.vodplayerview.view.control.ControlView;
import com.aliyun.vodplayerview.view.more.AliyunShowMoreValue;
import com.aliyun.vodplayerview.view.more.ShowMoreView;
import com.aliyun.vodplayerview.view.more.SpeedValue;
import com.aliyun.vodplayerview.widget.AliyunVodPlayerView;
import com.edu.hxdd_player.R;
import com.edu.hxdd_player.adapter.BaseFragmentPagerAdapter;
import com.edu.hxdd_player.fragment.ChapterFragment;
import com.edu.hxdd_player.fragment.JiangyiFragment;
import com.edu.hxdd_player.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;

public class PlayerActivity extends AppCompatActivity {
    AliyunVodPlayerView mAliyunVodPlayerView;
    TextView textView, textView1, textView2, textView3, textView4;
    TimeUtil timeUtil;

    TabLayout tabLayout;
    ViewPager viewPager;
    private List<String> tabTitles = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();
    BaseFragmentPagerAdapter fragmentAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(com.aliyun.vodplayer.R.style.NoActionTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        mAliyunVodPlayerView = findViewById(R.id.player_view);
        textView = findViewById(R.id.text);
        textView1 = findViewById(R.id.text1);
        textView2 = findViewById(R.id.text2);
        textView3 = findViewById(R.id.text3);
        textView4 = findViewById(R.id.text4);
        textView1.setOnClickListener(v -> timeUtil.pause());
        textView2.setOnClickListener(v -> timeUtil.resume());
        textView3.setOnClickListener(v -> timeUtil.stop());
        textView4.setOnClickListener(v -> timeUtil.start());
        mAliyunVodPlayerView = findViewById(R.id.player_view);
        String url = "http://vod-download.cn-shanghai.aliyuncs.com/testvideo/file_download_demo.mp4";
        AliyunLocalSource.AliyunLocalSourceBuilder asb = new AliyunLocalSource.AliyunLocalSourceBuilder();
        asb.setSource(url);
        AliyunLocalSource mLocalSource = asb.build();
        mAliyunVodPlayerView.setLocalSource(mLocalSource);
        mAliyunVodPlayerView.setKeepScreenOn(true);
        mAliyunVodPlayerView.setAutoPlay(true);
        mAliyunVodPlayerView.setOnShowMoreClickListener(new ControlView.OnShowMoreClickListener() {
            @Override
            public void showMore() {
                PlayerActivity.this.showMore(PlayerActivity.this);
            }
        });
        initTab();
        timeUtil = new TimeUtil();
        timeUtil.setCallback((long time) -> runOnUiThread(() ->
                textView.setText(time + "")));
        timeUtil.start();
    }

    private void initTab() {
        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.viewpager);

        tabTitles.add(getString(R.string.tab_1));
        tabTitles.add(getString(R.string.tab_2));

        fragments.add(ChapterFragment.newInstance());
        fragments.add(JiangyiFragment.newInstance());

        fragmentAdapter = new BaseFragmentPagerAdapter(getSupportFragmentManager(),
                tabTitles.toArray(new String[]{}), fragments);
        viewPager.setOffscreenPageLimit(fragments.size());
        viewPager.setAdapter(fragmentAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePlayerViewMode();
        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView.onResume();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updatePlayerViewMode();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView.onStop();
        }
    }

    @Override
    protected void onDestroy() {
        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView.onDestroy();
            mAliyunVodPlayerView = null;
        }
        super.onDestroy();
    }

    private void updatePlayerViewMode() {
        if (mAliyunVodPlayerView != null) {
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                //转为竖屏了。
                //显示状态栏
                //                if (!isStrangePhone()) {
                //                    getSupportActionBar().show();
                //                }

                this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                mAliyunVodPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

                //设置view的布局，宽高之类
                LinearLayout.LayoutParams aliVcVideoViewLayoutParams = (LinearLayout.LayoutParams) mAliyunVodPlayerView
                        .getLayoutParams();
                aliVcVideoViewLayoutParams.height = (int) (ScreenUtils.getWidth(this) * 9.0f / 16);
                aliVcVideoViewLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                //                if (!isStrangePhone()) {
                //                    aliVcVideoViewLayoutParams.topMargin = getSupportActionBar().getHeight();
                //                }

            } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                //转到横屏了。
                //隐藏状态栏
                if (!isStrangePhone()) {
                    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    mAliyunVodPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                }

                //设置view的布局，宽高
                LinearLayout.LayoutParams aliVcVideoViewLayoutParams = (LinearLayout.LayoutParams) mAliyunVodPlayerView
                        .getLayoutParams();
                aliVcVideoViewLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                aliVcVideoViewLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                //                if (!isStrangePhone()) {
                //                    aliVcVideoViewLayoutParams.topMargin = 0;
                //                }
            }
        }
    }

    protected boolean isStrangePhone() {
        boolean strangePhone = "mx5".equalsIgnoreCase(Build.DEVICE)
                || "Redmi Note2".equalsIgnoreCase(Build.DEVICE)
                || "Z00A_1".equalsIgnoreCase(Build.DEVICE)
                || "hwH60-L02".equalsIgnoreCase(Build.DEVICE)
                || "hermes".equalsIgnoreCase(Build.DEVICE)
                || ("V4".equalsIgnoreCase(Build.DEVICE) && "Meitu".equalsIgnoreCase(Build.MANUFACTURER))
                || ("m1metal".equalsIgnoreCase(Build.DEVICE) && "Meizu".equalsIgnoreCase(Build.MANUFACTURER));

        VcPlayerLog.e("lfj1115 ", " Build.Device = " + Build.DEVICE + " , isStrange = " + strangePhone);
        return strangePhone;
    }


    /**
     * 更多设置内容
     */
    private void showMore(final PlayerActivity activity) {
        AlivcShowMoreDialog showMoreDialog = new AlivcShowMoreDialog(activity);
        AliyunShowMoreValue moreValue = new AliyunShowMoreValue();
        moreValue.setSpeed(mAliyunVodPlayerView.getCurrentSpeed());
        moreValue.setVolume(mAliyunVodPlayerView.getCurrentVolume());
        moreValue.setScreenBrightness(mAliyunVodPlayerView.getCurrentScreenBrigtness());

        ShowMoreView showMoreView = new ShowMoreView(activity, moreValue);
        showMoreDialog.setContentView(showMoreView);
        showMoreDialog.show();
        showMoreView.setOnDownloadButtonClickListener(new ShowMoreView.OnDownloadButtonClickListener() {
            @Override
            public void onDownloadClick() {
                // 点击下载
                showMoreDialog.dismiss();
                FixedToastUtils.show(activity, "功能开发中, 敬请期待...");
            }
        });

        showMoreView.setOnScreenCastButtonClickListener(new ShowMoreView.OnScreenCastButtonClickListener() {
            @Override
            public void onScreenCastClick() {
                FixedToastUtils.show(PlayerActivity.this, "功能开发中, 敬请期待...");
            }
        });

        showMoreView.setOnBarrageButtonClickListener(new ShowMoreView.OnBarrageButtonClickListener() {
            @Override
            public void onBarrageClick() {
                FixedToastUtils.show(PlayerActivity.this, "功能开发中, 敬请期待...");
            }
        });

        showMoreView.setOnSpeedCheckedChangedListener(new ShowMoreView.OnSpeedCheckedChangedListener() {
            @Override
            public void onSpeedChanged(RadioGroup group, int checkedId) {
                // 点击速度切换
                if (checkedId == com.aliyun.vodplayer.R.id.rb_speed_normal) {
                    mAliyunVodPlayerView.changeSpeed(SpeedValue.One);
                } else if (checkedId == com.aliyun.vodplayer.R.id.rb_speed_onequartern) {
                    mAliyunVodPlayerView.changeSpeed(SpeedValue.OneQuartern);
                } else if (checkedId == com.aliyun.vodplayer.R.id.rb_speed_onehalf) {
                    mAliyunVodPlayerView.changeSpeed(SpeedValue.OneHalf);
                } else if (checkedId == com.aliyun.vodplayer.R.id.rb_speed_twice) {
                    mAliyunVodPlayerView.changeSpeed(SpeedValue.Twice);
                }

            }
        });

        // 亮度seek
        showMoreView.setOnLightSeekChangeListener(new ShowMoreView.OnLightSeekChangeListener() {
            @Override
            public void onStart(SeekBar seekBar) {

            }

            @Override
            public void onProgress(SeekBar seekBar, int progress, boolean fromUser) {
                mAliyunVodPlayerView.setCurrentScreenBrigtness(progress);
            }

            @Override
            public void onStop(SeekBar seekBar) {

            }
        });

        showMoreView.setOnVoiceSeekChangeListener(new ShowMoreView.OnVoiceSeekChangeListener() {
            @Override
            public void onStart(SeekBar seekBar) {

            }

            @Override
            public void onProgress(SeekBar seekBar, int progress, boolean fromUser) {
                mAliyunVodPlayerView.setCurrentVolume(progress);
            }

            @Override
            public void onStop(SeekBar seekBar) {

            }
        });

    }
}
