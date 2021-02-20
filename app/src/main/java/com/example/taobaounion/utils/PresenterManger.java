package com.example.taobaounion.utils;

import com.example.taobaounion.presenter.ICategoryPagerPresenter;
import com.example.taobaounion.presenter.IHomePresenter;
import com.example.taobaounion.presenter.IOnSellPagePresenter;
import com.example.taobaounion.presenter.ISearchPresenter;
import com.example.taobaounion.presenter.ISelectedPagePresenter;
import com.example.taobaounion.presenter.ITicketPresenter;
import com.example.taobaounion.presenter.impl.CategoryPagerPresenterImpl;
import com.example.taobaounion.presenter.impl.HomePresenterImpl;
import com.example.taobaounion.presenter.impl.OnSellPagePresenterImpl;
import com.example.taobaounion.presenter.impl.SearchPresenterImpl;
import com.example.taobaounion.presenter.impl.SelectedPagePresenterImpl;
import com.example.taobaounion.presenter.impl.TickerPresenterImpl;

public class PresenterManger {

    private static final PresenterManger ourInstance = new PresenterManger();
    private final ICategoryPagerPresenter mICategoryPagerPresenter;
    private final IHomePresenter mHomePresenter;
    private final ITicketPresenter mTickerPresenter;
    private final ISelectedPagePresenter mISelectedPagePresenter;
    private final IOnSellPagePresenter mOnSellPagePresenter;
    private final ISearchPresenter mISearchPresenter;


    public static PresenterManger getInstance() {
        return ourInstance;
    }

    public ICategoryPagerPresenter getICategoryPagerPresenter() {
        return mICategoryPagerPresenter;
    }

    public IHomePresenter getHomePresenter() {
        return mHomePresenter;
    }

    public ITicketPresenter getTickerPresenter() {
        return mTickerPresenter;
    }

    public ISelectedPagePresenter getISelectedPagePresenter(){
        return mISelectedPagePresenter;
    }

    public IOnSellPagePresenter getIOnSellPagePresenter(){
        return mOnSellPagePresenter;
    }

    public ISearchPresenter getISearchPresenter() {
        return mISearchPresenter;
    }

    private PresenterManger(){
        mICategoryPagerPresenter =new CategoryPagerPresenterImpl();
        mHomePresenter = new HomePresenterImpl();
        mTickerPresenter = new TickerPresenterImpl();
        mISelectedPagePresenter = new SelectedPagePresenterImpl();
        mOnSellPagePresenter = new OnSellPagePresenterImpl();
        mISearchPresenter = new SearchPresenterImpl();
    }

}
