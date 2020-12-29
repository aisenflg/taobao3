package com.example.taobaounion.view;

import com.example.taobaounion.base.IBaseCallback;
import com.example.taobaounion.model.domain.HomePagerContent;

import java.util.List;

public interface ICategoryPagerCallback extends IBaseCallback {

    /**
     * 数据加载回来
     * @param contents
     */
    void onContentLoad(List<HomePagerContent.DataBean> contents);

    /**
     * 加载更多数据
     * @param contents
     */
    void onLoadMoreLoaded(List<HomePagerContent.DataBean> contents);

    /**
     * 轮播图
     * @param contents
     */
    void onLooperListLoaded(List<HomePagerContent.DataBean> contents);

    int getCategoryId();


    /**
     * 加载更多出错
     */
    void onLoadMoreError();

    /**
     * 没有更多数据
     */
    void onLoadMoreEmpty();


}
