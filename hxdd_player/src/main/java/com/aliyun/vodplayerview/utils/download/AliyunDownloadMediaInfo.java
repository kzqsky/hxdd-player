package com.aliyun.vodplayerview.utils.download;

import android.text.TextUtils;

import com.aliyun.player.bean.ErrorCode;
import com.aliyun.player.nativeclass.TrackInfo;
import com.aliyun.player.source.VidAuth;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * 下载信息封装类
 *
 * @author hanyu
 */
public class AliyunDownloadMediaInfo {

    private static final String TAG = AliyunDownloadMediaInfo.class.getSimpleName();
    private String mVid;
    private String mQuality;
    private int mProgress = 0;
    private String mSavePath = null;
    private String mTitle;
    private String mCoverUrl;
    private long mDuration;
    private AliyunDownloadMediaInfo.Status mStatus;
    private long mSize;
    private String mFormat;
    private int mDownloadIndex = 0;
    private int isEncripted = 0;
    private TrackInfo mTrackInfo;
    private VidAuth mVidSts;
    private ErrorCode errorCode;
    private String errorMsg;
    private int mFileHandleProgress = 0;
    private int mQualityIndex;
    /**
     * 与新课件系统对接使用
     */
    private String newPlayerId;

    private String newPlayerTitle;
    /**
     * 课件id，用于筛选
     */
    private String coursewareCode;

    public String getCoursewareCode() {
        return coursewareCode;
    }

    public void setCoursewareCode(String coursewareCode) {
        this.coursewareCode = coursewareCode;
    }


    public String getNewPlayerTitle() {
        return newPlayerTitle;
    }

    public void setNewPlayerTitle(String newPlayerTitle) {
        this.newPlayerTitle = newPlayerTitle;
    }


    public AliyunDownloadMediaInfo() {
    }

    public int getQualityIndex() {
        return mQualityIndex;
    }

    public void setQualityIndex(int mQualityIndex) {
        this.mQualityIndex = mQualityIndex;
    }

    public int getmFileHandleProgress() {
        return mFileHandleProgress;
    }

    public void setmFileHandleProgress(int mFileHandleProgress) {
        this.mFileHandleProgress = mFileHandleProgress;
    }

    public int getDownloadIndex() {
        return this.mDownloadIndex;
    }

    public void setDownloadIndex(int mDownloadIndex) {
        this.mDownloadIndex = mDownloadIndex;
    }

    public String getVid() {
        return this.mVid;
    }

    public void setVid(String mVid) {
        this.mVid = mVid;
    }

    public String getQuality() {
        return this.mQuality;
    }

    public void setQuality(String mQuality) {
        this.mQuality = mQuality;
    }

    public int getProgress() {
        return this.mProgress;
    }

    public void setProgress(int mProgress) {
        this.mProgress = mProgress;
    }

    public String getSavePath() {
        return this.mSavePath;
    }

