package com.videobox.model.dailymotion.service;

import com.videobox.model.dailymotion.entity.DMChannelsBean;
import com.videobox.model.dailymotion.entity.DMVideosPageBean;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;


/**
 * Created by liyanju on 2017/4/13.
 */

public interface DailymotionService {

    @GET("videos")
    public Observable<DMVideosPageBean> getVideos(@QueryMap Map<String, String> options);

    @GET("channels")
    public Observable<DMChannelsBean> getChannels(@QueryMap Map<String, String> options);

    @GET("/channel/{id}/videos")
    public Observable<DMVideosPageBean> getChannelVideos(@Path("id") String id, @QueryMap Map<String, String> options);

    @GET("playlists")
    public Observable<DMVideosPageBean> getSearchVideo(@QueryMap Map<String, String> options, @Query("search") String search);

}
