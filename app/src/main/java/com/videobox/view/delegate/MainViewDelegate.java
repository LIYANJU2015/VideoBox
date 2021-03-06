package com.videobox.view.delegate;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.commonlibs.base.AdapterViewPager;
import com.commonlibs.base.BaseFragment;
import com.commonlibs.themvp.view.AppDelegate;
import com.commonlibs.util.LogUtils;
import com.commonlibs.util.SizeUtils;
import com.commonlibs.util.StatusBarColorCompat;
import com.videobox.R;
import com.videobox.main.DailyMotionFragment;
import com.videobox.main.MainActivity;
import com.videobox.main.YouTubeFragment;
import com.videobox.view.adapter.MenuItemAdapter;
import com.videobox.view.widget.ActionBarDrawerToggle;
import com.videobox.view.widget.CoordinatorTabLayout;
import com.videobox.view.widget.DrawerArrowDrawable;
import com.videobox.view.widget.LoadHeaderImagesListener;

import java.util.ArrayList;

/**
 * Created by liyanju on 2017/4/23.
 */

public class MainViewDelegate extends AppDelegate {

    private static final String TAG = MainViewDelegate.class.getSimpleName();

    private CoordinatorTabLayout mCoordinatorTabLayout;

    private MainActivity mMainActivity;

    private ViewPager mMainViewPager;

    private AdapterViewPager mMainViewPagerAdaper;

    private ImageView mNavigationLeft;

    public DrawerLayout mDrawerLayout;

    private ListView mDrawerList;

    private ActionBarDrawerToggle mDrawerToggle;

    private final String[] mTitles = {"dailymotion", "YouTube"};

    private DailyMotionFragment mDailyMotionFragment;
    private YouTubeFragment mYouTubeFragment;

    private SwipeRefreshLayout mMenuSwipeRefresh;

    public MenuItemAdapter menuItemAdapter;

    private LinearLayout mLeftMenuLinear;

    @Override
    public int getRootLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initWidget() {
        mMainActivity = getActivity();

        mMainViewPager = get(R.id.main_viewpager);
        mMainViewPagerAdaper = new AdapterViewPager(mMainActivity.getSupportFragmentManager());
        ArrayList<BaseFragment> list = new ArrayList<>();
        mDailyMotionFragment = new DailyMotionFragment();
        list.add(mDailyMotionFragment);
        mYouTubeFragment = new YouTubeFragment();
        list.add(mYouTubeFragment);
        mMainViewPagerAdaper.bindData(list, mTitles);
        mMainViewPager.setAdapter(mMainViewPagerAdaper);

        initCoordinatortab();

        mNavigationLeft = get(R.id.navigation_left);
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerToggle();
            }
        }, R.id.navigation_left_linear);

        initDrawerMenu();
    }

    public void drawerToggle() {
        if (mDrawerLayout.isDrawerOpen(mLeftMenuLinear)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

    private void initDrawerMenu() {
        mDrawerLayout = get(R.id.drawer_layout);
        mLeftMenuLinear = get(R.id.left_menu);
        DrawerArrowDrawable drawerArrow = new DrawerArrowDrawable(mMainActivity) {
            @Override
            public boolean isLayoutRtl() {
                return false;
            }
        };
        mDrawerToggle = new ActionBarDrawerToggle(mMainActivity, mDrawerLayout,
                drawerArrow, R.string.drawer_open,
                R.string.drawer_close, mNavigationLeft);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        mMenuSwipeRefresh = get(R.id.menu_swipeRefresh);
        mMenuSwipeRefresh.setOnRefreshListener(mMainActivity);

        mDrawerList = get(R.id.navdrawer);
        menuItemAdapter = new MenuItemAdapter(mContext);
        mDrawerList.setAdapter(menuItemAdapter);
    }

    public void showLoading() {
        if (mMenuSwipeRefresh != null) {
            mMenuSwipeRefresh.setRefreshing(true);
        }
    }

    public void hideLoading() {
        if (mMenuSwipeRefresh != null) {
            mMenuSwipeRefresh.setRefreshing(false);
        }
    }

    @Override
    public void onDestroy() {
        mDrawerLayout.removeDrawerListener(mDrawerToggle);
    }

    private void initCoordinatortab() {
        mCoordinatorTabLayout = get(R.id.coordinatortablayout);
        mCoordinatorTabLayout.setTitle(null)
                .setLoadHeaderImagesListener(new LoadHeaderImagesListener() {
                    @Override
                    public void loadHeaderImages(final ImageView imageView, TabLayout.Tab tab) {
                        LogUtils.v("loadHeaderImages", tab.getPosition());
                        switch (tab.getPosition()) {
                            case 0:
                                mMainActivity.setVideoListFragment(mDailyMotionFragment);
                                Glide.with(mContext).load(R.drawable.dailymotion_poster2).asBitmap().into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                        imageView.setImageBitmap(resource);
                                        mCoordinatorTabLayout.setContentScrimColor(ContextCompat.getColor(mContext, R.color.dailymotion_color));
                                    }
                                });
                                StatusBarColorCompat.setColorNoTranslucent(mMainActivity,
                                        ContextCompat.getColor(mContext, R.color.dailymotion_color));
                                break;
                            case 1:
                                mMainActivity.setVideoListFragment(mYouTubeFragment);
                                Glide.with(mContext).load(R.drawable.youtube_poster3).asBitmap().into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                        imageView.setImageBitmap(resource);
                                        mCoordinatorTabLayout.setContentScrimColor(ContextCompat.getColor(mContext, R.color.youtube_color));
                                    }
                                });
                                StatusBarColorCompat.setColorNoTranslucent(mMainActivity,
                                        ContextCompat.getColor(mContext, R.color.youtube_color));
                                break;

                        }
                    }
                })
                .setupWithViewPager(mMainViewPager);

        mCoordinatorTabLayout.getTabLayout().addOnTabSelectedListener(mMainActivity);
        mCoordinatorTabLayout.getTabLayout().setSelectedTabIndicatorHeight(SizeUtils.dp2px(3));
    }
}
