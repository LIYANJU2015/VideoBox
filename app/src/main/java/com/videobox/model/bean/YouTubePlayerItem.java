package com.videobox.model.bean;

import com.commonlibs.base.BaseRecyclerViewAdapter;
import com.videobox.model.youtube.entity.YTBVideoPageBean;

import java.util.ArrayList;

/**
 * Created by liyanju on 2017/5/13.
 */

public class YouTubePlayerItem {

    public int type;

    public ArrayList<YTBVideoPageBean.YouTubeVideo> videoList = new ArrayList<>();

    public IntroduceVideo curPlayVideo;

    public BaseRecyclerViewAdapter.OnRecyclerViewItemClickListener<YTBVideoPageBean.YouTubeVideo> listener;

    public YTBVideoPageBean.YouTubeVideo relateVideo;

    public int position = -1;


    public static class IntroduceVideo {

        public String title;

        public String description;
    }
}
