package com.edu.hxdd_player.bean.media;

import java.util.Date;

/**
 * 试题选项信息
 */
// 可以链式调用 setter
public class QuestionOption {

    public Long    optionId;        // 选项主键id
    public Long    questionId;      // 试题id;
    public String  quesOption;      // 选项内容;
    public String  quesValue;       // 选项值 如A、B、C、D等;
    public Integer sequenceNum;     // 序号;
    public boolean correct;         // 是否正确的选项;
    public Integer isEnabled;       // 是否启用(0无效1有效)
    public Integer isDel;           // 是否删除(0未删除1删除)
    public String  createdBy;       // 创建人
    public String  updatedBy;       // 操作人
    public Date    createdAt;       // 创建时间
    public Date    updatedAt;       // 更新时间

}
