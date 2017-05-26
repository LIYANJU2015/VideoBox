package com.videobox.view.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.commonlibs.base.BaseHolder;
import com.commonlibs.commonloader.AsyncComDataLoader;
import com.commonlibs.commonloader.IComDataLoader;
import com.commonlibs.commonloader.IComDataLoaderListener;
import com.commonlibs.util.LogUtils;
import com.commonlibs.util.StringUtils;
import com.util.YouTubeUtil;
import com.videobox.AppAplication;
import com.videobox.R;
import com.videobox.model.youtube.entity.YTBVideoPageBean;

import java.util.List;

/**
 * Created by liyanju on 2017/5/7.
 */

public class YouTubeListRecyclerAdapter extends BaseRecyclerViewAdapter<YTBVideoPageBean.YouTubeVideo> {

    private Activity mActivity;

    private int mType = COMMON_TYPE;
    public static final int COMMON_TYPE = 1;
    public static final int PLAYLIST_TYPE = 2;

    private ContentDetailsLoaderListener loaderListener = new ContentDetailsLoaderListener();
    private PlayListCountLoaderListener playlistListener = new PlayListCountLoaderListener();


    public YouTubeListRecyclerAdapter(List<YTBVideoPageBean.YouTubeVideo> infos, Activity activity, int type) {
        this(infos, activity);
        mType = type;
    }

    public YouTubeListRecyclerAdapter(List<YTBVideoPageBean.YouTubeVideo> infos, Activity activity) {
        super(infos);
        mActivity = activity;
    }

    @Override
    public BaseHolder<YTBVideoPageBean.YouTubeVideo> getHolder(View v, int viewType) {
        LogUtils.v("YouTubeListRecyclerAdapter getHolder");
        if (mType == PLAYLIST_TYPE) {
            return new YouTubePlayItemHodler(v, mActivity);
        } else {
            return new YouTubeItemHolder(v, mActivity);
        }
    }

    @Override
    public int getLayoutId(int viewType) {
        if (mType == PLAYLIST_TYPE) {
            return R.layout.playitem_list_layout;
        } else {
            return R.layout.dm_related_item;
        }
    }

    public class YouTubePlayItemHodler extends BaseHolder<YTBVideoPageBean.YouTubeVideo> {

        private Activity mActivity;

        private TextView mTitleIV;
        private ImageView thumbnail;
        private TextView mTimeTV;

        private ImageView playIV;

        public YouTubePlayItemHodler(View itemView, Activity activity) {
            super(itemView);
            mActivity = activity;
            thumbnail = (ImageView) itemView.findViewById(R.id.item_poseter);
            mTitleIV = (TextView) itemView.findViewById(R.id.title);
            mTimeTV = (TextView) itemView.findViewById(R.id.time);
            playIV = (ImageView) itemView.findViewById(R.id.play_iv);

        }

        @Override
        public void setData(YTBVideoPageBean.YouTubeVideo data, int position) {
            mTitleIV.setText(data.snippet.title);
            Glide.with(mActivity).load(data.getThumbnailsUrl())
                    .placeholder(R.drawable.dm_item_img_default)
                    .error(R.drawable.dm_item_img_default).crossFade().into(thumbnail);

            if (data.isPlaying) {
                LogUtils.v(" YouTubeItemHolder isPlaying");
                playIV.setVisibility(View.VISIBLE);
                thumbnail.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.playing_bg));
            } else {
                playIV.setVisibility(View.GONE);
                thumbnail.setBackgroundDrawable(null);
            }

