package com.edu.hxdd_player.view.exam;

import android.content.Context;

import com.edu.hxdd_player.bean.media.QuestionOption;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yx on 2018/3/19.
 * 试题结果选项构建类
 */

public class ExamResultQuestionItem {

//    @Override
//    public ArrayList<ZKExamQuestionItem> builderItem(Context context, final ZKExamQuestionBean examQuestionBean) {
//        ArrayList<ZKExamQuestionItem> list = new ArrayList<>();
//        String userAnswer = examQuestionBean.userAnswer == null ? "" : examQuestionBean.userAnswer.toLowerCase();
//        String rightAnswer =  examQuestionBean.answer == null ? "" : examQuestionBean.answer.toLowerCase();
//        for (ZKQuestionChoice choice : examQuestionBean.questionChoices) {
//            ZKExamQuestionItem item = new ZKExamQuestionItem(context);
//            item.setContent(choice.content);
//            item.setCircleViewText(choice.order);
//            if (userAnswer.contains(choice.order.toLowerCase()) && rightAnswer.contains(choice.order.toLowerCase())) {
//                item.green();
//            } else if (userAnswer.contains(choice.order.toLowerCase()) && !rightAnswer.contains(choice.order.toLowerCase())) {
//                item.red();
//            }
//            list.add(item);
//        }
//
//        return list;
//    }

    public ArrayList<ExamQuestionItemView> builderItem(Context context, String userAnswer, List<QuestionOption> questionChoices) {
        ArrayList<ExamQuestionItemView> list = new ArrayList<>();
        if (questionChoices == null) {
            return list;
        }
        String temp_userAnswer = userAnswer == null ? "" : userAnswer;
        for (QuestionOption choice : questionChoices) {
            ExamQuestionItemView item = new ExamQuestionItemView(context);
            item.setContent(choice.quesOption);
            item.setCircleViewText(choice.quesValue);
            if (temp_userAnswer.contains(choice.quesValue) && choice.correct) {
                item.green();
            } else if (temp_userAnswer.contains(choice.quesValue) && !choice.correct) {
                item.red();
            }
            list.add(item);
        }

        return list;
    }
}
