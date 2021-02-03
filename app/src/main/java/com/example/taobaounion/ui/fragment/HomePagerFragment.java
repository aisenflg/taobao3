package com.example.taobaounion.ui.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.taobaounion.R;
import com.example.taobaounion.base.BaseFragment;
import com.example.taobaounion.model.domain.Categories;
import com.example.taobaounion.model.domain.HomePagerContent;
import com.example.taobaounion.presenter.ICategoryPagerPresenter;
import com.example.taobaounion.presenter.ITicketPresenter;
import com.example.taobaounion.ui.activity.TicketActivity;
import com.example.taobaounion.ui.adapter.HomePagerContentAdapter;
import com.example.taobaounion.ui.adapter.LooperPagerAdapter;
import com.example.taobaounion.ui.custom.AutoLoopViewPager;
import com.example.taobaounion.utils.Constants;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.PresenterManger;
import com.example.taobaounion.utils.SizeUtils;
import com.example.taobaounion.utils.ToastUtils;
import com.example.taobaounion.view.ICategoryPagerCallback;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.views.TbNestedScrollView;

import java.util.List;

import butterknife.BindView;

public class HomePagerFragment extends BaseFragment implements ICategoryPagerCallback, HomePagerContentAdapter.onListItemClickListener, LooperPagerAdapter.onLooperItemClickListener {

    private ICategoryPagerPresenter mPresenter;
    private int mMaterialId;
    @BindView(R.id.home_pager_content_list)
    public RecyclerView mContentList;
    @BindView(R.id.looper_pager)
    public AutoLoopViewPager looperPager;
    @BindView(R.id.home_pager_title)
    public TextView titleTv;
    @BindView(R.id.looper_point_container)
    public LinearLayout looperPointContainer;
    @BindView(R.id.home_pager_refresh)
    public TwinklingRefreshLayout mRefreshLayout;
    @BindView(R.id.home_pager_parent)
    public LinearLayout homePagerParent;
    @BindView(R.id.home_pager_scroll)
    public TbNestedScrollView homePagerScroll;
    @BindView(R.id.home_pager_header_container)
    public LinearLayout homeHeaderContainer;
    private HomePagerContentAdapter mContentAdapter;
    private LooperPagerAdapter mLooperPagerAdapter;
    private ITicketPresenter mTickerPresenter;

