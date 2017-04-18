package com.videobox.data.youtube.service;

import com.videobox.bean.YTBCategoriesBean;
import com.videobox.bean.YTBLanguagesBean;
import com.videobox.bean.YTbRegionsBean;
import com.videobox.bean.YTBVideoPageBean;
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
    public Call<YTBVideoPageBean> getMostPopularVideos(@QueryMap Map<String, String> options);

    @GET("videoCategories")
    public Call<YTBCategoriesBean> getYouTubeCategories(@QueryMap Map<String, String> options);


    @GET("i18nLanguages?part=snippet&key="+ APIConstant.YouTube.DEVELOPER_KEY)
    public Call<YTBLanguagesBean> getYouTubeLanguages();

    @GET("i18nRegions?&part=snippet&key="+ APIConstant.YouTube.DEVELOPER_KEY)
    public Call<YTbRegionsBean> getYouTubeRegions();

    @GET("search")
    public Call<YTBVideoPageBean> getSearchVideos(@QueryMap Map<String, String> options);

}
