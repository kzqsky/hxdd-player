package com.edu.hxdd_player.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Base64;
import android.widget.TextView;

import com.blankj.utilcode.util.ScreenUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;


/**
 * 用于获取html文本中<img>标签中的图片
 */

public class URLImageParser implements Html.ImageGetter {
    /**
     * 大图宽度不超过235px，超过则固定宽度235px，高度按原尺寸等比缩放
     */
    private final TextView mTextView;
    private Context mContext;
    //private int imageHeight;//图片的高度 以文本的高度为基准
    private int canvasOffset;//绘制图片时Y轴的偏移量 保证与文本的上端对齐
    private boolean isBig = true;//默认加载原图大小
    private int imageHeight;

    public URLImageParser(TextView ctx, Context tv) {
        this.mTextView = ctx;
        this.mContext = tv;
        Paint.FontMetrics fontMetrics = mTextView.getPaint().getFontMetrics();
        canvasOffset=(int) (Math.abs(fontMetrics.top)-Math.abs(fontMetrics.ascent));
//        imageHeight = (int) (Math.abs(fontMetrics.top) + Math.abs(fontMetrics.bottom));
        imageHeight = (int) (Math.ceil(mTextView.getTextSize()) * 2);
    }

    @Override
    public Drawable getDrawable(final String source) {
        final UrlDrawable urlDrawable = new UrlDrawable();
        displayImage(urlDrawable, new BitmapDrawable(base64ToBitmap(source)));
        return urlDrawable;
    }


    private void displayImage(String imageUri, UrlDrawable urlDrawable, File loadedImageFile){
        try {
            InputStream is = new FileInputStream(loadedImageFile);
            final BitmapDrawable drawable = new BitmapDrawable(mContext.getResources(), is);
            displayImage(urlDrawable, drawable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过设置BitmapDrawable画出图片内容
     */
    private void displayImage(UrlDrawable urlDrawable, BitmapDrawable drawable) {
        try {
            if (drawable.getBitmap() == null) {
                return;
            }
            int displayWidth = drawable.getBitmap().getWidth();
            int displayHeight = drawable.getBitmap().getHeight();

            int swidth = ScreenUtils.getScreenWidth();
            if (displayWidth > swidth) {
                float coefficient = (float) swidth / (float) displayWidth;
                displayWidth = (int) (coefficient * displayWidth);
                displayHeight = (int) (coefficient * displayHeight);
            }
            if (displayHeight < imageHeight) {
                float coefficient = (float) imageHeight / (float) displayHeight;
                displayWidth = (int) (coefficient * displayWidth);
                displayHeight = imageHeight;
            }
            Rect rect = new Rect(0, 0, displayWidth, displayHeight);
            drawable.setBounds(rect);
            urlDrawable.setBounds(rect);
            urlDrawable.setDrawable(drawable);
            mTextView.setText(mTextView.getText());
            mTextView.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示内容主要靠设置的Drawable
     */
    private class UrlDrawable extends BitmapDrawable {

        private Drawable drawable;

        @Override
        public void draw(Canvas canvas) {
            if (drawable != null) {
                canvas.save();
                canvas.translate(0, -canvasOffset);
                drawable.draw(canvas);
                canvas.restore();
            }
        }

        public Drawable getDrawable() {
            return drawable;
        }

        public void setDrawable(Drawable drawable) {
            this.drawable = drawable;
        }
    }

    /**
     * 解析base64图片返回bitmap
     * @param string
     * @return
     */
    public static Bitmap base64ToBitmap(String string) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray = Base64.decode(string.split(",")[1], Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}