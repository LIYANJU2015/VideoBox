package com.commonlibs.integration;


import android.content.Context;

import com.commonlibs.util.FileUtils;
import com.commonlibs.util.Preconditions;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import io.rx_cache2.internal.RxCache;
import io.victoralbertos.jolyglot.GsonSpeaker;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * 用来管理网络请求层,以及数据缓存层,以后可能添加数据库请求层
 * Created by jess on 13/04/2017 09:52
 * Contact with jess.yan.effort@gmail.com
 */
public class RepositoryManager implements IRepositoryManager {
    private Retrofit mRetrofit;
    private RxCache mRxCache;
    private final Map<String, Object> mRetrofitServiceCache = new LinkedHashMap<>();
    private final Map<String, Object> mCacheServiceCache = new LinkedHashMap<>();

    public RepositoryManager(Context context) {
        File cacheDirectory = new File(FileUtils.getCacheFile(context), "RxCache");
        this.mRxCache = new RxCache.Builder().persistence(FileUtils.makeDirs(cacheDirectory),
                new GsonSpeaker());
    }

    public RepositoryManager(Context context, Retrofit retrofit) {
        this(context);
        this.mRetrofit = retrofit;
    }

    /**
     * 注入RetrofitService
     *
     * @param services
     */
    @Override
    public void setRetrofitService(Class<?>... services) {
        for (Class<?> service : services) {
            if (mRetrofitServiceCache.containsKey(service.getName())) continue;
            mRetrofitServiceCache.put(service.getName(), mRetrofit.create(service));
        }
    }

    public void setRetrofitService(Retrofit retrofit, Class<?>... services) {
        for (Class<?> service : services) {
            if (mRetrofitServiceCache.containsKey(service.getName())) continue;
            mRetrofitServiceCache.put(service.getName(), retrofit.create(service));
        }
    }

    /**
     * 注入CacheService
     *
     * @param services
     */
    @Override
    public void setCacheService(Class<?>... services) {
        for (Class<?> service : services) {
            if (mCacheServiceCache.containsKey(service.getName())) continue;
            mCacheServiceCache.put(service.getName(), mRxCache.using(service));
        }
    }

    /**
     * 根据传入的Class获取对应的Retrift service
     *
     * @param service
     * @param <T>
     * @return
     */
    @Override
    public <T> T obtainRetrofitService(Class<T> service) {
        Preconditions.checkState(mRetrofitServiceCache.containsKey(service.getName())
                , "Unable to find %s,first call injectRetrofitService(%s) in ConfigModule", service.getName(), service.getSimpleName());
        return (T) mRetrofitServiceCache.get(service.getName());
    }

    /**
     * 根据传入的Class获取对应的RxCache service
     *
     * @param cache
     * @param <T>
     * @return
     */
    @Override
    public <T> T obtainCacheService(Class<T> cache) {
        Preconditions.checkState(mCacheServiceCache.containsKey(cache.getName())
                , "Unable to find %s,first call injectCacheService(%s) in ConfigModule", cache.getName(), cache.getSimpleName());
        return (T) mCacheServiceCache.get(cache.getName());
    }

    public static final class RetrofitBuilder {

        private String baseUrl;

        private OkHttpClient okHttpClient;

        public RetrofitBuilder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public RetrofitBuilder client(OkHttpClient okHttpClient) {
            this.okHttpClient = okHttpClient;
            return this;
        }

        public Retrofit build() {
            return new Retrofit.Builder().baseUrl(baseUrl)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
        }
    }
}
