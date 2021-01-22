package com.example.taobaounion.utils;

import com.example.taobaounion.presenter.ICategoryPagerPresenter;
import com.example.taobaounion.presenter.IHomePresenter;
import com.example.taobaounion.presenter.ITicketPresenter;
import com.example.taobaounion.presenter.impl.CategoryPagerPresenterImpl;
import com.example.taobaounion.presenter.impl.HomePresenterImpl;
import com.example.taobaounion.presenter.impl.TickerPresenterImpl;

public class PresenterManger {

    private static final PresenterManger ourInstance = new PresenterManger();
    private final ICategoryPagerPresenter mICategoryPagerPresenter;
    private final IHomePresenter mHomePresenter;
    private final ITicketPresenter mTickerPresenter;

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

    private PresenterManger(){
        mICategoryPagerPresenter =new CategoryPagerPresenterImpl();
        mHomePresenter = new HomePresenterImpl();
        mTickerPresenter = new TickerPresenterImpl();
    }

}
