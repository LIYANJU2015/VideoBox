package com.videobox.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.commonlibs.themvp.presenter.ActivityPresenter;
import com.commonlibs.util.LogUtils;
import com.videobox.R;
import com.videobox.model.APIConstant;
import com.videobox.model.dailymotion.DaiyMotionModel;
import com.videobox.model.dailymotion.entity.DMVideosPageBean;
import com.videobox.view.MainViewDelegate;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


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

public class MainActivity extends ActivityPresenter<MainViewDelegate> implements View.OnClickListener{

    private static final String TAG = MainActivity.class.getSimpleName();

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                mDaiyMotionModel.getVideos(APIConstant.DailyMontion.sWatchVideosMap, false, 1)
                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<DMVideosPageBean>() {
                            @Override
                            public void call(DMVideosPageBean dmVideosPageBean) {
                                LogUtils.d(TAG, " getVideos success ", dmVideosPageBean);
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                LogUtils.d(TAG, " getVideos failed ", throwable);
                            }
                        });
                break;
        }
    }
}
