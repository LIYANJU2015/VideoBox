package com.videobox.model.dailymotion.entity;

import java.util.ArrayList;

/**
 * Created by liyanju on 2017/4/13.
 */

public class DMVideosPageBean {

    public int page; //当前页数

    public int limit; // 一页显示个数

    public boolean explicit;

    public boolean has_more; //是否有更多

    public int total;// 视频总个数

    public ArrayList<DMVideoBean> list;

}
