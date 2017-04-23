package com.commonlibs.base;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.view.View;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;


public abstract class BaseActivity extends RxAppCompatActivity {
    protected final String TAG = this.getClass().getSimpleName();

    protected AppComponent mAppComponent;

    public static final String IS_NOT_ADD_ACTIVITY_LIST = "is_add_activity_list";//是否加入到activity的list，管理

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAppComponent = ((BaseApplication)getApplication()).getAppComponent();
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }



    public void fullScreencall() {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}
