package com.videobox.view.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.commonlibs.base.BaseHolder;
import com.commonlibs.util.TimeUtils;
import com.videobox.R;
import com.videobox.model.dailymotion.entity.DMVideoBean;

import java.util.List;

/**
 * Created by liyanju on 2017/4/29.
 */

public class DMMainRecyclerAdapter extends BaseRecyclerViewAdapter<DMVideoBean> {

    private Activity mActivity;

    public DMMainRecyclerAdapter(List<DMVideoBean> infos, Activity activity) {
        super(infos);
        mActivity = activity;
    }

    @Override
    public BaseHolder<DMVideoBean> getHolder(View v, int viewType) {
        return new DailyMotionItemHolder(v, mActivity);
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.daily_motion_item;
    }

    public static class DailyMotionItemHolder extends BaseHolder<DMVideoBean> {

        private ImageView videoPoster;

        private Activity mActivity;

        private TextView titleTV;

        private TextView timeTV;

        public DailyMotionItemHolder(View itemView, Activity activity) {
            super(itemView);
            mActivity = activity;
            videoPoster = (ImageView) itemView.findViewById(R.id.video_poster);
            titleTV = (TextView)itemView.findViewById(R.id.title);
            timeTV = (TextView)itemView.findViewById(R.id.time);
        }

        @Override
        public void setData(DMVideoBean data, int position) {
            Glide.with(mActivity).load(data.thumbnail_url)
                    .placeholder(R.drawable.dm_item_img_default)
                    .error(R.drawable.dm_item_img_default).crossFade().into(videoPoster);
            titleTV.setText(data.title);
            timeTV.setText(TimeUtils.stringForTime(data.duration * 1000));
        }
    }
}
