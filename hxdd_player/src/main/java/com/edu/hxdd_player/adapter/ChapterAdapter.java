package com.edu.hxdd_player.adapter;

import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.edu.hxdd_player.R;
import com.edu.hxdd_player.bean.ChapterBean;
import com.edu.hxdd_player.utils.StartPlayerUtils;

import java.util.List;

public class ChapterAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {
    public static final int TYPE_LEVEL_1 = 1;
    public static final int TYPE_LEVEL_2 = 2;
    public static final int TYPE_LEVEL_3 = 3;
    public static final int TYPE_LEVEL_4 = 4;

    public int selectIndex = -1;
    //    public int lastSelect = 0;
    public boolean downloadMode = false;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public ChapterAdapter(List<MultiItemEntity> data) {
        super(data);
        addItemType(TYPE_LEVEL_1, R.layout.hxdd_player_item_expandable_lv1);
        addItemType(TYPE_LEVEL_2, R.layout.hxdd_player_item_expandable_lv2);
    }


    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {
        final ChapterBean baseItem = (ChapterBean) item;
        switch (helper.getItemViewType()) {
            case TYPE_LEVEL_1:
            case TYPE_LEVEL_2:
            case TYPE_LEVEL_3:
            case TYPE_LEVEL_4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
                helper.setText(R.id.hxdd_player_txt_title, baseItem.title).setText(R.id.hxdd_player_txt_time, baseItem.getMediaDuration());
                if (baseItem.isMedia == 0) { //代表沒有媒体
                    helper.setGone(R.id.hxdd_player_txt_time, true);
                    helper.setGone(R.id.hxdd_player_imageView, true);
                } else {
                    helper.setVisible(R.id.hxdd_player_txt_time, true);
                    helper.setVisible(R.id.hxdd_player_imageView, true);
                    ImageView imageView = helper.getView(R.id.hxdd_player_imageView);

                    if (helper.getAdapterPosition() == selectIndex && !downloadMode) {
                        helper.setTextColor(R.id.hxdd_player_txt_title, StartPlayerUtils.getColorPrimary());
                        helper.setTextColor(R.id.hxdd_player_txt_time, StartPlayerUtils.getColorPrimary());
                        imageView.setColorFilter(StartPlayerUtils.getColorPrimary());
                    } else {
                        if (baseItem.accumulativeTimeLast >= baseItem.mediaDuration - 20) {
                            helper.setTextColor(R.id.hxdd_player_txt_title, StartPlayerUtils.getColorLearned());
                            helper.setTextColor(R.id.hxdd_player_txt_time, StartPlayerUtils.getColorLearned());
                            imageView.setColorFilter(StartPlayerUtils.getColorLearned());
                        } else {
                            helper.setTextColor(R.id.hxdd_player_txt_title, getContext().getResources().getColor(R.color.black));
                            helper.setTextColor(R.id.hxdd_player_txt_time, getContext().getResources().getColor(R.color.black));
                            imageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.black));
                        }
                    }

                    if (downloadMode) {
                        imageView.setImageResource(R.drawable.icon_donwload);
                    } else {
                        imageView.setImageResource(R.drawable.ic_play_n);
                    }
                }

                break;

        }
    }

    public void checked(int index) {
        selectIndex = index;
        notifyDataSetChanged();
    }

    public int getCheckedIndex(String id) {
        int index = 0;
        for (MultiItemEntity itemEntity : getData()) {
            ChapterBean chapterBean = (ChapterBean) itemEntity;
            if (chapterBean.id.equals(id)) {
                return index;
            }
            index++;
        }
        return 0;
    }

    /**
     * 用于继续播放
     *
     * @return
     */
    public ChapterBean checkNext() {
        if (selectIndex == getItemCount() - 1) {
            selectIndex = 0;
        } else {
            selectIndex++;
        }

        if (isMedia(selectIndex)) {
            notifyDataSetChanged();
            return (ChapterBean) getItem(selectIndex);
        } else {
            return checkNext();
        }
    }

    public boolean isMedia(int index) {
        if (getData() == null || getData().size() == 0)
            return false;
        ChapterBean baseItem = (ChapterBean) getData().get(index);
        if (baseItem.isMedia == 0) { //代表沒有媒体
            return false;
        }
        return true;
    }

    private void setDownloadMode(boolean isDownloadMode) {
        this.downloadMode = isDownloadMode;
        notifyDataSetChanged();
    }

    public void changeMode() {
        setDownloadMode(!downloadMode);
    }
}
