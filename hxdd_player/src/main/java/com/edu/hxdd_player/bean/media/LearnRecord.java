package com.edu.hxdd_player.bean.media;

import java.util.Date;


/**
 * @Description 听课记录表
 * @Author cyt
 * @Date 2019/3/6 15:10
 */

public class LearnRecord  {
    public Long   learnRecordId;       // 听课记录主键
  
    public String userId;              // 用户 ID

    public String userName;            // 用户名称
 
    public String clientCode;          // 客户端编码
    public String businessLineCode;    // 业务线编码
    public String courseCode;          // 课程编码
  
    public String coursewareCode;      // 课件编码
  
    public Long   catalogId;           // 章节 ID
    public Long   videoTime;           // 视频时长(单位秒)
    public Long   validTime;           // 有效学习时长(单位秒)
    public String   createdAt;           // 创建时间/开始学习时间
    public String   synchronizedAt;      // 同步学习时间
 
    public Long   lastTime;            // 最后一次播放时间点
   
    public Long   accumulativeTime;    // 本次累计时长

    // 听课回传使用
    public String backUrl;             // 回传地址
    public Long   timestamp;           // 时间戳
    public String publicKey;           // 加密参数
    public Long   listenTimes;         // 听课记录步长
    public String coursewareName;      // 课件名称
    public String catalogTitle;        // 章节名称
    public boolean isPass;             // 考核是否通过
    public Long    questionId;         // 考核点试题 ID
    public String  examinePoint;       // 考核点名称
}
