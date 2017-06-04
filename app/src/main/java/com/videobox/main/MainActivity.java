package com.videobox.main;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.commonlibs.rxerrorhandler.core.RxErrorHandler;
import com.commonlibs.rxerrorhandler.handler.RetryWithDelay;
import com.commonlibs.themvp.presenter.ActivityPresenter;
import com.commonlibs.util.ErrorHandleSubscriber2;
import com.commonlibs.util.LogUtils;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.videobox.util.AdViewManager;
import com.videobox.AppAplication;
import com.videobox.R;
import com.videobox.model.APIConstant;
import com.videobox.model.dailymotion.DaiyMotionModel;
import com.videobox.model.dailymotion.entity.DMChannelsBean;
import com.videobox.model.youtube.YouTuBeModel;
import com.videobox.model.youtube.entity.YTBCategoriesBean;
import com.videobox.search.SearchActivity;
import com.videobox.util.FirebaseAnalyticsUtil;
import com.videobox.view.delegate.Contract;
import com.videobox.view.delegate.MainViewDelegate;
import com.videobox.view.widget.CoordinatorTabLayout;

import java.util.ArrayList;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

import static com.commonlibs.util.LogUtils.A;


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

public class MainActivity extends ActivityPresenter<MainViewDelegate> implements DrawerLayout.DrawerListener, SwipeRefreshLayout.OnRefreshListener,
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
            FirebaseAnalyticsUtil.of().logEventExitApp();
            super.onBackPressed(); // finish
        }
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {
        FirebaseAnalyticsUtil.of().logEventLeftMenu();
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        if (channelType == SEARCH_CHANCEL) {
            channelType = 0;
            SearchActivity.launch(mContext, MainActivity.this);
        }
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppAplication.sIsColdLaunch) {
            AppAplication.sIsColdLaunch = false;
            SplashActivity.launch(this);
        }
        FirebaseAnalyticsUtil.of().logEventEnterApp();
    }

    public static final int SEARCH_CHANCEL = 1;
    public int channelType = 0;

    @Override
    protected void initAndBindEven() {
        mDaiyMotionModel = new DaiyMotionModel(getAppComponent().repositoryManager());
        mYoutuBeModel = new YouTuBeModel(getAppComponent().repositoryManager());
        mRxErrorHandler = getAppComponent().rxErrorHandler();

        viewDelegate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int postion, long l) {
                try {
                    if (mChannelType == DAILY_MOTION_TYPE) {
                        DMChannelsBean.Channel channel = mDMChannel.get(postion);
                        if (mIVideoListFragment != null) {
                            mIVideoListFragment.showChannelVideoByID(channel.id);
                            FirebaseAnalyticsUtil.of().logEventLeftMenuClick(" DM Channel name " + channel.name);
                        }
                    } else {
                        if (mYouTubeChannel.size() > postion) {
                            YTBCategoriesBean.Categories categories = mYouTubeChannel.get(postion);
                            if (mIVideoListFragment != null) {
                                mIVideoListFragment.showChannelVideoByID(categories.id);
                                FirebaseAnalyticsUtil.of().logEventLeftMenuClick(" Youtube Channel name "
                                        + categories.snippet.title);
                            }
                        }
                    }
                    viewDelegate.drawerToggle();
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                channelType = SEARCH_CHANCEL;
                viewDelegate.drawerToggle();
            }
        }, R.id.search);

        ((DrawerLayout)viewDelegate.get(R.id.drawer_layout)).addDrawerListener(this);

        CoordinatorTabLayout coordinatorTabLayout = viewDelegate.get(R.id.coordinatortablayout);
        coordinatorTabLayout.getTabLayout().getTabAt(0).setTag(DAILY_MOTION_TYPE);
        coordinatorTabLayout.getTabLayout().getTabAt(1).setTag(YOU_TU_BE_TYPE);
        coordinatorTabLayout.getTabLayout().addOnTabSelectedListener(this);

        requestDMChannel(false);

//        if (AppAplication.spUtils.getBoolean("Disclaimer", true)) {
//            new MaterialDialog.Builder(this)
//                    .title(R.string.dialog_title)
//                    .content(R.string.dialog_cotent)
//                    .positiveText(R.string.dialog_cancel)
//                    .negativeText(R.string.dilaog_agress)
//                    .onNegative(new MaterialDialog.SingleButtonCallback() {
//                        @Override
//                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                            dialog.dismiss();
//                            AppAplication.spUtils.put("Disclaimer", false);
//                        }
//                    })
//                    .onPositive(new MaterialDialog.SingleButtonCallback() {
//                        @Override
//                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                            dialog.dismiss();
//                            MainActivity.this.finish();
//                        }
//                    })
//                    .show().setOnKeyListener(new DialogInterface.OnKeyListener() {
//                @Override
//                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                    if (keyCode == KeyEvent.KEYCODE_BACK) {
//                        finish();
//                    }
//                    return false;
//                }
//            });
//        }
    }

    private void requestYouTubeChannel(boolean update) {
        mYoutuBeModel.getYouTubeCategories(AppAplication.getCurrentRegions(),
                APIConstant.YouTube.sCategoriesMap, update)
                .subscribeOn(Schedulers.io())
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
                .compose(this.<YTBCategoriesBean>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new ErrorHandleSubscriber2<YTBCategoriesBean>() {
                    @Override
                    public void onNext(YTBCategoriesBean dmChannelsBean) {
                        mYouTubeChannel.clear();
                        LogUtils.v("requestYouTubeChannel onNext");
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
                .compose(this.<DMChannelsBean>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new ErrorHandleSubscriber2<DMChannelsBean>() {
                    @Override
                    public void onNext(DMChannelsBean dmChannelsBean) {
                        mDMChannel.clear();
                        LogUtils.v("requestDMChannel onNext");
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
            requestYouTubeChannel(true);
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        LogUtils.v("onTabSelected", "getTag " + tab.getTag());
        if (((int)tab.getTag()) == YOU_TU_BE_TYPE) {
            mChannelType = YOU_TU_BE_TYPE;
            if (mYouTubeChannel.size() == 0) {
                requestYouTubeChannel(true);
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
