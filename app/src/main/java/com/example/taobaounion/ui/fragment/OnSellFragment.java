package com.example.taobaounion.ui.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taobaounion.R;
import com.example.taobaounion.base.BaseFragment;
import com.example.taobaounion.model.domain.OnSellContent;
import com.example.taobaounion.presenter.IOnSellPagePresenter;
import com.example.taobaounion.presenter.ITicketPresenter;
import com.example.taobaounion.ui.activity.TicketActivity;
import com.example.taobaounion.ui.adapter.OnSellContentAdapter;
import com.example.taobaounion.utils.PresenterManger;
import com.example.taobaounion.utils.SizeUtils;
import com.example.taobaounion.utils.ToastUtils;
import com.example.taobaounion.view.IOnSellPageCallback;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import butterknife.BindView;

public class OnSellFragment extends BaseFragment implements IOnSellPageCallback, OnSellContentAdapter.onSellItemClickListener {

    public static final int DEFAULT_SPAN = 2;

    private IOnSellPagePresenter mPresenter;

    @BindView(R.id.on_sell_content_list)
    public RecyclerView mContentRv;

    @BindView(R.id.on_sell_refresh_layout)
    public TwinklingRefreshLayout mRefreshLayout;

    @BindView(R.id.frag_bar_title_tv)
    public TextView titleTv;

    private OnSellContentAdapter mAdapter;
    private ITicketPresenter mTickerPresenter;

    @Override
    protected void initPresenter() {
        mPresenter = PresenterManger.getInstance().getIOnSellPagePresenter();
        mPresenter.registerViewCallback(this);
        mPresenter.getOnSellContent();
    }

    @Override
    protected void release() {
        if (mPresenter != null) {
            mPresenter.unregisterViewCallback(this);
        }
    }


    @Override
    protected View loadRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_with_bar_layout, container,false);
    }

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_on_sell;
    }


    @Override
    protected void initView(View rootView) {
        setUpState(State.SUCCESS);
        titleTv.setText(R.string.text_on_sell);
        mContentRv.setLayoutManager(new GridLayoutManager(getContext(), DEFAULT_SPAN));
        mAdapter = new OnSellContentAdapter();
        mContentRv.setAdapter(mAdapter);

        mContentRv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = SizeUtils.dip2px(getContext(),2.5f);
                outRect.bottom = SizeUtils.dip2px(getContext(),2.5f);
                outRect.left = SizeUtils.dip2px(getContext(),2.5f);
                outRect.right = SizeUtils.dip2px(getContext(),2.5f);
            }
        });

        //设置refresh相关内容
        mRefreshLayout.setEnableRefresh(false);
        mRefreshLayout.setEnableLoadmore(true);
    }

    @Override
    protected void initListener() {
        mAdapter.onItemClickListener(this);
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                if (mPresenter != null) {
                    mPresenter.loadMore();
                }
            }
        });
    }

    @Override
    public void onSellItemClickListener(OnSellContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean item) {
        //拿到TickerPresenter
        mTickerPresenter = PresenterManger.getInstance().getTickerPresenter();
        //领劵界面
        String url = item.getCoupon_click_url();
        //没有卷时,跳转到购买界面
        if (TextUtils.isEmpty(url)) {
            //购买界面
            url = item.getClick_url();
        }
        mTickerPresenter.getTicket(item.getTitle(), url, item.getPict_url());
        startActivity(new Intent(getActivity(), TicketActivity.class));
    }

    @Override
    public void onContentLoadSuccess(OnSellContent result) {
        setUpState(State.SUCCESS);
        mAdapter.setData(result);
    }

    @Override
    public void onMoreLoaded(OnSellContent moreResult) {
        mAdapter.addData(moreResult);
        if (mRefreshLayout != null) {
            mRefreshLayout.finishLoadmore();
        }
    }

    @Override
    public void onMoreLoadedError() {
        if (mRefreshLayout != null) {
            mRefreshLayout.finishLoadmore();
        }
        ToastUtils.showToast("网络错误,请稍后再试");
    }

    @Override
    public void onMoreLoadedEmpty() {
        if (mRefreshLayout != null) {
            mRefreshLayout.finishLoadmore();
        }
        ToastUtils.showToast("没有更多数据了");
    }

    @Override
    public void onNetworkError() {
        setUpState(State.ERROR);
    }

    @Override
    public void onLoading() {
        setUpState(State.LOADING);
    }

    @Override
    public void onEmpty() {
        setUpState(State.EMPTY);
    }

    @Override
    protected void onRetryClick() {
        if (mPresenter != null) {
            mPresenter.reLoad();
        }
    }

}
