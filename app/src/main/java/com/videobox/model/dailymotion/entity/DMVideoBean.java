package com.videobox.model.dailymotion.entity;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by liyanju on 2017/4/13.
 */

public class DMVideoBean implements Parcelable{

    @Expose
    public boolean isPlaying;

    public String channel;

    @SerializedName("channel.id")
    public String channelID;

    public String description;

    public int duration;

    public String id;

    public String thumbnail_url;

    @SerializedName(value = "title", alternate={"name"})
    public String title;

    public long updated_time;

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.channel);
        dest.writeString(this.channelID);
        dest.writeString(this.description);
        dest.writeInt(this.duration);
        dest.writeString(this.id);
        dest.writeString(this.thumbnail_url);
        dest.writeString(this.title);
        dest.writeLong(this.updated_time);
    }

    public DMVideoBean() {
    }

    protected DMVideoBean(Parcel in) {
        this.channel = in.readString();
        this.channelID = in.readString();
        this.description = in.readString();
        this.duration = in.readInt();
        this.id = in.readString();
        this.thumbnail_url = in.readString();
        this.title = in.readString();
        this.updated_time = in.readLong();
    }

    public static final Creator<DMVideoBean> CREATOR = new Creator<DMVideoBean>() {
        @Override
        public DMVideoBean createFromParcel(Parcel source) {
            return new DMVideoBean(source);
        }

        @Override
        public DMVideoBean[] newArray(int size) {
            return new DMVideoBean[size];
        }
    };
}
