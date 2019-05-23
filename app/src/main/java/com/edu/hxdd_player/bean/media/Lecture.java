package com.edu.hxdd_player.bean.media;

import java.util.Date;

/**
 * @author: liyafei
 * @Description: 讲义对象
 * @date: 2019/1/14 10:36
 */
public class Lecture {

    public Long    lectureId;       // 讲义主键 ID
    public Long    startTime;       // 开始时间
    public Long    endTime;         // 结束时间
    public String  coursewareCode;  // 课件编码
    public Long    catalogId;       // 章节 ID
    public Long    mediaId;         // 媒体 ID
    public String  title;           // 讲义标题
    public String  content;         // 讲义内容
    public Integer isEnabled;       // 是否启用(0无效1有效)
    public Integer isDel;           // 是否删除(0未删除1删除)
    public String  createdBy;       // 创建人
    public String  updatedBy;       // 操作人
    public Date    createdAt;       // 创建时间
    public Date    updatedAt;       // 更新时间


}
