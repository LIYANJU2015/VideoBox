package com.videobox.bean;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by liyanju on 2017/4/14.
 */

public class YouTubeVideoPageBean {

    public String nextPageToken;

    public String prevPageToken;

    public PageInfo pageInfo;

    public ArrayList<YouTubeVideo> items;

    public static class PageInfo {

        public int totalResults;

        public int resultsPerPage;
    }

    public static class YouTubeVideo {


        public Object id;

        public Snippet snippet;

        public String getVideoID() {
            if (id instanceof String) {
                return (String)id;
            } else if (id instanceof ID) {
                return ((ID)id).videoId;
            }
            return "-1";
        }

        public class ID {

            public String kind;

            public String videoId;
        }

    }

    public static class Snippet {

        public String publishedAt;

        public String channelId;

        public String title;

        public String description;

        public Thumbnails thumbnails;

        public String channelTitle;

        public String tags[];

        public String liveBroadcastContent;

        public String defaultAudioLanguage;

        public class Thumbnails {

            public Standard standard;

            public class Standard {
                public String url;
            }
        }
    }

}
