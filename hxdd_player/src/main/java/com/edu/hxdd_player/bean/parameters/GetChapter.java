package com.edu.hxdd_player.bean.parameters;

public class GetChapter extends BaseParameters {
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

    public String validTime;

    public boolean isQuestion = true; // 默认是true 不需要弹题 改为false
    /**
     * 给下载传递用
     */
    public String id;
    /**
     * 课件服务器地址
     */
    public String serverUrl;
    /**
     * 拖拽进度条, 默认为 0 允许拖拽, 1 为不允许拖拽
     */
    public int drag;
    /**
     * 提示点 (默认为 1 显示提示点, 0 则隐藏)
     */
    public int hintPoint;
    /**
     * 水印 url
     */
    public String logoUrl;

    public int logoPosition;
    public float logoAlpha;
    public int logoWidth;
    public int logoHeight;

    public String backUrl; //回调业务系统
    public String defaultQuality = "LD";  //指定播放的清晰度  LD标清   FD流畅
    /**
     * 顺序播放，播放完毕才能播放下一个，默认false 可以随便播放
     */
    public boolean playByOrder = false;
    /**
     * 能否重复播放已播完的，默认false 可以重复播放
     */
    public boolean playCanRepeat = false;
    /**
     * 超出学习时长限制后不能继续学习，直接退出播放页面，默认false 不受限制
     */
    public boolean overMaxLearnHoursStop = false;

    public int maxTimePerDay = 0; //按照每天来限制学时时长，单位是秒
    public int maxTimePerTime = 0; //按照单次打开窗口限制学时时长，单位是秒
    public String classNum; //培训平台使用的参数

}
