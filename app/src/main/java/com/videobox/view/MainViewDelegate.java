package com.videobox.view;

import android.view.View;

import com.commonlibs.themvp.view.AppDelegate;
import com.videobox.R;
import com.videobox.presenter.MainActivity;

/**
 * Created by liyanju on 2017/4/23.
 */

public class MainViewDelegate extends AppDelegate {

    @Override
    public int getRootLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initWidget() {
        MainActivity activity = getActivity();
        setOnClickListener(activity, R.id.btn1);
    }
}
