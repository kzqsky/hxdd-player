package com.edu.hxdd_player.bean.media;

/**
 * 说明：Media bean
 *
 * **/


public class Media{


    public Long        id;                 // 主键id

    public String      mediaTitle;         // 媒体全名

    public String      mediaSource;        // 媒体路径/阿里云视频编码

    public String      serverCode;         // 媒体服务器代码

    public String      mediaOwnerCode;     // 媒体拥有者代码

    public Integer     enabled;            // 是否启用 1：是 0：否

    public Long        mediaDuration;      // 媒体时长
    public String      mediaPrevPicUrl;    // 媒体预览图片
    public String      clarityLevel;       // 清晰度级别 多个值用逗号隔开 （字典表外键）
    public Integer     del;                // 是否删除 1：是 0：否
    public String      createdBy;          // 创建人
    public String      updatedBy;          // 更新人
    public String        createdAt;          // 创建时间
    public String   updatedAt;          // 更新时间
    public boolean     valid;              // 导入数据使用是否有效

    public String      serverType;         // 服务器类型（扩展）
    public String      playAuth;           // 播放凭证
    public String      serverCluster;      // 服务器群


}
