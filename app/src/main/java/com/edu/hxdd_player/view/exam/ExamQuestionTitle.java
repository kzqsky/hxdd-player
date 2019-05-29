package com.edu.hxdd_player.view.exam;


import android.content.Context;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aliyun.vodplayerview.utils.DensityUtil;
import com.edu.hxdd_player.R;

/**
 * Created by yx on 2018/3/19.
 */

public class ExamQuestionTitle  {

    /**
     * 构建问题内容
     * @param title 题目内容
     * @return 题目view
     */
    public TextView builderTitle(Context context, String title) {
        ExamTextImageView textImageView = new ExamTextImageView(context);
//        String t = mQuestionBean.title.replace("<p>", "");
        textImageView.setDisplayContent(title);//.replace("</p>","\n"));
        textImageView.setTextColor(context.getResources().getColor(R.color.text));
        textImageView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.exam_textsize));
        textImageView.setPadding(0, DensityUtil.dip2px(context, 5), 0, DensityUtil.dip2px(context,5));
        textImageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        return textImageView;
    }
}
