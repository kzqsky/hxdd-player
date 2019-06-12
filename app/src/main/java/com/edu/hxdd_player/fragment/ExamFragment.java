package com.edu.hxdd_player.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.vodplayerview.utils.DensityUtil;
import com.edu.hxdd_player.R;
import com.edu.hxdd_player.bean.media.Question;
import com.edu.hxdd_player.bean.media.QuestionOption;
import com.edu.hxdd_player.view.exam.ExamQuestionAnswer;
import com.edu.hxdd_player.view.exam.ExamQuestionItem;
import com.edu.hxdd_player.view.exam.ExamQuestionItemView;
import com.edu.hxdd_player.view.exam.ExamQuestionParse;
import com.edu.hxdd_player.view.exam.ExamQuestionTitle;
import com.edu.hxdd_player.view.exam.ExamResultQuestionItem;
import com.edu.hxdd_player.view.exam.ExamTextImageView;
import com.google.gson.Gson;

import java.util.ArrayList;

public class ExamFragment extends AppCompatDialogFragment {

    public interface ExamFragmentCallback {
        void commit(Question question);

        void over(Question question);

        void cancel(Question question);
    }

    private static String QUESTION = "question";
    private Question mQuestionBean;
    private LinearLayout mLayoutQuestion, mLayoutAnswer;
    private TextView mTextCommit, mTextCancel, mTextConfirm;
    private ExamTextImageView mTxtAnswer;
    private ExamTextImageView mTxtParse;

    private ExamQuestionTitle mExamQuestionTitle;
    private ExamQuestionItem mExamQuestionItem;
    private ExamQuestionAnswer mExamQuestionAnswer;
    private ExamResultQuestionItem mExamResultQuestionItem;

    private ArrayList<ExamQuestionItemView> mItemList;

    private ExamFragmentCallback mCallback;

