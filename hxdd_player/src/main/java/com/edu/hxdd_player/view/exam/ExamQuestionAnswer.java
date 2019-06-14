package com.edu.hxdd_player.view.exam;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;

import com.edu.hxdd_player.R;


/**
 * Created by yx on 2018/3/19.
 * 构建选择题答案
 */

public class ExamQuestionAnswer {

    public SpannableString builderAnswer(Context context, String answer, String userAnswer) {
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(answer)) {
            sb.append("正确答案：").append(answer.toUpperCase());
        }

        if (!TextUtils.isEmpty(userAnswer)) {
            sb.append("    ").append("您的答案：").append(userAnswer.toUpperCase());
        }
        String s = sb.toString();
        SpannableString span = new SpannableString(s);
        if (!TextUtils.isEmpty(answer)) {
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.answer_sheet_green));
            span.setSpan(foregroundColorSpan, 5, answer.length() + 5, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            span.setSpan(new AbsoluteSizeSpan(18, true), 5, answer.length() + 5, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        if (!TextUtils.isEmpty(userAnswer)) {
            ForegroundColorSpan foregroundColorSpan1 = new ForegroundColorSpan(context.getResources().getColor(R.color.answer_sheet_red));
            span.setSpan(foregroundColorSpan1, s.length() - userAnswer.length(), s.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            span.setSpan(new AbsoluteSizeSpan(18, true), s.length() - userAnswer.length(), s.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return span;
    }
}
