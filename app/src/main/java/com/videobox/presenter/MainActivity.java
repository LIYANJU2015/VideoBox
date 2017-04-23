package com.videobox.presenter;

import android.os.Bundle;

import com.commonlibs.themvp.presenter.ActivityPresenter;
import com.videobox.model.dailymotion.DaiyMotionModel;
import com.videobox.view.MainViewDelegate;

/**
 * dailymotion
 *
 * https://api.dailymotion.com/playlists?search=rr
 *
 * https://api.dailymotion.com/videos?sort=recent?fields=description,
 *
 * https://api.dailymotion.com/channels?sort=popular
 * https://api.dailymotion.com/channel/music/videos
 *
 */

public class MainActivity extends ActivityPresenter<MainViewDelegate> {

    private DaiyMotionModel mDaiyMotionModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDaiyMotionModel = new DaiyMotionModel(getAppComponent().repositoryManager());
    }

    @Override
    protected Class<MainViewDelegate> getDelegateClass() {
        return MainViewDelegate.class;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDaiyMotionModel.onDestroy();
    }
}