    public static ExamFragment newInstance(String question) {
        ExamFragment fragment = new ExamFragment();
        Bundle bundle = new Bundle();

        bundle.putString(QUESTION, question);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exam, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mQuestionBean = new Gson().fromJson(getArguments().getString(QUESTION), Question.class);
        mLayoutQuestion = view.findViewById(R.id.layout_question);
        mLayoutAnswer = view.findViewById(R.id.layout_answer);
        mTextCommit = view.findViewById(R.id.text_commit);
        mTextCancel = view.findViewById(R.id.text_cancel);
        mTextConfirm = view.findViewById(R.id.text_confirm);
        mTxtAnswer = view.findViewById(R.id.txt_answer);
        mTxtParse = view.findViewById(R.id.txt_parse);

        try {
            mCallback = (ExamFragmentCallback) getActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mExamQuestionTitle = new ExamQuestionTitle();
        mExamQuestionItem = new ExamQuestionItem(new ExamQuestionItem.ExamQuestionItemCallback() {
            @Override
            public void onQuestionSelected(ExamQuestionItemView item) {
                switch (mQuestionBean.questionType) {
                    case 1:
                        onRadioQuestionSelected(item);
                        break;
                    case 2:
                        onMultipleQuestionSelected(item);
                        break;
                    default:
                        Toast.makeText(getContext(), "没有找到合适的题目类型", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mExamQuestionAnswer = new ExamQuestionAnswer();
        mExamResultQuestionItem = new ExamResultQuestionItem();

        setCancelable(false);//强制不能返回取消
        getDialog().setCanceledOnTouchOutside(false);//强制不能点击空白处取消
        initEvent();
        build();
    }

    @Override
    public void onDestroyView() {
        mCallback = null;
        super.onDestroyView();
    }

    private void initEvent() {
        mTextCommit.setOnClickListener(v -> {
            if (TextUtils.isEmpty(mQuestionBean.userAnswer)) {
                Toast.makeText(getContext(), "请作答后提交", Toast.LENGTH_SHORT).show();
                return;
            }

            if (mCallback != null) {
                mCallback.commit(mQuestionBean);
            }

            mTextCommit.setVisibility(View.GONE);
            mTextCancel.setVisibility(View.GONE);
            mTextConfirm.setVisibility(View.VISIBLE);
            mLayoutAnswer.setVisibility(View.VISIBLE);
            buildResult();
        });

        mTextCancel.setOnClickListener(v -> {
            if (mCallback != null) {
                mCallback.cancel(mQuestionBean);
            }
            dismiss();
        });
        mTextConfirm.setOnClickListener(v -> {
            if (mCallback != null) {
                mCallback.over(mQuestionBean);
            }
            dismiss();
        });
    }

    /**
     * 显示题目
     */
    private void build() {
        if (mLayoutQuestion != null) {
            mLayoutQuestion.removeAllViews();
        }
        buildType();
        buildTitle();
        buildItem();
    }

    /**
     * 显示题目结果
     */
    private void buildResult() {
        if (mLayoutQuestion != null) {
            mLayoutQuestion.removeAllViews();
        }
        buildType();
        buildTitle();
        buildResultItem();
        buildAnswer();
        buildParse();
    }


    /**
     * 构造题目
     */
    private void buildTitle() {
        mLayoutQuestion.addView(mExamQuestionTitle.builderTitle(getContext(), mQuestionBean.questionStem));
//        View view = new View(getContext());
//        view.setBackgroundColor(getResources().getColor(R.color.divide));
//        mLayoutQuestion.addView(view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(getContext(), 1)));
    }


    /**
     * 构建答案解析
     */
    private void buildParse() {
        new ExamQuestionParse().buildParse(mTxtParse, mQuestionBean.analysis);
    }

    /**
     * 构造考试选项
     *
     * @return 选项ZKExamQuestionItem的集合
     */
    private void buildItem() {
        mItemList = mExamQuestionItem.builderItem(getContext(), mQuestionBean.optionList);
        for (ExamQuestionItemView item : mItemList) {
            mLayoutQuestion.addView(item);
        }
    }

    /**
     * 构造考试结果选项
     *
     * @return 选项ZKExamQuestionItem的集合
     */
    private void buildResultItem() {
        ArrayList<ExamQuestionItemView> list = mExamResultQuestionItem.builderItem(getContext(), mQuestionBean.userAnswer, mQuestionBean.optionList);
        for (ExamQuestionItemView item : list) {
            mLayoutQuestion.addView(item);
        }
    }

    /**
     * 单选答案
     */
    private void buildAnswer() {
        try {
            StringBuilder sb = new StringBuilder();
            for (QuestionOption choice : mQuestionBean.optionList) {
                if (choice.correct) {
                    sb.append(choice.quesValue);
                }
            }

            String answer = sb.toString();
            if (answer.equals(mQuestionBean.userAnswer)) {
                mQuestionBean.isPass = true;
            } else {
                mQuestionBean.isPass = false;
            }

            mTxtAnswer.setText(mExamQuestionAnswer.builderAnswer(getContext(), answer, mQuestionBean.userAnswer));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 单选规则
     *
     * @param item
     */
    private void onRadioQuestionSelected(ExamQuestionItemView item) {
        boolean isSelected = item.getSelectedStats();
        if (mItemList == null) {
            return;
        }
        for (ExamQuestionItemView temp : mItemList) {
            temp.unSelectItem();
        }
        if (isSelected) {
            item.unSelectItem();
            mQuestionBean.userAnswer = null;
        } else {
            item.selectItem();
            mQuestionBean.userAnswer = item.getAnswerOrder();
        }
    }

    /**
     * 多选规则
     *
     * @param item
     */
    private void onMultipleQuestionSelected(ExamQuestionItemView item) {
        if (item.getSelectedStats()) {
            item.unSelectItem();
        } else {
            item.selectItem();
        }
        if (mItemList == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (ExamQuestionItemView temp : mItemList) {
            if (temp.getSelectedStats()) {
                sb.append(temp.getAnswerOrder());
            }
        }
        mQuestionBean.userAnswer = sb.toString();
    }

    private void buildType() {
        TextView textView = new TextView(getContext());
        textView.setTextColor(getContext().getResources().getColor(R.color.text));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimension(R.dimen.exam_textsize));
        textView.setPadding(0,DensityUtil.dip2px(getContext(),5),0,DensityUtil.dip2px(getContext(),5));
        textView .setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        switch (mQuestionBean.questionType) {
            case 1:
                textView.setText("单选题");
                break;
            case 2:
                textView.setText("多选题");
                break;
        }
        mLayoutQuestion.addView(textView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }
}