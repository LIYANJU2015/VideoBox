package com.commonlibs.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle.components.support.RxFragment;

/**
 * Created by jess on 2015/12/8.
 */
public abstract class BaseFragment extends RxFragment {
    protected BaseActivity mActivity;
    protected View mRootView;
    protected final String TAG = this.getClass().getSimpleName();

    private AppComponent mAppComponent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = initView(inflater,container);
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (BaseActivity) getActivity();
        mAppComponent = mActivity.mApplication.getAppComponent();
        initData();
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mActivity = null;
        this.mRootView = null;
    }

    protected abstract View initView(LayoutInflater inflater, ViewGroup container);

    protected abstract void initData();

}
