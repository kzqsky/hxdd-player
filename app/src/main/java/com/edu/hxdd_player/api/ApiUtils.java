package com.edu.hxdd_player.api;

import android.content.Context;

import com.edu.hxdd_player.api.net.ApiCall;
import com.edu.hxdd_player.api.net.RetrofitFactory;
import com.edu.hxdd_player.bean.parameters.BaseParameters;
import com.edu.hxdd_player.bean.parameters.GetChapter;
import com.edu.hxdd_player.bean.parameters.PutLearnRecords;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class ApiUtils {
    private static ApiUtils instance;
    private Api api;
    private static Context context;

    private ApiUtils(Context context) {
        api = RetrofitFactory.getInstance(context).create(Api.class);
        this.context = context;
    }

    public static ApiUtils getInstance(Context context) {
        if (instance == null)
            instance = new ApiUtils(context);
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
}
