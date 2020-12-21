package com.example.taobaounion.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.taobaounion.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {

    private State currentState = State.NONE;
    private View mSuccessView;
    private View mLoadingView;
    private View mErrorView;
    private View mEmptyView;

    public enum State{
        NONE,LOADING,SUCCESS,ERROR,EMPTY;
    }

    private Unbinder mBind;
    private FrameLayout mBaseContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.base_fragment_layout, container, false);
        mBaseContainer = rootView.findViewById(R.id.base_container);
        loadStatusView(inflater,container);
        mBind = ButterKnife.bind(this, rootView);
        initView(rootView);
        initPresenter();
        LoadData();
        return rootView;
    }

    /**
     * 加载各种网络状态
     * @param inflater
     * @param container
     */
    protected void loadStatusView(LayoutInflater inflater, ViewGroup container) {
        mSuccessView = loadSuccessView(inflater,container);
        mBaseContainer.addView(mSuccessView);

        mLoadingView = loadLoadingView(inflater, container);
        mBaseContainer.addView(mLoadingView);

        mErrorView = LoadErrorView(inflater, container);
        mBaseContainer.addView(mErrorView);

        mEmptyView = loadEmptyView(inflater, container);
        mBaseContainer.addView(mEmptyView);

        setUpState(State.NONE);
    }




    public void setUpState(State state){
        this.currentState = state;
        mSuccessView.setVisibility(currentState == State.SUCCESS? View.VISIBLE : View.GONE);
        mLoadingView.setVisibility(currentState == State.LOADING? View.VISIBLE : View.GONE);
        mErrorView.setVisibility(currentState == State.ERROR? View.VISIBLE : View.GONE);
        mEmptyView.setVisibility(currentState == State.EMPTY? View.VISIBLE : View.GONE);
    }

    protected void initView(View rootView) {

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBind != null) {
            mBind.unbind();
        }
        release();
    }

    protected void release() {
        //释放资源
    }

    protected void initPresenter() {
        //创建presenter
    }

    protected void LoadData() {
        //加载数据
    }

    protected View loadSuccessView(LayoutInflater inflater, ViewGroup container) {
        int resId = getRootViewResId();
        return inflater.inflate(resId,container,false);
    }

    /**
     * 加载中界面
     * @param inflater
     * @param container
     * @return
     */
    protected View loadLoadingView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_loading,container,false);

    }

    protected View LoadErrorView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_error,container,false);
    }

    protected View loadEmptyView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_empty,container,false);
    }

    protected abstract int getRootViewResId();
}
