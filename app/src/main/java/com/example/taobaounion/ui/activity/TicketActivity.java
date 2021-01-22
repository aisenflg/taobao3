package com.example.taobaounion.ui.activity;

import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.taobaounion.R;
import com.example.taobaounion.base.BaseActivity;
import com.example.taobaounion.model.domain.TicketResult;
import com.example.taobaounion.presenter.ITicketPresenter;
import com.example.taobaounion.utils.PresenterManger;
import com.example.taobaounion.utils.UrlUtils;
import com.example.taobaounion.view.ITicketPagerCallback;

import butterknife.BindView;

public class TicketActivity extends BaseActivity implements ITicketPagerCallback {

    private ITicketPresenter mTickerPresenter;

    @BindView(R.id.ticket_cover)
    public ImageView mCover;
    @BindView(R.id.ticket_code)
    public EditText mTicketCode;
    @BindView(R.id.ticket_copy_or_open_btn)
    public TextView mOpenOrCopyBtn;


    @Override
    protected void initPresenter() {
        mTickerPresenter = PresenterManger.getInstance().getTickerPresenter();
        if (mTickerPresenter != null) {
            mTickerPresenter.registerViewCallback(this);
        }
    }

    @Override
    protected void initView() {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_ticket;
    }

    @Override
    public void onTicketLoaded(String cover, TicketResult result) {
        if (mCover != null && TextUtils.isEmpty(cover)) {
            Glide.with(this).load(UrlUtils.getCoverPath(cover)).into(mCover);
        }
        if (result != null && result.getData().getTbk_tpwd_create_response() != null) {
            mTicketCode.setText(result.getData().getTbk_tpwd_create_response().getData().getModel());
        }
    }

    @Override
    protected void release() {
        if (mTickerPresenter != null) {
            mTickerPresenter.unregisterViewCallback(this);
        }
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
