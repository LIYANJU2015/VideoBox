package com.videobox.view.delegate;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.commonlibs.themvp.view.AppDelegate;
import com.videobox.R;
import com.videobox.model.dailymotion.entity.DMVideoBean;
import com.videobox.view.adapter.DailyMotionRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liyanju on 2017/4/29.
 */

public class DailyMotionDelegate extends AppDelegate {

    private RecyclerView mRecyclerView;

    @Override
    public int getRootLayoutId() {
        return R.layout.daily_motion_fragment_layout;
    }

    @Override
    public void initWidget() {
        mRecyclerView = get(R.id.dm_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        List<DMVideoBean> infos = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            infos.add(new DMVideoBean());
        }
        mRecyclerView.setAdapter(new DailyMotionRecyclerAdapter(infos));
    }
}
