package com.videobox.view.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.commonlibs.base.BaseHolder;
import com.util.YouTubeUtil;
import com.videobox.R;
import com.videobox.model.youtube.entity.YTBVideoPageBean;

import java.util.List;

/**
 * Created by liyanju on 2017/5/8.
 */

public class YouTubeMainRecyclerAdapter extends BaseRecyclerViewAdapter<YTBVideoPageBean.YouTubeVideo> {

    private Activity mActivity;

    public YouTubeMainRecyclerAdapter(List<YTBVideoPageBean.YouTubeVideo> infos, Activity activity) {
        super(infos);
        mActivity = activity;
    }

    @Override
    public BaseHolder<YTBVideoPageBean.YouTubeVideo> getHolder(View v, int viewType) {
        return new YouTubeItemHolder(v, mActivity);
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.daily_motion_item;
    }

    public static class YouTubeItemHolder extends BaseHolder<YTBVideoPageBean.YouTubeVideo> {

        private ImageView videoPoster;

        private Activity mActivity;

        private TextView titleTV;

        private TextView timeTV;

        public YouTubeItemHolder(View itemView, Activity activity) {
            super(itemView);
            mActivity = activity;
            videoPoster = (ImageView) itemView.findViewById(R.id.video_poster);
            titleTV = (TextView)itemView.findViewById(R.id.title);
            timeTV = (TextView)itemView.findViewById(R.id.time);
        }

        @Override
        public void setData(YTBVideoPageBean.YouTubeVideo data, int position) {
            Glide.with(mActivity).load(data.getThumbnailsUrl())
                    .placeholder(R.drawable.dm_item_img_default)
                    .error(R.drawable.dm_item_img_default).crossFade().into(videoPoster);

            titleTV.setText(data.snippet.title);

            if (data.contentDetails != null) {
                timeTV.setVisibility(View.VISIBLE);
                timeTV.setText(YouTubeUtil.convertDuration(data.contentDetails.duration));
            } else {
                timeTV.setVisibility(View.GONE);
            }

        }
    }
}
