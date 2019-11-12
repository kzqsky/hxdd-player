package com.aliyun.vodplayerview.view.download;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aliyun.vodplayerview.utils.download.AliyunDownloadMediaInfo;
import com.aliyun.vodplayerview.view.sectionlist.SectionParameters;
import com.aliyun.vodplayerview.view.sectionlist.StatelessSection;
import com.edu.hxdd_player.R;
import com.edu.hxdd_player.utils.StartPlayerUtils;
import com.edu.hxdd_player.view.exam.RoundCheckBox;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

/**
 * @author Mulberry
 * create on 2018/4/12.
 */

public class DownloadSection extends StatelessSection {

    public static final String DOWNLOADED_TAG = "DownloadedTag";
    public static final String DOWNLOADING_TAG = "DownloadingTag";

    private ArrayList<AlivcDownloadMediaInfo> alivcDownloadMediaInfos = new ArrayList<>();
    private final String title;
    private final String tag;
    private WeakReference<Context> context;

    public DownloadSection(Context context, String tag, String title,
                           ArrayList<AlivcDownloadMediaInfo> alivcDownloadMediaInfos) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.alivc_download_item)
//              .headerResourceId(R.layout.alivc_download_section_item)
                .build());
        this.context = new WeakReference<Context>(context);
        this.tag = tag;
        this.title = title;
        this.alivcDownloadMediaInfos = alivcDownloadMediaInfos;
    }


    @Override
    public ViewHolder getHeaderViewHolder(View view) {
        return new SectionItemViewHolder(view);
    }

    @Override
    public ViewHolder getItemViewHolder(View view) {
        return new DownloadInfoItemViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(ViewHolder holder) {
        final SectionItemViewHolder headerHolder = (SectionItemViewHolder) holder;
        headerHolder.tvSectionItemTitle.setText(title);
    }

    @Override
    public void onBindItemViewHolder(ViewHolder holder, int position) {
        final DownloadInfoItemViewHolder itemViewHolder = (DownloadInfoItemViewHolder) holder;
        AliyunDownloadMediaInfo mediaInfo = alivcDownloadMediaInfos.get(position).getAliyunDownloadMediaInfo();

//        new ImageLoaderImpl().loadImage(this.context.get(), mediaInfo.getCoverUrl(), new ImageLoaderOptions.Builder()
//                                        .crossFade()
//                                        .centerCrop()
//                                        .placeholder(R.color.alivc_common_bg_cyan_light).build()
//                                       ).into(itemViewHolder.ivVideoCover);

        itemViewHolder.tvVideoTitle.setText(mediaInfo.getNewPlayerTitle());

        itemViewHolder.cbSelect.setVisibility(alivcDownloadMediaInfos.get(position).isEditState() ? View.VISIBLE : View.GONE);
        itemViewHolder.cbSelect.setChecked(alivcDownloadMediaInfos.get(position).isCheckedState());
        AliyunDownloadMediaInfo.Status status = mediaInfo.getStatus();
        if (status == AliyunDownloadMediaInfo.Status.NoDownload || status == AliyunDownloadMediaInfo.Status.Start) {
            itemViewHolder.cbSelect.setVisibility(View.GONE);
        }

        itemViewHolder.iv_download.setColorFilter(StartPlayerUtils.getColorPrimary());
        /**
         * 数据刷新的时候有可能失败,导致已完成的数据一直显示下载中
         */
        if (status == AliyunDownloadMediaInfo.Status.Start && mediaInfo.getProgress() == 100) {
            mediaInfo.setStatus(AliyunDownloadMediaInfo.Status.Complete);
            status = AliyunDownloadMediaInfo.Status.Complete;
        }
        if (status == AliyunDownloadMediaInfo.Status.NoDownload) {
            itemViewHolder.tvDownloadVideoStats.setText("");
            itemViewHolder.iv_download.setVisibility(View.VISIBLE);
        } else if (status == AliyunDownloadMediaInfo.Status.Prepare) {
            //prepare
            itemViewHolder.tvDownloadVideoStats.setText(context.get().getResources().getString(R.string.download_wait));
            itemViewHolder.iv_download.setVisibility(View.GONE);
        } else if (status == AliyunDownloadMediaInfo.Status.Wait) {
            //wait
            itemViewHolder.tvDownloadVideoStats.setText(context.get().getResources().getString(R.string.download_wait));
            itemViewHolder.iv_download.setVisibility(View.GONE);
        } else if (status == AliyunDownloadMediaInfo.Status.Start) {
            //start
            itemViewHolder.tvDownloadVideoStats.setText(context.get().getResources().getString(R.string.download_downloading) + "\n" + mediaInfo.getProgress() + "%");
            itemViewHolder.iv_download.setVisibility(View.GONE);
        } else if (status == AliyunDownloadMediaInfo.Status.Stop) {
            //stop
            itemViewHolder.tvDownloadVideoStats.setText(context.get().getResources().getString(R.string.download_pause));
            itemViewHolder.iv_download.setVisibility(View.GONE);
        } else if (status == AliyunDownloadMediaInfo.Status.Complete) {
            //complete
            itemViewHolder.tvDownloadVideoStats.setText("下载完成");
            itemViewHolder.iv_download.setVisibility(View.GONE);
        } else if (status == AliyunDownloadMediaInfo.Status.Error) {
            // error
            //下载出错 暂时不显示文字
//            itemViewHolder.tvDownloadVideoStats.setText(context.get().getResources().getString(R.string.download_error));
            //下载出错 暂时不显示文字
            itemViewHolder.iv_download.setVisibility(View.GONE);
        }

        itemViewHolder.llDownloadItemRootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final int adapterPosition = itemViewHolder.getAdapterPosition();
                if (onSectionItemClickListener != null) {
                    if (adapterPosition < 0) {
                        return;
                    }
                    onSectionItemClickListener.onItemClick(adapterPosition, tag);
                }
            }
        });
    }

    private String formatSize(long size) {
        int kb = (int) (size / 1024f);
        if (kb < 1024) {
            return kb + "KB";
        }

        int mb = (int) (kb / 1024f);
        return mb + "MB";

    }

    /**
     * 视频大小格式化,这里由于要和IOS同步,所以先四舍五入保留两位小数,再四舍五入保留一位小数
     */
    private String formatSizeDecimal(long size) {
        float kb = (size / 1024 * 1.0f);
        BigDecimal bigDecimal = new BigDecimal(kb);

        if (kb < 1024) {
            return String.format("%.1f", bigDecimal.setScale(2, RoundingMode.HALF_UP)) + "KB";
        }

        float mb = (kb / 1024 * 1.0f);
        BigDecimal decimal = new BigDecimal(mb);
        return String.format("%.1f", decimal.setScale(2, RoundingMode.HALF_UP)) + "MB";
    }

    @Override
    public int getContentItemsTotal() {
        return alivcDownloadMediaInfos.size();
    }

    private static class SectionItemViewHolder extends RecyclerView.ViewHolder {
        private TextView tvSectionItemTitle;

        SectionItemViewHolder(View view) {
            super(view);
            tvSectionItemTitle = (TextView) view.findViewById(R.id.tv_section_item_title);
        }
    }

    private static class DownloadInfoItemViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout llDownloadItemRootView;
        private RoundCheckBox cbSelect;

        private TextView tvVideoTitle;
        private TextView tvDownloadVideoStats;
        private ImageView iv_download;

        DownloadInfoItemViewHolder(View view) {
            super(view);
            llDownloadItemRootView = (LinearLayout) view.findViewById(R.id.ll_download_item_root_view);
            cbSelect = (RoundCheckBox) view.findViewById(R.id.cb_select);
            cbSelect.setButtonTintList(ColorStateList.valueOf(StartPlayerUtils.getColorPrimary()));
            tvVideoTitle = (TextView) view.findViewById(R.id.tv_video_title);
            tvDownloadVideoStats = (TextView) view.findViewById(R.id.tv_download_video_stats);
            iv_download = view.findViewById(R.id.iv_download);
        }
    }


    private OnSectionItemClickListener onSectionItemClickListener;

    public void setOnSectionItemClickListener(
            OnSectionItemClickListener onSectionItemClickListener) {
        this.onSectionItemClickListener = onSectionItemClickListener;
    }

    public interface OnSectionItemClickListener {
        void onItemClick(int posion, String tag);
    }
}
