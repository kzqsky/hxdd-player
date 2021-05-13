package com.edu.hxdd_player.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;

import com.aliyun.player.source.VidAuth;
import com.aliyun.vodplayerview.utils.download.AliyunDownloadManager;
import com.edu.hxdd_player.api.ApiUtils;
import com.edu.hxdd_player.api.net.ApiCall;
import com.edu.hxdd_player.bean.media.Catalog;
import com.edu.hxdd_player.bean.parameters.GetChapter;

public class DownLoadService extends Service {

    private AliyunDownloadManager downloadManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initDownLoad();
        initAction();
        return START_STICKY;
    }

    private void initDownLoad() {

    }

    private void initAction() {
//        LiveDataBus.get()
////                .with("download", GetChapter.class)
////                .observe(this, chapter -> {
//////                    getChapter = chapter;
////                    getMedia(chapter);
////                });
    }

    private void getMedia(GetChapter getChapter) {
        ApiUtils.getInstance(this, getChapter.serverUrl).getChapterDetail(getChapter, getChapter.id, new ApiCall<Catalog>() {
            @Override
            protected void onResult(Catalog data) {
                toDownload(data);
            }
        });
    }

    private void toDownload(Catalog catalog) {
        VidAuth vidAuth = new VidAuth();
        vidAuth.setVid(catalog.media.mediaSource);
        vidAuth.setPlayAuth(catalog.media.playAuth);
//        downloadManager.prepareDownload(vidAuth);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
