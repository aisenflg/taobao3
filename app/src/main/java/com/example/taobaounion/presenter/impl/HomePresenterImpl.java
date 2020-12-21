package com.example.taobaounion.presenter.impl;

import com.example.taobaounion.model.Api;
import com.example.taobaounion.model.domain.Categories;
import com.example.taobaounion.presenter.IHomePresenter;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.RetrofitManager;
import com.example.taobaounion.view.IHomeCallback;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomePresenterImpl implements IHomePresenter {

    private IHomeCallback mCallback = null;

    @Override
    public void getCategories() {
        if (mCallback != null) {
            mCallback.onLoading();
        }
        //加载分类内容
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        Api api = retrofit.create(Api.class);
        Call<Categories> task = api.getCategories();
        task.enqueue(new Callback<Categories>() {
            @Override
            public void onResponse(Call<Categories> call, Response<Categories> response) {
                //数据结果
                LogUtils.d(HomePresenterImpl.this, "result code is ------>" + response.code());
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    //请求成功
                    Categories categories = response.body();
                    if (mCallback != null) {
                        if (categories == null || categories.getData().size() == 0) {
                            mCallback.onEmpty();
                        } else {

                            mCallback.onCategoriesLoaded(categories);
                        }
                    }
                } else {
                    //请求失败
                    if (mCallback != null) {
                        mCallback.onNetworkError();
                    }
                    LogUtils.d(HomePresenterImpl.this, "请求失败");

                }
            }

            @Override
            public void onFailure(Call<Categories> call, Throwable e) {
                //加载失败
                LogUtils.e(HomePresenterImpl.this, "请求错误" + e.toString());
            }
        });
    }

    @Override
    public void registerCallback(IHomeCallback callback) {
        mCallback = callback;
    }

    @Override
    public void unregisterCallback(IHomeCallback callback) {
        mCallback = null;
    }
}
