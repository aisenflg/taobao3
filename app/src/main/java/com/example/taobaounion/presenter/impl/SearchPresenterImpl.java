package com.example.taobaounion.presenter.impl;

import com.example.taobaounion.model.Api;
import com.example.taobaounion.model.domain.Histories;
import com.example.taobaounion.model.domain.SearchRecommend;
import com.example.taobaounion.model.domain.SearchResult;
import com.example.taobaounion.presenter.ISearchPresenter;
import com.example.taobaounion.utils.JsonCacheUtil;
import com.example.taobaounion.utils.RetrofitManager;
import com.example.taobaounion.utils.ToastUtils;
import com.example.taobaounion.view.ISearchPageCallback;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchPresenterImpl implements ISearchPresenter {

    private ISearchPageCallback mCallback;
    private final Api mApi;
    private static final int DEFAULT_PAGE = 0;
    private int mCurrentPage = DEFAULT_PAGE;
    private String mCurrentKeyword;
    private final JsonCacheUtil mJsonCacheUtil;

    public SearchPresenterImpl() {
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        mApi = retrofit.create(Api.class);
        mJsonCacheUtil = JsonCacheUtil.getInstance();
    }


    @Override
    public void getHistories() {
        Histories histories = mJsonCacheUtil.getValue(KEY_HISTORY, Histories.class);
        if (mCallback != null) {
            mCallback.onHistoriesLoaded(histories);
        }
    }

    @Override
    public void delHistories() {
        mJsonCacheUtil.delCache(KEY_HISTORY);
        if (mCallback !=null) {
            mCallback.onHistoriesDel();
        }

    }

    public static final String KEY_HISTORY = "key_history";
    public static final int DEFAULT_HISTORIES_SIZE = 10;
    private int mHistoryMaxSize = DEFAULT_HISTORIES_SIZE;

    /**
     * 添加历史记录
     *
     * @param history
     */
    private void saveHistory(String history) {
        Histories histories = mJsonCacheUtil.getValue(KEY_HISTORY, Histories.class);
        //如果已经存在,就干掉,然后在添加
        List<String> historiesList = null;
        if (histories != null && histories.getHistories() != null) {
            historiesList = histories.getHistories();
            if (historiesList.contains(history)) {
                historiesList.remove(history);
            }
        }
        //去重完成
        //处理没有数据的情况
        if (historiesList == null) {
            historiesList = new ArrayList<>();
        }
        if (histories == null) {
            histories = new Histories();
            histories.setHistories(historiesList);
        }
        //对个数进行限制
        if (historiesList.size() > mHistoryMaxSize) {
            historiesList = historiesList.subList(0, mHistoryMaxSize);
        }
        //添加记录
        historiesList.add(history);
        //保存记录
        mJsonCacheUtil.saveCache(KEY_HISTORY, histories);

    }


    @Override
    public void doSearch(String keyword) {
        Map<String, Object> params = new HashMap<>();

        if (mCallback != null) {
            this.saveHistory(keyword);
            mCurrentKeyword = keyword;
        }
        mCallback.onLoading();
        params.put("keyword", keyword);
        params.put("page", DEFAULT_PAGE);
        Call<SearchResult> task = mApi.doSearch(params);
        task.enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    SearchResult result = response.body();
                    if (result != null) {
                        if (mCallback != null) {
                            mCallback.onSearch(result);
                        }
                    } 
                } else {
                    onNetworkError();
                }
            }

            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {
                onNetworkError();
            }
        });

    }

    private void onNetworkError() {
        if (mCallback != null) {
            mCallback.onNetworkError();
            ToastUtils.showToast("网络出错请重试");
        }
    }

    @Override
    public void reSearch() {
        if (mCurrentKeyword == null) {
            if (mCallback != null) {
                mCallback.onEmpty();
            }
        } else {
            this.doSearch(mCurrentKeyword);
        }
    }

    @Override
    public void loaderMore() {
        mCurrentPage++;
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", mCurrentKeyword);
        params.put("page", DEFAULT_PAGE);
        Call<SearchResult> task = mApi.doSearch(params);
        task.enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    SearchResult result = response.body();
                    if (result != null) {
                        if (mCallback != null) {
                            onLoaderMore(result);
                        }
                    } else {
                        onMoreLoadedError();
                    }
                } else {
                    onMoreLoadedError();
                }
            }

            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {
                onNetworkError();
            }
        });

    }

    @Override
    public void getRecommendWords() {
        Call<SearchRecommend> task = mApi.getRecommendWords();
        task.enqueue(new Callback<SearchRecommend>() {
            @Override
            public void onResponse(Call<SearchRecommend> call, Response<SearchRecommend> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    SearchRecommend result = response.body();
                    if (mCallback != null) {
                        List<SearchRecommend.DataBean> recommendWords = new ArrayList<>();
                        for (SearchRecommend.DataBean data : result.getData()) {
                            recommendWords.add(data);
                        }
                        mCallback.onRecommendWordsLoaded(recommendWords);
                    }
                }
            }

            @Override
            public void onFailure(Call<SearchRecommend> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void onMoreLoadedError() {
        mCurrentPage--;
        mCallback.onLoadedMoreError();
    }

    private void onLoaderMore(SearchResult result) {
        try {
            if (isEmpty(result)) {
                mCallback.onLoadedMoreEmpty();
            } else {
                mCallback.onLoaderMore(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mCallback.onLoadedMoreEmpty();
        }
    }

    private boolean isEmpty(SearchResult content) {
        int size = content.getData().getTbk_dg_material_optional_response().getResult_list().getMap_data().size();
        return size == 0;
    }

    @Override
    public void registerViewCallback(ISearchPageCallback callback) {
        mCallback = callback;
    }

    @Override
    public void unregisterViewCallback(ISearchPageCallback callback) {
        mCallback = null;
    }
}
