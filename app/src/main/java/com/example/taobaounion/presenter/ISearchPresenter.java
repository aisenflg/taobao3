package com.example.taobaounion.presenter;

import com.example.taobaounion.base.IBasePresenter;
import com.example.taobaounion.view.ISearchPageCallback;

public interface ISearchPresenter extends IBasePresenter<ISearchPageCallback> {

    /**
     * 搜索历史
     */
    void getHistories();

    /**
     * 删除搜索历史
     */
    void delHistories();

    /**
     * 搜索
     * @param keyword
     */
    void doSearch(String keyword);

    /**
     * 重试
     */
    void reSearch();

    /**
     * 获取更多搜索结果
     */
    void loaderMore();

    /**
     * 热门搜索
     */
    void getRecommendWords();
}
