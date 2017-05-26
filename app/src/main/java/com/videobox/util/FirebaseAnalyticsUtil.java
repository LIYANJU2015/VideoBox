package com.videobox.util;

import android.os.Bundle;

import com.commonlibs.util.TimeUtils;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.videobox.AppAplication;

/**
 * Created by liyanju on 2017/5/26.
 */

public class FirebaseAnalyticsUtil {

    private FirebaseAnalytics firebaseAnalytics;

    private static FirebaseAnalyticsUtil sfirebaseAnalyticsUtil;

    public static FirebaseAnalyticsUtil of() {
        if (sfirebaseAnalyticsUtil == null) {
            sfirebaseAnalyticsUtil = new FirebaseAnalyticsUtil();
        }
        return sfirebaseAnalyticsUtil;
    }

    private FirebaseAnalyticsUtil() {
        firebaseAnalytics = FirebaseAnalytics.getInstance(AppAplication.getContext());
    }

    public void logEvent(final String event, final Bundle bundle) {
        firebaseAnalytics.logEvent(event, bundle);
    }

    public void logEventEnterApp(){
        Bundle bundle = new Bundle();
        bundle.putString("time", String.valueOf(TimeUtils.millis2Date(System.currentTimeMillis())));
        logEvent("main_enter", bundle);
    }

    public void logEventExitApp() {
        Bundle bundle = new Bundle();
        bundle.putString("time", String.valueOf(TimeUtils.millis2Date(System.currentTimeMillis())));
        logEvent("main_exit", bundle);
    }

    public void logEventDMPlayer(){
        Bundle bundle = new Bundle();
        bundle.putString("time", String.valueOf(TimeUtils.millis2Date(System.currentTimeMillis())));
        logEvent("dm_player_show", bundle);
    }

    public void logEventDMPlayerClick(String value) {
        Bundle bundle = new Bundle();
        bundle.putString("time", String.valueOf(TimeUtils.millis2Date(System.currentTimeMillis())));
        bundle.putString("click", value);
        logEvent("dm_player_click", bundle);
    }

    public void logEventYouTubePlayer() {
        Bundle bundle = new Bundle();
        bundle.putString("time", String.valueOf(TimeUtils.millis2Date(System.currentTimeMillis())));
        logEvent("youtube_player_show", bundle);
    }

    public void logEventYouTubePlayerClick(String value) {
        Bundle bundle = new Bundle();
        bundle.putString("time", String.valueOf(TimeUtils.millis2Date(System.currentTimeMillis())));
        bundle.putString("click", value);
        logEvent("youtube_player_click", bundle);
    }

    public void logEventSearch(){
        Bundle bundle = new Bundle();
        bundle.putString("time", String.valueOf(TimeUtils.millis2Date(System.currentTimeMillis())));
        logEvent("search_show", bundle);
    }

    public void logEventSearchClick(String value){
        Bundle bundle = new Bundle();
        bundle.putString("time", String.valueOf(TimeUtils.millis2Date(System.currentTimeMillis())));
        bundle.putString("click", value);
        logEvent("search_click", bundle);
    }

    public void logEventLeftMenu() {
        Bundle bundle = new Bundle();
        bundle.putString("time", String.valueOf(TimeUtils.millis2Date(System.currentTimeMillis())));
        logEvent("leftmenu_show", bundle);
    }

    public void logEventLeftMenuClick(String value) {
        Bundle bundle = new Bundle();
        bundle.putString("time", String.valueOf(TimeUtils.millis2Date(System.currentTimeMillis())));
        bundle.putString("click", value);
        logEvent("leftmenu_click", bundle);
    }

    public void logEventInterstitialAd() {
        Bundle bundle = new Bundle();
        bundle.putString("time", String.valueOf(TimeUtils.millis2Date(System.currentTimeMillis())));
        logEvent("interstitialAd_show", bundle);
    }

}
