package com.edu.hxdd_player.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
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
import com.edu.hxdd_player.bean.BaseBean;
import com.edu.hxdd_player.bean.ClientConfigBean;
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
import com.edu.hxdd_player.utils.ComputeUtils;
import com.edu.hxdd_player.utils.DensityUtils;
import com.edu.hxdd_player.utils.DialogUtils;
import com.edu.hxdd_player.utils.LiveDataBus;
import com.edu.hxdd_player.utils.PhoneInfo;
import com.edu.hxdd_player.utils.StartPlayerUtils;
import com.edu.hxdd_player.utils.TablayoutUtil;
import com.edu.hxdd_player.utils.TimeUtil;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

public class PlayerActivity extends AppCompatActivity implements ExamFragment.ExamFragmentCallback {
    AliyunVodPlayerView mAliyunVodPlayerView;

    TabLayout tabLayout;
    ViewPager viewPager;
    private List<String> tabTitles = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();
    BaseFragmentPagerAdapter fragmentAdapter;

    TimeUtil timeUtil_record, timeUtil_question, timeUtil_face;
    Catalog mCatalog;
    Map<Long, Question> questionMap;
    String learnRecordId = null;

    GetChapter getChapter;
    long recordTime;

    long questionTime;
    ImageView image_logo;

    private String showErrorMessage = "";

