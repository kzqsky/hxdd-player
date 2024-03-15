package com.edu.hxdd_player.api.net;

import android.content.Context;
import android.text.TextUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by yu on 2017/2/21.
 */

public class RetrofitFactory {
    private static OkHttpClient client;
    private static Retrofit retrofit;

    private static String HOST = "http://cws.edu-edu.com";//测试地址
//    private static final String HOST = "http://dpxapp1.0.entengdz.com";//正式地址
//    private static final String HOST = BuildConfig.URL;

    public static Retrofit getInstance(Context context, String serverUrl) {
        if (retrofit == null) {
            if (!TextUtils.isEmpty(serverUrl))
                HOST = serverUrl;
            initRetrofit(context);
        }
        return retrofit;
    }

    private static void initRetrofit(Context context) {
        //拦截器—修改url
        Interceptor mTokenInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                Request oldRequest = chain.request();
//
//                //拿到拥有以前的request里的url的那些信息的builder
//                HttpUrl.Builder builder = oldRequest
//                        .url()
//                        .newBuilder();
//                String version = BuildConfig.VERSION_NAME;
//                String uid = SPUtils.getInstance().getString("uid");
//                if (TextUtils.isEmpty(uid)) {
//                    uid = "0";
//                }
//                String time = System.currentTimeMillis() + "";
//                String token = MathUtils.md5(uid + "dingpanxing2018" + time + version);
//                //得到新的url（已经追加好了参数）
//                HttpUrl newUrl = builder.addQueryParameter("version", version)
//                        .addQueryParameter("timestamp", time)
//                        .addQueryParameter("app", "android")
//                        .addQueryParameter("uid", uid)
//                        .addQueryParameter("token", token)
//                        .build();
//
//                //利用新的Url，构建新的request，并发送给服务器
//                Request newRequest = oldRequest
//                        .newBuilder()
//                        .url(newUrl)
//                        .build();
//                Response response = chain.proceed(newRequest);

                Response response = chain.proceed(oldRequest);
                return response;
            }
        };
        client = RetrofitUrlManager.getInstance().with(OkUtils.getOkhttpBuilder())
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .callTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(mTokenInterceptor)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(HOST)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static void clear() {
        client = null;
        retrofit = null;
    }

}
