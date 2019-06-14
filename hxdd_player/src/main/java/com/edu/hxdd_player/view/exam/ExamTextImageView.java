package com.edu.hxdd_player.view.exam;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.edu.hxdd_player.utils.URLImageParser;

import org.xml.sax.XMLReader;

import java.util.Locale;


/**
 * Created by yx on 2018/1/18.
 * 显示图片text
 */

@SuppressLint("AppCompatCustomView")
public class ExamTextImageView extends TextView {

    public static final String TAG = ExamTextImageView.class.getSimpleName();

    private String displayContent = "";
    private Context mContext;
    private boolean mImgClickable = true;
    private PopupWindow mPopupWindow;
//    private PhotoView mPhotoView;

    public ExamTextImageView(Context context) {
        this(context, null);
    }

    public ExamTextImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ExamTextImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    public void setDisplayContent(String content) {
        if (content == null) {
            return;
        }
        displayContent = content;
        displayContent();
    }

    /**
     * 显示图文内容
     */
    private void displayContent() {
        URLTagHandler urlTagHandler = new URLTagHandler();
        URLImageParser urlParserChebien = new URLImageParser(this, getContext());
        Spanned htmlSpan = Html.fromHtml(displayContent, urlParserChebien, urlTagHandler);
        setText(htmlSpan);
        if (mImgClickable) {
            setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    public boolean isImgClickable() {
        return mImgClickable;
    }

    public void setImgClickable(boolean mImgClickable) {
        this.mImgClickable = mImgClickable;
    }

//    private void showPopWindow(View view, File img) {
//        try {
//            InputStream is = new FileInputStream(img);
//            final BitmapDrawable drawable = new BitmapDrawable(mContext.getResources(), is);
//            mPhotoView.setImageDrawable(drawable);
//            mPopupWindow.showAtLocation(view,
//                    Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL,
//                    0, 0);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 图片点击处理
     */
    class ImageClickableSpan extends ClickableSpan {

        String sourceUrl = "";

        public ImageClickableSpan(String url) {
            this.sourceUrl = url;
        }

        @Override
        public void onClick(final View view) {
//            View popView = LayoutInflater.from(mContext)
//                    .inflate(R.layout.zk_layout_zoom_popwindow, null);
//            mPhotoView = popView.findViewById(R.id.photoview);
//            mPhotoView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
//
//                @Override
//                public void onViewTap(View view, float x, float y) {
//                    mPopupWindow.dismiss();
//                }
//            });
//            mPopupWindow = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.MATCH_PARENT);
//            mPopupWindow.setFocusable(true);
//            mPopupWindow.setOutsideTouchable(true);
//            ColorDrawable dw = new ColorDrawable(Color.WHITE);
//            mPopupWindow.setBackgroundDrawable(dw);
//            OkGo.get(ZKUrls.IMAGE_URL + sourceUrl)
//                    .cacheKey(sourceUrl)
//                    .cacheMode(CacheMode.IF_NONE_CACHE_REQUEST)
//                    .cacheTime(24 * 60 * 60 * 1000)
//                    .execute(new ZKFileCallback() {
//
//                        @Override
//                        public void onCacheSuccess(File file, Call call) {
//                            super.onCacheSuccess(file, call);
//                            Logger.t(TAG).i("读取缓存：" + sourceUrl);
//                            showPopWindow(view, file);
//                        }
//
//                        @Override
//                        public void onSuccess(File file, Call call, Response response) {
//                            Logger.t(TAG).i("网络请求：" + sourceUrl);
//                            showPopWindow(view, file);
//                        }
//            });
        }
    }

    /**
     *
     */
    class URLTagHandler implements Html.TagHandler {

        public URLTagHandler() {
        }

        @Override
        public void handleTag(boolean opening, String tag, Editable output,
                              XMLReader xmlReader) {
            // 处理标签<img>
            if (tag.toLowerCase(Locale.getDefault()).equals("img")) {
                // 获取长度
                int len = output.length();
                // 获取图片地址
                ImageSpan[] images = output.getSpans(len - 1, len, ImageSpan.class);
                String imgURL = images[0].getSource();
                // 使图片可点击并监听点击事件
                output.setSpan(new ImageClickableSpan(imgURL), len - 1, len,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }
}
