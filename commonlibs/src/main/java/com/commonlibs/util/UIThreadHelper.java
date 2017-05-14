/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package com.commonlibs.util;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

public class UIThreadHelper {

    private volatile static UIThreadHelper sInstance;
    private Handler mHandler;

    private UIThreadHelper() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    private boolean mDesstory = false;

    private static Object object = new Object();

    public static UIThreadHelper getInstance() {
        if (sInstance == null) {
            synchronized(object) {
                if (sInstance == null) {
                    sInstance = new UIThreadHelper();
                }
            }
        }
        return sInstance;
    }

    public boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    public void runOnUIThread(Runnable runnable) {
        if (!mDesstory) {
            if (mHandler != null) {
                mHandler.post(runnable);
            }
        }
    }

    public void runViewUIThread(View view, final Runnable runnable) {
        view.post(new Runnable() {
            @Override
            public void run() {
                if (mHandler != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (runnable != null) {
                                    runnable.run();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    public Handler getHandler() {
        return mHandler;
    }

    public void destory() {
        mDesstory = true;
        mHandler.removeCallbacksAndMessages(null);
        sInstance = null;
    }

    public boolean isDestoryed() {
        return mDesstory;
    }

}
