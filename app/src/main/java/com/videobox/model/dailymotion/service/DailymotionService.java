package com.videobox.model.dailymotion.service;

import com.videobox.model.dailymotion.entity.DMChannelsBean;
import com.videobox.model.dailymotion.entity.DMVideosPageBean;

import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;


/**
 * Created by liyanju on 2017/4/13.
 */

public interface DailymotionService {

    @GET("videos")
    public Observable<DMVideosPageBean> getVideos(@Query("page") int page, @QueryMap Map<String, String> options);

    @GET("channels")
    public Observable<DMChannelsBean> getChannels(@QueryMap Map<String, String> options, @Query("page") int page);

    @GET("/channel/{id}/videos")
    public Observable<DMVideosPageBean> getChannelVideos(@Path("id") String id, @QueryMap Map<String, String> options, @Query("page") int page);

    @GET("videos")
    public Observable<DMVideosPageBean> getSearchVideo(@QueryMap Map<String, String> options, @Query("search") String search, @Query("page") int page);

    @GET("/video/{id}/related")
    public Observable<DMVideosPageBean> getVideoRelated(@Path("id")String id, @QueryMap Map<String, String> options, @Query("page") int page);

}