    public void setSavePath(String mSavePath) {
        this.mSavePath = mSavePath;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getCoverUrl() {
        return this.mCoverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.mCoverUrl = coverUrl;
    }

    public long getDuration() {
        return this.mDuration;
    }

    public void setDuration(long duration) {
        this.mDuration = duration;
    }

    public AliyunDownloadMediaInfo.Status getStatus() {
        return this.mStatus;
    }

    public void setStatus(AliyunDownloadMediaInfo.Status mStatus) {
        this.mStatus = mStatus;
    }

    public long getSize() {
        return this.mSize;
    }

    public String getSizeStr() {
        int kbSize = (int) ((float) this.mSize / 1024.0F);
        return kbSize < 1024 ? kbSize + "KB" : (float) kbSize / 1024.0F + "MB";
    }

    public void setSize(long mSize) {
        this.mSize = mSize;
    }

    public String getFormat() {
        return this.mFormat;
    }

    public void setFormat(String mFormat) {
        this.mFormat = mFormat;
    }

    public int isEncripted() {
        return this.isEncripted;
    }

    public void setEncripted(int encripted) {
        this.isEncripted = encripted;
    }

    public TrackInfo getTrackInfo() {
        return mTrackInfo;
    }

    public void setTrackInfo(TrackInfo mTrackInfo) {
        this.mTrackInfo = mTrackInfo;
    }

    public VidAuth getVidSts() {
        return mVidSts;
    }

    public void setVidSts(VidAuth mVidSts) {
        this.mVidSts = mVidSts;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public static String getJsonFromInfos(List<AliyunDownloadMediaInfo> infos) {
        JSONArray jsonArray = new JSONArray();
        if (infos != null && !infos.isEmpty()) {
            Iterator var2 = infos.iterator();

            while (var2.hasNext()) {
                AliyunDownloadMediaInfo info = (AliyunDownloadMediaInfo) var2.next();
                JSONObject infoJsonobject = formatInfoToJsonobj(info);
                if (infoJsonobject != null) {
                    jsonArray.put(infoJsonobject);
                }
            }

            return jsonArray.toString();
        } else {
            return jsonArray.toString();
        }
    }

    private static JSONObject formatInfoToJsonobj(AliyunDownloadMediaInfo info) {
        if (info == null) {
            return null;
        } else {
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("vid", info.getVid());
                jsonObject.put("quality", info.getQuality());
                jsonObject.put("format", info.getFormat());
                jsonObject.put("coverUrl", info.getCoverUrl());
                jsonObject.put("duration", info.getDuration());
                jsonObject.put("title", info.getTitle());
                jsonObject.put("savePath", info.getSavePath());
                jsonObject.put("status", info.getStatus());
                jsonObject.put("size", info.getSize());
                jsonObject.put("progress", info.getProgress());
                jsonObject.put("dIndex", info.getDownloadIndex());
                jsonObject.put("encript", info.isEncripted());
                return jsonObject;
            } catch (JSONException var3) {

                return null;
            }
        }
    }

    public static List<AliyunDownloadMediaInfo> getInfosFromJson(String infoContent) {
        if (TextUtils.isEmpty(infoContent)) {
            return null;
        } else {
            JSONArray jsonArray = null;

            try {
                jsonArray = new JSONArray(infoContent);
            } catch (JSONException var8) {

            }

            if (jsonArray == null) {
                return null;
            } else {
                List<AliyunDownloadMediaInfo> infos = new ArrayList();
                int size = jsonArray.length();

                for (int i = 0; i < size; ++i) {
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        AliyunDownloadMediaInfo info = getInfoFromJson(jsonObject);
                        infos.add(info);
                    } catch (JSONException var7) {

                    }
                }

                return infos;
            }
        }
    }

    private static AliyunDownloadMediaInfo getInfoFromJson(JSONObject jsonObject) throws JSONException {
        AliyunDownloadMediaInfo info = new AliyunDownloadMediaInfo();
        info.setVid(jsonObject.getString("vid"));
        info.setTitle(jsonObject.getString("title"));
        info.setQuality(jsonObject.getString("quality"));
        info.setFormat(jsonObject.getString("format"));
        info.setCoverUrl(jsonObject.getString("coverUrl"));
        info.setDuration(jsonObject.getInt("duration"));
        info.setSavePath(jsonObject.getString("savePath"));
        info.setStatus(AliyunDownloadMediaInfo.Status.valueOf(jsonObject.getString("status")));
        info.setSize(jsonObject.getInt("size"));
        info.setProgress(jsonObject.getInt("progress"));
        info.setDownloadIndex(jsonObject.getInt("dIndex"));
        info.setEncripted(jsonObject.getInt("encript"));
        return info;
    }

    public String getNewPlayerId() {
        return newPlayerId;
    }

    public void setNewPlayerId(String newPlayerId) {
        this.newPlayerId = newPlayerId;
    }

    public static enum Status {
        /**
         * 空闲状态
         */
        Idle,
        /**
         * 准备状态
         */
        Prepare,
        /**
         * 等待状态
         */
        Wait,
        /**
         * 开始状态
         */
        Start,
        /**
         * 暂停状态
         */
        Stop,
        /**
         * 完成状态
         */
        Complete,
        /**
         * 错误状态
         */
        Error,
        /**
         * 删除状态
         */
        Delete,
        /**
         * 文件处理状态
         */
        File,
        /**
         * 未点击下载
         */
        NoDownload,
        /**
         * 无法下载
         */
        UnableDownload;

        private Status() {
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AliyunDownloadMediaInfo that = (AliyunDownloadMediaInfo) o;
        return newPlayerId == that.newPlayerId ;
    }

    @Override
    public int hashCode() {
        Object[] hashObject = new Object[1];
        hashObject[0] = newPlayerId;
        return Arrays.hashCode(hashObject);
    }
}
