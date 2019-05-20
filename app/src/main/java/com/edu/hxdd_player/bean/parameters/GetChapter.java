package com.edu.hxdd_player.bean.parameters;

public class GetChapter {
    public String businessLineCode;              // 业务线编码
    public String coursewareCode;             // 课件编码(必填)
    public String courseCode;                   // 课程编码
    public String catalogId;       // 章节 ID (具体哪个媒体对应的章节 ID)
    public String clientCode;                   // 客户端编码(必填)
    public String userId;                    // 用户 ID(必填)
    public String userName;               // 用户名称
    public String lastTime;                     // 上次听课时长(秒)
    public String timestamp;            // 时间戳(必填)
    public String publicKey;// md5加密 校验参数(必填)
}
