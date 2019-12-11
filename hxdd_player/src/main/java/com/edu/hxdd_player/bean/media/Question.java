package com.edu.hxdd_player.bean.media;

import com.google.gson.Gson;

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
    public String  answer;                    // 正确答案,
    public String  analysis;                  // 答案解析
    public Integer isEnabled;                 // 是否启用(0无效1有效)
    public Integer isDel;                     // 是否删除(0未删除1删除)
    public String  createdBy;                 // 创建人
    public String  updatedBy;                 // 操作人
    public String    createdAt;                 // 创建时间
    public String    updatedAt;                 // 更新时间
    public List<QuestionOption> optionList;   // 选项对象
    public String  userAnswer;                //用来保存用户选择答案
    public boolean isPass;                    //考核点是否通过(弹题是否选择正确)
    public String rightAnswer;                //正确答案
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
