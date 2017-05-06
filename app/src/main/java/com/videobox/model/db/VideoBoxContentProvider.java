package com.videobox.model.db;

import android.database.sqlite.SQLiteDatabase;
import android.util.SparseArray;

import com.commonlibs.provider.BaseContentProvider;
import com.commonlibs.provider.TableInfo;


/**
 * Created by liyanju on 2017/4/17.
 */

public class VideoBoxContentProvider extends BaseContentProvider{

    private static final int DMCHANNEL_CODE = 100;
    private static final int DMVIDEO_CODE = 101;
    private static final int CATEGORIES_CODE = 102;
    private static final int LAUGUAGES_CODE = 103;
    private static final int REGIONS_CODE = 104;
    private static final int VIDEO_CODE = 105;
    private static final int SEARCH_HISTORY_CODE =106;

    @Override
    public void onAddTableInfo(SparseArray<TableInfo> tableInfoArray) {
        tableInfoArray.put(DMCHANNEL_CODE, new VideoBoxContract.DMChannels());
        tableInfoArray.put(DMVIDEO_CODE, new VideoBoxContract.DMVideo());
        tableInfoArray.put(CATEGORIES_CODE, new VideoBoxContract.YouTubeCategories());
        tableInfoArray.put(LAUGUAGES_CODE, new VideoBoxContract.YouTubeLanguages());
        tableInfoArray.put(REGIONS_CODE, new VideoBoxContract.YouTubeRgions());
        tableInfoArray.put(VIDEO_CODE, new VideoBoxContract.YouTubeVideo());
        tableInfoArray.put(SEARCH_HISTORY_CODE, new VideoBoxContract.SearchHistory());
    }

    @Override
    public String onDataBaseName() {
        return VideoBoxContract.DATABASE_NAME;
    }

    @Override
    public int onDataBaseVersion() {
        return VideoBoxContract.DATABASE_VERSION;
    }

    @Override
    public void onDBUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