    public static HomePagerFragment newInstance(Categories.DataBean category) {

        HomePagerFragment homePagerFragment = new HomePagerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_HOME_PAGER_TITLE, category.getTitle());
        bundle.putInt(Constants.KEY_HOME_PAGER_MATERIAL_ID, category.getId());
        homePagerFragment.setArguments(bundle);
        return homePagerFragment;
    }


    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_home_pager;
    }

    @Override
    protected void initView(View rootView) {
        mContentList.setLayoutManager(new LinearLayoutManager(getContext()));
        mContentList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = 8;
                outRect.bottom = 8;

            }
        });
        mContentAdapter = new HomePagerContentAdapter();
        mContentList.setAdapter(mContentAdapter);
        //轮播图
        mLooperPagerAdapter = new LooperPagerAdapter();

        looperPager.setAdapter(mLooperPagerAdapter);

        //设置refresh相关内容
        mRefreshLayout.setEnableRefresh(false);
        mRefreshLayout.setEnableLoadmore(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        //可见的时候开始轮播
        looperPager.startLoop();
    }

    @Override
    public void onPause() {
        super.onPause();
        //暂停轮播
        looperPager.stopLoop();
    }

    @Override
    protected void initListener() {
        //点击事件
        mContentAdapter.setonListItemClickListener(this);
        mLooperPagerAdapter.setonListItemClickListener(this);

        homePagerParent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (homeHeaderContainer == null) {
                    return;
                }
                int headerHeight = homeHeaderContainer.getMeasuredHeight();
                int measuredHeight = homePagerParent.getMeasuredHeight();
                homePagerScroll.setHeaderHeight(headerHeight);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mContentList.getLayoutParams();
                layoutParams.height = measuredHeight;
                mContentList.setLayoutParams(layoutParams);
                if (measuredHeight != 0) {
                    homePagerParent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
        looperPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mLooperPagerAdapter.getDataSize() == 0) {
                    return;
                }
                int targetPosition = position % mLooperPagerAdapter.getDataSize();
                //切换指示器
                updateLooperIndicator(targetPosition);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                LogUtils.d(HomePagerFragment.this, "加载更多.......");
                if (mPresenter != null) {
                    mPresenter.loadMore(mMaterialId);
                }
            }
        });
    }

    /**
     * 切换指示器
     *
     * @param targetPosition
     */
    private void updateLooperIndicator(int targetPosition) {
        for (int i = 0; i < looperPointContainer.getChildCount(); i++) {
            View point = looperPointContainer.getChildAt(i);
            if (i == targetPosition) {
                point.setBackgroundResource(R.drawable.shape_indicator_point_select);
            } else {
                point.setBackgroundResource(R.drawable.shape_indicator_point_normal);
            }
        }
    }

    @Override
    protected void initPresenter() {
        mPresenter = PresenterManger.getInstance().getICategoryPagerPresenter();
        mPresenter.registerViewCallback(this);
    }

    @Override
    protected void LoadData() {
        Bundle arguments = getArguments();
        String title = arguments.getString(Constants.KEY_HOME_PAGER_TITLE);
        mMaterialId = arguments.getInt(Constants.KEY_HOME_PAGER_MATERIAL_ID);
        //加载数据
        if (mPresenter != null) {
            mPresenter.getContentByCategoryId(mMaterialId);
        }
        if (titleTv != null) {
            titleTv.setText(title);
        }
    }

    @Override
    public void onContentLoad(List<HomePagerContent.DataBean> contents) {
        setUpState(State.SUCCESS);
        //数据列表加载到了，更新UI
        mContentAdapter.setData(contents);
        if (mRefreshLayout != null) {
            mRefreshLayout.onFinishRefresh();
        }
    }

    @Override
    public void onLoadMoreLoaded(List<HomePagerContent.DataBean> contents) {
        //添加到适配器数据的底部
        mContentAdapter.addData(contents);
        if (mRefreshLayout != null) {
            mRefreshLayout.finishLoadmore();
        }
        ToastUtils.showToast("加载了" + contents.size() + "数据");
    }

    @Override
    public void onLooperListLoaded(List<HomePagerContent.DataBean> contents) {
        LogUtils.d(this, "looper size ----------->" + contents.size());
        mLooperPagerAdapter.setData(contents);
        //中间点的size不一定为0,所以显示的就不是第一个
        int dx = (Integer.MAX_VALUE / 2) % contents.size();
        int targetCenterPosition = (Integer.MAX_VALUE / 2) - dx;
        looperPager.setCurrentItem(targetCenterPosition);
        //添加点
        for (int i = 0; i < contents.size(); i++) {
            View point = new View(getContext());
            int size = SizeUtils.dip2px(getContext(), 8);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, size);
            layoutParams.leftMargin = SizeUtils.dip2px(getContext(), 5);
            layoutParams.rightMargin = SizeUtils.dip2px(getContext(), 5);
            point.setLayoutParams(layoutParams);
            if (i == 0) {
                point.setBackgroundResource(R.drawable.shape_indicator_point_select);
            } else {
                point.setBackgroundResource(R.drawable.shape_indicator_point_normal);
            }
            looperPointContainer.addView(point);
        }
    }

    @Override
    public int getCategoryId() {
        return mMaterialId;
    }

    @Override
    public void onNetworkError() {
        //网络错误
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
    public void onLoadMoreError() {
        if (mRefreshLayout != null) {
            mRefreshLayout.finishLoadmore();
        }
        ToastUtils.showToast("网络异常,请稍后重试");
    }

    @Override
    public void onLoadMoreEmpty() {
        ToastUtils.showToast("没有更多商品了");
    }

    @Override
    protected void release() {
        if (mPresenter != null) {
            mPresenter.unregisterViewCallback(this);
        }
    }

    @Override
    public void onItemClick(HomePagerContent.DataBean item) {
        handlerItem(item);

    }

    private void handlerItem(HomePagerContent.DataBean item) {
        //拿到TickerPresenter
        mTickerPresenter = PresenterManger.getInstance().getTickerPresenter();
       //领劵界面
        String url = item.getCoupon_click_url();
        //没有卷时,跳转到购买界面
        if (TextUtils.isEmpty(url)) {
            //购买界面
            url = item.getClick_url();
        }
        mTickerPresenter.getTicket(item.getTitle(),url, item.getPict_url());
        startActivity(new Intent(getActivity(), TicketActivity.class));
    }

    @Override
    public void onLooperItemClick(HomePagerContent.DataBean item) {
        handlerItem(item);
    }
}
