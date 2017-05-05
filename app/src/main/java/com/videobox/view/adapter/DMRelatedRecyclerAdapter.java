package com.videobox.view.adapter;

import android.app.Activity;
import android.content.pm.ProviderInfo;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.commonlibs.base.BaseHolder;
import com.commonlibs.base.BaseRecyclerViewAdapter;
import com.videobox.R;
import com.videobox.model.dailymotion.entity.DMVideoBean;

import java.util.List;

/**
 * Created by liyanju on 2017/5/5.
 */

public class DMRelatedRecyclerAdapter extends BaseRecyclerViewAdapter<DMVideoBean> {

    private Activity mActivity;

    public DMRelatedRecyclerAdapter(List<DMVideoBean> infos, Activity activity) {
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

        public DailyMotionItemHolder(View itemView, Activity activity){
            super(itemView);
            mActivity = activity;
            videoPoster = (ImageView) itemView.findViewById(R.id.dm_poster);
            nameTV = (TextView)itemView.findViewById(R.id.name);
        }
        @Override
        public void setData(DMVideoBean data, int position) {
            Glide.with(mActivity).load(data.thumbnail_url)
                    .placeholder(R.drawable.dm_item_img_default)
                    .error(R.drawable.dm_item_img_default).crossFade().into(videoPoster);
            nameTV.setText(data.title);
        }
    }
}
