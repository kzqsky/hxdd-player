package com.edu.hxdd_player.adapter;

import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.edu.hxdd_player.R;
import com.edu.hxdd_player.bean.ChapterBean;

import java.util.List;

public class ChapterAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {
    public static final int TYPE_LEVEL_1 = 1;
    public static final int TYPE_LEVEL_2 = 2;
    public static final int TYPE_LEVEL_3 = 3;
    public static final int TYPE_LEVEL_4 = 4;

    public int selectIndex = -1;
//    public int lastSelect = 0;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public ChapterAdapter(List<MultiItemEntity> data) {
        super(data);
        addItemType(TYPE_LEVEL_1, R.layout.item_expandable_lv1);
        addItemType(TYPE_LEVEL_2, R.layout.item_expandable_lv2);
//        addItemType(TYPE_LEVEL_3, R.layout.item_expandable_lv3);
//        addItemType(TYPE_LEVEL_4, R.layout.item_expandable_lv4);
//        addItemType(5, R.layout.item_expandable_lv4);
//        addItemType(6, R.layout.item_expandable_lv4);
//        addItemType(7, R.layout.item_expandable_lv4);
//        addItemType(8, R.layout.item_expandable_lv4);
//        addItemType(9, R.layout.item_expandable_lv4);
//        addItemType(10, R.layout.item_expandable_lv4);
    }

    @Override
    protected void convert(final BaseViewHolder helper, MultiItemEntity item) {
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
                helper.setText(R.id.txt_title, baseItem.title).setText(R.id.txt_time, baseItem.getMediaDuration());
                if (baseItem.isMedia == 0) { //代表沒有媒体
                    helper.setGone(R.id.txt_time, false);
                    helper.setGone(R.id.imageView, false);
                } else {
                    helper.setGone(R.id.txt_time, true);
                    helper.setGone(R.id.imageView, true);
                }
                ImageView imageView=helper.getView(R.id.imageView);
                if (helper.getAdapterPosition() == selectIndex) {
                    helper.setTextColor(R.id.txt_title, mContext.getResources().getColor(R.color.colorPrimary));
                    helper.setTextColor(R.id.txt_time, mContext.getResources().getColor(R.color.colorPrimary));
//                    helper.setImageResource(R.id.imageView, R.drawable.ic_play_p);
                    imageView.setColorFilter(ContextCompat.getColor(mContext, R.color.colorPrimary));
                } else {
                    helper.setTextColor(R.id.txt_title, mContext.getResources().getColor(R.color.black));
                    helper.setTextColor(R.id.txt_time, mContext.getResources().getColor(R.color.black));
//                    helper.setImageResource(R.id.imageView, R.drawable.ic_play_n);
                    imageView.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
                }
//                helper.getView(R.id.imageView).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        int pos = helper.getAdapterPosition();
//                        if (baseItem.isExpanded()) {
//                            collapse(pos);
//                        } else {
//                            expand(pos);
//                        }
//                    }
//                });
                break;

        }
    }

    public void checked(int index) {
//        clearCheck();
//        Item2 item2 = (Item2) mData.get(index);
//        item2.checked = true;
        selectIndex = index;
        notifyDataSetChanged();
    }

//    public void lastChecked(String code) {
//        lastChecked(code, 0);
//    }
//
//    public void lastChecked(String code, int index) {
//        for (int i = index; i < mData.size(); i++) {
//            BaseItem baseItem = (BaseItem) mData.get(i);
//            if (TextUtils.isEmpty(code) || code.equals(baseItem.code)) {
//                checked(i);
//                break;
//            } else if (code.startsWith(baseItem.code)) {
//                expand(i);
//                lastChecked(code, ++i);
//                break;
//            }
//        }
//    }


}