    ClientConfigBean clientConfigBean;

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
        getClientConfig();
    }


    private void getClientConfig() {
        ApiUtils.getInstance(this, getChapter.serverUrl).getClientConfig(getChapter.clientCode, new ApiCall<ClientConfigBean>() {
            @Override
            protected void onResult(ClientConfigBean data) {
                clientConfigBean = data;
                initFloatingActionButton(data);
            }

            @Override
            public void onFailure(Call<BaseBean<ClientConfigBean>> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
    }

    private void initFloatingActionButton(ClientConfigBean data) {
        if (data == null)
            return;
        if (data.assessment == 1 || data.correction == 1) {
            findViewById(R.id.multiple_actions).setVisibility(View.VISIBLE);
            if (data.assessment == 1) {//评课
                findViewById(R.id.action_pk).setVisibility(View.VISIBLE);
                findViewById(R.id.action_pk).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(PlayerActivity.this, WebViewActivity.class);
                        intent.putExtra("url", getChapter.serverUrl + "/page/client#/evaluate" + getUrlParamers());
                        intent.putExtra("title", "评课");
                        startActivity(intent);
                    }
                });
            } else {
                findViewById(R.id.action_pk).setVisibility(View.GONE);
            }
            if (data.correction == 1) {//纠错
                findViewById(R.id.action_jc).setVisibility(View.VISIBLE);
                findViewById(R.id.action_jc).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(PlayerActivity.this, WebViewActivity.class);
                        intent.putExtra("url", getChapter.serverUrl + "/page/client#/errorCorrection" + getUrlParamers());
                        intent.putExtra("title", "纠错");
                        startActivity(intent);
                    }
                });
            } else {
                findViewById(R.id.action_jc).setVisibility(View.GONE);
            }
        } else {
            findViewById(R.id.multiple_actions).setVisibility(View.GONE);
        }
    }

    private String getUrlParamers() {
        String appName = PhoneInfo.getAppName(this);
        String appVersion = PhoneInfo.getVerCode(this);
        String phoneModel = PhoneInfo.getDeviceInfo(this);
        String paramers = "?uid=" + getChapter.businessLineCode
                + "&userId=" + getChapter.userId + "&userName=" + getChapter.userName
                + "&clientCode=" + getChapter.clientCode + "&coursewareCode=" + getChapter.coursewareCode
                + "&catalogId=" + getChapter.catalogId +
                "&appName=" + appName + "&appVersion=" + appVersion
                + "&phoneModel=" + phoneModel;
        try {
            paramers = Uri.encode(paramers, "-![.:/,%?&=]");
        } catch (Exception e) {

        }
        return paramers;
//        return "";
    }

    private void getIntentData() {
        getChapter = (GetChapter) getIntent().getSerializableExtra("data");
        if (TextUtils.isEmpty(getChapter.logoUrl)) {
            image_logo.setVisibility(View.GONE);
        } else {
            image_logo.setVisibility(View.VISIBLE);

            Glide.with(PlayerActivity.this).load(getChapter.logoUrl).into(image_logo);
            image_logo.setAlpha(0.5f);

            image_logo.setAlpha(getChapter.logoAlpha);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) image_logo.getLayoutParams();
            layoutParams.width = DensityUtils.dp2px(this, getChapter.logoWidth);
            layoutParams.height = DensityUtils.dp2px(this, getChapter.logoHeight);

            if (getChapter.logoPosition == 1) {
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
            } else if (getChapter.logoPosition == 2) {
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, R.id.hxdd_player_player_view);
            } else if (getChapter.logoPosition == 3) {
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
            } else if (getChapter.logoPosition == 4) {
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
                layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
            } else if (getChapter.logoPosition == 5) {
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            } else if (getChapter.logoPosition == 6) {
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
                layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
            } else if (getChapter.logoPosition == 7) {
                layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.hxdd_player_player_view);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
            } else if (getChapter.logoPosition == 8) {
                layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.hxdd_player_player_view);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            } else if (getChapter.logoPosition == 9) {
                layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.hxdd_player_player_view);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
            }

            image_logo.setLayoutParams(layoutParams);
        }
    }

    private void initPlayer() {
        mAliyunVodPlayerView.setAutoPlay(true);
        mAliyunVodPlayerView.setOnScreenBrightness(new MyOnScreenBrightnessListener(this));
        PlayerConfig playerConfig = mAliyunVodPlayerView.getPlayerConfig();
        playerConfig.mNetworkRetryCount = 5;
        PackageManager manager = this.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            if (info != null)//设置Referrer
                playerConfig.mReferrer = "https://" + info.packageName + ".android";
        } catch (Exception e) {
            e.printStackTrace();
        }
        mAliyunVodPlayerView.setPlayerConfig(playerConfig);
    }

    private void initTab() {
        tabLayout = findViewById(R.id.hxdd_player_tabs);
        viewPager = findViewById(R.id.hxdd_player_viewpager);

        if (StartPlayerUtils.getCacheMode()) {
            tabTitles.add(getString(R.string.tab_3));
            fragments.add(DownLoadFragment.newInstance(getChapter));
        } else {
            tabTitles.add(getString(R.string.tab_1));
            if (StartPlayerUtils.getHandOut())
                tabTitles.add(getString(R.string.tab_2));
            if (StartPlayerUtils.getHasDownload())
                tabTitles.add(getString(R.string.tab_3));

            fragments.add(ChapterFragment.newInstance(getChapter));
            if (StartPlayerUtils.getHandOut())
                fragments.add(JiangyiFragment.newInstance());
            if (StartPlayerUtils.getHasDownload())
                fragments.add(DownLoadFragment.newInstance(getChapter));
        }
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
                    if (timeUtil_record != null)
                        timeUtil_record.stop();
                    videoRecord(recordTime);
                    recordTime = 0;
                    if (timeUtil_record != null)
                        timeUtil_record.start();
                    learnRecordId = null;
                    mCatalog = catalog;
                    setMedia(catalog);
                    getQuestionMap();
                });
        LiveDataBus.get().with("CacheMode", String.class).observe(this, s -> {
            UrlSource urlSource = new UrlSource();
            urlSource.setUri(s);
            urlSource.setTitle("");
            mAliyunVodPlayerView.setLocalSource(urlSource);
        });
        LiveDataBus.get().with("stop", String.class).observe(this, s -> {
            videoPause();
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
        if (media.serverType != null && media.serverType.toLowerCase().contains("aliyuncode")) {
            if (TextUtils.isEmpty(catalog.savePath)) { //是否本地缓存
                VidAuth vidAuth = new VidAuth();
//                vidAuth.setAuthTimeout(600);超时时间
                vidAuth.setVid(media.mediaSource);
                vidAuth.setPlayAuth(media.playAuth);
                if (!TextUtils.isEmpty(getChapter.defaultQuality))
                    vidAuth.setQuality(getChapter.defaultQuality, false);
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
            String url = media.mediaSource;
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
            if (timeUtil_face != null)
                timeUtil_face.resume();
        }
        if (tabLayout != null) {
            //我们在这里对TabLayout的宽度进行修改。。数值越大表示宽度越小。
            tabLayout.post(() -> TablayoutUtil.setIndicator(tabLayout, (int) getResources().getDimension(R.dimen.tablayout_textsize) * 4));
        }

        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView.setScreenBrightness(BrightnessDialog.getActivityBrightness(this));
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
        if (timeUtil_face != null) {
            timeUtil_face.stop();
        }
        mCatalog = null;
        getChapter = null;
        LiveDataBus.get().clear();
        ApiUtils.getInstance(this, "").clear();
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
                initFloatingActionButton(clientConfigBean);
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
                findViewById(R.id.multiple_actions).setVisibility(View.GONE);
                findViewById(R.id.action_pk).setVisibility(View.GONE);
                findViewById(R.id.action_jc).setVisibility(View.GONE);
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
            if (time % 60 == 0) {
                videoRecord(time);
            }
        });

        if (StartPlayerUtils.getCallBackTime() > 0) {
            timeUtil_face = new TimeUtil();
            timeUtil_face.setCallback(time -> {
                if (time % StartPlayerUtils.getCallBackTime() == 0) {
                    if (StartPlayerUtils.timeCallBack != null)
                        runOnUiThread(() -> StartPlayerUtils.timeCallBack.onTime());
                }
            });
        }

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
            if (timeUtil_face != null && !timeUtil_face.isStart())
                timeUtil_face.start();
            if (mCatalog != null && mCatalog.learnRecord != null && mCatalog.learnRecord.lastTime > 0) {
                mAliyunVodPlayerView.seekTo((int) (mCatalog.learnRecord.lastTime * 1000));
            }
        });
        mAliyunVodPlayerView.setOnSeekCompleteListener(() -> {
            timeUtil_record.resume();
            timeUtil_question.resume();
            if (timeUtil_face != null)
                timeUtil_face.resume();
        });
        //播放状态
        mAliyunVodPlayerView.setOnPlayStateBtnClickListener(playerState -> {
            if (playerState == IPlayer.started) {
                Log.i("test", "暂停:");
                timeUtil_record.pause();
                timeUtil_question.pause();
                if (timeUtil_face != null)
                    timeUtil_face.pause();
            } else if (playerState == IPlayer.paused) {
                Log.i("test", "播放:");
                timeUtil_record.resume();
                timeUtil_question.resume();
                if (timeUtil_face != null)
                    timeUtil_face.resume();
            }
        });
    }

    private void videoPause() {
        timeUtil_question.pause();
        timeUtil_record.pause();
        if (timeUtil_face != null)
            timeUtil_face.pause();
        runOnUiThread(() -> mAliyunVodPlayerView.pause());
    }

    private void videoStart() {
        timeUtil_question.resume();
        timeUtil_record.resume();
        if (timeUtil_face != null)
            timeUtil_face.resume();
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
        if (mCatalog == null) {
            return;
        }
        long videoTime = mCatalog.mediaDuration;
        long lastTime = mAliyunVodPlayerView.getCurrentPosition() / 1000;
        PutLearnRecords putLearnRecords =
                PutLearnRecords.getRecord(learnRecordId, getChapter, mCatalog.id, videoTime, lastTime, accumulativeTime);
        uploadRecord(putLearnRecords);
    }

    private void uploadRecord(PutLearnRecords putLearnRecords) {
//        if (TextUtils.isEmpty(putLearnRecords.learnRecordId)) {
//            if (timeUtil_record != null)
//                timeUtil_record.stop();
//            putLearnRecords.accumulativeTime = 60;
//            recordTime = 60;
//            if (timeUtil_record != null)
//                timeUtil_record.start();
//        }
        ApiUtils.getInstance(this, getChapter.serverUrl).learnRecord(putLearnRecords, new ApiCall<Object>() {
            @Override
            protected void onResult(Object object) {
                LearnRecordBean data;
                String jsonString = "";
                try {
                    jsonString = new Gson().toJson(object);
                    data = new Gson().fromJson(jsonString, LearnRecordBean.class);
                } catch (Exception e) {
                    return;
                }
                if (data != null && mCatalog != null && mCatalog.id !=
                        null && data.catalogId.equals(mCatalog.id)) {
                    learnRecordId = data.learnRecordId;
//                ToastUtils.showLong("弹题记录发送完毕");
                }
                if (data != null) {
                    if (!TextUtils.isEmpty(data.backUrl) && getChapter != null) {
                        ApiUtils.getInstance(PlayerActivity.this, getChapter.serverUrl).callBackUrl(data.backUrl, jsonString);
                    }
                }
            }

            @Override
            public void onApiFailure(String message) {
                super.onApiFailure(message);
                if (!message.equals(showErrorMessage)) {
                    showErrorMessage = message;
                    DialogUtils.showDialog(PlayerActivity.this, message);
                }
            }
        });

    }


    /**
     * 设置屏幕亮度
     */
    private void setWindowBrightness(int brightness) {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = ComputeUtils.div(brightness, 100, 2);
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
