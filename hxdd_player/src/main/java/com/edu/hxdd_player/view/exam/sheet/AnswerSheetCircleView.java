package com.edu.hxdd_player.view.exam.sheet;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;

import com.aliyun.vodplayerview.utils.DensityUtil;
import com.edu.hxdd_player.R;
import com.edu.hxdd_player.utils.StartPlayerUtils;


/**
 * Created by yx on 2017/7/19.
 */

public class AnswerSheetCircleView extends AppCompatTextView {

    private boolean checked = false;
    private int marginLeft, marginTop, marginRight, marginBottom;
    //View 直径
    private int width;
    //字体大小
    private int textSizeDp;
    private int textColor = R.color.text;
    private Paint bgPaint;
    private int off = 2;

    /**
     *
     * @param context
     * @param marginLeft
     * @param marginTop
     * @param marginRight
     * @param marginBottom
     */
    public AnswerSheetCircleView(Context context, int textSizeDp, int marginLeft, int marginTop,
                                 int marginRight, int marginBottom) {
        this(context, 0, textSizeDp, marginLeft, marginTop, marginRight, marginBottom);
    }

    public AnswerSheetCircleView(Context context, int widthDp, int textSizeDp,
                                 int marginLeft, int marginTop,
                                 int marginRight, int marginBottom) {
        super(context);
        this.width = widthDp;
        this.textSizeDp = textSizeDp;
        this.marginLeft = marginLeft;
        this.marginTop = marginTop;
        this.marginRight = marginRight;
        this.marginBottom = marginBottom;
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    void init() {
        off = DensityUtil.dip2px(getContext(), 1);

        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        bgPaint.setColor(Color.WHITE);

        setGravity(Gravity.CENTER);
        setTextColor(getResources().getColor(textColor));
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSizeDp);
        setBackgroundResource(R.drawable.circle_stroke_bg);
        int miniWidth = textSizeDp * 3 / 2;
        if (width < miniWidth) {
            width = miniWidth;
        }
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(DensityUtil.dip2px(getContext(),width),
                DensityUtil.dip2px(getContext(),width));
        lp.setMargins(marginLeft, marginTop, marginRight, marginBottom);
        setLayoutParams(lp);
    }

    public boolean isChecked() {
        return checked;
    }

    /**
     * 取消效果
     */
    public void unChecked() {
        this.checked = false;
        setTextColor(getContext().getResources().getColor(textColor));
        bgPaint.setColor(Color.WHITE);
        invalidate();
    }

    /**
     * 绿色背景
     */
    public void green() {
        this.checked = false;
        setTextColor(Color.WHITE);
//        setBackgroundResource(R.drawable.circle_green_bg);
        bgPaint.setColor(getContext().getResources().getColor(R.color.answer_sheet_green));
        invalidate();
    }

    /**
     * 红色背景
     */
    public void red() {
        this.checked = false;
        setTextColor(Color.WHITE);
//        setBackgroundResource(R.drawable.circle_red_bg);
        bgPaint.setColor(getContext().getResources().getColor(R.color.answer_sheet_red));
        invalidate();
    }

    /**
     * 选中效果
     */
    public void checked() {
        this.checked = true;
        setTextColor(Color.WHITE);
//        setBackgroundResource(R.drawable.circle_primary_bg);
        bgPaint.setColor(StartPlayerUtils.getColorPrimary());
        invalidate();
    }

    public void setNormalTextColor(int textColor) {
        this.textColor = textColor;
        setTextColor(getResources().getColor(textColor));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawOval(off, off, getWidth() - off, getHeight() - off, bgPaint);
        super.onDraw(canvas);
    }
}
