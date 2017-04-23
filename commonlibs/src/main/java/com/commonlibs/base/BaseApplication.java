package com.commonlibs.base;

import android.app.Application;
import android.content.Context;

import com.commonlibs.integration.ActivityLifecycle;
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
 * Created by liyanju on 2017/4/23.
 */

public abstract class BaseApplication extends Application{

    private static Context sContext;

    protected AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;

        ConfigBuilder configBuilder = new ConfigBuilder(this);
        applyOptions(this, configBuilder);
        mAppComponent = configBuilder.build();

        ((BaseAppComponent)mAppComponent).setRepositoryManager(createRepositoryManager());

        registerActivityLifecycleCallbacks(new ActivityLifecycle(mAppComponent.appManager()));
    }

    public static Context getContext() {
        return sContext;
    }

    public abstract void applyOptions(Context context, ConfigBuilder builder);

    public abstract IRepositoryManager createRepositoryManager();

    public AppComponent getAppComponent() {
        return mAppComponent;
    }

    public static class BaseAppComponent implements AppComponent {

        private ImageLoader mImageLoader;
        private IRepositoryManager mIRepositoryManager;
        private OkHttpClient mOkHttpClient;
        private File mCacheFile;
        private AppManager mAppManager;
        private Application mApplicaiton;

        public BaseAppComponent(Application application, List<Interceptor> interceptors, Interceptor networkInterceptor,
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

    public static class ConfigBuilder {

        private List<Interceptor> interceptors = new ArrayList<>();
        private Interceptor networkInterceptor;
        private File cacheFile;
        private ImageLoader imageLoader;
        private IRepositoryManager iRepositoryManager;
        private Application application;

        private ConfigBuilder(Application application){
            this.application = application;
        }

        public ConfigBuilder addInterceptor(Interceptor interceptor) {//动态添加任意个interceptor
            this.interceptors.add(interceptor);
            return this;
        }

        public ConfigBuilder addNetWorkInterceptor(Interceptor networkInterceptor){
            this.networkInterceptor = networkInterceptor;
            return this;
        }

        public ConfigBuilder cacheFile(File cacheFile) {
            this.cacheFile = cacheFile;
            return this;
        }

        public ConfigBuilder imageLoader(ImageLoader imageLoader) {
            this.imageLoader = imageLoader;
            return this;
        }

        public BaseAppComponent build() {
            Preconditions.checkNotNull(cacheFile, "cacheFile is required");

            return new BaseAppComponent(application, interceptors, networkInterceptor,
                    cacheFile, imageLoader);
        }
    }
}
