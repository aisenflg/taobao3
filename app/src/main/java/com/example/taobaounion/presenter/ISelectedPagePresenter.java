package com.example.taobaounion.presenter;

import com.example.taobaounion.base.IBasePresenter;
import com.example.taobaounion.model.domain.SelectedPageCategory;
import com.example.taobaounion.view.ISelectedPageCallback;

public interface ISelectedPagePresenter extends IBasePresenter<ISelectedPageCallback> {

    /**
     * 获取分类
     */
    void getCategories();

    /**
     * 获取分类内容
     */

    void getContentByCategoryId(SelectedPageCategory.DataBean item);

    /**
     * 重新加载
     */
    void reloadContent();
}
