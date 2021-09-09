package com.edu.hxdd_player.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.aliyun.player.bean.ErrorCode;
import com.aliyun.player.source.VidAuth;
import com.aliyun.private_service.PrivateService;
import com.aliyun.vodplayerview.utils.Common;
import com.aliyun.vodplayerview.utils.FixedToastUtils;
import com.aliyun.vodplayerview.utils.database.DatabaseManager;
import com.aliyun.vodplayerview.utils.database.LoadDbDatasListener;
import com.aliyun.vodplayerview.utils.download.AliyunDownloadInfoListener;
import com.aliyun.vodplayerview.utils.download.AliyunDownloadManager;
import com.aliyun.vodplayerview.utils.download.AliyunDownloadMediaInfo;
import com.aliyun.vodplayerview.view.download.AlivcDownloadMediaInfo;
import com.aliyun.vodplayerview.view.download.DownloadDataProvider;
import com.aliyun.vodplayerview.view.download.DownloadView;
import com.edu.hxdd_player.R;
import com.edu.hxdd_player.api.ApiUtils;
import com.edu.hxdd_player.api.net.ApiCall;
import com.edu.hxdd_player.bean.ChapterBean;
import com.edu.hxdd_player.bean.media.Catalog;
import com.edu.hxdd_player.bean.parameters.GetChapter;
import com.edu.hxdd_player.utils.LiveDataBus;
import com.edu.hxdd_player.utils.StartPlayerUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class DownLoadFragment extends Fragment {
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"
    };
    private static final int REQUEST_EXTERNAL_STORAGE = 1;


    private DownloadView downloadView;
    private DownloadDataProvider downloadDataProvider;
    private AliyunDownloadManager downloadManager;
    private Common commenUtils;
    private static String preparedVid;

    private RelativeLayout rlDownloadManagerContent;
    /**
     * 判断是否在下载中
     */
    private boolean mDownloadInPrepare = false;
    private AliyunDownloadMediaInfo aliyunDownloadMediaInfo;
    GetChapter getChapter;
    private AliyunDownloadMediaInfo downloadMediaInfo;

    List<ChapterBean> chapterList;

    public static DownLoadFragment newInstance(GetChapter getChapter) {
        DownLoadFragment fragment = new DownLoadFragment();
        Bundle args = new Bundle();
        args.putSerializable("getChapter", getChapter);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            getChapter = (GetChapter) args.getSerializable("getChapter");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hxdd_player_fragment_download, container, false);
        initView(view);
        initLiveData();
        initAliDownload();
        return view;
    }

    private void initView(View view) {
        downloadView = view.findViewById(R.id.download_view);
        rlDownloadManagerContent = view.findViewById(R.id.rl_download_manager_content);
    }

    private void initLiveData() {
//        LiveDataBus.get()
//                .with("download", GetChapter.class)
//                .observe(this, chapter -> {
//                    getChapter = chapter;
//                    getMedia(chapter, true);
//                });
        LiveDataBus.get().with("chatper", Object.class).observe(this, list -> {
            if (!StartPlayerUtils.getCacheMode()) {
                chapterList = (List<ChapterBean>) list;
                downloadView.initDownloadList(chapterList);
            }
        });

        LiveDataBus.get()
                .with("urlVideo", Object.class)
                .observe(this, catalog -> {
                    if (downloadView != null)
                        downloadView.cleanCheck();
                });

    }

    private void getMedia(String vid, boolean newAdd) {
//        getChapter.catalogId = vid;
        ApiUtils.getInstance(getContext(), getChapter.serverUrl).getChapterDetail(getChapter, vid, new ApiCall<Catalog>() {
            @Override
            protected void onResult(Catalog data) {
                toDownload(data, newAdd);
            }
        });
    }

    /**
     * 播放本地
     */
    private void toPlay(AliyunDownloadMediaInfo aliyunDownloadInfo) {
        if (StartPlayerUtils.getCacheMode()) {
            LiveDataBus.get().with("CacheMode").setValue(aliyunDownloadInfo.getSavePath());
        } else {
            ApiUtils.getInstance(getContext(), getChapter.serverUrl).getChapterDetail(getChapter, aliyunDownloadInfo.getNewPlayerId(), new ApiCall<Catalog>() {
                @Override
                protected void onResult(Catalog data) {
                    data.savePath = aliyunDownloadInfo.getSavePath();
                    LiveDataBus.get().with("Catalog").setValue(data);
                }
            });
        }
    }

    private void toDownload(Catalog catalog, boolean newAdd) {
        VidAuth vidAuth = new VidAuth();
        vidAuth.setVid(catalog.media.mediaSource);
        vidAuth.setPlayAuth(catalog.media.playAuth);
        if (newAdd) {
            if (!mDownloadInPrepare) {
                mDownloadInPrepare = true;
                downloadManager.prepareDownload(vidAuth, catalog.title, catalog.id, catalog.coursewareCode);
            }
        } else {
            if (downloadManager != null) {
                downloadMediaInfo.setVidSts(vidAuth);
                downloadManager.prepareDownloadByQuality(downloadMediaInfo, 0);
            }
        }
    }

    private void initAliDownload() {
        DatabaseManager.getInstance().createDataBase(getContext());
        copyAssets();
    }

    private void copyAssets() {

        File file = new File(StartPlayerUtils.getVideoPath());
        if (!file.exists()) {
            file.mkdir();
        }
        // 获取AliyunDownloadManager对象
        downloadManager = AliyunDownloadManager.getInstance(getContext());
//        downloadManager.setEncryptFilePath(Environment.getExternalStorageDirectory().getAbsolutePath() + "/aliyun/encryptedApp.dat");
        PrivateService.initService(getContext(), getFromAssets("encryptedApp.dat").getBytes());
        downloadManager.setDownloadDir(file.getAbsolutePath());
        //设置同时下载个数
        downloadManager.setMaxNum(3);

        downloadDataProvider = DownloadDataProvider.getSingleton(getContext());
        // 更新sts回调
//                        downloadManager.setRefreshStsCallback(new MyRefreshStsCallback());

        // 视频下载的回调
        downloadManager.setDownloadInfoListener(new MyDownloadInfoListener(DownLoadFragment.this));
        downloadViewSetting(downloadView);
    }

    public String getFromAssets(String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void onDestroy() {
        if (commenUtils != null) {
            commenUtils.onDestroy();
            commenUtils = null;
        }

        super.onDestroy();
    }

    private List<AliyunDownloadMediaInfo> currentPreparedMediaInfo = null;

    private void onDownloadPrepared(List<AliyunDownloadMediaInfo> infos, boolean showAddDownloadView) {
        currentPreparedMediaInfo = new ArrayList<>();
        currentPreparedMediaInfo.addAll(infos);
//        if (showAddDownloadView) {
//            showAddDownloadView(mCurrentDownloadScreenMode);
//        }
        aliyunDownloadMediaInfo = infos.get(0);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permission = ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                if (getActivity() == null)
                    return;
                ActivityCompat.requestPermissions(getActivity(), PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE);
            } else {
                addNewInfo(aliyunDownloadMediaInfo);
            }
        } else {
            addNewInfo(aliyunDownloadMediaInfo);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                addNewInfo(aliyunDownloadMediaInfo);
            } else {
                // Permission Denied
                FixedToastUtils.show(getContext(), "没有sd卡读写权限, 无法下载");
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void addNewInfo(AliyunDownloadMediaInfo info) {
        if (downloadManager != null && info != null) {
            //todo
            if (downloadView != null) {
                boolean hasAdd = downloadView.hasAdded(info);
                if (!hasAdd) {
                    if (downloadView != null && info != null) {
                        downloadView.addDownloadMediaInfo(info);
                    }
                    downloadManager.startDownload(info);
                }
            }
            downloadView.updateInfo(info);

        }
    }

    /**
     * downloadView的配置 里面配置了需要下载的视频的信息, 事件监听等 抽取该方法的主要目的是, 横屏下download dialog的离线视频列表中也用到了downloadView, 而两者显示内容和数据是同步的,
     * 所以在此进行抽取 AliyunPlayerSkinActivity.class#showAddDownloadView(DownloadVie view)中使用
     */
    private void downloadViewSetting(final DownloadView downloadView) {
        downloadDataProvider.restoreMediaInfo(new LoadDbDatasListener() {
            @Override
            public void onLoadSuccess(List<AliyunDownloadMediaInfo> dataList) {
                if (downloadView != null) {
                    if (StartPlayerUtils.getCacheMode()) {
                        downloadView.addAllDownload(dataList,getChapter.coursewareCode);
                    } else {
                        downloadView.addAllDownloadMediaInfo(dataList);
                    }
                }
            }
        });

        downloadView.setOnDownloadViewListener(new DownloadView.OnDownloadViewListener() {

            @Override
            public void onNewDownLoad(AliyunDownloadMediaInfo downloadMediaInfo) {
                getMedia(downloadMediaInfo.getNewPlayerId(), true);
            }

            @Override
            public void onStop(AliyunDownloadMediaInfo downloadMediaInfo) {
//                downloadManager.stopDownload(downloadMediaInfo);
            }

            @Override
            public void onStart(AliyunDownloadMediaInfo downloadMediaInfo) {
//                downloadManager.startDownload(downloadMediaInfo);
//                refreshDownloadVidSts(downloadMediaInfo);
            }

            @Override
            public void onDeleteDownloadInfo(final ArrayList<AlivcDownloadMediaInfo> alivcDownloadMediaInfos) {
                if (alivcDownloadMediaInfos != null && alivcDownloadMediaInfos.size() > 0) {
                    if (StartPlayerUtils.getCacheMode()) {
                        downloadView.deleteDownloadInfoCache();
                    }else {
                        downloadView.deleteDownloadInfo();
                    }
                    if (downloadManager != null) {
                        for (AlivcDownloadMediaInfo alivcDownloadMediaInfo : alivcDownloadMediaInfos) {
                            downloadManager.deleteFile(alivcDownloadMediaInfo.getAliyunDownloadMediaInfo());
                        }

                    }
                    downloadDataProvider.deleteAllDownloadInfo(alivcDownloadMediaInfos);
                } else {
                    FixedToastUtils.show(getContext(), "没有删除的视频选项...");
                }
            }

        });

        downloadView.setOnDownloadedItemClickListener(new DownloadView.OnDownloadItemClickListener() {
            @Override
            public void onDownloadedItemClick(final int positin) {
//                ArrayList<AlivcDownloadMediaInfo> allDownloadMediaInfo = downloadView.getAllDownloadMediaInfo();
//                if (positin < 0) {
//                    FixedToastUtils.show(getContext(), "视频资源不存在");
//                    return;
//                }
//                // 如果点击列表中的视频, 需要将类型改为vid
//                AliyunDownloadMediaInfo aliyunDownloadMediaInfo = allDownloadMediaInfo.get(positin).getAliyunDownloadMediaInfo();
//                PlayParameter.PLAY_PARAM_TYPE = "localSource";
//                if (aliyunDownloadMediaInfo != null) {
//                    PlayParameter.PLAY_PARAM_URL = aliyunDownloadMediaInfo.getSavePath();
//                    //播放本地视频调用 预留
////                    mAliyunVodPlayerView.updateScreenShow();
////                    changePlayLocalSource(PlayParameter.PLAY_PARAM_URL, aliyunDownloadMediaInfo.getTitle());
//                }

            }

            @Override
            public void onDownloadingItemClick(ArrayList<AlivcDownloadMediaInfo> infos, int position) {
                AlivcDownloadMediaInfo alivcInfo = infos.get(position);
                AliyunDownloadMediaInfo aliyunDownloadInfo = alivcInfo.getAliyunDownloadMediaInfo();
                AliyunDownloadMediaInfo.Status status = aliyunDownloadInfo.getStatus();
                if (status == AliyunDownloadMediaInfo.Status.Error || status == AliyunDownloadMediaInfo.Status.Wait ||
                        status == AliyunDownloadMediaInfo.Status.Stop || status == AliyunDownloadMediaInfo.Status.NoDownload) {
                    downloadManager.startDownload(aliyunDownloadInfo);
                } else if (status == AliyunDownloadMediaInfo.Status.Start) {
                    downloadManager.stopDownload(aliyunDownloadInfo);
                } else if (status == AliyunDownloadMediaInfo.Status.Complete) {
                    toPlay(aliyunDownloadInfo);
                }
            }
        });
    }


    /**
     * 下载监听
     */
    private static class MyDownloadInfoListener implements AliyunDownloadInfoListener {

        private WeakReference<DownLoadFragment> weakReference;

        public MyDownloadInfoListener(DownLoadFragment aliyunPlayerSkinActivity) {
            weakReference = new WeakReference<>(aliyunPlayerSkinActivity);
        }

        @Override
        public void onPrepared(List<AliyunDownloadMediaInfo> infos) {
//            preparedVid = infos.get(0).getVid();
//            Collections.sort(infos, new Comparator<AliyunDownloadMediaInfo>() {
//                @Override
//                public int compare(AliyunDownloadMediaInfo mediaInfo1, AliyunDownloadMediaInfo mediaInfo2) {
//                    if (mediaInfo1.getSize() > mediaInfo2.getSize()) {
//                        return 1;
//                    }
//                    if (mediaInfo1.getSize() < mediaInfo2.getSize()) {
//                        return -1;
//                    }
//
//                    if (mediaInfo1.getSize() == mediaInfo2.getSize()) {
//                        return 0;
//                    }
//                    return 0;
//                }
//            });
            DownLoadFragment aliyunPlayerSkinActivity = weakReference.get();
            if (aliyunPlayerSkinActivity != null) {
                aliyunPlayerSkinActivity.mDownloadInPrepare = false;
                aliyunPlayerSkinActivity.onDownloadPrepared(infos, false);
            }
        }

        @Override
        public void onAdd(AliyunDownloadMediaInfo info) {
            DownLoadFragment aliyunPlayerSkinActivity = weakReference.get();
            if (aliyunPlayerSkinActivity != null) {
                if (aliyunPlayerSkinActivity.downloadDataProvider != null) {
                    aliyunPlayerSkinActivity.downloadDataProvider.addDownloadMediaInfo(info);
                }
            }
        }

        @Override
        public void onStart(AliyunDownloadMediaInfo info) {
            DownLoadFragment aliyunPlayerSkinActivity = weakReference.get();
            if (aliyunPlayerSkinActivity != null) {
                if (aliyunPlayerSkinActivity.downloadView != null) {
                    aliyunPlayerSkinActivity.downloadView.updateInfo(info);
                }

            }
        }

        @Override
        public void onProgress(AliyunDownloadMediaInfo info, int percent) {
            DownLoadFragment aliyunPlayerSkinActivity = weakReference.get();
            if (aliyunPlayerSkinActivity != null) {
                if (aliyunPlayerSkinActivity.downloadView != null) {
                    aliyunPlayerSkinActivity.downloadView.updateInfo(info);
                }
            }
        }

        @Override
        public void onStop(AliyunDownloadMediaInfo info) {
            DownLoadFragment aliyunPlayerSkinActivity = weakReference.get();
            if (aliyunPlayerSkinActivity != null) {
                if (aliyunPlayerSkinActivity.downloadView != null) {
                    aliyunPlayerSkinActivity.downloadView.updateInfo(info);
                }
            }
        }

        @Override
        public void onCompletion(AliyunDownloadMediaInfo info) {
            DownLoadFragment aliyunPlayerSkinActivity = weakReference.get();
            if (aliyunPlayerSkinActivity != null) {
                synchronized (aliyunPlayerSkinActivity) {
                    if (aliyunPlayerSkinActivity.downloadView != null) {
                        aliyunPlayerSkinActivity.downloadView.updateInfoByComplete(info);
                    }


                    if (aliyunPlayerSkinActivity.downloadDataProvider != null) {
                        aliyunPlayerSkinActivity.downloadDataProvider.addDownloadMediaInfo(info);
                    }
                }
            }
        }

        @Override
        public void onError(AliyunDownloadMediaInfo info, ErrorCode code, String msg, String requestId) {
            DownLoadFragment aliyunPlayerSkinActivity = weakReference.get();
            if (aliyunPlayerSkinActivity != null) {
                aliyunPlayerSkinActivity.mDownloadInPrepare = false;
                if (aliyunPlayerSkinActivity.downloadView != null) {
                    aliyunPlayerSkinActivity.downloadView.updateInfoByError(info);
                }
                //鉴权过期
//                if (code.getValue() == ErrorCode.ERROR_SERVER_POP_UNKNOWN.getValue()) {
                aliyunPlayerSkinActivity.refreshDownloadVidSts(info);
            }
        }

        @Override
        public void onWait(AliyunDownloadMediaInfo info) {
//            mPlayerDownloadAdapter.updateData(info);
        }

        @Override
        public void onDelete(AliyunDownloadMediaInfo info) {
//            mPlayerDownloadAdapter.deleteData(info);
        }

        @Override
        public void onDeleteAll() {
//            mPlayerDownloadAdapter.clearAll();
        }

        @Override
        public void onFileProgress(AliyunDownloadMediaInfo info) {

        }
    }


    /**
     * 刷新下载的VidSts
     */
    private void refreshDownloadVidSts(final AliyunDownloadMediaInfo downloadMediaInfo) {
        this.downloadMediaInfo = downloadMediaInfo;
        getMedia(downloadMediaInfo.getNewPlayerId(), false);
    }
}