            LogUtils.v("YouTubePlayItemHodler", " contentDetails :" + data.contentDetails);
            if (data.contentDetails != null && !StringUtils.isEmpty(data.contentDetails.duration)) {
                mTimeTV.setVisibility(View.VISIBLE);
                mTimeTV.setText(YouTubeUtil.convertDuration(data.contentDetails.duration));
            } else {
                if (!data.status.isPrivacy()) {
                    AsyncComDataLoader.getInstance().display(loaderListener, data, mTimeTV);
                }
                mTimeTV.setVisibility(View.GONE);
            }
        }
    }

    public class ContentDetailsLoaderListener implements IComDataLoaderListener<YTBVideoPageBean.ContentDetails> {
        @Override
        public void onLoadingComplete(IComDataLoader<YTBVideoPageBean.ContentDetails> infoLoader, View... views) {
            if (!StringUtils.isEmpty(infoLoader.getLoadDataObj().duration)) {
                views[0].setVisibility(View.VISIBLE);
                ((TextView) views[0]).setText(YouTubeUtil
                        .convertDuration(infoLoader.getLoadDataObj().duration));
            }
        }

        @Override
        public void onCancelLoading(View... views) {

        }

        @Override
        public void onStartLoading(View... views) {

        }
    }

    public class PlayListCountLoaderListener implements IComDataLoaderListener<YTBVideoPageBean.ContentDetails> {
        @Override
        public void onLoadingComplete(IComDataLoader<YTBVideoPageBean.ContentDetails> infoLoader, View... views) {
            if (infoLoader.getLoadDataObj().itemCount != 0) {
                views[0].setVisibility(View.VISIBLE);
                ((TextView) views[0]).setText(String.format(AppAplication.getContext().getString(R.string.video),
                        String.valueOf(infoLoader.getLoadDataObj().itemCount)));
            }
        }

        @Override
        public void onCancelLoading(View... views) {

        }

        @Override
        public void onStartLoading(View... views) {

        }
    }

    public class YouTubeItemHolder extends BaseHolder<YTBVideoPageBean.YouTubeVideo> {

        private ImageView videoPoster;
        private TextView nameTV;
        private LinearLayout bgLinear;

        private Activity mActivity;

        private TextView mTimeTV;

        private TextView playlistCountTV;

        public YouTubeItemHolder(View itemView, Activity activity) {
            super(itemView);
            mActivity = activity;
            videoPoster = (ImageView) itemView.findViewById(R.id.dm_poster);
            nameTV = (TextView) itemView.findViewById(R.id.name);
            bgLinear = (LinearLayout) itemView.findViewById(R.id.bg_linear);
            mTimeTV = (TextView) itemView.findViewById(R.id.time);
            playlistCountTV = (TextView) itemView.findViewById(R.id.update_time_tv);
        }

        @Override
        public void setData(YTBVideoPageBean.YouTubeVideo data, int position) {
            Glide.with(mActivity).load(data.getThumbnailsUrl())
                    .placeholder(R.drawable.dm_item_img_default)
                    .error(R.drawable.dm_item_img_default).crossFade().into(videoPoster);
            nameTV.setText(data.snippet.title);

            if (data.contentDetails != null && data.contentDetails.itemCount > 0) {
                playlistCountTV.setVisibility(View.VISIBLE);
                mTimeTV.setVisibility(View.GONE);
                playlistCountTV.setText(String.format(AppAplication.getContext().getString(R.string.video),
                        String.valueOf(data.contentDetails.itemCount)));
            } else if (data.contentDetails != null && !StringUtils.isEmpty(data.contentDetails.duration)) {
                mTimeTV.setVisibility(View.VISIBLE);
                playlistCountTV.setVisibility(View.GONE);
                mTimeTV.setText(YouTubeUtil.convertDuration(data.contentDetails.duration));
            } else {
                if (StringUtils.isEmpty(data.getPlaylistID())) {
                    AsyncComDataLoader.getInstance().display(loaderListener, data, mTimeTV);
                } else {
                    AsyncComDataLoader.getInstance().display(playlistListener, data, playlistCountTV);
                }
                playlistCountTV.setVisibility(View.GONE);
                mTimeTV.setVisibility(View.GONE);
            }
        }
    }
}
