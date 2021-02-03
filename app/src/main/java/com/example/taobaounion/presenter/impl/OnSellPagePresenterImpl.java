package com.example.taobaounion.presenter.impl;

import com.example.taobaounion.model.Api;
import com.example.taobaounion.model.domain.OnSellContent;
import com.example.taobaounion.presenter.IOnSellPagePresenter;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.RetrofitManager;
import com.example.taobaounion.utils.UrlUtils;
import com.example.taobaounion.view.IOnSellPageCallback;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OnSellPagePresenterImpl implements IOnSellPagePresenter {

    private final Api mApi;
    private IOnSellPageCallback mCallback;
    private static final int DEFAULT_PAGE = 1;
    private int mCurrentPage = DEFAULT_PAGE;

    /**
     * 当前加载状态
     */
    private boolean isLoading = false;

    public OnSellPagePresenterImpl() {
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        mApi = retrofit.create(Api.class);
    }

    @Override
    public void getOnSellContent() {
        if (isLoading) {
            return;
        }
        isLoading = true;
        //通知UI为加载状态
        if (mCallback != null) {
            mCallback.onLoading();
        }
        Call<OnSellContent> task = mApi.getOnSellContent(UrlUtils.getOnSellPageUrl(mCurrentPage));
        task.enqueue(new Callback<OnSellContent>() {
            @Override
            public void onResponse(Call<OnSellContent> call, Response<OnSellContent> response) {
                isLoading = false;
                //请求成功
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    OnSellContent result = response.body();
                    if (mCallback != null) {
                        onSuccess(result);
                    }
                } else {
                    //请求失败
                    onLoadedError();
                    LogUtils.d(OnSellPagePresenterImpl.this, "请求失败");
                }
            }

            @Override
            public void onFailure(Call<OnSellContent> call, Throwable t) {
                //请求失败
                onLoadedError();
                LogUtils.d(OnSellPagePresenterImpl.this, "请求失败");
            }

        });
    }



    @Override
    public void loadMore() {
        if (isLoading) {
            return;
        }
        isLoading = true;
        //页码+1
        mCurrentPage++;
        Call<OnSellContent> task = mApi.getOnSellContent(UrlUtils.getOnSellPageUrl(mCurrentPage));
        task.enqueue(new Callback<OnSellContent>() {
            @Override
            public void onResponse(Call<OnSellContent> call, Response<OnSellContent> response) {
                isLoading = false;
                //请求成功
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    OnSellContent result = response.body();
                    if (mCallback != null) {
                        onLoaderMore(result);
                    }
                } else {
                    //请求失败
                    onMoreLoadedError();
                    LogUtils.d(OnSellPagePresenterImpl.this, "请求失败");
                }
            }

            @Override
            public void onFailure(Call<OnSellContent> call, Throwable t) {
                //请求失败
                onMoreLoadedError();
                LogUtils.d(OnSellPagePresenterImpl.this, "请求失败");
            }

        });
    }



    private boolean isEmpty(OnSellContent content) {
        int size = content.getData().getTbk_dg_optimus_material_response().getResult_list().getMap_data().size();
        return size == 0;
    }

    private void onLoaderMore(OnSellContent result) {
        try {
            if (isEmpty(result)) {
                mCallback.onMoreLoadedEmpty();
            } else {
                mCallback.onMoreLoaded(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mCallback.onMoreLoadedEmpty();
        }
    }

    private void onSuccess(OnSellContent result) {
        try {
            if (isEmpty(result)) {
                onEmpty();
            } else {
                mCallback.onContentLoadSuccess(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            onEmpty();
        }
    }

    private void onMoreLoadedError() {
        isLoading = false;
        mCurrentPage--;
        mCallback.onMoreLoadedError();
    }

    @Override
    public void reLoad() {
        this.getOnSellContent();
    }



    private void onEmpty() {
        if (mCallback != null) {
            mCallback.onEmpty();
        }
    }

    private void onLoading() {
        if (mCallback != null) {
            mCallback.onLoading();
        }
    }

    private void onLoadedError() {
        isLoading = false;
        if (mCallback != null) {
            mCallback.onNetworkError();
        }
    }



    @Override
    public void registerViewCallback(IOnSellPageCallback callback) {
        mCallback = callback;
    }

    @Override
    public void unregisterViewCallback(IOnSellPageCallback callback) {
        mCallback = null;
    }


}
