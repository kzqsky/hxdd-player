package com.edu.hxdd_player.activity;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.aliyun.player.IPlayer;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hxdd_player_activity_player);
        mAliyunVodPlayerView = findViewById(R.id.hxdd_player_player_view);
        initPlayer();
        getIntentData();
        initTab();
        initLiveData();
        initVideoBack();
        initTimer();
    }


    private void getIntentData() {
        getChapter = (GetChapter) getIntent().getSerializableExtra("data");
    }

    private void initPlayer() {
        mAliyunVodPlayerView.setAutoPlay(true);
        mAliyunVodPlayerView.setOnScreenBrightness(new MyOnScreenBrightnessListener(this));
//        mAliyunVodPlayerView.
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

    private void setMedia(Catalog catalog) {
        Media media = catalog.media;
        if ("aliyunCode".equals(media.serverType)) {
            if (TextUtils.isEmpty(catalog.savePath)) { //是否本地缓存
                VidAuth vidAuth = new VidAuth();
                vidAuth.setVid(media.mediaSource);
                vidAuth.setPlayAuth(media.playAuth);
                mAliyunVodPlayerView.setAuthInfo(vidAuth);
            } else {
                //播放本地缓存视频
                UrlSource urlSource = new UrlSource();
                urlSource.setUri(catalog.savePath);
                urlSource.setTitle(catalog.title);
                mAliyunVodPlayerView.setLocalSource(urlSource);
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
        });
        //播放错误
        mAliyunVodPlayerView.setOnErrorListener(errorInfo -> {

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
        mAliyunVodPlayerView.pause();
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
        PutLearnRecords putLearnRecords =
                PutLearnRecords.getQuestionRecord(learnRecordId, getChapter, mCatalog.id, question.isPass, question.questionId, question.examinePoint);
        uploadRecord(putLearnRecords);
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
        ApiUtils.getInstance(this).learnRecord(putLearnRecords, new ApiCall<LearnRecordBean>() {
            @Override
            protected void onResult(LearnRecordBean data) {
                if (data.catalogId.equals(mCatalog.id))
                    learnRecordId = data.learnRecordId;
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
