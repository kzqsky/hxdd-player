package com.edu.hxdd_player.api.net;


import com.edu.hxdd_player.bean.BaseBean;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public abstract class ApiCall<T> implements Callback<BaseBean<T>> {

    boolean showLoading;

    public ApiCall() {
        this(false);
    }

    public ApiCall(boolean showLoading) {

        this.showLoading = showLoading;

    }

    @Override
    public void onResponse(Call<BaseBean<T>> call, Response<BaseBean<T>> response) {
        BaseBean baseBean = null;
        try {
            baseBean = (BaseBean) response.body();
        } catch (Exception e) {
            onApiFailure("数据解析失败", 0);
            return;
        }
        if (baseBean != null) {
            if (baseBean.success == true) {
                onResult((T) baseBean.data);
            } else {
                onApiFailure(baseBean.message, baseBean.code);
            }
        } else {
            onApiFailure("服务器错误", 0);
        }
    }

    //    java.net.ConnectException: Failed to connect to /192.168.10.118:8888
    @Override
    public void onFailure(Call<BaseBean<T>> call, Throwable t) {
//        if (t instanceof java.net.ConnectException) {
//
//        } else {
//
//        }
    }

    protected abstract void onResult(T data);

    /**
     * 调用接口成功，但接口返回错误
     */
    public void onApiFailure(String message, int code) {
    }
}
