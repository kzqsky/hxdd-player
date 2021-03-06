package com.edu.hxdd_player.activity;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.aliyun.player.IPlayer;
import com.aliyun.player.nativeclass.PlayerConfig;
import com.aliyun.player.source.UrlSource;
import com.aliyun.player.source.VidAuth;
import com.aliyun.vodplayerview.utils.FixedToastUtils;
import com.aliyun.vodplayerview.utils.ScreenUtils;
import com.aliyun.vodplayerview.view.choice.AlivcShowMoreDialog;
import com.aliyun.vodplayerview.view.gesturedialog.BrightnessDialog;
import com.aliyun.vodplayerview.view.more.AliyunShowMoreValue;
import com.aliyun.vodplayerview.view.more.ShowMoreView;
import com.aliyun.vodplayerview.view.more.SpeedValue;
import com.aliyun.vodplayerview.widget.AliyunVodPlayerView;
import com.bumptech.glide.Glide;
import com.edu.hxdd_player.R;
import com.edu.hxdd_player.adapter.BaseFragmentPagerAdapter;
import com.edu.hxdd_player.api.ApiUtils;
import com.edu.hxdd_player.api.net.ApiCall;
import com.edu.hxdd_player.bean.LearnRecordBean;
import com.edu.hxdd_player.bean.media.Catalog;
import com.edu.hxdd_player.bean.media.Media;
import com.edu.hxdd_player.bean.media.Question;
import com.edu.hxdd_player.bean.parameters.GetChapter;
import com.edu.hxdd_player.bean.parameters.PutLearnRecords;
import com.edu.hxdd_player.fragment.ChapterFragment;
import com.edu.hxdd_player.fragment.DownLoadFragment;
import com.edu.hxdd_player.fragment.ExamFragment;
import com.edu.hxdd_player.fragment.JiangyiFragment;
import com.edu.hxdd_player.utils.LiveDataBus;
import com.edu.hxdd_player.utils.StartPlayerUtils;
import com.edu.hxdd_player.utils.TablayoutUtil;
import com.edu.hxdd_player.utils.TimeUtil;
import com.google.android.material.tabs.TabLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerActivity extends AppCompatActivity implements ExamFragment.ExamFragmentCallback {
    AliyunVodPlayerView mAliyunVodPlayerView;

    TabLayout tabLayout;
    ViewPager viewPager;
    private List<String> tabTitles = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();
    BaseFragmentPagerAdapter fragmentAdapter;

    TimeUtil timeUtil_record, timeUtil_question;
    Catalog mCatalog;
    Map<Long, Question> questionMap;
    String learnRecordId = null;

    GetChapter getChapter;
    long recordTime;

    long questionTime;
    ImageView image_logo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hxdd_player_activity_player);
        mAliyunVodPlayerView = findViewById(R.id.hxdd_player_player_view);
        image_logo = findViewById(R.id.image_logo);
        initPlayer();
        getIntentData();
        initTab();
        initLiveData();
        initVideoBack();
        initTimer();
    }


    private void getIntentData() {
        getChapter = (GetChapter) getIntent().getSerializableExtra("data");
        if (TextUtils.isEmpty(getChapter.logoUrl)) {
            image_logo.setVisibility(View.GONE);
        } else {
            image_logo.setVisibility(View.VISIBLE);

            Glide.with(PlayerActivity.this).load(getChapter.logoUrl).into(image_logo);
            image_logo.setAlpha(0.5f);
        }
    }

    private void initPlayer() {
        mAliyunVodPlayerView.setAutoPlay(true);
        mAliyunVodPlayerView.setOnScreenBrightness(new MyOnScreenBrightnessListener(this));
        PlayerConfig playerConfig = mAliyunVodPlayerView.getPlayerConfig();
        playerConfig.mNetworkRetryCount = 5;
        mAliyunVodPlayerView.setPlayerConfig(playerConfig);
    }

    private void initTab() {
        tabLayout = findViewById(R.id.hxdd_player_tabs);
        viewPager = findViewById(R.id.hxdd_player_viewpager);

        tabTitles.add(getString(R.string.tab_1));
        tabTitles.add(getString(R.string.tab_2));
        if (StartPlayerUtils.getHasDownload())
            tabTitles.add(getString(R.string.tab_3));

        fragments.add(ChapterFragment.newInstance(getChapter));
        fragments.add(JiangyiFragment.newInstance());
        if (StartPlayerUtils.getHasDownload())
            fragments.add(DownLoadFragment.newInstance(getChapter));

        fragmentAdapter = new BaseFragmentPagerAdapter(getSupportFragmentManager(),
                tabTitles.toArray(new String[]{}), fragments);
        viewPager.setOffscreenPageLimit(fragments.size());
        viewPager.setAdapter(fragmentAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(StartPlayerUtils.getColorPrimary());
        tabLayout.setTabTextColors(getResources().getColor(R.color.text), StartPlayerUtils.getColorPrimary());
    }

    private void initLiveData() {
        LiveDataBus.get()
                .with("Catalog", Catalog.class)
                .observe(this, catalog -> {
                    videoRecord(recordTime);
                    recordTime = 0;
                    learnRecordId = null;
                    mCatalog = catalog;
                    setMedia(catalog);
                    getQuestionMap();
                });
    }

    private void getQuestionMap() {
        if (mCatalog == null || mCatalog.questions == null)
            return;
        questionMap = new HashMap<>();
        for (Question question : mCatalog.questions) {
            questionMap.put(question.mediaTime, question);
        }
        mAliyunVodPlayerView.setTimePointList(new ArrayList<>(questionMap.keySet()));
    }

    //    boolean isfirst = true;
    private void setMedia(Catalog catalog) {
        Media media = catalog.media;
        if ("aliyunCode".equals(media.serverType)) {
            if (TextUtils.isEmpty(catalog.savePath)) { //是否本地缓存
                VidAuth vidAuth = new VidAuth();
                vidAuth.setVid(media.mediaSource);
                vidAuth.setPlayAuth(media.playAuth);
//                if (isfirst) {
//                    vidAuth.setPlayAuth("eyJTZWN1cml0eVRva2VuIjoiQ0FJUzN3SjFxNkZ0NUIyeWZTaklyNVdER3ZLR2d1ZGpnb1M0VGxQZmpYSVphOFJEMXBMQ29UejJJSHBOZTNocUIrMGZzUGt3bEdsVTZmZ2Nsck1xRjg4YkhCV1lONVFwdHMwUHIxNzlKcExGc3QySjZyOEpqc1Z3NTl3bXBWaXBzdlhKYXNEVkVma3VFNVhFTWlJNS8wMGU2TC8rY2lyWVhEN0JHSmFWaUpsaFE4MEtWdzJqRjFSdkQ4dFhJUTBRazYxOUszemRaOW1nTGlidWkzdnhDa1J2MkhCaWptOHR4cW1qL015UTV4MzFpMXYweStCM3dZSHRPY3FjYThCOU1ZMVdUc3Uxdm9oemFyR1Q2Q3BaK2psTStxQVU2cWxZNG1YcnM5cUhFa0ZOd0JpWFNaMjJsT2RpTndoa2ZLTTNOcmRacGZ6bjc1MUN0L2ZVaXA3OHhtUW1YNGdYY1Z5R0ZkMzhtcE9aUXJ6eGFvWmdLZStxQVJtWGpJRFRiS3VTbWhnL2ZIY1dPRGxOZjljY01YSnFBWFF1TUdxQWMvRDJvZzZYTzFuK0ZQamNqUDVvajRBSjVsSHA3TWVNR1YrRGVMeVF5aDBFSWFVN2EwNDQxTUtpUXVranBzUWFnQUdXajBrcVQxaDh0YXBLVDZWeXdEcTc2b0RXMzhNQld5NXIza1RNelk5ZXZKMFRlVi9McXlPSEtDOTFUTzNBVm1KVytyblA4aXJab0ZLNytBV0dtejliKzBMck1TZXEyeEtLTlZ3Q2lmb0RTZU51S0hQN1Fud0thbFlsQjJyK011emVycHdNTHY3UFkzcjNLaG5qYUtzcCtNYUV0WGZxREZDblBYUnhqeVlnUlE9PSIsIkF1dGhJbmZvIjoie1wiQ0lcIjpcIkJ6YU0vR3lEUVNQYXNwV0FxWmNWZml3NGxGbW5DUXpnSmQwTzhrdzQzTFBZdW5ZMklUcnBERVhFaFB6a2Vub0dYQnBmRVhjQzl0ejkwRmVjNTIwQm1DWU1Eckx3bVhMWUx0ajhzMVAwaE80PVwiLFwiQ2FsbGVyXCI6XCJxeVJxd0N4eG93WU55SWlTRVkyNitNMG9ITXlqOEViSjhCN3NOaGFLSkIwPVwiLFwiRXhwaXJlVGltZVwiOlwiMjAyMC0wOS0wOFQwMjoyMzoxOVpcIixcIk1lZGlhSWRcIjpcIjE2ZmZmMWQ3MWVhZjQxYTE4MDk2MWRiZTIzY2Q2NzI4XCIsXCJQbGF5RG9tYWluXCI6XCJna3N0cmVhbS5lZHUtZWR1LmNvbS5jblwiLFwiU2lnbmF0dXJlXCI6XCJhOE1UZHd0QVdHWEI0VHFIcU5UMDNGOW5leFU9XCJ9IiwiVmlkZW9NZXRhIjp7IlN0YXR1cyI6Ik5vcm1hbCIsIlZpZGVvSWQiOiIxNmZmZjFkNzFlYWY0MWExODA5NjFkYmUyM2NkNjcyOCIsIlRpdGxlIjoiMTYxMF8wNzk0OF9jaDAxLm1wNCIsIkNvdmVyVVJMIjoiaHR0cHM6Ly9na3N0cmVhbS5lZHUtZWR1LmNvbS5jbi8xNmZmZjFkNzFlYWY0MWExODA5NjFkYmUyM2NkNjcyOC9jb3ZlcnMvNmQ0ZjFjZWE3ZWNlNGZkNThjODJhOWVhYWVlZmFmNTUtMDAwMDUuanBnIiwiRHVyYXRpb24iOjYxOS4wODJ9LCJBY2Nlc3NLZXlJZCI6IlNUUy5OVjZRSDJvOEI1RlNMdW5pckxnS285V2lDIiwiUGxheURvbWFpbiI6Imdrc3RyZWFtLmVkdS1lZHUuY29tLmNuIiwiQWNjZXNzS2V5U2VjcmV0IjoiRFRmRzJLQm9tZjZHempnMjhNUVhtQnQ4b0NrN3F3QnVjcDdyVjZZYVBUZmIiLCJSZWdpb24iOiJjbi1zaGFuZ2hhaSIsIkN1c3RvbWVySWQiOjE0ODYxMTQyNzEwMTA2NTR9");
//                    vidAuth.setVid("16fff1d71eaf41a180961dbe23cd6728");
//                    isfirst = false;
//                }
                mAliyunVodPlayerView.setAuthInfo(vidAuth);
                LiveDataBus.get().with("urlVideo").setValue(null);
            } else {
                //播放本地缓存视频
                UrlSource urlSource = new UrlSource();
                urlSource.setUri(catalog.savePath);
                urlSource.setTitle(catalog.title);
                mAliyunVodPlayerView.setLocalSource(urlSource);
                LiveDataBus.get().with("localVideo").setValue(null);
            }
        } else {
            String url = media.serverCluster + "/" + media.mediaSource + "_sd.mp4";
            UrlSource urlSource = new UrlSource();
            urlSource.setUri(url);
            mAliyunVodPlayerView.setLocalSource(urlSource);
        }

        mAliyunVodPlayerView.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePlayerViewMode();
        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView.onResume();

            timeUtil_question.resume();
            timeUtil_record.resume();
        }
        if (tabLayout != null) {
            //我们在这里对TabLayout的宽度进行修改。。数值越大表示宽度越小。
            tabLayout.post(() -> TablayoutUtil.setIndicator(tabLayout, (int) getResources().getDimension(R.dimen.tablayout_textsize) * 4));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoPause();
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
        videoRecord(recordTime);
        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView.onDestroy();
            mAliyunVodPlayerView = null;
        }
        if (timeUtil_question != null) {
            timeUtil_question.stop();
        }

        if (timeUtil_record != null) {
            timeUtil_record.stop();
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
                RelativeLayout.LayoutParams aliVcVideoViewLayoutParams = (RelativeLayout.LayoutParams) mAliyunVodPlayerView
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
                RelativeLayout.LayoutParams aliVcVideoViewLayoutParams = (RelativeLayout.LayoutParams) mAliyunVodPlayerView
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


        return strangePhone;
    }


    /**
     * 更多设置内容
     */
    private void showMore(final PlayerActivity activity) {
        AlivcShowMoreDialog showMoreDialog = new AlivcShowMoreDialog(activity);
        AliyunShowMoreValue moreValue = new AliyunShowMoreValue();
        moreValue.setSpeed(mAliyunVodPlayerView.getCurrentSpeed());
        moreValue.setVolume((int) mAliyunVodPlayerView.getCurrentVolume());
        moreValue.setScreenBrightness(BrightnessDialog.getActivityBrightness(this));

        ShowMoreView showMoreView = new ShowMoreView(activity, moreValue);
        showMoreDialog.setContentView(showMoreView);
        showMoreDialog.show();
        showMoreView.setOnDownloadButtonClickListener(() -> {
            // 点击下载
            showMoreDialog.dismiss();
            FixedToastUtils.show(activity, "功能开发中, 敬请期待...");
        });

        showMoreView.setOnScreenCastButtonClickListener(() -> FixedToastUtils.show(PlayerActivity.this, "功能开发中, 敬请期待..."));

        showMoreView.setOnBarrageButtonClickListener(() -> FixedToastUtils.show(PlayerActivity.this, "功能开发中, 敬请期待..."));

        showMoreView.setOnSpeedCheckedChangedListener((group, checkedId) -> {
            // 点击速度切换
            if (checkedId == R.id.rb_speed_normal) {
                mAliyunVodPlayerView.changeSpeed(SpeedValue.One);
                timeUtil_question.setTimeInterval(TimeUtil.DEFAULT);
            } else if (checkedId == R.id.rb_speed_onequartern) {
                mAliyunVodPlayerView.changeSpeed(SpeedValue.OneQuartern);
                timeUtil_question.setTimeInterval(TimeUtil.ONE_HALF);
            } else if (checkedId == R.id.rb_speed_onehalf) {
                mAliyunVodPlayerView.changeSpeed(SpeedValue.OneHalf);
                timeUtil_question.setTimeInterval(TimeUtil.ONE_SEVEN_FIVE);
            } else if (checkedId == R.id.rb_speed_twice) {
                mAliyunVodPlayerView.changeSpeed(SpeedValue.Twice);
                timeUtil_question.setTimeInterval(TimeUtil.TWO);
            }

        });

        /**
         * 初始化亮度
         */
        if (mAliyunVodPlayerView != null) {
            showMoreView.setBrightness(mAliyunVodPlayerView.getScreenBrightness());
        }
//        // 亮度seek
//        showMoreView.setOnLightSeekChangeListener(new ShowMoreView.OnLightSeekChangeListener() {
//            @Override
//            public void onStart(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onProgress(SeekBar seekBar, int progress, boolean fromUser) {
//                setWindowBrightness(progress);
//                if (mAliyunVodPlayerView != null) {
//                    mAliyunVodPlayerView.setScreenBrightness(progress);
//                }
//            }
//
//            @Override
//            public void onStop(SeekBar seekBar) {
//
//            }
//        });
//        /**
//         * 初始化音量
//         */
//        if (mAliyunVodPlayerView != null) {
//            showMoreView.setVoiceVolume(mAliyunVodPlayerView.getCurrentVolume());
//        }
//        //声音seek
//        showMoreView.setOnVoiceSeekChangeListener(new ShowMoreView.OnVoiceSeekChangeListener() {
//            @Override
//            public void onStart(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onProgress(SeekBar seekBar, int progress, boolean fromUser) {
//                mAliyunVodPlayerView.setCurrentVolume(progress / 100.00f);
//            }
//
//            @Override
//            public void onStop(SeekBar seekBar) {
//
//            }
//        });

    }

    private void initTimer() {
        timeUtil_question = new TimeUtil();
        timeUtil_question.setCallback(time -> {
//            Log.i("test", "timeUtil_question:" + time);
            long currentPosition = mAliyunVodPlayerView.getCurrentPosition();
            long ss = currentPosition / 1000;
//            Log.e("test", "currentPosition:" + currentPosition);
//            Log.e("test", "ss:" + ss + "---questionTime:" + questionTime);
            if (ss == questionTime) {
                return;
            }
            questionTime = ss;
            if (questionMap != null && questionMap.containsKey(ss)) {
                videoPause();
                showQuestion(questionMap.get(ss));
            }
        });

        timeUtil_record = new TimeUtil();
        timeUtil_record.setCallback(time -> {
            recordTime = time;
//            Log.e("test", "timeUtil_record: " + time);
            if (StartPlayerUtils.getCallBackTime() != 0 && time % StartPlayerUtils.getCallBackTime() == 0) {
//                Log.e("test", "StartPlayerUtils.getCallBackTime() == 0");
                if (StartPlayerUtils.timeCallBack != null)
                    runOnUiThread(() -> StartPlayerUtils.timeCallBack.onTime());
            }

            if (time % 60 == 0) {
                videoRecord(time);
            }
        });

    }

    private void initVideoBack() {
        mAliyunVodPlayerView.setKeepScreenOn(true);
//        mAliyunVodPlayerView.setAutoPlay(true);
        mAliyunVodPlayerView.setOnShowMoreClickListener(() -> PlayerActivity.this.showMore(PlayerActivity.this));

        //播放完成
        mAliyunVodPlayerView.setOnCompletionListener(() -> {
            timeUtil_record.stop();
            LiveDataBus.get().with("playNext").setValue(null);
        });
        //播放错误
        mAliyunVodPlayerView.setOnErrorListener(errorInfo -> {
            LiveDataBus.get().with("refreshVid").setValue(null);
        });
        //播放停止
        mAliyunVodPlayerView.setOnStoppedListener(() -> {
            Log.i("test", "播放停止:");
        });
        //开始播放
        mAliyunVodPlayerView.setOnFirstFrameStartListener(() -> {
            Log.i("test", "开始播放:");
            timeUtil_record.start();
            timeUtil_question.start();
            if (mCatalog.learnRecord != null && mCatalog.learnRecord.lastTime > 0) {
                mAliyunVodPlayerView.seekTo((int) (mCatalog.learnRecord.lastTime * 1000));
            }
        });
        mAliyunVodPlayerView.setOnSeekCompleteListener(() -> {
            timeUtil_record.resume();
            timeUtil_question.resume();
        });
        //播放状态
        mAliyunVodPlayerView.setOnPlayStateBtnClickListener(playerState -> {
            if (playerState == IPlayer.started) {
                Log.i("test", "暂停:");
                timeUtil_record.pause();
                timeUtil_question.pause();
            } else if (playerState == IPlayer.paused) {
                Log.i("test", "播放:");
                timeUtil_record.resume();
                timeUtil_question.resume();
            }
        });
    }

    private void videoPause() {
        timeUtil_question.pause();
        timeUtil_record.pause();
        runOnUiThread(() -> mAliyunVodPlayerView.pause());
    }

    private void videoStart() {
        timeUtil_question.resume();
        timeUtil_record.resume();
        mAliyunVodPlayerView.start();
    }

    private void showQuestion(Question question) {
        ExamFragment examFragment = ExamFragment.newInstance(question.toString());
        examFragment.show(getSupportFragmentManager(), "exam");
    }

    @Override
    public void commit(Question question) {

    }

    @Override
    public void over(Question question) {
        questionResult(question);
        videoStart();
    }

    @Override
    public void cancel(Question question) {
        questionResult(question);
        videoStart();
    }

    private void questionResult(Question question) {
        long videoTime = 0l;
        if (mCatalog != null)
            videoTime = mCatalog.mediaDuration;
        long lastTime = mAliyunVodPlayerView.getCurrentPosition() / 1000;

        PutLearnRecords putLearnRecords =
                PutLearnRecords.getQuestionRecord(learnRecordId, getChapter, mCatalog.id, question.isPass, question.questionId, question.examinePoint, videoTime, lastTime, recordTime);
        uploadRecord(putLearnRecords);
//        PutLearnRecords putLearnRecords =
//                PutLearnRecords.getQuestionRecord(learnRecordId, getChapter, mCatalog.id, question.isPass, question.questionId, question.examinePoint);
//        uploadRecord(putLearnRecords);

//        ToastUtils.showLong("开始发送弹题记录:"+putLearnRecords.toString());
    }

    private void videoRecord(long accumulativeTime) {
        if (mCatalog == null)
            return;
        long videoTime = mCatalog.mediaDuration;
        long lastTime = mAliyunVodPlayerView.getCurrentPosition() / 1000;
        PutLearnRecords putLearnRecords =
                PutLearnRecords.getRecord(learnRecordId, getChapter, mCatalog.id, videoTime, lastTime, accumulativeTime);
        uploadRecord(putLearnRecords);
    }

    private void uploadRecord(PutLearnRecords putLearnRecords) {

        ApiUtils.getInstance(this, getChapter.serverUrl).learnRecord(putLearnRecords, new ApiCall<LearnRecordBean>() {
            @Override
            protected void onResult(LearnRecordBean data) {
                if (data.catalogId.equals(mCatalog.id))
                    learnRecordId = data.learnRecordId;
//                ToastUtils.showLong("弹题记录发送完毕");
            }
        });

    }

    /**
     * 设置屏幕亮度
     */
    private void setWindowBrightness(int brightness) {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = brightness / 255.0f;
        window.setAttributes(lp);
    }

    private static class MyOnScreenBrightnessListener implements AliyunVodPlayerView.OnScreenBrightnessListener {
        private WeakReference<PlayerActivity> weakReference;

        public MyOnScreenBrightnessListener(PlayerActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onScreenBrightness(int brightness) {
            PlayerActivity aliyunPlayerSkinActivity = weakReference.get();
            if (aliyunPlayerSkinActivity != null) {
                aliyunPlayerSkinActivity.setWindowBrightness(brightness);
                if (aliyunPlayerSkinActivity.mAliyunVodPlayerView != null) {
                    aliyunPlayerSkinActivity.mAliyunVodPlayerView.setScreenBrightness(brightness);
                }
            }
        }
    }

}
