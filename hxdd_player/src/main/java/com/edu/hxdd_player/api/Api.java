package com.edu.hxdd_player.api;

import com.edu.hxdd_player.bean.BaseBean;
import com.edu.hxdd_player.bean.ChapterBean;
import com.edu.hxdd_player.bean.ClientConfigBean;
import com.edu.hxdd_player.bean.CourseInfoBean;
import com.edu.hxdd_player.bean.media.Catalog;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {
    @POST("/appApi/catalogs")
    Call<BaseBean<List<ChapterBean>>> catalogs(@Body RequestBody body);

    @PUT("/appApi/learnRecords")
    Call<BaseBean<Object>> learnRecords(@Body RequestBody body);

    @POST("/appApi/catalogInfo/{catalogId}")
    Call<BaseBean<Catalog>> catalogInfo(@Body RequestBody body, @Path("catalogId") String catalogId);

    @GET("/appApi/client")
    Call<BaseBean<ClientConfigBean>> getClientConfig(@Query("clientCode") String clientCode);

    @PUT("/client/learnRecords/{action}")
    Call<BaseBean<Object>> newLearnRecords(@Body RequestBody body, @Path("action") String action);

    @GET("/client/coursewares/{coursewarecode}/findCwByCode")
    Call<BaseBean<CourseInfoBean>> getCourseInfo(@Path("coursewarecode") String coursewarecode);
}
