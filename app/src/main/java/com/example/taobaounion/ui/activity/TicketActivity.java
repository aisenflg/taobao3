package com.example.taobaounion.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.taobaounion.R;
import com.example.taobaounion.base.BaseActivity;
import com.example.taobaounion.model.domain.TicketResult;
import com.example.taobaounion.presenter.ITicketPresenter;
import com.example.taobaounion.utils.PresenterManger;
import com.example.taobaounion.utils.ToastUtils;
import com.example.taobaounion.utils.UrlUtils;
import com.example.taobaounion.view.ITicketPagerCallback;

import butterknife.BindView;

public class TicketActivity extends BaseActivity implements ITicketPagerCallback, View.OnClickListener {

    private ITicketPresenter mTickerPresenter;
    private boolean mHasTaobaoApp = false;

    @BindView(R.id.ticket_cover)
    public ImageView mCover;
    @BindView(R.id.ticket_code)
    public EditText mTicketCode;
    @BindView(R.id.ticket_copy_or_open_btn)
    public TextView mOpenOrCopyBtn;
    @BindView(R.id.ticket_load_retry)
    public TextView retryLoadText;
    @BindView(R.id.ticket_back_press)
    public View backPress;
    @BindView(R.id.ticket_cover_loading)
    public View loadingView;


    @Override
    protected void initPresenter() {
        mTickerPresenter = PresenterManger.getInstance().getTickerPresenter();
        if (mTickerPresenter != null) {
            mTickerPresenter.registerViewCallback(this);
        }
        //判断是否安装淘宝
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo("com.taobao.taobao", PackageManager.MATCH_UNINSTALLED_PACKAGES);
            mHasTaobaoApp = packageInfo != null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            mHasTaobaoApp = false;
        }
        //根据这个值去修改UI
        mOpenOrCopyBtn.setText(mHasTaobaoApp ? "打开淘宝领劵" : "复制淘口令");
    }


    @Override
    protected void initView() {

    }

    @Override
    protected void initEvent() {
        backPress.setOnClickListener(this);
        mOpenOrCopyBtn.setOnClickListener(this);
    }


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_ticket;
    }

    @Override
    public void onTicketLoaded(String cover, TicketResult result) {
        if (mCover != null && !TextUtils.isEmpty(cover)) {
            Glide.with(this).load(UrlUtils.getTicketUrl(cover)).into(mCover);
        }
        if (result != null && result.getData().getTbk_tpwd_create_response() != null) {
            mTicketCode.setText(result.getData().getTbk_tpwd_create_response().getData().getModel());
        }
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
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
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
        if (retryLoadText != null) {
            retryLoadText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoading() {
        if (loadingView != null) {
            loadingView.setVisibility(View.VISIBLE);
        }
        if (retryLoadText != null) {
            retryLoadText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onEmpty() {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ticket_back_press:
                finish();
                break;
            case R.id.ticket_copy_or_open_btn:
                //复制淘口令
                String tickCode = mTicketCode.getText().toString().trim();
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                //复制到粘贴板
                ClipData clipData = ClipData.newPlainText("sob_taobao_ticket_code", tickCode);
                cm.setPrimaryClip(clipData);
                //如果有淘宝则打开淘宝
                if (mHasTaobaoApp) {
                    Intent tbIntent = new Intent();
//                    tbIntent.setAction("android.intent.action.Main");
//                    tbIntent.addCategory("android.intent.category.LAUNCHER");
                    ComponentName componentName = new ComponentName("com.taobao.taobao","com.taobao.tao.TBMainActivity");
                    tbIntent.setComponent(componentName);
                    startActivity(tbIntent);
                } else {
                    //没有则提示复制成功
                    ToastUtils.showToast("已经复制,粘贴分享,或打开淘宝");
                }
        }
    }
}