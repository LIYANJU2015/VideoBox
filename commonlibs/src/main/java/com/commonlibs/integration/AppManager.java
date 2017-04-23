package com.commonlibs.integration;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class AppManager {
    protected final String TAG = this.getClass().getSimpleName();
    public static final String APPMANAGER_MESSAGE = "appmanager_message";
    public static final int START_ACTIVITY = 0;
    public static final int SHOW_SNACKBAR = 1;
    public static final int KILL_ALL = 2;
    public static final int APP_EXIT = 3;
    private Application mApplication;

    //管理所有activity
    public List<Activity> mActivityList;
    //当前在前台的activity
    private Activity mCurrentActivity;

    public AppManager(Application application) {
        this.mApplication = application;
    }

    /**
     * 让在前台的activity,打开下一个activity
     *
     * @param intent
     */
    public void startActivity(Intent intent) {
        if (getCurrentActivity() == null) {
            //如果没有前台的activity就使用new_task模式启动activity
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mApplication.startActivity(intent);
            return;
        }
        getCurrentActivity().startActivity(intent);
    }

    /**
     * 让在前台的activity,打开下一个activity
     *
     * @param activityClass
     */
    public void startActivity(Class activityClass) {
        startActivity(new Intent(mApplication, activityClass));
    }

    /**
     * 释放资源
     */
    public void release() {
        mActivityList.clear();
        mActivityList = null;
        mCurrentActivity = null;
        mApplication = null;
    }

    /**
     * 将在前台的activity保存
     *
     * @param currentActivity
     */
    public void setCurrentActivity(Activity currentActivity) {
        this.mCurrentActivity = currentActivity;
    }

    /**
     * 获得当前在前台的activity
     *
     * @return
     */
    public Activity getCurrentActivity() {
        return mCurrentActivity;
    }

    /**
     * 返回一个存储所有未销毁的activity的集合
     *
     * @return
     */
    public List<Activity> getActivityList() {
        if (mActivityList == null) {
            mActivityList = new LinkedList<>();
        }
        return mActivityList;
    }


    /**
     * 添加Activity到集合
     */
    public void addActivity(Activity activity) {
        if (mActivityList == null) {
            mActivityList = new LinkedList<>();
        }
        synchronized (AppManager.class) {
            if (!mActivityList.contains(activity)) {
                mActivityList.add(activity);
            }
        }
    }

    /**
     * 删除集合里的指定activity
     *
     * @param activity
     */
    public void removeActivity(Activity activity) {
        if (mActivityList == null) {
            return;
        }
        synchronized (AppManager.class) {
            if (mActivityList.contains(activity)) {
                mActivityList.remove(activity);
            }
        }
    }

    /**
     * 删除集合里的指定位置的activity
     *
     * @param location
     */
    public Activity removeActivity(int location) {
        if (mActivityList == null) {
            return null;
        }
        synchronized (AppManager.class) {
            if (location > 0 && location < mActivityList.size()) {
                return mActivityList.remove(location);
            }
        }
        return null;
    }

    /**
     * 关闭指定activity
     *
     * @param activityClass
     */
    public void killActivity(Class<?> activityClass) {
        if (mActivityList == null) {
            return;
        }
        for (Activity activity : mActivityList) {
            if (activity.getClass().equals(activityClass)) {
                activity.finish();
            }
        }
    }


    /**
     * 指定的activity实例是否存活
     *
     * @param activity
     * @return
     */
    public boolean activityInstanceIsLive(Activity activity) {
        if (mActivityList == null) {
            return false;
        }
        return mActivityList.contains(activity);
    }


    /**
     * 指定的activity class是否存活(一个activity可能有多个实例)
     *
     * @param activityClass
     * @return
     */
    public boolean activityClassIsLive(Class<?> activityClass) {
        if (mActivityList == null) {
            return false;
        }
        for (Activity activity : mActivityList) {
            if (activity.getClass().equals(activityClass)) {
                return true;
            }
        }

        return false;
    }


    /**
     * 关闭所有activity
     */
    public void killAll() {
//        while (getActivityList().size() != 0) { //此方法只能兼容LinkedList
//            getActivityList().remove(0).finish();
//        }

        Iterator<Activity> iterator = getActivityList().iterator();
        while (iterator.hasNext()) {
            iterator.next().finish();
            iterator.remove();
        }

    }


    /**
     * 退出应用程序
     */
    public void AppExit() {
        try {
            killAll();
            if (mActivityList != null)
                mActivityList = null;
            ActivityManager activityMgr =
                    (ActivityManager) mApplication.getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.killBackgroundProcesses(mApplication.getPackageName());
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
