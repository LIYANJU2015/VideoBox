package com.videobox.bean;

import java.util.ArrayList;

/**
 * Created by liyanju on 2017/4/14.
 * i18n语言列表
 */

public class YouTubeLanguages {

    public ArrayList<Languages> items;

    public class Languages {

        public String id;

        public Snippet snippet;

        public class Snippet {

            public String hl;

            public String name;
        }
    }

}
