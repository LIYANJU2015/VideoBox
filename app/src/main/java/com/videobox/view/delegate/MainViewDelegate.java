package com.videobox.view.delegate;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.commonlibs.base.AdapterViewPager;
import com.commonlibs.base.BaseFragment;
import com.commonlibs.themvp.view.AppDelegate;
import com.commonlibs.util.LogUtils;
import com.commonlibs.util.MyAnimatorListener;
import com.commonlibs.widget.imageloader.glide.GlideImageConfig;
import com.videobox.R;
import com.videobox.presenter.DailyMotionFragment;
import com.videobox.presenter.MainActivity;
import com.videobox.view.widget.CoordinatorTabLayout;
import com.videobox.view.widget.DrawerArrowDrawable;
import com.videobox.view.widget.LoadHeaderImagesListener;
import com.yalantis.guillotine.animation.GuillotineAnimation;

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

    private ImageView mNavigationIV;

    private AdapterViewPager mMainViewPagerAdaper;

    private final String[] mTitles = {"Android", "Web", "iOS", "Other"};

    private DrawerArrowDrawable mDrawerArrowDrawable;

    private AppBarLayout mAppBarLayout;

    private View mMainMenuView;

    private boolean mIsFirst = true;

    private static final String BACK_TAG = "back_tag";
    private static final String MENU_TAG = "menu_tag";

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

        mNavigationIV = get(R.id.navigation_left);
        mNavigationIV.setTag(BACK_TAG);
        mDrawerArrowDrawable = new DrawerArrowDrawable(this.getActivity()) {
            @Override
            public boolean isLayoutRtl() {
                return false;
            }
        };
        mDrawerArrowDrawable.setProgress(1.f);
        mNavigationIV.setImageDrawable(mDrawerArrowDrawable);
        setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (BACK_TAG.equals(view.getTag())) {
                    mMainActivity.finish();
                } else {

                }

            }
        }, R.id.navigation_left);

        mAppBarLayout = get(R.id.appbar_layout);
        mAppBarLayout.post(new Runnable() {
            @Override
            public void run() {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        mAppBarLayout.addOnOffsetChangedListener(listener);
                    }
                });
            }
        });

        DrawerArrowDrawable drawerArrowDrawable = new DrawerArrowDrawable(this.getActivity()) {
            @Override
            public boolean isLayoutRtl() {
                return false;
            }
        };
        mMainMenuView = LayoutInflater.from(mContext).inflate(R.layout.main_menu_layout, null);
        ((ImageView)mMainMenuView.findViewById(R.id.guillotine_hamburger)).setImageDrawable(drawerArrowDrawable);
        ((FrameLayout)get(R.id.content)).addView(mMainMenuView);
        new GuillotineAnimation.GuillotineBuilder(mMainMenuView,
                mMainMenuView.findViewById(R.id.guillotine_hamburger), mNavigationIV)
                .setStartDelay(250)
                .setActionBarViewForAnimation(get(R.id.toolbar))
                .setClosedOnStart(true)
                .build();
    }

    @Override
    public void onDestroy() {
        mAppBarLayout.removeOnOffsetChangedListener(listener);
    }

    private ValueAnimator mNavigationAnimator;

    private void showMenuNavigationAnimation() {
        if (mNavigationAnimator != null && mNavigationAnimator.isRunning()) {
            mNavigationAnimator.cancel();
        }
        mNavigationAnimator = ValueAnimator.ofFloat(1.f, 0.f);
        mNavigationAnimator.setDuration(800);
        mNavigationAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Float value = (Float)valueAnimator.getAnimatedValue();
                mDrawerArrowDrawable.setProgress(value);
            }
        });
        mNavigationAnimator.addListener(new MyAnimatorListener(){
            @Override
            public void onAnimationEnd(Animator animator) {
                mNavigationIV.setTag(MENU_TAG);
            }
        });
        mNavigationAnimator.start();
    }

    private void showBackNavigationAnimation() {
        if (mNavigationAnimator != null && mNavigationAnimator.isRunning()) {
            mNavigationAnimator.cancel();
        }
        mNavigationAnimator = ValueAnimator.ofFloat(0.f, 1.f);
        mNavigationAnimator.setDuration(800);
        mNavigationAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Float value = (Float)valueAnimator.getAnimatedValue();
                mDrawerArrowDrawable.setProgress(value);
            }
        });
        mNavigationAnimator.addListener(new MyAnimatorListener(){
            @Override
            public void onAnimationEnd(Animator animator) {
                mNavigationIV.setRotation(-360);
                mNavigationIV.setTag(BACK_TAG);
            }
        });
        mNavigationAnimator.start();
    }

    AppBarLayout.OnOffsetChangedListener listener = new AppBarLayout.OnOffsetChangedListener() {
        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            if (mIsFirst) {
                mIsFirst = false;
                return;
            }

            if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) { //滑动顶部
                showMenuNavigationAnimation();
            } else if (verticalOffset == 0) { //滑动底部
                showBackNavigationAnimation();
            }
        }
    };
}
