package com.videobox.presenter;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.commonlibs.rxerrorhandler.core.RxErrorHandler;
import com.commonlibs.themvp.presenter.ActivityPresenter;
import com.videobox.R;
import com.videobox.model.dailymotion.DaiyMotionModel;
import com.videobox.view.delegate.MainViewDelegate;
import com.videobox.view.widget.CoordinatorTabLayout;
import com.videobox.view.widget.LoadHeaderImagesListener;

import java.util.ArrayList;


/**
 * dailymotion_poster
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
    private RxErrorHandler mRxErrorHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        mDaiyMotionModel = new DaiyMotionModel(getAppComponent().repositoryManager());
        mRxErrorHandler = getAppComponent().rxErrorHandler();
    }

    @Override
    protected void iniAndBindEven() {

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
//        switch (v.getId()) {
//            case R.id.btn1:
//                mDaiyMotionModel.getVideos(APIConstant.DailyMontion.sWatchVideosMap, false, 1)
//                        .compose(this.<DMVideosPageBean>bindToLifecycle())
//                        .retryWhen(new RetryWithDelay(3, 2))
//                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(new ErrorHandleSubscriber<DMVideosPageBean>(mRxErrorHandler) {
//                            @Override
//                            public void onNext(DMVideosPageBean dmVideosPageBean) {
//                                ToastUtils.showShortToast(String.valueOf(dmVideosPageBean.total));
//                            }
//                        });
//                break;
//        }
    }
}
