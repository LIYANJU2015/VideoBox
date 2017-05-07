package com.videobox.model.youtube.service;

import com.videobox.model.APIConstant;
import com.videobox.model.youtube.entity.YTBCategoriesBean;
import com.videobox.model.youtube.entity.YTBLanguagesBean;
import com.videobox.model.youtube.entity.YTBVideoPageBean;
import com.videobox.model.youtube.entity.YTbRegionsBean;
import com.videobox.model.youtube.entity.YTbRegionsListBean;


import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by liyanju on 2017/4/14.
 */

public interface YouTubeService {

    /**
     * 赋值 videoCategoryId hl
     *
     * @param options
     * @return
     */
    @GET("videos")
    public Observable<YTBVideoPageBean> getMostPopularVideos(@Query("pageToken") String pageToken, @QueryMap Map<String, String> options);

    /**
     * 获取视频类别，必须要传regionCode
     *
     * @param options
     * @return
     */
    @GET("videoCategories")
    public Observable<YTBCategoriesBean> getYouTubeCategories(@Query("regionCode") String regionCode, @QueryMap Map<String, String> options);


    @GET("i18nLanguages?part=snippet&key=" + APIConstant.YouTube.DEVELOPER_KEY)
    public Observable<YTBLanguagesBean> getYouTubeLanguages();

    @GET("i18nRegions?&part=snippet&key=" + APIConstant.YouTube.DEVELOPER_KEY)
    public Observable<YTbRegionsListBean> getYouTubeRegions();

    @GET("search")
    public Observable<YTBVideoPageBean> getSearchVideos(@Query("pageToken") String pageToken, @Query("q") String queryContent, @QueryMap Map<String, String> options);

    @GET("search")
    public Observable<YTBVideoPageBean> getRelatedVideo(@Query("relatedToVideoId") String relatedToVideoId, @QueryMap Map<String, String> options);


    public Observable<YTBVideoPageBean> getPlaylistItems(@Query("playlistId") String playlistId, @QueryMap Map<String, String> options);



}
