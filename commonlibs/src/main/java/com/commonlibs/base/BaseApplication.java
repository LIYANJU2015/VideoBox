package com.commonlibs.base;

import android.app.Application;
import android.content.Context;

import com.commonlibs.http.GlobeHttpHandler;
import com.commonlibs.http.RequestInterceptor;
import com.commonlibs.integration.ActivityLifecycle;
import com.commonlibs.integration.AppManager;
import com.commonlibs.integration.IRepositoryManager;
import com.commonlibs.util.FileUtils;
import com.commonlibs.widget.imageloader.ImageLoader;
import com.commonlibs.widget.imageloader.glide.GlideImageLoaderStrategy;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * Created by liyanju on 2017/4/23.
 */

public abstract class BaseApplication extends Application implements AppComponent {

    private ImageLoader mImageLoader;

    private IRepositoryManager mIRepositoryManager;

    private AppManager mAppManager;

    private static Context sContext;

    private OkHttpClient mOkHttpClient;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;

        mAppManager = new AppManager(this);
        mImageLoader = new ImageLoader(new GlideImageLoaderStrategy());

        if (!initSelfOkHttpClient()) {
            RequestInterceptor intercept = new RequestInterceptor(GlobeHttpHandler.EMPTY);
            initOkHttpClient(null, intercept);
        }

        mIRepositoryManager = initRepositoryManager();
        if (mIRepositoryManager != null) {
            setRepositoryManager(mIRepositoryManager);
        }

        registerActivityLifecycleCallbacks(new ActivityLifecycle(mAppManager));
    }

    public boolean initSelfOkHttpClient() {
        return false;
    }

    private void initOkHttpClient(Interceptor netWorkInterceptor, Interceptor ...appInterceptors) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS);
        for (Interceptor interceptor : appInterceptors) {
            builder.addInterceptor(interceptor);
        }

        if (netWorkInterceptor != null) {
            builder.addNetworkInterceptor(netWorkInterceptor);
        }

        mOkHttpClient = builder.build();
    }

    public static BaseApplication getBaseApplication() {
        return sContext;
    }

    public void setImageLoader(ImageLoader imageLoader) {
        mImageLoader = imageLoader;
    }

    public void setRepositoryManager(IRepositoryManager iRepositoryManager) {
        mIRepositoryManager = iRepositoryManager;
    }

    public abstract IRepositoryManager initRepositoryManager();

    @Override
    public Application Application() {
        return this;
    }

    @Override
    public IRepositoryManager repositoryManager() {
        return mIRepositoryManager;
    }

    @Override
    public OkHttpClient okHttpClient() {
        return mOkHttpClient;
    }

    @Override
    public ImageLoader imageLoader() {
        return mImageLoader;
    }

    @Override
    public File cacheFile() {
        return FileUtils.getCacheFile(this);
    }

    @Override
    public AppManager appManager() {
        return mAppManager;
    }

    public AppComponent getAppComponent() {
        return this;
    }
}
