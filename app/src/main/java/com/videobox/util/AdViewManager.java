package com.videobox.util;


import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.commonlibs.util.LogUtils;
import com.commonlibs.util.ScreenUtils;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.NativeExpressAdView;
import com.videobox.AppAplication;
import com.videobox.R;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by liyanju on 2017/5/25.
 */

public class AdViewManager {

    private static AdViewManager sAdViewManager;

    public static final boolean IS_DEBUG = true;

    private HashMap<Class, ArrayList<View>> mAdViewMaps = new HashMap();

    private InterstitialAd mInterstitialAd;

    private Context mContext;

    public static AdViewManager getInstances() {
        if (sAdViewManager == null) {
            synchronized (AdViewManager.class) {
                if (sAdViewManager == null) {
                    sAdViewManager = new AdViewManager();
                }
            }
        }
        return sAdViewManager;
    }

    private AdViewManager() {
        mContext = AppAplication.getContext();
    }

    public void initInterstitialAd() {
        mInterstitialAd = new InterstitialAd(mContext);
        mInterstitialAd.setAdUnitId(mContext.getString(R.string.main_ad));
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                if (closeRunnable != null) {
                    closeRunnable.run();
                    closeRunnable = null;
                }

                requestNewInterstitial();
            }
        });
    }

    private Runnable closeRunnable;

    public void setInterstitialAdCloseListener(Runnable runnable) {
        closeRunnable = runnable;
    }

    public boolean interstitialAdShow() {
        boolean isLoaded = mInterstitialAd.isLoaded();
        LogUtils.v("interstitialAdShow " + isLoaded);
        if (isLoaded) {
            FirebaseAnalyticsUtil.of().logEventInterstitialAd();
            mInterstitialAd.show();
        }
        return isLoaded;
    }

    public void requestNewInterstitial() {
        AdRequest.Builder builder = new AdRequest.Builder();
        if (IS_DEBUG) {
            builder.addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID");
        }
        AdRequest adRequest = builder.build();

        mInterstitialAd.loadAd(adRequest);
    }


    public static AdRequest createAdRequest() {
        AdRequest.Builder builder = new AdRequest.Builder();
        if (IS_DEBUG) {
            builder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
        }
        return builder.build();
    }

    public synchronized void loadCurrShowAdView(Class clazz, View adView) {
        ArrayList<View> adViews = mAdViewMaps.get(clazz);
        if (adViews != null){
            LogUtils.v("loadCurrShowAdView2222");
            if (adView instanceof AdView) {
                ((AdView)adView).loadAd(createAdRequest());
            } else if (adView instanceof NativeExpressAdView) {
                ((NativeExpressAdView)adView).loadAd(createAdRequest());
            }
            adViews.add(adView);
            return;
        }

        LogUtils.v("loadCurrShowAdView");
        if (adView instanceof AdView) {
            ((AdView)adView).loadAd(createAdRequest());
        } else if (adView instanceof NativeExpressAdView) {
            ((NativeExpressAdView)adView).loadAd(createAdRequest());
        }
        adViews = new ArrayList<>();
        adViews.add(adView);
        mAdViewMaps.put(clazz, adViews);
    }

    public synchronized void onDestroy(Activity activity) {
        try {
            if (mAdViewMaps.get(activity.getClass()) == null) {
                return;
            }

            for (Class clazz : mAdViewMaps.keySet()) {
                if (clazz == activity.getClass()) {
                    LogUtils.v("onDestroy " + activity.getClass());
                    for (View adView : mAdViewMaps.get(clazz)) {
                         if (adView instanceof AdView) {
                             ((AdView) adView).destroy();
                         }
                    }
                }
            }
            mAdViewMaps.remove(activity.getClass());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void onResume(Activity activity) {
        try {
            if (mAdViewMaps.get(activity.getClass()) == null) {
                return;
            }

            for (Class clazz : mAdViewMaps.keySet()) {
                if (clazz == activity.getClass()) {
                    LogUtils.v("onResume "+ activity.getClass());
                    for (View adView : mAdViewMaps.get(clazz)) {
                         if (adView instanceof AdView) {
                             ((AdView)adView).resume();
                         }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void onPause(Activity activity) {
        try {
            if (mAdViewMaps.get(activity.getClass()) == null) {
                return;
            }

            for (Class clazz : mAdViewMaps.keySet()) {
                if (clazz == activity.getClass()) {
                    LogUtils.v("onPause " + activity.getClass());
                    for (View adView : mAdViewMaps.get(clazz)) {
                        if (adView instanceof AdView) {
                            ((AdView)adView).pause();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
