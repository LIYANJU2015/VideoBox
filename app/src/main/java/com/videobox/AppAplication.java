package com.videobox;

import android.content.Context;

import com.commonlibs.base.BaseApplication;
import com.commonlibs.base.GlobalConfig;
import com.commonlibs.http.GlobeHttpHandler;
import com.commonlibs.http.RequestInterceptor;
import com.commonlibs.integration.IRepositoryManager;
import com.commonlibs.integration.RepositoryManager;
import com.commonlibs.util.FileUtils;
import com.commonlibs.util.LogUtils;
import com.videobox.model.APIConstant;
import com.videobox.model.dailymotion.cache.DailyMotionCache;
import com.videobox.model.dailymotion.service.DailymotionService;
import com.videobox.model.youtube.service.YouTubeService;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Created by liyanju on 2017/4/23.
 */

public class AppAplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IRepositoryManager createRepositoryManager(GlobalConfig config) {
        OkHttpClient okHttpClient = config.getOkHttpClient();

        RepositoryManager repositoryManager = new RepositoryManager(this);
        Retrofit youtobeRetrofit = new RepositoryManager.RetrofitBuilder().client(okHttpClient)
                .baseUrl(APIConstant.YouTube.HOST_URL).build();
        repositoryManager.setRetrofitService(youtobeRetrofit, YouTubeService.class);

        Retrofit dmRetrofit = new RepositoryManager.RetrofitBuilder().client(okHttpClient)
                .baseUrl(APIConstant.DailyMontion.HOST_URL).build();
        repositoryManager.setRetrofitService(dmRetrofit, DailymotionService.class);

        repositoryManager.setCacheService(DailyMotionCache.class);
        return repositoryManager;
    }


    @Override
    public void applyOptions(Context context, GlobalConfig.ConfigBuilder builder) {
        builder.addInterceptor(new RequestInterceptor(GlobeHttpHandler.EMPTY));
        builder.cacheFile(FileUtils.getCacheFile(this));
    }
}
