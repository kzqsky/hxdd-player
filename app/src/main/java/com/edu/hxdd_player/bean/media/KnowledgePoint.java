package com.edu.hxdd_player.bean.media;

import java.util.Date;

/**
 * 知识点 Bean
 */

public class KnowledgePoint {
    public Long    id;                 // 主键id
    public String  code;               // 知识点编码
    public String  title;              // 知识点名称
    public Long    parentId;           // 父知识点id
    public String  businessLineCode;   // 业务线编码
  
    public String  courseCode;         // 课程编码
    public Integer sequenceNum;        // 序号
    public Integer isEnabled;          // 是否启用: 启用(1)、禁用(0)
    public Integer isDel;              // 是否删除: 已删除(1)、未删除(0)
    public String  createdBy;          // 创建人
    public String  updatedBy;          // 更新人
    public String    createdAt;          // 创建时间
    public String    updatedAt;          // 更新时间
}
