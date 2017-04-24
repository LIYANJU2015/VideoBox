package com.commonlibs.base;

import android.app.Application;

import com.commonlibs.integration.AppManager;
import com.commonlibs.integration.IRepositoryManager;
import com.commonlibs.util.Preconditions;
import com.commonlibs.widget.imageloader.ImageLoader;
import com.commonlibs.widget.imageloader.glide.GlideImageLoaderStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * Created by liyanju on 2017/4/24.
 */

public class GlobalConfig {

    private ImageLoader mImageLoader;
    private IRepositoryManager mIRepositoryManager;
    private OkHttpClient mOkHttpClient;
    private File mCacheFile;
    private AppManager mAppManager;
    private Application mApplicaiton;

    public GlobalConfig(Application application, List<Interceptor> interceptors, Interceptor networkInterceptor,
                        File cacheFile, ImageLoader imageLoader) {
        mApplicaiton = application;

        if (imageLoader == null) {
            mImageLoader = new ImageLoader(new GlideImageLoaderStrategy());
        } else {
            mImageLoader = imageLoader;
        }

        initOkHttpClient(networkInterceptor, interceptors);

        mCacheFile = cacheFile;

        mAppManager = new AppManager(mApplicaiton);
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    public AppComponent createAppComponent(IRepositoryManager repositoryManager) {
        return new AppComponentImpl(mApplicaiton, mOkHttpClient,
                mCacheFile, mImageLoader, repositoryManager);
    }

    private void initOkHttpClient(Interceptor netWorkInterceptor, List<Interceptor> appInterceptors) {
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


    public static class ConfigBuilder {

        private List<Interceptor> interceptors = new ArrayList<>();
        private Interceptor networkInterceptor;
        private File cacheFile;
        private ImageLoader imageLoader;
        private IRepositoryManager iRepositoryManager;
        private Application application;

        public ConfigBuilder(Application application){
            this.application = application;
        }

        public GlobalConfig.ConfigBuilder addInterceptor(Interceptor interceptor) {//动态添加任意个interceptor
            this.interceptors.add(interceptor);
            return this;
        }

        public GlobalConfig.ConfigBuilder addNetWorkInterceptor(Interceptor networkInterceptor){
            this.networkInterceptor = networkInterceptor;
            return this;
        }

        public GlobalConfig.ConfigBuilder cacheFile(File cacheFile) {
            this.cacheFile = cacheFile;
            return this;
        }

        public GlobalConfig.ConfigBuilder imageLoader(ImageLoader imageLoader) {
            this.imageLoader = imageLoader;
            return this;
        }

        public GlobalConfig build() {
            Preconditions.checkNotNull(cacheFile, "cacheFile is required");
            return new GlobalConfig(application, interceptors, networkInterceptor,
                    cacheFile, imageLoader);
        }
    }
}
