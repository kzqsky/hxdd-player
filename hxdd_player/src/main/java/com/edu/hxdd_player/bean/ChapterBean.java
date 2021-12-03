package com.edu.hxdd_player.bean;

import com.edu.hxdd_player.adapter.ChapterAdapter;
import com.edu.hxdd_player.utils.TimeFormatUtils;

import java.util.List;

/**
 * http://yapi.edu-edu.com/project/23/interface/api/676
 */
public class ChapterBean  implements com.chad.library.adapter.base.entity.MultiItemEntity {
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
    public String coursewareCode;
    public long isEnabled;


    @Override
    public int getItemType() {
        if (parentId == 0) return ChapterAdapter.TYPE_LEVEL_1;
        return ChapterAdapter.TYPE_LEVEL_2;
    }

    public String getMediaDuration() {
       return TimeFormatUtils.format(mediaDuration);
    }
}
