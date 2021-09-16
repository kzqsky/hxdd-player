package com.edu.hxdd_player.api;

import android.content.Context;
import android.text.TextUtils;

import com.blankj.utilcode.util.ToastUtils;
import com.edu.hxdd_player.api.net.ApiCall;
import com.edu.hxdd_player.api.net.OkUtils;
import com.edu.hxdd_player.api.net.RetrofitFactory;
import com.edu.hxdd_player.bean.BaseBean;
import com.edu.hxdd_player.bean.LearnRecordBean;
import com.edu.hxdd_player.bean.parameters.BaseParameters;
import com.edu.hxdd_player.bean.parameters.GetChapter;
import com.edu.hxdd_player.bean.parameters.PutLearnRecords;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiUtils {
    private static ApiUtils instance;
    private Api api;
    private static Context context;

    private ApiUtils(Context context, String serverUrl) {
        api = RetrofitFactory.getInstance(context, serverUrl).create(Api.class);
        this.context = context;
    }

    public static ApiUtils getInstance(Context context, String serverUrl) {
        if (instance == null)
            instance = new ApiUtils(context, serverUrl);
        return instance;
    }

    private RequestBody getRequestBody(BaseParameters baseParameters) {
        return RequestBody.create(MediaType.parse("application/json"), baseParameters.toString());
    }

    public void getChapter(GetChapter parameters, ApiCall apiCall) {
        api.catalogs(getRequestBody(parameters)).enqueue(apiCall);
    }

    public void learnRecord(PutLearnRecords parameters, ApiCall apiCall) {
        api.learnRecords(getRequestBody(parameters)).enqueue(apiCall);
    }

    public void getChapterDetail(GetChapter parameters, String catalogId, ApiCall apiCall) {
        api.catalogInfo(getRequestBody(parameters), catalogId).enqueue(apiCall);
    }

    public void callBackUrl(String url, LearnRecordBean data) {
        if (data == null || TextUtils.isEmpty(url))
            return;
        OkHttpClient okHttpClient = OkUtils.getOkhttpBuilder().build();
        MediaType mediaType = MediaType.parse("text/x-markdown; charset=utf-8");
        Gson gson = new Gson();
        String requestBody = gson.toJson(data);
        final Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(mediaType, requestBody))
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastUtils.showLong("回传出错:" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    Gson gson = new Gson();
                    String data = response.body().string();
                    if (!TextUtils.isEmpty(data)) {
                        BaseBean<String> baseBean;
                        try {
                            baseBean = gson.fromJson(data, BaseBean.class);
                            if (baseBean != null && baseBean.success) {
                                ToastUtils.showLong("回传成功！！");
                            } else {
                                String message = "返回数据无法处理";
                                if (baseBean != null)
                                    message = baseBean.message;
                                ToastUtils.showLong("回传出错:" + message);
                            }
                        } catch (Exception e) {
                            ToastUtils.showLong("回传出错:" + e.getMessage());
                        }
                    }
                } else {
                    ToastUtils.showLong("回传出错:" + response.message());
                }
            }
        });
    }
}
