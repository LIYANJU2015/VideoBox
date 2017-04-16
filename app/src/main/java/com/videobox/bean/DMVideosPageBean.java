package com.videobox.bean;

import java.util.ArrayList;

/**
 * Created by liyanju on 2017/4/13.
 */

public class DMVideosPageBean {

    public int page;

    public int limit;

    public boolean explicit;

    public boolean has_more;

    public int total;// 视频总个数

    public ArrayList<DMVideoBean> list;

}
