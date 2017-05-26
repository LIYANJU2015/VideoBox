package com.util;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.commonlibs.util.LogUtils;

/**
 * Created by liyanju on 2017/5/25.
 */

public class MyActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks{

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        AdViewManager.getInstances().onResume(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        AdViewManager.getInstances().onPause(activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        AdViewManager.getInstances().onDestroy(activity);
    }
}
