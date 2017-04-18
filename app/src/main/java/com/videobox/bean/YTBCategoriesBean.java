package com.videobox.bean;

import java.util.ArrayList;

/**
 * Created by liyanju on 2017/4/14.
 */

public class YTBCategoriesBean {

    public ArrayList<Categories> items;

    public static class Categories {

        public String kind;

        public String etag;

        public String id; // 等同videoCategoryId

        public Snippet snippet;

        public class Snippet {

            public String channelId;

            public String title;

            public boolean assignable;
        }
    }
}
