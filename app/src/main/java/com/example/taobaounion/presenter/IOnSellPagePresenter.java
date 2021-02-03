package com.example.taobaounion.presenter;

import com.example.taobaounion.base.IBasePresenter;
import com.example.taobaounion.view.IOnSellPageCallback;

public interface IOnSellPagePresenter extends IBasePresenter<IOnSellPageCallback> {

    /**
     * 加载特惠内容
     */
    void getOnSellContent();

    /**
     * 重新加载内容
     */

    void reLoad();

    /**
     * 加载更多
     */
    void loadMore();
}
