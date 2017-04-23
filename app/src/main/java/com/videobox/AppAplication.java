package com.videobox;

import com.commonlibs.base.BaseApplication;
import com.commonlibs.integration.IRepositoryManager;
import com.commonlibs.integration.RepositoryManager;
import com.videobox.data.APIConstant;
import com.videobox.data.dailymotion.service.DailymotionService;
import com.videobox.data.youtube.service.YouTubeService;

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
    public IRepositoryManager initRepositoryManager() {
        Retrofit youtobeRetrofit = new RepositoryManager.RetrofitBuilder().client(okHttpClient())
                .baseUrl(APIConstant.YouTube.HOST_URL).build();

        RepositoryManager repositoryManager = new RepositoryManager(this, youtobeRetrofit);
        repositoryManager.setRetrofitService(YouTubeService.class);
        Retrofit dmRetrofit = new RepositoryManager.RetrofitBuilder().client(okHttpClient())
                .baseUrl(APIConstant.DailyMontion.HOST_URL).build();
        repositoryManager.setRetrofitServiceCache(dmRetrofit, DailymotionService.class);

        return repositoryManager;
    }
}
