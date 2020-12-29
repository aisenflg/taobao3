package com.example.taobaounion.view;

import com.example.taobaounion.base.IBaseCallback;
import com.example.taobaounion.model.domain.Categories;

public interface IHomeCallback extends IBaseCallback {
    /**
     *
     */
    void onCategoriesLoaded(Categories categories);

}
