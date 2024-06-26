package com.edu.hxdd_player.bean.parameters;

import com.edu.hxdd_player.utils.MD5Utils;

public class PutLearnRecords extends BaseParameters {

    public long videoTime;
    public String clientCode;
    public String learnRecordId;
    public long lastTime;
    public String catalogId;
    public long accumulativeTime;
    public String courseCode;
    public String coursewareCode;
    public String businessLineCode;
    public String userId;
    public String userName;

    public boolean isPass;
    public long questionId;
    public String examinePoint;

    public String backUrl; //回调业务系统

    public String deviceType = "TYPE_ANDROID";

    public long md5Timestamp;
    public String md5;
    public String classNum;


    public int maxTimePerDay = 0; //按照每天来限制学时时长，单位是秒
    public int maxTimePerTime = 0; //按照单次打开窗口限制学时时长，单位是秒

    public void Md5() {
        md5Timestamp = System.currentTimeMillis();
        md5 = MD5Utils.encodeMD5String(clientCode + userId + coursewareCode + catalogId + accumulativeTime + "14daab0a-4aff-4f6e-b303-c85f09c39f42" + md5Timestamp);
    }

    public static PutLearnRecords getRecord(String learnRecordId, GetChapter getChapter, String catalogId, long videoTime, long lastTime, long accumulativeTime) {
        PutLearnRecords records = getPublic(learnRecordId, getChapter, catalogId);
        records.videoTime = videoTime;
        records.lastTime = lastTime;
        records.accumulativeTime = accumulativeTime;
        return records;
    }


    public static PutLearnRecords getQuestionRecord(String learnRecordId, GetChapter getChapter, String catalogId, boolean isPass, long questionId, String examinePoint) {
        PutLearnRecords records = getPublic(learnRecordId, getChapter, catalogId);
        records.isPass = isPass;
        records.questionId = questionId;
        records.examinePoint = examinePoint;
        return records;
    }

    public static PutLearnRecords getQuestionRecord(String learnRecordId, GetChapter getChapter, String catalogId, boolean isPass, long questionId, String examinePoint, long videoTime, long lastTime, long accumulativeTime) {
        PutLearnRecords records = getPublic(learnRecordId, getChapter, catalogId);
        records.isPass = isPass;
        records.questionId = questionId;
        records.examinePoint = examinePoint;

        records.videoTime = videoTime;
        records.lastTime = lastTime;
        records.accumulativeTime = accumulativeTime;
        return records;
    }

    private static PutLearnRecords getPublic(String learnRecordId, GetChapter getChapter, String catalogId) {
        PutLearnRecords records = new PutLearnRecords();
        records.learnRecordId = learnRecordId;
        records.clientCode = getChapter.clientCode;
        records.businessLineCode = getChapter.businessLineCode;
        records.courseCode = getChapter.courseCode;
        records.coursewareCode = getChapter.coursewareCode;
        records.catalogId = catalogId;
        records.userId = getChapter.userId;
        records.userName = getChapter.userName;
        records.backUrl = getChapter.backUrl;
        records.classNum = getChapter.classNum;
        records.maxTimePerDay = getChapter.maxTimePerDay;
        records.maxTimePerTime = getChapter.maxTimePerTime;
        return records;
    }
}
