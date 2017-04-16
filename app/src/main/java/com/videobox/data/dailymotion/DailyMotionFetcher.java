package com.videobox.data.dailymotion;

import com.videobox.bean.DMChannels;
import com.videobox.bean.DMVideosPageBean;
import com.videobox.commonlibs.network.retrofit.RetrofitManager;
import com.videobox.data.APIConstant;
import com.videobox.data.dailymotion.service.DailymotionService;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by liyanju on 2017/4/16.
 */

public class DailyMotionFetcher implements DailymotionService{

    private static DailyMotionFetcher sDailyMotionFetcher;

    private DailymotionService mService;

    private DailyMotionFetcher(){
        mService = RetrofitManager.create(APIConstant.DailyMontion.HOST_URL)
                .create(DailymotionService.class);
    }

    public static DailyMotionFetcher getInstance() {
        if (sDailyMotionFetcher == null) {
            synchronized (DailyMotionFetcher.class) {
                if (sDailyMotionFetcher == null) {
                    sDailyMotionFetcher = new DailyMotionFetcher();
                }
            }
        }
        return sDailyMotionFetcher;
    }

    @Override
    public Call<DMVideosPageBean> getVideos(@QueryMap Map<String, String> options) {
        Call<DMVideosPageBean> call = mService.getVideos(options);
        return call;
    }

    @Override
    public Call<DMChannels> getChannels(@QueryMap Map<String, String> options) {
        Call<DMChannels> call = mService.getChannels(options);
        return call;
    }

    @Override
    public Call<DMVideosPageBean> getChannelVideos(@Path("id") String id,
                                                   @QueryMap Map<String, String> options) {
        Call<DMVideosPageBean> call = mService.getChannelVideos(id, options);
        return call;
    }

    @Override
    public Call<DMVideosPageBean> getSearchVideo(@QueryMap Map<String, String> options,
                                                 @Query("search") String search) {
        Call<DMVideosPageBean> call = mService.getSearchVideo(options, search);
        return call;
    }
}
