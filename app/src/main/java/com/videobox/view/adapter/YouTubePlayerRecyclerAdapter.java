package com.videobox.view.adapter;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.commonlibs.base.BaseHolder;
import com.commonlibs.util.LogUtils;
import com.commonlibs.util.ScreenUtils;
import com.commonlibs.util.SizeUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.VideoOptions;
import com.videobox.AppAplication;
import com.videobox.util.AdViewManager;
import com.videobox.util.YouTubeUtil;
import com.videobox.R;
import com.videobox.model.bean.YouTubePlayerItem;
import com.videobox.model.youtube.entity.YTBVideoPageBean;

import java.util.List;

import jaydenxiao.com.expandabletextview.ExpandableTextView;

import static android.R.attr.data;

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

    private final SparseBooleanArray mCollapsedStatus;

    public YouTubePlayerRecyclerAdapter(Activity activity, List<YouTubePlayerItem> infos) {
        super(infos);
        mActivity = activity;
        mCollapsedStatus = new SparseBooleanArray();

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
        LogUtils.v("getHolder", "viewType " + viewType);
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
        private ImageView playIV;

        public YouTubeRelatedVideoHodler(View itemView) {
            super(itemView);
            posterIV = (ImageView) itemView.findViewById(R.id.dm_poster);
            titleTV = (TextView) itemView.findViewById(R.id.name);
            timeTV = (TextView)itemView.findViewById(R.id.time);
            playIV = (ImageView)itemView.findViewById(R.id.play_iv);
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
                playIV.setVisibility(View.VISIBLE);
                posterIV.setBackgroundDrawable(ContextCompat.getDrawable(mActivity, R.drawable.playing_bg));
            } else {
                playIV.setVisibility(View.GONE);
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
        private ExpandableTextView introduceTV;
        private AdView adView;
        private LinearLayout introduceLinear;

        public YouTubeIntroduceHodler(View itemView) {
            super(itemView);
            titleTV = (TextView) itemView.findViewById(R.id.title_tv);
            introduceTV = (ExpandableTextView) itemView.findViewById(R.id.expand_text_view);
            introduceLinear = (LinearLayout)itemView.findViewById(R.id.introduce_linear);

            if (AppAplication.isShowABC()) {
                NativeExpressAdView adView = new NativeExpressAdView(mActivity);
                adView.setAdUnitId(mActivity.getString(R.string.main_dailymotionplayer_ad2));

                int adWidth = SizeUtils.px2dp(ScreenUtils.getScreenWidth() - SizeUtils.dp2px(40));

                adView.setAdSize(new AdSize(adWidth, 100));
                adView.setVideoOptions(new VideoOptions.Builder()
                        .setStartMuted(true)
                        .build());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(SizeUtils.dp2px(adWidth),
                        SizeUtils.dp2px(100));
                introduceLinear.addView(adView, params);
                adView.loadAd(AdViewManager.createAdRequest());
            }
        }

        @Override
        public void setData(YouTubePlayerItem data, int position) {
            titleTV.setText(data.curPlayVideo.title);
            introduceTV.setText(data.curPlayVideo.description, position);
            introduceTV.setOnExpandStateChangeListener(new ExpandableTextView.OnExpandStateChangeListener() {
                @Override
                public void onExpandStateChanged(TextView textView, boolean isExpanded) {
                    if (!isExpanded) {
                        if (recyclerView != null) {
                            recyclerView.scrollToPosition(0);
                        }
                    }
                }
            });
        }
    }

    private RecyclerView recyclerView;

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        super.onAttachedToRecyclerView(recyclerView);
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
