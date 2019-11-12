package com.edu.hxdd_player.bean.media;

import java.util.List;


/**
 * 章节目录 Bean
 */

public class Catalog {
    public String id;                 // 主键 ID

    public String coursewareCode;     // 课件编码
    public String title;              // 章节目录名称
    public Long parentId;           // 父节点 ID
    public Integer sequenceNum;        // 序号
    public Integer isMedia;            // 是否是视频,视频(1),不是视频(0)
    public Integer isEnabled;          // 是否启用,启用(1),禁用(0)
    public Integer isDel;              // 是否删除,已删除(1),未删除(0)
    public String createdBy;          // 创建人
    public String updatedBy;          // 更新人
    public String createdAt;          // 创建时间
    public String updatedAt;          // 更新时间
    public Long mediaDuration;      // 媒体时长,单位为秒,冗余字段


    public List<Media> mediaList;   // 章节绑定的媒体集合(已废弃)
    public Media media;       // 媒体对象
    public Long mediaId;     // 媒体 id
    public List<Lecture> lectures;    // 讲义列表
    public List<CatalogKnowledgePoint> catalogKnowledgePoints; // 媒体知识点
    public List<Question> questions;   // 试题列表
    // 使用zTree扩展字段
    public String name;                 // 章节目录名称
    public Long pId;                    // 父节点 ID

    public LearnRecord learnRecord;

    public String savePath;
}
