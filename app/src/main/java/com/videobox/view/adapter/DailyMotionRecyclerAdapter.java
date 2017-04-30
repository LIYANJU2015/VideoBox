package com.videobox.view.adapter;

import android.view.View;
import android.widget.TextView;

import com.commonlibs.base.BaseHolder;
import com.commonlibs.base.BaseRecyclerViewAdapter;
import com.videobox.R;
import com.videobox.model.dailymotion.entity.DMVideoBean;

import java.util.List;

/**
 * Created by liyanju on 2017/4/29.
 */

public class DailyMotionRecyclerAdapter extends BaseRecyclerViewAdapter<DMVideoBean>{


    public DailyMotionRecyclerAdapter(List<DMVideoBean> infos) {
        super(infos);
    }

    @Override
    public BaseHolder<DMVideoBean> getHolder(View v, int viewType) {
        return new DailyMotionItemHolder(v);
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.daily_motion_item;
    }

    public static class DailyMotionItemHolder extends BaseHolder<DMVideoBean> {

        private TextView textView;

        public DailyMotionItemHolder(View itemView){
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text);
        }
        @Override
        public void setData(DMVideoBean data, int position) {
            textView.setText("1111111111");
        }
    }
}
