package com.videobox.data.dailymotion.service;

import com.videobox.bean.DMChannels;
import com.videobox.bean.DMVideosPageBean;

import java.util.Map;

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
    public Call<DMVideosPageBean> getVideos(@QueryMap Map<String, String> options);

    @GET("channels")
    public Call<DMChannels> getChannels(@QueryMap Map<String, String> options);

    @GET("/channel/{id}/videos")
    public Call<DMVideosPageBean> getChannelVideos(@Path("id") String id, @QueryMap Map<String, String> options);

    @GET("playlists")
    public Call<DMVideosPageBean> getSearchVideo(@QueryMap Map<String, String> options, @Query("search") String search);

}
