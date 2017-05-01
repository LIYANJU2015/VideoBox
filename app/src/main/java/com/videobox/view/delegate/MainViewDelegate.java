package com.videobox.view.delegate;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.commonlibs.base.AdapterViewPager;
import com.commonlibs.base.BaseFragment;
import com.commonlibs.themvp.view.AppDelegate;
import com.commonlibs.util.LogUtils;
import com.videobox.R;
import com.videobox.model.dailymotion.entity.DMChannelsBean;
import com.videobox.presenter.DailyMotionFragment;
import com.videobox.presenter.MainActivity;
import com.videobox.presenter.YouTubeFragment;
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

    private DrawerLayout mDrawerLayout;

    private ListView mDrawerList;

    private ActionBarDrawerToggle mDrawerToggle;

    private final String[] mTitles = {"dailymotion", "YouTube"};

    private DailyMotionFragment mDailyMotionFragment;
    private YouTubeFragment mYouTubeFragment;

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
        mCoordinatorTabLayout.setTitle(null)
                .setLoadHeaderImagesListener(new LoadHeaderImagesListener() {
                    @Override
                    public void loadHeaderImages(final ImageView imageView, TabLayout.Tab tab) {
                        switch (tab.getPosition()) {
                            case 0:
                                Glide.with(mContext).load(R.drawable.dailymotion_poster).asBitmap().into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                        imageView.setImageBitmap(resource);
                                        changeToolbarColor(resource);
                                    }
                                });
                                break;
                            case 1:
                                Glide.with(mContext).load(R.drawable.youtube_poster).asBitmap().into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                        imageView.setImageBitmap(resource);
                                        changeToolbarColor(resource);
                                    }
                                });
                                break;

                        }
                    }
                })
                .setupWithViewPager(mMainViewPager);
    }

    private void changeToolbarColor(Bitmap bitmap) {
        Palette.Builder builder = Palette.from(bitmap);
        builder.generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                LogUtils.v("changeToolbarColor");
                //获取到充满活力的这种色调
                Palette.Swatch vibrant = palette.getVibrantSwatch();
                Palette.Swatch darkvibrant = palette.getDarkVibrantSwatch();
                mCoordinatorTabLayout.setContentScrimColor(vibrant.getRgb());
                mCoordinatorTabLayout.getTabLayout().setSelectedTabIndicatorColor(darkvibrant.getRgb());
            }
        });
    }

    private int colorBurn(int RGBValues) {
        int alpha = RGBValues >> 24;
        int red = RGBValues >> 16 & 0xFF;
        int green = RGBValues >> 8 & 0xFF;
        int blue = RGBValues & 0xFF;
        red = (int) Math.floor(red * (1 - 0.1));
        green = (int) Math.floor(green * (1 - 0.1));
        blue = (int) Math.floor(blue * (1 - 0.1));
        return Color.rgb(red, green, blue);
    }
}
