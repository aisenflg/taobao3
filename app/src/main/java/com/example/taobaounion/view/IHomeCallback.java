package com.example.taobaounion.view;

import com.example.taobaounion.model.domain.Categories;

public interface IHomeCallback {
    /**
     *
     */
    void onCategoriesLoaded(Categories categories);

    void onNetworkError();

    void onLoading();

    void onEmpty();

}
