package com.edu.hxdd_player.view.exam;

import android.text.TextUtils;


/**
 * Created by yx on 2018/3/19.
 * 构建题目解析
 */

public class ExamQuestionParse {
    public void buildParse(ExamTextImageView textView, String answer) {
        if (!TextUtils.isEmpty(answer)) {
            textView.setDisplayContent(answer);
        }
    }
}
