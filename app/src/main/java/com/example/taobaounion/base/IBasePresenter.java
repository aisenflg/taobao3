package com.example.taobaounion.base;

public interface IBasePresenter<T> {


    /**
     * 注册UI的通知接口
     * @param callback
     */
    void registerViewCallback(T callback);

    /**
     * 取消UI通知更新的接口
     * @param callback
     */
    void unregisterViewCallback(T callback);
}
