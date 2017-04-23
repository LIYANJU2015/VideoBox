package com.videobox.data.youtube;

import com.commonlibs.base.BaseApplication;
import com.videobox.bean.YTBCategoriesBean;
import com.videobox.bean.YTBLanguagesBean;
import com.videobox.bean.YTbRegionsBean;
import com.videobox.bean.YTBVideoPageBean;
import com.commonlibs.network.retrofit.RetrofitManager;
import com.videobox.data.APIConstant;
import com.videobox.data.dailymotion.DailyMotionFetcher;
import com.videobox.data.youtube.service.YouTubeService;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.QueryMap;

/**
 * Created by liyanju on 2017/4/16.
 */

public class YouTubeFetcher implements YouTubeService{

    private static YouTubeFetcher sYouTubeFetcher;

    private YouTubeService mService;

    private YouTubeFetcher(){
        mService = RetrofitManager.create(APIConstant.YouTube.HOST_URL)
                .create(YouTubeService.class);
    }

    public static YouTubeFetcher getInstance() {
        if (sYouTubeFetcher == null) {
            synchronized (DailyMotionFetcher.class) {
                if (sYouTubeFetcher == null) {
                    sYouTubeFetcher = new YouTubeFetcher();
                }
            }
        }
        return sYouTubeFetcher;
    }


    @Override
    public Call<YTBVideoPageBean> getMostPopularVideos(@QueryMap Map<String, String> options) {
        Call<YTBVideoPageBean> call = mService.getMostPopularVideos(options);
        return call;
    }

    @Override
    public Call<YTBCategoriesBean> getYouTubeCategories(@QueryMap Map<String, String> options) {
        Call<YTBCategoriesBean> call = mService.getYouTubeCategories(options);
        return call;
    }

    @Override
    public Call<YTBLanguagesBean> getYouTubeLanguages() {
        Call<YTBLanguagesBean> call = mService.getYouTubeLanguages();
        return call;
    }

    @Override
    public Call<YTbRegionsBean> getYouTubeRegions() {
        Call<YTbRegionsBean> call = mService.getYouTubeRegions();
        return call;
    }

    @Override
    public Call<YTBVideoPageBean> getSearchVideos(@QueryMap Map<String, String> options) {
        Call<YTBVideoPageBean> call = mService.getSearchVideos(options);
        return call;
    }
}
