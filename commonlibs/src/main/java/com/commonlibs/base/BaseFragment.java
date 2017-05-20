package com.commonlibs.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle.components.support.RxFragment;

/**
 * Created by jess on 2015/12/8.
 */
public abstract class BaseFragment<T> extends RxFragment {
    protected BaseActivity mActivity;
    protected View mRootView;
    protected final String TAG = this.getClass().getSimpleName();

    private AppComponent mAppComponent;
    protected Context mContext;

    protected T mHost;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (BaseActivity) getActivity();
        mAppComponent = mActivity.getAppComponent();
        mHost = (T)getActivity();
        mContext = getContext();
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mRootView = null;
    }

}
