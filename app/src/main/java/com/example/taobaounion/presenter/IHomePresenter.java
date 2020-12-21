package com.example.taobaounion.presenter;

import com.example.taobaounion.view.IHomeCallback;

public interface IHomePresenter {
    /**
     * 获取商品分类
     */
    void getCategories();

    /**
     * 注册UI的通知接口
     * @param callback
     */
    void registerCallback(IHomeCallback callback);

    /**
     * 取消UI通知更新的接口
     * @param callback
     */
    void unregisterCallback(IHomeCallback callback);

}
