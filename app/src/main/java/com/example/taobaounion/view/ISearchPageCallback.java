package com.example.taobaounion.view;

import com.example.taobaounion.base.IBaseCallback;
import com.example.taobaounion.model.domain.Histories;
import com.example.taobaounion.model.domain.SearchRecommend;
import com.example.taobaounion.model.domain.SearchResult;

import java.util.List;

public interface ISearchPageCallback extends IBaseCallback {

    /**
     * 搜索历史
     * @param histories
     */
    void onHistoriesLoaded(Histories histories);

    /**
     * 删除搜索历史
     */
    void onHistoriesDel();

    /**
     * 获取推荐词
     * @param recommendWords
     */
    void onRecommendWordsLoaded(List<SearchRecommend.DataBean> recommendWords);


    /**
     * 搜索
     * @param result
     */
    void onSearch(SearchResult result);

    /**
     * 获取更多搜索结果
     * @param result
     */
    void onLoaderMore(SearchResult result);

    /**
     * 获取更多失败
     */
    void onLoadedMoreError();

    /**
     * 没有更多数据
     */
    void onLoadedMoreEmpty();

}
