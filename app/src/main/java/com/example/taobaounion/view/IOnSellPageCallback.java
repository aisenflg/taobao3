package com.example.taobaounion.view;

import com.example.taobaounion.base.IBaseCallback;
import com.example.taobaounion.model.domain.OnSellContent;

public interface IOnSellPageCallback extends IBaseCallback {

    /**
     * 特惠内容加载完成
     * @param result
     */
    void onContentLoadSuccess(OnSellContent result);

    /**
     *加载更多
     */
    void onMoreLoaded(OnSellContent moreResult);

    /**
     * 加载更多出错
     */
    void onMoreLoadedError();

    /**
     * 没有更多内容了
     */
    void onMoreLoadedEmpty();
}
