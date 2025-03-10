package com.edu.hxdd_player.bean;

import com.edu.hxdd_player.bean.parameters.GetChapter;
import com.google.gson.Gson;

/**
 * AI问答 h5页面需要的参数
 */
public class AIBean {
    public String coursewareCode;
    public String userId;
    public String clientId;
    public String chapterCode;
    public String chapterTitle;
    public String parentId;
    public String parentName;


    public String setData(GetChapter getChapter, ChapterBean chapterBean) {
        this.coursewareCode = getChapter.coursewareCode;
        this.userId = getChapter.userId;
        this.clientId = getChapter.clientCode;
        this.chapterCode = chapterBean.id;
        this.chapterTitle = chapterBean.title;
        this.parentId = chapterBean.parentId + "";
        this.parentName = chapterBean.parentName;

        return new Gson().toJson(this);
    }
}
