package com.commonlibs.base;

import android.app.Application;
import android.content.Context;

import com.commonlibs.integration.ActivityLifecycle;
import com.commonlibs.integration.IRepositoryManager;
import com.commonlibs.rxerrorhandler.core.RxErrorHandler;

/**
 * Created by liyanju on 2017/4/23.
 */

public abstract class BaseApplication extends Application{

    private static Context sContext;

    protected AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;

        GlobalConfig.ConfigBuilder configBuilder = new GlobalConfig.ConfigBuilder(this);
        applyOptions(this, configBuilder);
        GlobalConfig config = configBuilder.build();
        mAppComponent = config.createAppComponent(createRepositoryManager(config));

        registerActivityLifecycleCallbacks(new ActivityLifecycle(mAppComponent.appManager()));
    }

    public static Context getContext() {
        return sContext;
    }

    public abstract void applyOptions(Context context, GlobalConfig.ConfigBuilder builder);

    public abstract IRepositoryManager createRepositoryManager(GlobalConfig config);

    public AppComponent getAppComponent() {
        return mAppComponent;
    }

}
