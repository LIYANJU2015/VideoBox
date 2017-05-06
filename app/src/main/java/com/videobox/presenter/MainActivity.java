package com.videobox.presenter;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.commonlibs.rxerrorhandler.core.RxErrorHandler;
import com.commonlibs.rxerrorhandler.handler.ErrorHandleSubscriber;
import com.commonlibs.rxerrorhandler.handler.RetryWithDelay;
import com.commonlibs.themvp.presenter.ActivityPresenter;
import com.videobox.R;
import com.videobox.model.APIConstant;
import com.videobox.model.dailymotion.DaiyMotionModel;
import com.videobox.model.dailymotion.entity.DMChannelsBean;
import com.videobox.search.SearchActivity;
import com.videobox.view.delegate.Contract;
import com.videobox.view.delegate.MainViewDelegate;

import java.util.ArrayList;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;


/**
 * dailymotion_poster2
 *
 * https://api.dailymotion.com/playlists?search=rr
 *
 * https://api.dailymotion.com/videos?sort=recent?fields=description,
 *
 * https://api.dailymotion.com/channels?sort=popular
 * https://api.dailymotion.com/channel/music/videos
 *
 */

public class MainActivity extends ActivityPresenter<MainViewDelegate> implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private DaiyMotionModel mDaiyMotionModel;
    private RxErrorHandler mRxErrorHandler;

    private ArrayList<DMChannelsBean.Channel> mDMChannel = new ArrayList<>();

    private Contract.IVideoListFragment mIVideoListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

    }

    public void setVideoListFragment(Contract.IVideoListFragment videoListFragment) {
        mIVideoListFragment = videoListFragment;
    }

    @Override
    protected void initAndBindEven() {
        mDaiyMotionModel = new DaiyMotionModel(getAppComponent().repositoryManager());
        mRxErrorHandler = getAppComponent().rxErrorHandler();

        requestDMChannel(false);

        viewDelegate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int postion, long l) {
                DMChannelsBean.Channel channel = mDMChannel.get(postion);
                if (mIVideoListFragment != null) {
                    mIVideoListFragment.showChannelVideoByID(channel.id);
                }
                viewDelegate.drawerToggle();
            }
        }, R.id.navdrawer);

        viewDelegate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (mIVideoListFragment != null) {
                    mIVideoListFragment.showChannelVideoByID(null);
                }
            }
        }, R.id.menu_home);

        viewDelegate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SearchActivity.launch(mContext);
            }
        }, R.id.search);
    }

    private void requestDMChannel(boolean update) {
        mDaiyMotionModel.getChannels(APIConstant.DailyMontion.sChannelsMap, update, 1)
                .subscribeOn(Schedulers.io())
                .compose(this.<DMChannelsBean>bindToLifecycle())
                .retryWhen(new RetryWithDelay(3, 2))
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        if (viewDelegate != null) {
                            viewDelegate.showLoading();
                        }
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(new Action0() {
                    @Override
                    public void call() {
                        if (viewDelegate != null) {
                            viewDelegate.hideLoading();
                        }
                    }
                })
                .subscribe(new ErrorHandleSubscriber<DMChannelsBean>(mRxErrorHandler) {
                    @Override
                    public void onNext(DMChannelsBean dmChannelsBean) {
                        mDMChannel.clear();

                        if (dmChannelsBean.list != null) {
                            mDMChannel.addAll(dmChannelsBean.list);
                        }

                        viewDelegate.menuItemAdapter.updateDMChannel(mDMChannel);
                    }
                });
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
    public void onRefresh() {
        requestDMChannel(true);
    }
}
