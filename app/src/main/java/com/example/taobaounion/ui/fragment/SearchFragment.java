package com.example.taobaounion.ui.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.taobaounion.R;
import com.example.taobaounion.base.BaseFragment;
import com.example.taobaounion.model.domain.SearchResult;
import com.example.taobaounion.presenter.ISearchPresenter;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.PresenterManger;
import com.example.taobaounion.view.ISearchPageCallback;

import java.util.List;

public class SearchFragment extends BaseFragment implements ISearchPageCallback {
    private ISearchPresenter mISearchPresenter;


    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_search;
    }

    @Override
    protected View loadRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_search_layout, container,false);
    }

    @Override
    protected void initPresenter() {
        mISearchPresenter = PresenterManger.getInstance().getISearchPresenter();
        mISearchPresenter.registerViewCallback(this);
        mISearchPresenter.getRecommendWords();
        mISearchPresenter.doSearch("键盘");
        mISearchPresenter.getHistories();
    }

    @Override
    protected void release() {
        mISearchPresenter.unregisterViewCallback(this);
    }

    @Override
    protected void initView(View rootView) {
        setUpState(State.SUCCESS);
    }

    @Override
    public void onHistoriesLoaded(List<String> histories) {
        LogUtils.d(this, histories.toString());
    }

    @Override
    public void onHistoriesDel(List<String> histories) {

    }

    @Override
    public void onSearch(SearchResult result) {

    }

    @Override
    public void onLoaderMore(SearchResult result) {

    }

    @Override
    public void onLoadedMoreError() {

    }

    @Override
    public void onLoadedMoreEmpty() {

    }

    @Override
    public void onNetworkError() {

    }

    @Override
    public void onLoading() {

    }

    @Override
    public void onEmpty() {

    }
}
