package com.videobox.util;


import android.app.Activity;
import android.content.Context;

import com.commonlibs.util.LogUtils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
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

    private HashMap<Class, ArrayList<AdView>> mAdViewMaps = new HashMap();

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
                requestNewInterstitial();
            }
        });
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


    public AdRequest createAdRequest() {
        AdRequest.Builder builder = new AdRequest.Builder();
        if (IS_DEBUG) {
            builder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
        }
        return builder.build();
    }

    public synchronized void loadCurrShowAdView(Class clazz, AdView adView) {
        ArrayList<AdView> adViews = mAdViewMaps.get(clazz);
        if (adViews != null){
            LogUtils.v("loadCurrShowAdView2222");
            adView.loadAd(createAdRequest());
            adViews.add(adView);
            return;
        }

        LogUtils.v("loadCurrShowAdView");
        adView.loadAd(createAdRequest());
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
                    for (AdView adView : mAdViewMaps.get(clazz)) {
                         adView.destroy();
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
                    for (AdView adView : mAdViewMaps.get(clazz)) {
                         adView.resume();
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
                    for (AdView adView : mAdViewMaps.get(clazz)) {
                        adView.pause();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
