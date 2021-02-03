package com.example.taobaounion.ui.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taobaounion.R;
import com.example.taobaounion.base.BaseFragment;
import com.example.taobaounion.model.domain.SelectedContent;
import com.example.taobaounion.model.domain.SelectedPageCategory;
import com.example.taobaounion.presenter.ISelectedPagePresenter;
import com.example.taobaounion.presenter.ITicketPresenter;
import com.example.taobaounion.ui.activity.TicketActivity;
import com.example.taobaounion.ui.adapter.SelectedPageLeftAdapter;
import com.example.taobaounion.ui.adapter.SelectedPageRightAdapter;
import com.example.taobaounion.utils.PresenterManger;
import com.example.taobaounion.utils.SizeUtils;
import com.example.taobaounion.view.ISelectedPageCallback;

import java.util.List;

import butterknife.BindView;

public class SelectedFragment extends BaseFragment implements ISelectedPageCallback, SelectedPageLeftAdapter.onLeftItemClickListener, SelectedPageRightAdapter.onSelectedPageContentListener {

    private ISelectedPagePresenter mPresenter;
    private ITicketPresenter mTickerPresenter;

    @BindView(R.id.left_category_list)
    public RecyclerView leftCategoryList;
    @BindView(R.id.content_list)
    public RecyclerView rightContentList;
    @BindView(R.id.frag_bar_title_tv)
    public TextView titleTv;
    private SelectedPageLeftAdapter mLeftAdapter;
    private SelectedPageRightAdapter mRightAdapter;


    @Override
    protected View loadRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_with_bar_layout, container,false);
    }

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_selected;
    }

    @Override
    protected void initPresenter() {
        mPresenter = PresenterManger.getInstance().getISelectedPagePresenter();
        mPresenter.registerViewCallback(this);
        mPresenter.getCategories();

    }

    @Override
    protected void initView(View rootView) {
        setUpState(State.SUCCESS);
        titleTv.setText(R.string.selected_title);
        leftCategoryList.setLayoutManager(new LinearLayoutManager(getContext()));
        mLeftAdapter = new SelectedPageLeftAdapter();
        leftCategoryList.setAdapter(mLeftAdapter);

        rightContentList.setLayoutManager(new LinearLayoutManager(getContext()));
        mRightAdapter = new SelectedPageRightAdapter();
        rightContentList.setAdapter(mRightAdapter);
        rightContentList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = SizeUtils.dip2px(getContext(), 4);
                outRect.left = SizeUtils.dip2px(getContext(),6 );
                outRect.right = SizeUtils.dip2px(getContext(),6 );
                outRect.bottom = SizeUtils.dip2px(getContext(), 4);
            }
        });

    }

    @Override
    protected void initListener() {
        mLeftAdapter.setItemClickListener(this);
        mRightAdapter.onItemClickListener(this);
    }


    /**
     * 左边分类点击事件
     * @param data
     */
    @Override
    public void onLeftItemClick(SelectedPageCategory.DataBean data) {
        mPresenter.getContentByCategoryId(data);
    }

    /**
     * 右边分类点击事件
     * @param data
     */
    @Override
    public void onItemRightClickListener(SelectedContent.DataBean.TbkUatmFavoritesItemGetResponseBean.ResultsBean.UatmTbkItemBean data) {
        //拿到TickerPresenter
        mTickerPresenter = PresenterManger.getInstance().getTickerPresenter();
        //领劵界面
        String url = data.getCoupon_click_url();
        //没有卷时,跳转到购买界面
        if (TextUtils.isEmpty(url)) {
            //购买界面
            url = data.getClick_url();
        }
        mTickerPresenter.getTicket(data.getTitle(),url, data.getPict_url());
        startActivity(new Intent(getActivity(), TicketActivity.class));
    }




    @Override
    public void onCategoryLoaded(SelectedPageCategory result) {
        setUpState(State.SUCCESS);
        //分类数据
        mLeftAdapter.setData(result);
        List<SelectedPageCategory.DataBean> data = result.getData();

    }

    @Override
    public void onContentLoaded(SelectedContent content) {
        mRightAdapter.setData(content);
        rightContentList.scrollToPosition(0);
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
            mPresenter.reloadContent();
        }
    }
}
