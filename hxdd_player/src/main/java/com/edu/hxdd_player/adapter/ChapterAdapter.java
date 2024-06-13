package com.edu.hxdd_player.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.edu.hxdd_player.R;
import com.edu.hxdd_player.bean.ChapterBean;
import com.edu.hxdd_player.bean.ClientConfigBean;
import com.edu.hxdd_player.utils.DensityUtils;
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
    ClientConfigBean clientConfigBean;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public ChapterAdapter(List<MultiItemEntity> data, ClientConfigBean clientConfigBean) {
        super(data);
        addItemType(TYPE_LEVEL_1, R.layout.hxdd_player_item_expandable_lv1);
        addItemType(TYPE_LEVEL_2, R.layout.hxdd_player_item_expandable_lv2);
        this.clientConfigBean = clientConfigBean;
    }


    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {
        final ChapterBean baseItem = (ChapterBean) item;
        switch (item.getItemType()) {
            case TYPE_LEVEL_1:
            case TYPE_LEVEL_2:
            case TYPE_LEVEL_3:
            case TYPE_LEVEL_4:
            case 5:
            case 6:

                helper.setText(R.id.hxdd_player_txt_title, "  " + baseItem.title).setText(R.id.hxdd_player_txt_time, "  " + baseItem.getMediaDuration()).setText(R.id.hxdd_player_txt_ratio, baseItem.getRatioString());

                TextView textView = helper.getView(R.id.hxdd_player_txt_title);
                if (baseItem.isMedia == 0) { //代表沒有媒体
                    helper.setGone(R.id.hxdd_player_txt_time, true);
                    helper.setGone(R.id.hxdd_player_imageView, true);
                    helper.setGone(R.id.hxdd_player_txt_ratio, true);
                    helper.setGone(R.id.view_line, false);
                    textView.setPadding(0, DensityUtils.dp2px(getContext(), 5), 0, DensityUtils.dp2px(getContext(), 5));
                    helper.setBackgroundColor(R.id.hxdd_player_txt_title, getContext().getResources().getColor(R.color.F4F6FA));
                } else {
                    helper.setVisible(R.id.hxdd_player_txt_time, true);
                    helper.setVisible(R.id.hxdd_player_imageView, true);
                    if (clientConfigBean != null && clientConfigBean.learningProgress) {
                        helper.setVisible(R.id.hxdd_player_txt_ratio, true);
                    } else {
                        helper.setGone(R.id.hxdd_player_txt_ratio, false);
                    }
                    helper.setVisible(R.id.view_line, true);
                    textView.setPadding(0, 0, 0, 0);
                    helper.setBackgroundColor(R.id.hxdd_player_txt_title, getContext().getResources().getColor(R.color.white));

                    ImageView imageView = helper.getView(R.id.hxdd_player_imageView);
                    if (downloadMode) {
                        imageView.setImageResource(R.drawable.icon_donwload);
                    } else {
                        if (getItemPosition(item) == selectIndex) { //选中
                            helper.setTextColor(R.id.hxdd_player_txt_title, StartPlayerUtils.getColorPrimary());
                            imageView.setImageResource(R.drawable.ic_play_p);
                            imageView.setColorFilter(StartPlayerUtils.getColorPrimary());
                        } else { //没选中
                            if (StartPlayerUtils.nextLearning()) {   //启用顺序学习
                                // 1.在选中项之后 并且进度小于100 加锁   2.禁止重复播放且学完了 加锁
                                if (getItemPosition(item) > selectIndex && baseItem.getRatio() < 100 || StartPlayerUtils.getChapter.playCanRepeat && baseItem.getRatio() >= 100) {
                                    helper.setTextColor(R.id.hxdd_player_txt_title, getContext().getResources().getColor(R.color.text_gary));
                                    imageView.setImageResource(R.drawable.ic_play_lock);
                                    imageView.setColorFilter(null);
                                } else {
                                    helper.setTextColor(R.id.hxdd_player_txt_title, getContext().getResources().getColor(R.color.black));
                                    imageView.setImageResource(R.drawable.ic_play_n);
                                    imageView.setColorFilter(null);
                                }
                            } else {//未启用顺序学习
                                if (StartPlayerUtils.getChapter.playCanRepeat && baseItem.getRatio() >= 100) { //不可重复播放并且学完了  加锁
                                    helper.setTextColor(R.id.hxdd_player_txt_title, getContext().getResources().getColor(R.color.text_gary));
                                    imageView.setImageResource(R.drawable.ic_play_lock);
                                    imageView.setColorFilter(null);
                                } else {
                                    //不可重复播放
                                    helper.setTextColor(R.id.hxdd_player_txt_title, getContext().getResources().getColor(R.color.black));
                                    imageView.setImageResource(R.drawable.ic_play_n);
                                    imageView.setColorFilter(null);
                                }
                            }
                        }
                        //显示进度
                        if (baseItem.getRatio() >= 100) { //已经学完
                            helper.setTextColor(R.id.hxdd_player_txt_ratio, StartPlayerUtils.getColorLearned());
                        } else {//未学完
                            helper.setTextColor(R.id.hxdd_player_txt_ratio, getContext().getResources().getColor(R.color.text_gary));
                        }
                    }

                }

                break;

        }
    }

    public void checked(int index) {
        selectIndex = index;
        notifyDataSetChanged();
    }

    public void updateLearning(String id, long accumulativeTime) {
        int index = 0;
        for (MultiItemEntity itemEntity : getData()) {
            ChapterBean chapterBean = (ChapterBean) itemEntity;
            if (chapterBean.id.equals(id)) {
                break;
            }
            index++;
        }

        ChapterBean chapterBean = (ChapterBean) getItem(index);
        chapterBean.accumulativeTime = accumulativeTime;
        notifyItemChanged(index);
    }

    public int getCheckedIndex() {
        int index = 0;
        for (MultiItemEntity itemEntity : getData()) {
            ChapterBean chapterBean = (ChapterBean) itemEntity;
            if (chapterBean.isLastPlay == 1) {
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

    public int getNextMediaIndex(int nextIndex) {
        int index = nextIndex;
        if (index == getItemCount() - 1) {
            index = 0;
        } else {
            index++;
        }
        ChapterBean baseItem = (ChapterBean) getData().get(index);
        if (isMedia(index) && baseItem.getRatio() < 100) {//是媒体并且 未学完
            return index;
        } else {
            return getNextMediaIndex(index);
        }
    }

    /**
     * 当前选中章节是否学习完成
     *
     * @return
     */
    public boolean learnEnd() {
        ChapterBean baseItem = (ChapterBean) getItem(selectIndex);
        if (baseItem.getRatio() >= 99) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isMedia(int index) {
        if (getData() == null || getData().size() == 0) return false;
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
