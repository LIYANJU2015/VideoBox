package com.videobox.model.dailymotion.service;

import com.videobox.model.dailymotion.entity.DMChannelsBean;
import com.videobox.model.dailymotion.entity.DMVideosPageBean;

import java.util.Map;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;


/**
 * Created by liyanju on 2017/4/13.
 */

public interface DailymotionService {

    @GET("videos?limit=20")
    public Observable<DMVideosPageBean> getVideos(@Query("page") int page, @QueryMap Map<String, String> options);

    @GET("channels?limit=20")
    public Observable<DMChannelsBean> getChannels(@QueryMap Map<String, String> options, @Query("page") int page);

    @GET("/channel/{id}/videos?limit=20")
    public Observable<DMVideosPageBean> getChannelVideos(@Path("id") String id, @QueryMap Map<String, String> options, @Query("page") int page);

    @GET("playlists?limit=20")
    public Observable<DMVideosPageBean> getSearchVideo(@QueryMap Map<String, String> options, @Query("search") String search, @Query("page") int page);

}
