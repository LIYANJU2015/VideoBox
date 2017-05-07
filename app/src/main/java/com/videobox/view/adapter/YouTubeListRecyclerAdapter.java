package com.videobox.view.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.commonlibs.base.BaseHolder;
import com.commonlibs.base.BaseRecyclerViewAdapter;
import com.commonlibs.util.StringUtils;
import com.videobox.R;
import com.videobox.model.youtube.entity.YTBVideoPageBean;

import java.util.List;

/**
 * Created by liyanju on 2017/5/7.
 */

public class YouTubeListRecyclerAdapter extends BaseRecyclerViewAdapter<YTBVideoPageBean.YouTubeVideo> {

    private Activity mActivity;

    public YouTubeListRecyclerAdapter(List<YTBVideoPageBean.YouTubeVideo> infos, Activity activity) {
        super(infos);
        mActivity = activity;
    }

    @Override
    public BaseHolder<YTBVideoPageBean.YouTubeVideo> getHolder(View v, int viewType) {
        return new YouTubeItemHolder(v, mActivity);
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.dm_related_item;
    }

    public static class YouTubeItemHolder extends BaseHolder<YTBVideoPageBean.YouTubeVideo> {

        private ImageView videoPoster;
        private TextView nameTV;

        private Activity mActivity;

        public YouTubeItemHolder(View itemView, Activity activity){
            super(itemView);
            mActivity = activity;
            videoPoster = (ImageView) itemView.findViewById(R.id.dm_poster);
            nameTV = (TextView)itemView.findViewById(R.id.name);
        }
        @Override
        public void setData(YTBVideoPageBean.YouTubeVideo data, int position) {
            String thumbnailUrl;
            if (data.snippet.thumbnails.higth != null && !StringUtils.isEmpty(data.snippet.thumbnails.higth.url)) {
                thumbnailUrl = data.snippet.thumbnails.higth.url;
            } else {
                thumbnailUrl = data.snippet.thumbnails.default1.url;
            }
            Glide.with(mActivity).load(thumbnailUrl)
                    .placeholder(R.drawable.dm_item_img_default)
                    .error(R.drawable.dm_item_img_default).crossFade().into(videoPoster);
            nameTV.setText(data.snippet.title);
        }
    }
}
