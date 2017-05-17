package com.videobox.view.adapter;

import android.app.Activity;
import android.content.pm.ProviderInfo;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.commonlibs.base.BaseHolder;
import com.commonlibs.base.BaseRecyclerViewAdapter;
import com.commonlibs.util.TimeUtils;
import com.util.DaiymotionUtil;
import com.videobox.R;
import com.videobox.model.dailymotion.entity.DMVideoBean;

import java.util.List;

import static com.commonlibs.util.LogUtils.D;
import static com.commonlibs.util.TimeUtils.millis2String;

/**
 * Created by liyanju on 2017/5/5.
 */

public class DMListRecyclerAdapter extends BaseRecyclerViewAdapter<DMVideoBean> {

    private Activity mActivity;

    public DMListRecyclerAdapter(List<DMVideoBean> infos, Activity activity) {
        super(infos);
        mActivity = activity;
    }

    @Override
    public BaseHolder<DMVideoBean> getHolder(View v, int viewType) {
        return new DailyMotionItemHolder(v, mActivity);
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.dm_related_item;
    }

    public static class DailyMotionItemHolder extends BaseHolder<DMVideoBean> {

        private ImageView videoPoster;
        private TextView nameTV;

        private Activity mActivity;

        private TextView timeTV;

        private TextView updateTimeTV;

        public DailyMotionItemHolder(View itemView, Activity activity){
            super(itemView);
            mActivity = activity;
            videoPoster = (ImageView) itemView.findViewById(R.id.dm_poster);
            nameTV = (TextView)itemView.findViewById(R.id.name);
            timeTV = (TextView)itemView.findViewById(R.id.time);
            updateTimeTV = (TextView)itemView.findViewById(R.id.update_time_tv);
        }
        @Override
        public void setData(DMVideoBean data, int position) {
            Glide.with(mActivity).load(data.thumbnail_url)
                    .placeholder(R.drawable.dm_item_img_default)
                    .error(R.drawable.dm_item_img_default).crossFade().into(videoPoster);

            nameTV.setText(data.title);

            updateTimeTV.setText(DaiymotionUtil.formatTime(data.updated_time));

            if (data.duration != 0) {
                timeTV.setVisibility(View.VISIBLE);
                timeTV.setText(TimeUtils.stringForTime((int)data.duration*1000));
            } else {
                timeTV.setVisibility(View.GONE);
            }
        }
    }
}
