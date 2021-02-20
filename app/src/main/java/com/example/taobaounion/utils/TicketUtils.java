package com.example.taobaounion.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.example.taobaounion.model.domain.IBaseInfo;
import com.example.taobaounion.presenter.ITicketPresenter;
import com.example.taobaounion.ui.activity.TicketActivity;

public class TicketUtils {

    private static ITicketPresenter mTickerPresenter;

    public static void toTicketPage(IBaseInfo baseInfo, Context context){

        mTickerPresenter = PresenterManger.getInstance().getTickerPresenter();
        //领劵界面
        String url = baseInfo.getUrl();
        //没有卷时,跳转到购买界面
        if (TextUtils.isEmpty(url)) {
            //购买界面
            url = baseInfo.getUrl();
        }
        mTickerPresenter.getTicket(baseInfo.getTitle(), url, baseInfo.getCover());
        context.startActivity(new Intent(context, TicketActivity.class));
    }
}
