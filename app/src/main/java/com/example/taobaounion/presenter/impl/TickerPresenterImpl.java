package com.example.taobaounion.presenter.impl;

import com.example.taobaounion.model.Api;
import com.example.taobaounion.model.domain.TicketParams;
import com.example.taobaounion.model.domain.TicketResult;
import com.example.taobaounion.presenter.ITicketPresenter;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.RetrofitManager;
import com.example.taobaounion.utils.UrlUtils;
import com.example.taobaounion.view.ITicketPagerCallback;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TickerPresenterImpl implements ITicketPresenter {

    private ITicketPagerCallback mViewCallback = null;
    private String mCover = null;
    private TicketResult mResult;

    enum LoadState {
        LOADING,SUCCESS,ERROR,NONE
    }
    private LoadState mCurrentState = LoadState.NONE;

    @Override
    public void getTicket(String title, String url, String cover) {
        this.onLoadingTicketError();
        this.mCover = cover;
        String ticketUrl = UrlUtils.getTicketUrl(url);
        //去获取淘口令
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        Api api = retrofit.create(Api.class);
        TicketParams ticketParams = new TicketParams(ticketUrl, title);
        Call<TicketResult> task = api.getTicket(ticketParams);
        task.enqueue(new Callback<TicketResult>() {
            @Override
            public void onResponse(Call<TicketResult> call, Response<TicketResult> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mResult = response.body();
                    LogUtils.d(TickerPresenterImpl.this, mResult.toString());
                    //通知UI更新
                    onLoadedTicketSuccess();
                } else {
                    //请求失败
                    onLoadedTicketError();

                }
            }

            @Override
            public void onFailure(Call<TicketResult> call, Throwable t) {
                onLoadedTicketError();

            }
        });
    }

    private void onLoadedTicketSuccess() {
        if (mViewCallback != null) {
            mViewCallback.onTicketLoaded(mCover, mResult);
        }else {
            mCurrentState = LoadState.SUCCESS;
        }
    }

    private void onLoadedTicketError() {
        if (mViewCallback != null) {
            mViewCallback.onNetworkError();
        }else {
            mCurrentState = LoadState.ERROR;
        }
    }

    @Override
    public void registerViewCallback(ITicketPagerCallback callback) {
        if (mCurrentState != LoadState.NONE) {
            if (mCurrentState == LoadState.SUCCESS) {
                onLoadedTicketSuccess();
            }else if (mCurrentState == LoadState.ERROR){
                onLoadedTicketError();
            }else if (mCurrentState == LoadState.ERROR){
                onLoadingTicketError();
            }
            this.mViewCallback = callback;
        }
    }

    private void onLoadingTicketError() {
        if (mViewCallback != null) {
            mViewCallback.onLoading();
        }else {
            mCurrentState = LoadState.LOADING;
        }
    }

    @Override
    public void unregisterViewCallback(ITicketPagerCallback callback) {
        mViewCallback = null;
    }
}
