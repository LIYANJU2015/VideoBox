package com.videobox.view.adapter;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.commonlibs.base.BaseHolder;
import com.commonlibs.base.BaseRecyclerViewAdapter;
import com.commonlibs.util.TimeUtils;
import com.util.YouTubeUtil;
import com.videobox.R;
import com.videobox.model.bean.YouTubePlayerItem;
import com.videobox.model.youtube.entity.YTBVideoPageBean;

import java.util.List;

/**
 * Created by liyanju on 2017/5/13.
 */

public class YouTubePlayerRecyclerAdapter extends BaseRecyclerViewAdapter<YouTubePlayerItem> {

    public static final int PLAYLIST_ITEM_TYPE = 1;
    public static final int INTRODUCE_TYPE = 2;
    public static final int RELATED_VIDEO = 3;

    private Activity mActivity;

    private OnRecyclerViewItemClickListener<YouTubePlayerItem> relateListener;

    private YouTubePlayItemHodler playItemHodler;

    public YouTubePlayerRecyclerAdapter(Activity activity, List<YouTubePlayerItem> infos) {
        super(infos);
        mActivity = activity;

        super.setOnItemClickListener(new OnRecyclerViewItemClickListener<YouTubePlayerItem>() {
            @Override
            public void onItemClick(View view, int viewType, YouTubePlayerItem data, int position) {
                if (relateListener != null && viewType == RELATED_VIDEO) {
                    relateListener.onItemClick(view, viewType, data, position);
                }
            }
        });
    }

    public void setOnItemClickRelateListener(OnRecyclerViewItemClickListener<YouTubePlayerItem> listener) {
        relateListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return getInfos().get(position).type;
    }

    public YouTubePlayItemHodler getYouTubePlayItemHodler() {
        return playItemHodler;
    }

    @Override
    public BaseHolder<YouTubePlayerItem> getHolder(View v, int viewType) {
        if (viewType == PLAYLIST_ITEM_TYPE) {
            playItemHodler = new YouTubePlayItemHodler(v);
            return playItemHodler;
        } else if (viewType == INTRODUCE_TYPE) {
            return new YouTubeIntroduceHodler(v);
        } else {
            return new YouTubeRelatedVideoHodler(v);
        }
    }

    @Override
    public int getLayoutId(int viewType) {
        if (viewType == PLAYLIST_ITEM_TYPE) {
            return R.layout.playitem_layout;
        } else if (viewType == INTRODUCE_TYPE) {
            return R.layout.youtube_introduce_item;
        } else {
            return R.layout.dm_related_item;
        }
    }

    public class YouTubeRelatedVideoHodler extends BaseHolder<YouTubePlayerItem> {

        private ImageView posterIV;
        private TextView titleTV;
        private TextView timeTV;

        public YouTubeRelatedVideoHodler(View itemView) {
            super(itemView);
            posterIV = (ImageView) itemView.findViewById(R.id.dm_poster);
            titleTV = (TextView) itemView.findViewById(R.id.name);
            timeTV = (TextView)itemView.findViewById(R.id.time);
        }

        @Override
        public void setData(YouTubePlayerItem data, int position) {

            if (data.relateVideo.contentDetails != null) {
                timeTV.setVisibility(View.VISIBLE);
                timeTV.setText(YouTubeUtil.convertDuration(data.relateVideo.contentDetails.duration));
            } else {
                timeTV.setVisibility(View.GONE);
            }

            if (data.relateVideo.isPlaying) {
                posterIV.setBackgroundDrawable(ContextCompat.getDrawable(mActivity, R.drawable.playing_bg));
            } else {
                posterIV.setBackgroundDrawable(null);
            }
            titleTV.setText(data.relateVideo.snippet.title);
            Glide.with(mActivity).load(data.relateVideo.getThumbnailsUrl())
                    .placeholder(R.drawable.dm_item_img_default)
                    .error(R.drawable.dm_item_img_default).crossFade().into(posterIV);
        }
    }

    public class YouTubeIntroduceHodler extends BaseHolder<YouTubePlayerItem> {

        private TextView titleTV;
        private TextView introduceTV;

        public YouTubeIntroduceHodler(View itemView) {
            super(itemView);
            titleTV = (TextView) itemView.findViewById(R.id.title_tv);
            introduceTV = (TextView) itemView.findViewById(R.id.introduce_tv);
        }

        @Override
        public void setData(YouTubePlayerItem data, int position) {
            titleTV.setText(data.curPlayVideo.title);
            introduceTV.setText(data.curPlayVideo.description);
        }
    }

    public class YouTubePlayItemHodler extends BaseHolder<YouTubePlayerItem> {

        private RecyclerView recyclerView;

        private YouTubeListRecyclerAdapter playListAdapter;

        private int postion;

        public YouTubePlayItemHodler(View itemView) {
            super(itemView);
            recyclerView = (RecyclerView)itemView.findViewById(R.id.playerlist_recyclerview);
            recyclerView.setLayoutManager(new LinearLayoutManager(mActivity.getApplicationContext(),
                    LinearLayoutManager.HORIZONTAL, false));
            recyclerView.setHasFixedSize(true);
        }

        public void notifyItemChanged(int position) {
            playListAdapter.notifyItemChanged(position);
        }

        @Override
        public void setData(YouTubePlayerItem data, int position) {
            if (playListAdapter == null) {
                playListAdapter = new YouTubeListRecyclerAdapter(data.videoList, mActivity, YouTubeListRecyclerAdapter.PLAYLIST_TYPE);
                playListAdapter.setOnItemClickListener(data.listener);
                recyclerView.setAdapter(playListAdapter);
            } else {
                playListAdapter.notifyDataSetChanged();
            }

            postion = -1;
            for(int i = 0; i < data.videoList.size(); i++) {
                YTBVideoPageBean.YouTubeVideo video = data.videoList.get(i);
                if (video.isPlaying) {
                    postion = i;
                    recyclerView.scrollToPosition(i);
                    break;
                }
            }
            if (postion != -1) {
                recyclerView.scrollToPosition(postion);
            }

        }
    }
}
