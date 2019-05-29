package com.edu.hxdd_player.view.exam.sheet;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by yx on 2017/8/9.
 */

public class AnswerSheetLayout extends ViewGroup {

    private int mRowNumber = 5;//一行几个内容

    private boolean alignParent = true;

    public AnswerSheetLayout(@NonNull Context context) {
        this(context, null);
    }

    public AnswerSheetLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs)
    {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 获得它的父容器为它设置的测量模式和大小
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        // 如果是warp_content情况下，记录宽和高
        int width = 0;
        int height = 0;
        /**
         * 记录每一行的宽度，width不断取最大宽度
         */
        int lineWidth = 0;
        /**
         * 每一行的高度，累加至height
         */
        int lineHeight = 0;

        int cCount = getChildCount();
        // 遍历每个子元素
        for (int i = 0; i < cCount; i++)
        {
            View child = getChildAt(i);
            // 得到child的lp
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            // 测量每一个child的宽和高
            measureChild(child, widthMeasureSpec, heightMeasureSpec);

            // 当前子空间实际占据的宽度
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            // 当前子空间实际占据的高度
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            /**
             * 如果加入当前child，则超出最大宽度，则的到目前最大宽度给width，叠加height 然后开启新行
             */
            if (i != 0 && i % mRowNumber == 0)
            {
                width = Math.max(lineWidth, childWidth);// 取最大的
                lineWidth = childWidth; // 重新开启新行，开始记录
                // 叠加当前高度，
                height += lineHeight;
                // 开启记录下一行的高度
                lineHeight = childHeight;
            } else
            // 否则累加值lineWidth,lineHeight取最大高度
            {
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
            }
            // 如果是最后一个，则将当前记录的最大宽度和当前lineWidth做比较
            if (i == cCount - 1)
            {
                width = Math.max(width, lineWidth);
                height += lineHeight;
            }

        }
        setMeasuredDimension((modeWidth == MeasureSpec.EXACTLY) ? sizeWidth
                : width, (modeHeight == MeasureSpec.EXACTLY) ? sizeHeight
                : height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int row = 0;//行号
        int column = 0;//列号
        int columnWidth = getWidth() / mRowNumber;
        for (int i = 0, count = getChildCount(); i < count; i++) {
            row = i / mRowNumber + 1;
            column = i % mRowNumber + 1;
            View child = getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            //计算左边的位置,最少有一个左右margin
            int off;
            int L;
            if (alignParent && mRowNumber > 2) {
                off = (columnWidth - child.getMeasuredWidth() - lp.leftMargin - lp.rightMargin) / (mRowNumber - 1);
                L = (columnWidth + off) * (column - 1 < 0 ? 0 : column - 1) + lp.leftMargin;
            } else {
                off = (columnWidth + lp.leftMargin - child.getMeasuredWidth()) / 2;
                L = columnWidth * (column - 1 < 0 ? 0 : column - 1) + off;//(column - 1 < 0 ? 0 : column - 1) * (child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin) + lp.leftMargin + lp.rightMargin;
            }
            //计算定点的位置,最少有一个上下margin
            int T = (row - 1 < 0 ? 0 : row - 1) * (child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin) + lp.topMargin;
            int R = L + child.getMeasuredWidth();
            int B = T + child.getMeasuredHeight();
            child.layout(L, T, R, B);
        }
    }
}
