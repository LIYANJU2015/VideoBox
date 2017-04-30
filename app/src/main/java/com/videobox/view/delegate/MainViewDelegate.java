package com.videobox.view.delegate;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.commonlibs.base.AdapterViewPager;
import com.commonlibs.base.BaseFragment;
import com.commonlibs.themvp.view.AppDelegate;
import com.commonlibs.widget.imageloader.glide.GlideImageConfig;
import com.videobox.R;
import com.videobox.model.dailymotion.entity.DMChannelsBean;
import com.videobox.presenter.DailyMotionFragment;
import com.videobox.presenter.MainActivity;
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

    private int[] mColorArray = new int[]{
        android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light};

    private CoordinatorTabLayout mCoordinatorTabLayout;

    private MainActivity mMainActivity;

    private ViewPager mMainViewPager;

    private AdapterViewPager mMainViewPagerAdaper;

    private ImageView mNavigationLeft;

    private DrawerLayout mDrawerLayout;

    private ListView mDrawerList;

    private ActionBarDrawerToggle mDrawerToggle;

    private final String[] mTitles = {"Android", "Web", "iOS", "Other"};

    private int mCurrentMenuState = MENU_CLOSE;
    private static final int MENU_OPEN = 1;
    private static final int MENU_CLOSE = 0;

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
        list.add(new DailyMotionFragment());
        list.add(new DailyMotionFragment());
        mMainViewPagerAdaper.bindData(list, mTitles);
        mMainViewPager.setAdapter(mMainViewPagerAdaper);

        initCoordinatortab();

        mNavigationLeft = get(R.id.navigation_left);
        setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                    mDrawerLayout.closeDrawer(mDrawerList);
                } else {
                    mDrawerLayout.openDrawer(mDrawerList);
                }
            }
        }, R.id.navigation_left);

        initDrawerMenu();

        mDrawerList = get(R.id.navdrawer);
        MenuItemAdapter menuItemAdapter = new MenuItemAdapter(mContext);
        ArrayList<DMChannelsBean.Channel> arrayList = new ArrayList<>();
        arrayList.add(new DMChannelsBean.Channel());
        arrayList.add(new DMChannelsBean.Channel());
        arrayList.add(new DMChannelsBean.Channel());
        menuItemAdapter.updateDMChannel(arrayList);
        mDrawerList.setAdapter(menuItemAdapter);

    }

    private void initDrawerMenu() {
        mDrawerLayout = get(R.id.drawer_layout);
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
    }

    @Override
    public void onDestroy() {
        mDrawerLayout.removeDrawerListener(mDrawerToggle);
    }

    private void initCoordinatortab() {
        mCoordinatorTabLayout = get(R.id.coordinatortablayout);
        mCoordinatorTabLayout.setTitle("")
                .setContentScrimColorArray(mColorArray)
                .setLoadHeaderImagesListener(new LoadHeaderImagesListener() {
                    @Override
                    public void loadHeaderImages(ImageView imageView, TabLayout.Tab tab) {
                        switch (tab.getPosition()) {
                            default:
                                String url = "https://raw.githubusercontent.com/hugeterry/CoordinatorTabLayout/master/sample/src/main/res/mipmap-hdpi/bg_android.jpg";
                                mMainActivity.getAppComponent().imageLoader().loadImage(mMainActivity, GlideImageConfig.builder().imageView(imageView).url(url).build());

                        }
                    }
                })
                .setupWithViewPager(mMainViewPager);
    }
}
