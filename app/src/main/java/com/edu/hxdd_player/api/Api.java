package com.edu.hxdd_player.api;

import com.edu.hxdd_player.bean.BaseBean;
import com.edu.hxdd_player.bean.ChapterBean;
import com.edu.hxdd_player.bean.LearnRecordBean;
import com.edu.hxdd_player.bean.media.Catalog;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface Api {
    @POST("/appApi/catalogs")
    Call<BaseBean<List<ChapterBean>>> catalogs(@Body RequestBody body);

    @PUT("/appApi/learnRecords")
    Call<BaseBean<LearnRecordBean>> learnRecords(@Body RequestBody body);

    @POST("/appApi/catalogInfo/{catalogId}")
    Call<BaseBean<Catalog>> catalogInfo(@Body RequestBody body);
}
