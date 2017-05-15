package com.videobox.player.youtube;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.commonlibs.util.LogUtils;
import com.commonlibs.util.StringUtils;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.videobox.AppAplication;
import com.videobox.R;
import com.videobox.model.APIConstant;
import com.videobox.model.bean.PlayRecordBean;
import com.videobox.model.bean.YouTubePlayerItem;
import com.videobox.model.db.VideoBoxContract;
import com.videobox.model.youtube.YouTuBeModel;
import com.videobox.model.youtube.entity.YTBVideoPageBean;
import com.videobox.view.adapter.YouTubePlayerRecyclerAdapter;
import com.videobox.view.widget.LoadingFrameLayout;

import java.util.ArrayList;

import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by liyanju on 2017/5/10.
 */

public class YouTubePlayerActivity extends YouTubeFailureRecoveryActivity implements IPlayItemUpdate{

    private YouTubePlayerView mYouTubePlayerView;

    private static final String VIDEO_ID = "VIDEO_ID";
    private static final String PLAY_LIST = "play_list";

    private String mVideoID;
    private String mPlaylistID;

    private RecyclerView mPlayerRecyclerView;

    private Context mContext;

    private AppAplication mAppApplication;

    private YouTuBeModel mYouTuBeModel;

    private YouTubePlayerManager mPlayerManager;

    private ArrayList<YouTubePlayerItem> mPlayerItems = new ArrayList<>();

    private YouTubePlayerRecyclerAdapter mPlayerAdapter;

    private LoadingFrameLayout mLoadingFrameLayout;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mContext = getApplicationContext();
        mAppApplication = (AppAplication)getApplication();

        setContentView(R.layout.youtube_player_layout);

        mYouTuBeModel = new YouTuBeModel(mAppApplication.getAppComponent().repositoryManager());

        mYouTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        mYouTubePlayerView.initialize(APIConstant.YouTube.DEVELOPER_KEY, this);

        parseIntent();

        initView();
    }

    private void initView() {
        mPlayerRecyclerView = (RecyclerView)findViewById(R.id.player_recyclerview);
        mPlayerRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mPlayerRecyclerView.setHasFixedSize(true);
        mPlayerRecyclerView.getRecycledViewPool()
                .setMaxRecycledViews(YouTubePlayerRecyclerAdapter.INTRODUCE_TYPE, 1);
        mPlayerRecyclerView.getRecycledViewPool()
                .setMaxRecycledViews(YouTubePlayerRecyclerAdapter.RELATED_VIDEO, 6);
        mPlayerRecyclerView.getRecycledViewPool()
                .setMaxRecycledViews(YouTubePlayerRecyclerAdapter.PLAYLIST_ITEM_TYPE, 1);

        mPlayerAdapter = new YouTubePlayerRecyclerAdapter(this, mPlayerItems);
        mPlayerRecyclerView.setAdapter(mPlayerAdapter);

        mLoadingFrameLayout = (LoadingFrameLayout)findViewById(R.id.loading_frame);
        mLoadingFrameLayout.smoothToshow();
    }

    private void parseIntent() {
        mVideoID = getIntent().getStringExtra(VIDEO_ID);
        mPlaylistID = getIntent().getStringExtra(PLAY_LIST);
    }

    @Override
    public void onUpdatePlayListItmes(int position) {
        if (mPlayerAdapter != null && mPlayerAdapter.getYouTubePlayItemHodler() != null) {
            mPlayerAdapter.getYouTubePlayItemHodler().notifyItemChanged(position);
        }
    }

    @Override
    public void onUpdatePlayListItmes() {
        if (mPlayerAdapter != null) {
            mPlayerAdapter.notifyItemChanged(0);
        }
    }

    private int updateCount = 0;

    @Override
    public void onUpateAll() {
        if (mPlayerAdapter != null) {
            mPlayerAdapter.notifyDataSetChanged();
        }
        mPlayerRecyclerView.scrollToPosition(0);

        updateCount++;

        if (updateCount == 2) {
            mLoadingFrameLayout.smoothToHide();
        }
    }

    @Override
    public void onUpdateItems(int postion) {
        if (mPlayerAdapter != null && mPlayerAdapter.getItemCount() > postion) {
            mPlayerAdapter.notifyItemChanged(postion);
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                        YouTubePlayer youTubePlayer, boolean wasRestored) {
        LogUtils.v("onInitializationSuccess", "mPlaylistID " + mPlaylistID + " mVideoID " + mVideoID);
        if (!wasRestored) {
            YouTubePlayerItem introduceItem = new YouTubePlayerItem();
            introduceItem.type = YouTubePlayerRecyclerAdapter.INTRODUCE_TYPE;
            introduceItem.curPlayVideo = new YouTubePlayerItem.IntroduceVideo();

            //add playlist item
            PlayListHandler playListHandler = null;
            if (!StringUtils.isEmpty(mPlaylistID)) {
                YouTubePlayerItem playerlistItem = new YouTubePlayerItem();
                playerlistItem.type = YouTubePlayerRecyclerAdapter.PLAYLIST_ITEM_TYPE;
                playListHandler = new PlayListHandler(mContext, mPlaylistID, mYouTuBeModel,
                        playerlistItem.videoList, this, introduceItem.curPlayVideo);
                playerlistItem.listener = playListHandler;
                mPlayerItems.add(playerlistItem);
            }

            mPlayerItems.add(introduceItem);

            RelateVideoHandler relateVideoHandler = new RelateVideoHandler(mContext, mVideoID,
                    mYouTuBeModel, mPlayerItems, this, introduceItem.curPlayVideo);
            mPlayerAdapter.setOnItemClickRelateListener(relateVideoHandler);

            mPlayerManager = new YouTubePlayerManager(youTubePlayer, playListHandler, relateVideoHandler);
        }
    }

    private void savePlayRecord() {
        YTBVideoPageBean.YouTubeVideo video = mPlayerManager.getCurPlayVideo();
        if (video != null) {
            PlayRecordBean recordBean = new PlayRecordBean();
            recordBean.vid = video.snippet.resourceId.videoId;
            recordBean.totalTime = mPlayerManager.getDurationMillis();
            recordBean.playTime = mPlayerManager.getCurrentTimeMillis();
            recordBean.thumbnailUrl = video.getThumbnailsUrl();
            recordBean.playlistId = mPlaylistID;
            recordBean.type = PlayRecordBean.YOUTUBE_TYPE;
            recordBean.title = video.snippet.title;
            LogUtils.v("savePlayRecord", " title : " + recordBean.title,
                    " playlistId" + recordBean.playlistId, " vid " + recordBean.vid);
            VideoBoxContract.PlayRecord.addPlayRecord(mContext, recordBean);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Observable.just(0).observeOn(Schedulers.io()).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                savePlayRecord();
            }
        });
    }

    @Override
    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return mYouTubePlayerView;
    }

    public static void launchVideoID(Context context, String videoID) {
        Intent intent = new Intent(context, YouTubePlayerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(VIDEO_ID, videoID);
        context.startActivity(intent);
    }

    public static void launchPlayListID(Context context, String playlistid) {
        Intent intent = new Intent(context, YouTubePlayerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(PLAY_LIST, playlistid);
        context.startActivity(intent);
    }
}
