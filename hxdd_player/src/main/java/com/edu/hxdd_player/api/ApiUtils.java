package com.edu.hxdd_player.api;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.blankj.utilcode.util.ToastUtils;
import com.edu.hxdd_player.api.net.ApiCall;
import com.edu.hxdd_player.api.net.OkUtils;
import com.edu.hxdd_player.api.net.RetrofitFactory;
import com.edu.hxdd_player.bean.BaseBean;
import com.edu.hxdd_player.bean.parameters.BaseParameters;
import com.edu.hxdd_player.bean.parameters.GetChapter;
import com.edu.hxdd_player.bean.parameters.PutLearnRecords;
import com.edu.hxdd_player.utils.DialogUtils;
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
    private Context context;
    public String serverUrl;

    private ApiUtils(Context context, String serverUrl) {
        api = RetrofitFactory.getInstance(context, serverUrl).create(Api.class);
        this.context = context;
        this.serverUrl = serverUrl;
    }

    public static ApiUtils getInstance() {
        if (instance == null)
            return null;
        return instance;
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

    public void newLearnRecord(PutLearnRecords parameters, String action, ApiCall apiCall) {
        api.newLearnRecords(getRequestBody(parameters), action).enqueue(apiCall);
    }


    public void getChapterDetail(GetChapter parameters, String catalogId, ApiCall apiCall) {
        api.catalogInfo(getRequestBody(parameters), catalogId).enqueue(apiCall);
    }

    /**
     * 获取评课纠错等配置
     *
     * @param clientCode
     * @param apiCall
     */
    public void getClientConfig(String clientCode, ApiCall apiCall) {
        api.getClientConfig(clientCode).enqueue(apiCall);
    }

    /**
     * 获取课件相关信息-如老师 教材 讲义等
     *
     * @param coursewarecode
     * @param apiCall
     */
    public void getCourseInfo(String coursewarecode, ApiCall apiCall) {
        api.getCourseInfo(coursewarecode).enqueue(apiCall);
    }


    public void callBackUrl(String url, String json) {
        if (json == null || TextUtils.isEmpty(url))
            return;
        OkHttpClient okHttpClient = OkUtils.getOkhttpBuilder().build();
        MediaType mediaType = MediaType.parse("text/x-markdown; charset=utf-8");
//        Gson gson = new Gson();
//        String requestBody = gson.toJson(data);
        final Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(mediaType, json))
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastUtils.showLong("回传学习记录失败:" + showErrorMessage(e.getMessage()));
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
//                                ToastUtils.showLong("回传成功！！");
                            } else {
                                String message = "返回数据无法处理";
                                if (baseBean != null)
                                    message = baseBean.message;
                                ToastUtils.showLong("回传学习记录失败:" + message);
                                showErrorDialog("回传学习记录失败:" + message);
                            }
                        } catch (Exception e) {
                            ToastUtils.showLong("回传学习记录失败:" + showErrorMessage(e.getMessage()));
                            showErrorDialog("回传学习记录失败:" + showErrorMessage(e.getMessage()));
                        }
                    }
                } else {
                    ToastUtils.showLong("回传学习记录失败:" + showErrorMessage(response.message()));
                    showErrorDialog("回传学习记录失败:" + showErrorMessage(response.message()));
                }
            }
        });
    }

    private void showErrorDialog(String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                DialogUtils.showDialog(context, message);
            }
        });
    }

    private String showErrorMessage(String error) {
        String s = "未知错误";
        if (error != null) {
            s = error;
            if (error.toLowerCase().equals("timeout")) {
                s = "网络异常，请检查网络";
            }
        }
        return s;
    }

    public void clear() {
        RetrofitFactory.clear();
        context = null;
        instance = null;
    }
}
