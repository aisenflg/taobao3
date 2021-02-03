package com.example.taobaounion.presenter.impl;

import com.example.taobaounion.model.Api;
import com.example.taobaounion.model.domain.SelectedContent;
import com.example.taobaounion.model.domain.SelectedPageCategory;
import com.example.taobaounion.presenter.ISelectedPagePresenter;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.RetrofitManager;
import com.example.taobaounion.utils.UrlUtils;
import com.example.taobaounion.view.ISelectedPageCallback;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SelectedPagePresenterImpl implements ISelectedPagePresenter {

    private final Api mApi;

    private ISelectedPageCallback mCallback = null;


    public SelectedPagePresenterImpl(){
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        mApi = retrofit.create(Api.class);
    }

    @Override
    public void getCategories() {
        onLoading();
        //加载分类内容
        Call<SelectedPageCategory> task = mApi.getSelectedCategories();
        task.enqueue(new Callback<SelectedPageCategory>() {
            @Override
            public void onResponse(Call<SelectedPageCategory> call, Response<SelectedPageCategory> response) {
                //请求成功
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    SelectedPageCategory categories = response.body();
                    //数据结果
                    if (mCallback != null) {
                        mCallback.onCategoryLoaded(categories);
                    }
                } else {
                    //请求失败
                    onLoadedError();
                    LogUtils.d(SelectedPagePresenterImpl.this, "请求失败");
                }
            }

            @Override
            public void onFailure(Call<SelectedPageCategory> call, Throwable t) {
                //加载失败
                mCallback.onNetworkError();
                LogUtils.d(SelectedPagePresenterImpl.this, "请求失败");

            }
        });

    }

    private void onLoadedError() {
        if (mCallback != null) {
            mCallback.onNetworkError();
        }
    }

    @Override
    public void getContentByCategoryId(SelectedPageCategory.DataBean item) {
        String url = UrlUtils.getSelectedPageContentUrl(item.getFavorites_id());
        Call<SelectedContent> task = mApi.getSelectedContent(url);
        task.enqueue(new Callback<SelectedContent>() {
            @Override
            public void onResponse(Call<SelectedContent> call, Response<SelectedContent> response) {
                //请求成功
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    SelectedContent content = response.body();
                    //数据结果
                    LogUtils.d(SelectedPagePresenterImpl.this, "result is ------>" + content.toString());
                    if (mCallback != null) {
                        mCallback.onContentLoaded(content);
                    }
                } else {
                    //请求失败
                    LogUtils.d(SelectedPagePresenterImpl.this, "请求失败");
                }
            }

            @Override
            public void onFailure(Call<SelectedContent> call, Throwable t) {
                onLoadedError();
                LogUtils.d(SelectedPagePresenterImpl.this,t.getMessage());
            }
        });
    }

    private void onLoading() {
        if (mCallback != null) {
            mCallback.onLoading();
        }
    }

    @Override
    public void reloadContent() {
        this.getCategories();
    }

    @Override
    public void registerViewCallback(ISelectedPageCallback callback) {
        mCallback = callback;
    }

    @Override
    public void unregisterViewCallback(ISelectedPageCallback callback) {
        mCallback = null;
    }
}
