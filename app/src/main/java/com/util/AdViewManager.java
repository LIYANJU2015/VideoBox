package com.util;


import android.app.Activity;

import com.commonlibs.util.LogUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.HashMap;

import static com.commonlibs.util.LogUtils.I;

/**
 * Created by liyanju on 2017/5/25.
 */

public class AdViewManager {

    private static AdViewManager sAdViewManager;

    public static final boolean IS_DEBUG = true;

    private HashMap<Class, AdView> mAdViewMaps = new HashMap();

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

    private AdRequest createAdRequest() {
        AdRequest.Builder builder = new AdRequest.Builder();
        if (IS_DEBUG) {
            builder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
        }
        return builder.build();
    }

    public synchronized void loadCurrShowAdView(Class clazz, AdView adView) {
        if (mAdViewMaps.get(clazz) == adView){
            return;
        }

        LogUtils.v("loadCurrShowAdView");
        adView.loadAd(createAdRequest());
        mAdViewMaps.put(clazz, adView);
    }

    public synchronized void onDestroy(Activity activity) {
        try {
            if (mAdViewMaps.get(activity.getClass()) == null) {
                return;
            }

            for (Class clazz : mAdViewMaps.keySet()) {
                if (clazz == activity.getClass()) {
                    LogUtils.v("onDestroy " + activity.getClass());
                    mAdViewMaps.get(clazz).destroy();
                }
            }
            mAdViewMaps.clear();
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
                    mAdViewMaps.get(clazz).resume();
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
                    mAdViewMaps.get(clazz).pause();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
