package com.commonlibs.base;

import android.app.Application;

import com.commonlibs.integration.AppManager;
import com.commonlibs.integration.IRepositoryManager;
import com.commonlibs.widget.imageloader.ImageLoader;
import com.commonlibs.widget.imageloader.glide.GlideImageLoaderStrategy;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * Created by liyanju on 2017/4/24.
 */

public class AppComponentImpl implements AppComponent {

    private ImageLoader mImageLoader;
    private IRepositoryManager mIRepositoryManager;
    private OkHttpClient mOkHttpClient;
    private File mCacheFile;
    private AppManager mAppManager;
    private Application mApplicaiton;

    public AppComponentImpl(Application application, OkHttpClient okHttpClient,
                            File cacheFile, ImageLoader imageLoader, IRepositoryManager repositoryManager) {
        mApplicaiton = application;
        mIRepositoryManager = repositoryManager;

        if (imageLoader == null) {
            mImageLoader = new ImageLoader(new GlideImageLoaderStrategy());
        } else {
            mImageLoader = imageLoader;
        }

        mOkHttpClient = okHttpClient;

        mCacheFile = cacheFile;

        mAppManager = new AppManager(mApplicaiton);
    }

    public void setRepositoryManager(IRepositoryManager repositoryManager) {
        mIRepositoryManager = repositoryManager;
    }

    public  void initOkHttpClient(Interceptor netWorkInterceptor, List<Interceptor> appInterceptors) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS);
        if (appInterceptors != null) {
            for (Interceptor interceptor : appInterceptors) {
                builder.addInterceptor(interceptor);
            }
        }

        if (netWorkInterceptor != null) {
            builder.addNetworkInterceptor(netWorkInterceptor);
        }

        mOkHttpClient = builder.build();
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
        return mCacheFile;
    }

    @Override
    public AppManager appManager() {
        return mAppManager;
    }
}
