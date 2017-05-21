package com.videobox.main;

import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;

import com.commonlibs.rxerrorhandler.core.RxErrorHandler;
import com.commonlibs.rxerrorhandler.handler.RetryWithDelay;
import com.commonlibs.themvp.presenter.ActivityPresenter;
import com.commonlibs.util.ErrorHandleSubscriber2;
import com.commonlibs.util.LogUtils;
import com.commonlibs.util.UIThreadHelper;
import com.videobox.AppAplication;
import com.videobox.R;
import com.videobox.model.APIConstant;
import com.videobox.model.dailymotion.DaiyMotionModel;
import com.videobox.model.dailymotion.entity.DMChannelsBean;
import com.videobox.model.youtube.YouTuBeModel;
import com.videobox.model.youtube.entity.YTBCategoriesBean;
import com.videobox.search.SearchActivity;
import com.videobox.view.delegate.Contract;
import com.videobox.view.delegate.MainViewDelegate;
import com.videobox.view.widget.CoordinatorTabLayout;

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

public class MainActivity extends ActivityPresenter<MainViewDelegate> implements SwipeRefreshLayout.OnRefreshListener,
        TabLayout.OnTabSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private DaiyMotionModel mDaiyMotionModel;
    private RxErrorHandler mRxErrorHandler;

    private ArrayList<DMChannelsBean.Channel> mDMChannel = new ArrayList<>();

    private ArrayList<YTBCategoriesBean.Categories> mYouTubeChannel = new ArrayList<>();

    private Contract.IVideoListFragment mIVideoListFragment;

    private int mChannelType = DAILY_MOTION_TYPE;
    public static final int YOU_TU_BE_TYPE = 1;
    public static final int DAILY_MOTION_TYPE = 2;

    private YouTuBeModel mYoutuBeModel;

    public void setVideoListFragment(Contract.IVideoListFragment videoListFragment) {
        mIVideoListFragment = videoListFragment;
    }

    @Override
    public void onBackPressed() {
        if (viewDelegate.mDrawerLayout != null && viewDelegate.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            viewDelegate.mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed(); // finish
        }
    }

    @Override
    protected void initAndBindEven() {
        mDaiyMotionModel = new DaiyMotionModel(getAppComponent().repositoryManager());
        mYoutuBeModel = new YouTuBeModel(getAppComponent().repositoryManager());
        mRxErrorHandler = getAppComponent().rxErrorHandler();

        viewDelegate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int postion, long l) {
                if (mChannelType == DAILY_MOTION_TYPE) {
                    DMChannelsBean.Channel channel = mDMChannel.get(postion);
                    if (mIVideoListFragment != null) {
                        mIVideoListFragment.showChannelVideoByID(channel.id);
                    }
                } else {
                    YTBCategoriesBean.Categories categories = mYouTubeChannel.get(postion);
                    if (mIVideoListFragment != null) {
                        mIVideoListFragment.showChannelVideoByID(categories.id);
                    }
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
                viewDelegate.drawerToggle();
            }
        }, R.id.menu_home);

        viewDelegate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SearchActivity.launch(mContext);
                UIThreadHelper.getInstance().getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        viewDelegate.drawerToggle();
                    }
                }, 300);
            }
        }, R.id.search);

        CoordinatorTabLayout coordinatorTabLayout = viewDelegate.get(R.id.coordinatortablayout);
        coordinatorTabLayout.getTabLayout().getTabAt(0).setTag(DAILY_MOTION_TYPE);
        coordinatorTabLayout.getTabLayout().getTabAt(1).setTag(YOU_TU_BE_TYPE);
        coordinatorTabLayout.getTabLayout().addOnTabSelectedListener(this);

        requestDMChannel(false);
    }

    private void requestYouTubeChannel(boolean update) {
        mYoutuBeModel.getYouTubeCategories(AppAplication.getCurrentRegions(), APIConstant.YouTube.sCategoriesMap, update)
                .subscribeOn(Schedulers.io())
                .compose(this.<YTBCategoriesBean>bindToLifecycle())
                .retryWhen(new RetryWithDelay(2, 1))
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
                .subscribe(new ErrorHandleSubscriber2<YTBCategoriesBean>() {
                    @Override
                    public void onNext(YTBCategoriesBean dmChannelsBean) {
                        mYouTubeChannel.clear();

                        if (dmChannelsBean.items != null) {
                            mYouTubeChannel.addAll(dmChannelsBean.items);
                        }

                        viewDelegate.menuItemAdapter.updateYouTubeCategories(mYouTubeChannel);
                    }
                });

    }

    private void requestDMChannel(boolean update) {
        mDaiyMotionModel.getChannels(APIConstant.DailyMontion.sChannelsMap, update, 1)
                .subscribeOn(Schedulers.io())
                .compose(this.<DMChannelsBean>bindToLifecycle())
                .retryWhen(new RetryWithDelay(2, 1))
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
                .subscribe(new ErrorHandleSubscriber2<DMChannelsBean>() {
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
        if (mChannelType == DAILY_MOTION_TYPE) {
            requestDMChannel(true);
        } else {
            requestYouTubeChannel(false);
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        LogUtils.v("onTabSelected", "getTag " + tab.getTag());
        if (((int)tab.getTag()) == YOU_TU_BE_TYPE) {
            mChannelType = YOU_TU_BE_TYPE;
            if (mYouTubeChannel.size() == 0) {
                requestYouTubeChannel(false);
            } else {
                viewDelegate.menuItemAdapter.updateYouTubeCategories(mYouTubeChannel);
            }
        } else {
            mChannelType = DAILY_MOTION_TYPE;
            if (mDMChannel.size() == 0) {
                requestDMChannel(false);
            } else {
                viewDelegate.menuItemAdapter.updateDMChannel(mDMChannel);
            }
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
