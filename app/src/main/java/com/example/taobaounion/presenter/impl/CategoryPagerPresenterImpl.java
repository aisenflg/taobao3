package com.example.taobaounion.presenter.impl;

import com.example.taobaounion.model.Api;
import com.example.taobaounion.model.domain.HomePagerContent;
import com.example.taobaounion.presenter.ICategoryPagerPresenter;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.RetrofitManager;
import com.example.taobaounion.utils.UrlUtils;
import com.example.taobaounion.view.ICategoryPagerCallback;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CategoryPagerPresenterImpl implements ICategoryPagerPresenter {

    private Map<Integer, Integer> pagerInfo = new HashMap<>();
    public static final int DEFAULT_PAGE = 1;
    private Integer mCurrentPage;

    @Override
    public void getContentByCategoryId(int categoryId) {
        for (ICategoryPagerCallback callback : callbacks) {
            if (callback.getCategoryId() == categoryId) {
                callback.onLoading();
            }
        }
        //根据分类id去加载类容
        Integer targetPage = pagerInfo.get(categoryId);
        if (targetPage == null) {
            targetPage = DEFAULT_PAGE;
            pagerInfo.put(categoryId, targetPage);
        }
        Call<HomePagerContent> task = createTask(categoryId, targetPage);
        task.enqueue(new Callback<HomePagerContent>() {
            @Override
            public void onResponse(Call<HomePagerContent> call, Response<HomePagerContent> response) {
                LogUtils.d(CategoryPagerPresenterImpl.this, "code-------->" + response.code());
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    HomePagerContent pageContent = response.body();
                    LogUtils.d(CategoryPagerPresenterImpl.this, pageContent.toString());
                    //更新UI
                    handlerHomePagerContentResult(pageContent, categoryId);
                } else {
                    handlerNetworkError(categoryId);
                }
            }

            @Override
            public void onFailure(Call<HomePagerContent> call, Throwable t) {
                LogUtils.d(CategoryPagerPresenterImpl.this, "onFailure-------->" + t.toString());
                handlerNetworkError(categoryId);
            }
        });
    }

    private Call<HomePagerContent> createTask(int categoryId, Integer targetPage) {
        String homePagerUrl = UrlUtils.createHomePagerUrl(categoryId, targetPage);
        LogUtils.d(this, "homePagerUrl--------------->" + homePagerUrl);
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        Api api = retrofit.create(Api.class);
        return api.getHomePagerContent(homePagerUrl);
    }

    private void handlerNetworkError(int categoryId) {
        for (ICategoryPagerCallback callback : callbacks) {
            if (callback.getCategoryId() == categoryId) {
                callback.onNetworkError();
            }
        }
    }

    private void handlerHomePagerContentResult(HomePagerContent pageContent, int categoryId) {
        //通知UI层更新数据
        List<HomePagerContent.DataBean> data = pageContent.getData();
        for (ICategoryPagerCallback callback : callbacks) {
            if (callback.getCategoryId() == categoryId) {
                if (pageContent == null || pageContent.getData().size() == 0) {
                    callback.onEmpty();
                } else {
                    List<HomePagerContent.DataBean> looperData = data.subList(data.size() - 5, data.size());
                    callback.onLooperListLoaded(looperData);
                    callback.onContentLoad(data);
                }
            }
        }
    }

    @Override
    public void loadMore(int categoryId) {
        //加载更多数据
        mCurrentPage = pagerInfo.get(categoryId);
        if (pagerInfo == null) {
            mCurrentPage = 1;
        }
        Call<HomePagerContent> task = createTask(categoryId, mCurrentPage++);
        task.enqueue(new Callback<HomePagerContent>() {
            @Override
            public void onResponse(Call<HomePagerContent> call, Response<HomePagerContent> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    HomePagerContent result = response.body();
                    LogUtils.d(CategoryPagerPresenterImpl.this, "result---->"+result.toString());
                    handlerLoadResult(result,categoryId);
                }else {
                    handlerLoadMoreError(categoryId);
                }
            }

            @Override
            public void onFailure(Call<HomePagerContent> call, Throwable t) {
                LogUtils.d(CategoryPagerPresenterImpl.this, t.getMessage());
                handlerLoadMoreError(categoryId);
            }
        });
    }

    /**
     * 加载更多数据成功
     * @param result
     * @param categoryId
     */
    private void handlerLoadResult(HomePagerContent result, int categoryId) {
        for (ICategoryPagerCallback callback : callbacks) {
            if (callback.getCategoryId() == categoryId) {
                if (result == null || result.getData().size() == 0) {
                    callback.onLoadMoreEmpty();
                }else {
                    callback.onLoadMoreLoaded(result.getData());
                }
            }
        }
    }

    /**
     * 加载更多数据失败
     * @param categoryId
     */
    private void handlerLoadMoreError(int categoryId) {
        mCurrentPage--;
        pagerInfo.put(categoryId, mCurrentPage);
        for (ICategoryPagerCallback callback : callbacks) {
            if (callback.getCategoryId() == categoryId) {
                callback.onLoadMoreError();
            }
        }
    }

    @Override
    public void reload(int categoryId) {

    }

    private List<ICategoryPagerCallback> callbacks = new ArrayList<>();

    @Override
    public void registerViewCallback(ICategoryPagerCallback callback) {
        if (!callbacks.contains(callback)) {
            callbacks.add(callback);
        }
    }

    @Override
    public void unregisterViewCallback(ICategoryPagerCallback callback) {
        callbacks.remove(callback);
    }
}
