package com.example.taobaounion.view;

import com.example.taobaounion.base.IBaseCallback;
import com.example.taobaounion.model.domain.SelectedContent;
import com.example.taobaounion.model.domain.SelectedPageCategory;

public interface ISelectedPageCallback extends IBaseCallback {

    /**
     * 分类内容加载回来
     * @param result
     */
    void onCategoryLoaded(SelectedPageCategory result);

    void onContentLoaded(SelectedContent content);
}
