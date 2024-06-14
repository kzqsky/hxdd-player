package com.edu.hxdd_player.bean;

import com.edu.hxdd_player.adapter.ChapterAdapter;
import com.edu.hxdd_player.utils.TimeFormatUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * http://yapi.edu-edu.com/project/23/interface/api/676
 */
public class ChapterBean implements com.chad.library.adapter.base.entity.MultiItemEntity {
    public String updatedBy;
    public long pId;
    public Object media;
    public String name;
    public long parentId;
    public String title;
    public long sequenceNum;
    public List<Object> lectures;
    public String createdBy;
    public List<Object> questions;
    public List<Object> catalogKnowledgePoints;
    public long isDel;
    public String createdAt;
    public String id;
    public String updatedAt;
    public long isMedia;
    public long mediaId;
    public long mediaDuration;
    public long accumulativeTimeLast;
    public long accumulativeTime;
    public String coursewareCode;
    public long isEnabled;
    // 是否为上次最后播放章节：0（非），1（是）
    public int isLastPlay;

    @Override
    public int getItemType() {
        if (parentId == 0) return ChapterAdapter.TYPE_LEVEL_1;
        return ChapterAdapter.TYPE_LEVEL_2;
    }

    public String getMediaDuration() {
        return TimeFormatUtils.format(mediaDuration);
    }

    public Integer getRatio() {
        int ratio = 0;
        if (mediaDuration > 0) {
            if (accumulativeTime >= mediaDuration) {
                return 100;
            }
            long time = accumulativeTime;
            if (accumulativeTime > 0) {//0秒
                time += 20;//加20秒冗余
            }
            BigDecimal at = new BigDecimal(time);
            BigDecimal md = new BigDecimal(mediaDuration);
            double ra = at.divide(md, 3, BigDecimal.ROUND_HALF_UP).doubleValue() * 100;
            // 使用Math.round进行四舍五入
            ratio = (int) Math.round(ra);
        }
        if (ratio > 100) {
            ratio = 100;
        }
        return ratio;
    }

    public String getRatioString() {
        return "已学" + getRatio() + "%";
    }

}
