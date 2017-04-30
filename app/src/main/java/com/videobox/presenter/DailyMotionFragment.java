package com.videobox.presenter;

import com.commonlibs.themvp.presenter.FragmentPresenter;
import com.videobox.view.delegate.DailyMotionDelegate;

/**
 * Created by liyanju on 2017/4/29.
 */

public class DailyMotionFragment extends FragmentPresenter<DailyMotionDelegate>{

    @Override
    protected Class<DailyMotionDelegate> getDelegateClass() {
        return DailyMotionDelegate.class;
    }

    @Override
    protected void initAndBindEvent() {

    }
}
