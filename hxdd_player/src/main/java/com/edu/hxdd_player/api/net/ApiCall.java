package com.edu.hxdd_player.api.net;

import com.blankj.utilcode.util.ToastUtils;
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
            ToastUtils.showLong("数据解析失败");
            return;
        }
        if (baseBean != null) {
            if (baseBean.success == true) {
                onResult((T) baseBean.data);
            } else {
                ToastUtils.showLong(baseBean.message);
                onApiFailure();
            }
        }
    }

    @Override
    public void onFailure(Call<BaseBean<T>> call, Throwable t) {
        ToastUtils.showLong("获取失败,请重试");
    }

    protected abstract void onResult(T data);

    /**
     * 调用接口成功，但接口返回错误
     */
    public void onApiFailure() {
    }
}
