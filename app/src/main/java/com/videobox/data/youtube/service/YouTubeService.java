package com.videobox.data.youtube.service;

import com.videobox.bean.YouTubeCategories;
import com.videobox.bean.YouTubeLanguages;
import com.videobox.bean.YouTubeRegions;
import com.videobox.bean.YouTubeVideoPageBean;
import com.videobox.data.APIConstant;


import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by liyanju on 2017/4/14.
 */

public interface YouTubeService {

    /**
     * 赋值 videoCategoryId hl
     * @param options
     * @return
     */
    @GET("videos")
    public Call<YouTubeVideoPageBean> getMostPopularVideos(@QueryMap Map<String, String> options);

    @GET("videoCategories")
    public Call<YouTubeCategories> getYouTubeCategories(@QueryMap Map<String, String> options);


    @GET("i18nLanguages?part=snippet&key="+ APIConstant.YouTube.DEVELOPER_KEY)
    public Call<YouTubeLanguages> getYouTubeLanguages();

    @GET("i18nRegions?&part=snippet&key="+ APIConstant.YouTube.DEVELOPER_KEY)
    public Call<YouTubeRegions> getYouTubeRegions();

    @GET("search")
    public Call<YouTubeVideoPageBean> getSearchVideos(@QueryMap Map<String, String> options);

}
