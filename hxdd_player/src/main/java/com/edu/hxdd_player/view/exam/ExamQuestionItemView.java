package com.edu.hxdd_player.view.exam;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;

import com.aliyun.vodplayerview.utils.DensityUtil;
import com.edu.hxdd_player.R;
import com.edu.hxdd_player.view.exam.sheet.AnswerSheetCircleView;


/**
 * Created by yx on 2018/1/25.
 */

public class ExamQuestionItemView extends LinearLayout {

    private AnswerSheetCircleView mCircleView;
    protected ExamTextImageView mContent;

    public ExamQuestionItemView(Context context) {
        super(context);
        initView();
    }

    protected void initView() {
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        //构造选项圆
        mCircleView = new AnswerSheetCircleView(getContext(),
                35, 17,
                DensityUtil.dip2px(getContext(), 0),
                DensityUtil.dip2px(getContext(), 10),
                DensityUtil.dip2px(getContext(), 10),
                DensityUtil.dip2px(getContext(), 10));
        mCircleView.setClickable(false);
//        mCircleView.setTextColor(getResources().getColor(R.color.primary));
        addView(mCircleView);
        //构造选择内容
        mContent = new ExamTextImageView(getContext());
        mContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimension(R.dimen.exam_textsize));
        mContent.setTextColor(getContext().getResources().getColor(R.color.text));
        addView(mContent);
    }

    public void setContent(String content) {
        content = content.replace("<p>", "").replace("</p>", "");
        mContent.setDisplayContent(content);
    }

    public void setCircleViewText(String text) {
        mCircleView.setText(text.toUpperCase());
    }

    public void selectItem() {
        mCircleView.checked();
    }

    public void unSelectItem() {
        mCircleView.unChecked();
    }

    public void red() {
        mCircleView.red();
    }

    public void green() {
        mCircleView.green();
    }

    public boolean getSelectedStats() {
        return mCircleView.isChecked();
    }

    //获取选项order,对应选项数据模型名称
    public String getAnswerOrder() {
        return mCircleView.getText().toString();
    }

}
