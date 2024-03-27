package com.edu.hxdd_player.adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.edu.hxdd_player.R;
import com.edu.hxdd_player.api.ApiUtils;
import com.edu.hxdd_player.bean.CourseInfoBean;
import com.edu.hxdd_player.utils.DensityUtils;
import com.edu.hxdd_player.utils.FileUtils;
import com.edu.hxdd_player.utils.OpenFileUtils;
import com.edu.hxdd_player.utils.StartPlayerUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;

import java.io.File;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class FileListAdapter extends BaseQuickAdapter<CourseInfoBean.UploadedFile, BaseViewHolder> {
    boolean showDownload = false;
    GradientDrawable drawable;

    public FileListAdapter(@Nullable List<CourseInfoBean.UploadedFile> data, boolean showDownload) {
        super(R.layout.hxdd_player_item_filelist, data);

        for (CourseInfoBean.UploadedFile uploadedFile : getData()) {
            if (FileUtils.urlFileExist(uploadedFile.url)) {
                uploadedFile.progress = 100;
            }
        }

        this.showDownload = showDownload;
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, CourseInfoBean.UploadedFile uploadedFile) {
        baseViewHolder.setText(R.id.tv_title, uploadedFile.filename);
        TextView button = baseViewHolder.getView(R.id.button);
        if (showDownload) {
            setColor();
            button.setVisibility(View.VISIBLE);
            if (uploadedFile.progress <= 0) {
                button.setText("下载");
                button.setClickable(true);
            } else if (uploadedFile.progress > 0 && uploadedFile.progress < 100) {
                button.setText(uploadedFile.progress + "%");
                button.setClickable(false);
            } else if (uploadedFile.progress == 100) {
                button.setText("打开");
                button.setClickable(true);
            }
            button.setBackground(drawable);
            button.setTextColor(StartPlayerUtils.getColorPrimary());

            button.setOnClickListener(v -> {
                if (uploadedFile.progress <= 0) { //开启下载
                    download(getItemPosition(uploadedFile), uploadedFile);
                } else if (uploadedFile.progress >= 100 || uploadedFile.progress == -1) { //下载完成 调用打开
                    OpenFileUtils.openFile(getContext(), new File(FileUtils.urlToFilePath(uploadedFile.url)));
                }
            });
        } else {
            button.setVisibility(View.GONE);
        }

    }

    private void setColor() {
        if (drawable == null) {
            drawable = new GradientDrawable();
            drawable.setCornerRadius(DensityUtils.dp2px(getContext(), 25));
            drawable.setStroke(DensityUtils.dp2px(getContext(), 1), StartPlayerUtils.getColorPrimary());
            drawable.setColor(Color.parseColor("#ffffff"));
        }
    }

    private int checkDowning() {
        int size = 0;
        for (CourseInfoBean.UploadedFile uploadedFile : getData()) {
            if (uploadedFile.progress > 0 && uploadedFile.progress < 100) {
                size++;
            }
        }
        return size;
    }

    /**
     * 开始下载
     *
     * @param index
     * @param uploadedFile
     */
    private void download(int index, CourseInfoBean.UploadedFile uploadedFile) {
        if (ApiUtils.getInstance() == null)
            return;
        if (checkDowning() >= 5) {
            ToastUtils.showLong("下载任务不能过多，请稍后再试");
            return;
        }

        OkGo.<File>get(ApiUtils.getInstance().serverUrl + uploadedFile.url).tag(ApiUtils.getInstance().serverUrl + uploadedFile.url).execute(new FileCallback(FileUtils.DIRECTORY_DOWNLOADS, FileUtils.getUrlFileName(uploadedFile.url)) {
            @Override
            public void onSuccess(File file, Call call, Response response) {
                uploadedFile.progress = 100;
                notifyItemChanged(index);
            }

            @Override
            public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                uploadedFile.progress = (int) (progress * 100);
                if (uploadedFile.progress % 5 == 0)
                    notifyItemChanged(index);
                super.downloadProgress(currentSize, totalSize, progress, networkSpeed);
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                ToastUtils.showLong("下载失败：" + e.getMessage());
            }
        });
    }


}
