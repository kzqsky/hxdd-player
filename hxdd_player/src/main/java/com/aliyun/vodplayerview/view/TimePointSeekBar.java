package com.aliyun.vodplayerview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.aliyun.vodplayerview.utils.DensityUtil;
import com.edu.hxdd_player.utils.StartPlayerUtils;

import java.util.List;

public class TimePointSeekBar extends androidx.appcompat.widget.AppCompatSeekBar {

    private List<Long> mList;
    private int mPaintColor = Color.WHITE;
    private Paint mPaint;
    private int pointHeight;


    public TimePointSeekBar(Context context) {
        super(context);
        init();
    }

    public TimePointSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TimePointSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        pointHeight = DensityUtil.dip2px(getContext(), 3);
        mPaint = new Paint();
        mPaint.setColor(mPaintColor);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(pointHeight);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            if (mList != null && StartPlayerUtils.getPoint()) {//增加是否显示弹题点控制
                Rect rect = getProgressDrawable().getBounds();
                for (long point : mList) {
                    float scale = point * 1000f / getMax();
                    if (scale > 1) {
                        scale = 1;
                    }
                    if (scale < 0) {
                        scale = 0;
                    }
                    canvas.drawPoint(getPaddingLeft() + scale * rect.right, rect.top + pointHeight / 2, mPaint);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTimePointList(List<Long> list) {
        this.mList = list;
    }

    public void setTimePaintColor(int paintColor) {
        this.mPaintColor = paintColor;
    }
}
