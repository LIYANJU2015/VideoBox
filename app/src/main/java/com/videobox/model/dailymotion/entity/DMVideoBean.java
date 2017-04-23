package com.videobox.model.dailymotion.entity;


import com.google.gson.annotations.SerializedName;

/**
 * Created by liyanju on 2017/4/13.
 */

public class DMVideoBean {

    public String channel;

    @SerializedName("channel.id")
    public String channelID;

    public String description;

    public String duration;

    public String id;

    public String thumbnail_url;

    @SerializedName(value = "title", alternate={"name"})
    public String title;

    public String updated_time;

}
