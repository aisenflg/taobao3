package com.example.taobaounion.ui.fragment;

import android.graphics.Rect;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taobaounion.R;
import com.example.taobaounion.base.BaseFragment;
import com.example.taobaounion.model.domain.Histories;
import com.example.taobaounion.model.domain.IBaseInfo;
import com.example.taobaounion.model.domain.SearchRecommend;
import com.example.taobaounion.model.domain.SearchResult;
import com.example.taobaounion.presenter.ISearchPresenter;
import com.example.taobaounion.ui.adapter.HomePagerContentAdapter;
import com.example.taobaounion.ui.custom.TextFlowLayout;
import com.example.taobaounion.utils.KeyBoardUtils;
import com.example.taobaounion.utils.PresenterManger;
import com.example.taobaounion.utils.SizeUtils;
import com.example.taobaounion.utils.TicketUtils;
import com.example.taobaounion.utils.ToastUtils;
import com.example.taobaounion.view.ISearchPageCallback;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class SearchFragment extends BaseFragment implements ISearchPageCallback, View.OnClickListener, TextFlowLayout.OnFlowTextItemClickListener {
    private ISearchPresenter mISearchPresenter;

    @BindView(R.id.search_history_view)
    public TextFlowLayout mHistoryView;
    @BindView(R.id.search_recommend_view)
    public TextFlowLayout mRecommendView;

    @BindView(R.id.search_history_container)
    public LinearLayout mHistoryContainer;
    @BindView(R.id.search_recommend_container)
    public LinearLayout mRecommendContainer;

    @BindView(R.id.search_delete_history)
    public ImageView mDeletedHistory;
    @BindView(R.id.search_result_list)
    public RecyclerView mResultRecycler;
    @BindView(R.id.search_result_container)
    public TwinklingRefreshLayout mResultContainer;

    @BindView(R.id.search_btn)
    public TextView mSearchBtn;
    @BindView(R.id.search_clean_btn)
    public ImageView mSearchCleanBtn;
    @BindView(R.id.search_input_box)
    public EditText mSearchInput;

    private HomePagerContentAdapter mAdapter;

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_search;
    }

    @Override
    protected View loadRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_search_layout, container, false);
    }

    @Override
    protected void initPresenter() {
        mISearchPresenter = PresenterManger.getInstance().getISearchPresenter();
        mISearchPresenter.registerViewCallback(this);
        mISearchPresenter.getRecommendWords();
        mISearchPresenter.getHistories();
    }

    @Override
    protected void release() {
        mISearchPresenter.unregisterViewCallback(this);
    }

    @Override
    protected void initView(View rootView) {
        mDeletedHistory.setOnClickListener(this);
        mResultRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new HomePagerContentAdapter();
        mResultRecycler.setAdapter(mAdapter);
        //设置刷新控件
        mResultContainer.setEnableLoadmore(true);
        mResultContainer.setEnableRefresh(false);
        //设置间距
        mResultRecycler.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = SizeUtils.dip2px(getContext(), 2f);
                outRect.bottom = SizeUtils.dip2px(getContext(), 2f);

            }
        });



    }

    @Override
    protected void initListener() {
        mResultContainer.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                //去加载更多内容
                if (mISearchPresenter != null) {
                    mISearchPresenter.loaderMore();
                }
            }
        });

        //设置小键盘搜索事件
        mSearchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH && mISearchPresenter != null) {
                    //判断拿到的内容是否为空
                    String keyword = v.getText().toString().trim();
                    if (TextUtils.isEmpty(keyword)) {
                        return false;
                    }
                    //发起搜索
                    toSearch(keyword);
                   // mISearchPresenter.doSearch(keyword);
                }
                return false;
            }
        });

        //监听输入框变化
        mSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //变化时通知
                //如果长度不为0,显示删除按钮
                mSearchCleanBtn.setVisibility(hasInput(true) ? View.VISIBLE : View.GONE);
                mSearchBtn.setText(hasInput(false) ? "搜索" : "取消");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mAdapter.setonListItemClickListener(new HomePagerContentAdapter.onListItemClickListener() {
            @Override
            public void onItemClick(IBaseInfo item) {
                //搜索列表内容点击事件
                TicketUtils.toTicketPage(item, getContext());
            }
        });

        //清除输入框内容
        mSearchCleanBtn.setOnClickListener(this);
        //发起搜索
        mSearchBtn.setOnClickListener(this);

        //历史和推荐界面点击发起搜索
        mHistoryView.setOnFlowTextItemClickListener(this);
        mRecommendView.setOnFlowTextItemClickListener(this);
    }



    @Override
    public void onHistoriesLoaded(Histories histories) {
        if (histories == null || histories.getHistories().size() == 0) {
            mHistoryContainer.setVisibility(View.GONE);
        } else {
            mHistoryContainer.setVisibility(View.VISIBLE);
            mHistoryView.setTextList(histories.getHistories());
        }
    }


    @Override
    public void onHistoriesDel() {
        //更新历史记录
        updateHistory();
    }

    @Override
    public void onRecommendWordsLoaded(List<SearchRecommend.DataBean> recommendWords) {
        setUpState(State.SUCCESS);
        List<String> recommendKeywords = new ArrayList<>();
        for (SearchRecommend.DataBean item : recommendWords) {
            recommendKeywords.add(item.getKeyword());
        }
        if (recommendWords == null || recommendWords.size() == 0) {
            mRecommendContainer.setVisibility(View.GONE);
        } else {
            mRecommendView.setTextList(recommendKeywords);
            mRecommendContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSearch(SearchResult result) {
        setUpState(State.SUCCESS);
        //隐藏历史记录和热门搜索
        mHistoryContainer.setVisibility(View.GONE);
        mRecommendContainer.setVisibility(View.GONE);
        //显示搜索界面
        mResultContainer.setVisibility(View.VISIBLE);
        //设置数据
        try {

            mAdapter.setData(result.getData()
                    .getTbk_dg_material_optional_response()
                    .getResult_list()
                    .getMap_data());
        } catch (Exception e) {
            e.printStackTrace();
            setUpState(State.EMPTY);
        }

    }

    @Override
    public void onLoaderMore(SearchResult result) {
        setUpState(State.SUCCESS);
        //加载到更多数据
        mAdapter.addData(result.getData().getTbk_dg_material_optional_response().getResult_list().getMap_data());
        if (mResultContainer != null) {
            mResultContainer.onFinishRefresh();
        }
    }


    @Override
    public void onLoadedMoreError() {
        ToastUtils.showToast("网络异常,请稍后重试");
    }

    @Override
    public void onLoadedMoreEmpty() {
        ToastUtils.showToast("已经没有更多数据了");
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
        //重新加载内容
        if (mISearchPresenter != null) {
            mISearchPresenter.reSearch();
        }
    }

    private boolean hasInput(boolean containSpace) {
        if (containSpace) {
            //保留空格
            return mSearchInput.getText().toString().length() > 0;
        } else {
            //去掉空格
            return mSearchInput.getText().toString().trim().length() > 0;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_delete_history:
                mISearchPresenter.delHistories();
                break;
            case R.id.search_clean_btn:
                mSearchInput.setText("");
                switch2HistoryPage();
                break;
            case R.id.search_btn:
                if (hasInput(false)) {
                    //发起搜索
                    if (mISearchPresenter != null) {
                        toSearch(mSearchInput.getText().toString().trim());
                        //mISearchPresenter.doSearch(mSearchInput.getText().toString().trim());
                        KeyBoardUtils.hide(getContext(), v);
                    }
                }else {
                    //隐藏键盘
                    KeyBoardUtils.hide(getContext(), v);
                }
                break;
        }
    }

    /**
     * 切换到历史和推荐界面
     */
    private void switch2HistoryPage() {
        mHistoryContainer.setVisibility(mHistoryView.getContentSize() != 0 ? View.VISIBLE : View.GONE);
        mRecommendContainer.setVisibility(mRecommendView.getContentSize() != 0 ? View.VISIBLE : View.GONE);
        //搜索内容隐藏
        mResultContainer.setVisibility(View.GONE);
        //更新历史记录
        updateHistory();

    }

    private void updateHistory() {
        if (mISearchPresenter != null) {
            mISearchPresenter.getHistories();
        }
    }

    @Override
    public void onFlowItemClick(String text) {
        //发起搜索
        toSearch(text);
    }

    private void toSearch(String text) {
        if (mISearchPresenter != null) {
            //回到顶部
            mResultRecycler.scrollToPosition(0);
            mSearchInput.setText(text);
            mISearchPresenter.doSearch(text);
        }
    }
}
