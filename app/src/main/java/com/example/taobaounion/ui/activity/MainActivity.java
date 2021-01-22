package com.example.taobaounion.ui.activity;

import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.taobaounion.R;
import com.example.taobaounion.base.BaseActivity;
import com.example.taobaounion.base.BaseFragment;
import com.example.taobaounion.ui.fragment.HomeFragment;
import com.example.taobaounion.ui.fragment.RedPacketFragment;
import com.example.taobaounion.ui.fragment.SearchFragment;
import com.example.taobaounion.ui.fragment.SelectedFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import butterknife.BindView;

public class MainActivity extends BaseActivity {

    @BindView(R.id.main_navigation_bar)
    public BottomNavigationView mBottomNavigationView;
    private HomeFragment mHomeFragment;
    private RedPacketFragment mRedPacketFragment;
    private SearchFragment mSearchFragment;
    private SelectedFragment mSelectedFragment;
    private FragmentManager mFm;


    @Override
    protected void initPresenter() {

    }

    @Override
    protected void initEvent() {
        initListener();
    }

    @Override
    protected void initView() {
        initFragments();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    private void initFragments() {
        mHomeFragment = new HomeFragment();
        mRedPacketFragment = new RedPacketFragment();
        mSearchFragment = new SearchFragment();
        mSelectedFragment = new SelectedFragment();
        mFm = getSupportFragmentManager();
        switchFragment(mHomeFragment);
    }

    private void initListener() {
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    switchFragment(mHomeFragment);
                } else if (item.getItemId() == R.id.selected) {
                    switchFragment(mSelectedFragment);
                } else if (item.getItemId() == R.id.red_packet) {
                    switchFragment(mRedPacketFragment);
                } else if (item.getItemId() == R.id.search) {
                    switchFragment(mSearchFragment);
                }
                return true;
            }
        });
    }



    /**
     * 上一次显示的fragment
     */
    private BaseFragment lastOneFragment = null;

    private void switchFragment(BaseFragment targetFragment) {
        //修改成add和hide的方式来控制fragment切换
        FragmentTransaction transaction = mFm.beginTransaction();
        if (!targetFragment.isAdded()) {
            transaction.add(R.id.main_page_container, targetFragment);
        } else {
            transaction.show(targetFragment);
        }
        if (lastOneFragment == targetFragment) {
            transaction.show(lastOneFragment);
        }else if (lastOneFragment != null){
            transaction.hide(lastOneFragment);
        }
        lastOneFragment = targetFragment;
        transaction.commit();
    }


}