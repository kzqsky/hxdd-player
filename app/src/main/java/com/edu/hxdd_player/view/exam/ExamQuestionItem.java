package com.edu.hxdd_player.view.exam;

import android.content.Context;
import android.view.View;

import com.edu.hxdd_player.bean.media.QuestionOption;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by yx on 2018/3/19.
 * 试题选项构建类
 */

public class ExamQuestionItem {

    public ExamQuestionItemCallback mCallback;
//    public String mQuestionId;

    public interface ExamQuestionItemCallback {
        void onQuestionSelected(ExamQuestionItemView item);
    }

//    public ExamQuestionItem(String questionId) {
//        mQuestionId = questionId;
//    }

    public ExamQuestionItem(ExamQuestionItemCallback callback) {
        mCallback = callback;
//        mQuestionId = questionId;
    }

    public ArrayList<ExamQuestionItemView> builderItem(Context context, List<QuestionOption> questionChoices) {
        ArrayList<ExamQuestionItemView> list = new ArrayList<>();
        if (questionChoices == null) {
            return list;
        }
        for (QuestionOption choice : questionChoices) {
            ExamQuestionItemView item = new ExamQuestionItemView(context);
            item.setContent(choice.quesOption);
            item.setCircleViewText(choice.quesValue);
            item.setOnClickListener((View v) -> {
                if (mCallback != null) {
                    mCallback.onQuestionSelected((ExamQuestionItemView) v);
                }
            });
            list.add(item);
        }
        return list;
    }

    public void onDestroy() {
        mCallback = null;
    }
}
