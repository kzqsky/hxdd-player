package com.edu.hxdd_player.bean.media;

import java.util.Date;
import java.util.List;

/**
 * 试题基本信息
 */
public class Question {

    public Long    questionId;                // 试题 ID
    public Long    mediaTime;                 // 弹题时间,单位秒
    public Long    mediaId;                   // 媒体 ID
    public String  mediaTitle;                // 媒体名称
    public String  coursewares;               // 所属课件
    public String  coursewareCode;            // 课件编码
    public Long    catalogId;                 // 章节 ID
    public String  examinePoint;              // 试题考核点
    public Integer questionType;              // 题目类型
    public String  questionStem;              // 题干
    public String  answer;                    // 正确答案
    public String  analysis;                  // 答案解析
    public Integer isEnabled;                 // 是否启用(0无效1有效)
    public Integer isDel;                     // 是否删除(0未删除1删除)
    public String  createdBy;                 // 创建人
    public String  updatedBy;                 // 操作人
    public Date    createdAt;                 // 创建时间
    public Date    updatedAt;                 // 更新时间
    public List<QuestionOption> optionList;   // 选项对象
}
