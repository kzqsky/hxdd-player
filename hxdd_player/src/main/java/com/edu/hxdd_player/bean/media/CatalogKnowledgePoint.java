package com.edu.hxdd_player.bean.media;

import java.util.List;


public class CatalogKnowledgePoint {
    public Long catalogKnowledgePointId;  // 主键id
 
    public String knowledgePointCode;     // 知识点code
  
    public Long startTime;                // 知识点开始时间
 
    public Long catalogId;                // 章节 ID
    
    public Long endTime;                  //知识点结束时间
    public List<KnowledgePoint> knowledgePoints;// 知识点列表
    /** 以下是扩展字段，数据库中不包含以下字段 知识点删除关联查询时侯使用 */
   
    public String coursewareName;         // 课件名称
   
    public String mediaTitle;             // 媒体名称
    public String title;                  // 章节名称

}
