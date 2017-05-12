package com.videobox.player.youtube;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.commonlibs.rxerrorhandler.handler.ErrorHandleSubscriber;
import com.commonlibs.rxerrorhandler.handler.RetryWithDelay;
import com.commonlibs.util.LogUtils;
import com.commonlibs.util.StringUtils;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.paginate.Paginate;
import com.videobox.AppAplication;
import com.videobox.R;
import com.videobox.model.APIConstant;
import com.videobox.model.youtube.YouTuBeModel;
import com.videobox.model.youtube.entity.YTBVideoPageBean;
import com.videobox.view.adapter.YouTubeListRecyclerAdapter;

import java.util.ArrayList;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * Created by liyanju on 2017/5/10.
 */

public class YouTubePlayerActivity extends YouTubeFailureRecoveryActivity implements Paginate.Callbacks{

    private YouTubePlayerView mYouTubePlayerView;

    private YouTubePlayer mPlayer;

    private static final String VIDEO_ID = "VIDEO_ID";
    private static final String PLAY_LIST = "play_list";

    private String mVideoID;
    private String mPlaylistID;

    private RecyclerView mPlayerListRecyclerView;

    private Context mContext;

    private ArrayList<YTBVideoPageBean.YouTubeVideo> mVideoList = new ArrayList<>();

    private YouTubeListRecyclerAdapter mPlayListAdapter;

    private AppAplication mAppApplication;

    private YouTuBeModel mYouTuBeModel;

    private String pageToken;

    private boolean mIsLoadingMore;
    private boolean isLoadedAll;

    private Paginate mPaginate;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mContext = getApplicationContext();
        mAppApplication = (AppAplication)getApplication();

        setContentView(R.layout.youtube_player_layout);

        mYouTuBeModel = new YouTuBeModel(mAppApplication.getAppComponent().repositoryManager());

        mYouTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        mYouTubePlayerView.initialize(APIConstant.YouTube.DEVELOPER_KEY, this);

        initPlayListView();

        parseIntent();

        if (!StringUtils.isEmpty(mPlaylistID)) {
            mPlayerListRecyclerView.setVisibility(View.VISIBLE);
            requestPlaylistItem(mPlaylistID);
        } else {
            mPlayerListRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoadMore() {
        requestPlaylistItem(mPlaylistID);
    }

    @Override
    public boolean isLoading() {
        return mIsLoadingMore;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return false;
    }

    private void requestPlaylistItem(String playlistId) {
        mYouTuBeModel.getPlaylistItems(APIConstant.YouTube.sPlayItemMap, playlistId, true, pageToken)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        LogUtils.v("requestPlaylistItem doOnSubscribe call");
                        mIsLoadingMore = true;
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(new Action0() {
                    @Override
                    public void call() {
                        LogUtils.v("requestPlaylistItem", "doAfterTerminate ");
                        mIsLoadingMore = false;
                    }
                })
                .subscribe(new ErrorHandleSubscriber<YTBVideoPageBean>(mAppApplication.getAppComponent().rxErrorHandler()) {
                    @Override
                    public void onNext(YTBVideoPageBean dmVideosPageBean) {
                        isLoadedAll = StringUtils.isEmpty(dmVideosPageBean.nextPageToken);
                        LogUtils.v("requestPlaylistItem", "onNext " + isLoadedAll);
                        if (!isLoadedAll) {
                            pageToken = dmVideosPageBean.nextPageToken;
                        }

                        int preEndIndex = mVideoList.size();
                        if (dmVideosPageBean.items != null) {
                            mVideoList.addAll(dmVideosPageBean.items);
                        }

                        if (mVideoList.size() > 0 && mPaginate == null) {
                            mPaginate = Paginate.with(mPlayerListRecyclerView, YouTubePlayerActivity.this)
                                    .setLoadingTriggerThreshold(0).addLoadingListItem(true)
                                    .setLoadingListItemCreator(new PlayListItemCreator())
                                    .build();
                            mPaginate.setHasMoreDataToLoad(false);
                        }

                        mPlayListAdapter.notifyItemRangeInserted(preEndIndex, dmVideosPageBean.items.size());
                    }
                });
    }

    private void initPlayListView() {
        mPlayerListRecyclerView = (RecyclerView)findViewById(R.id.playerlist_recyclerview);
        mPlayerListRecyclerView.setLayoutManager(new LinearLayoutManager(mContext,
                LinearLayoutManager.HORIZONTAL, false));
        mPlayerListRecyclerView.setHasFixedSize(true);
        mPlayListAdapter = new YouTubeListRecyclerAdapter(mVideoList, this, YouTubeListRecyclerAdapter.PLAYLIST_TYPE);
        mPlayerListRecyclerView.setAdapter(mPlayListAdapter);
    }

    private void parseIntent() {
        mVideoID = getIntent().getStringExtra(VIDEO_ID);
        mPlaylistID = getIntent().getStringExtra(PLAY_LIST);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPaginate != null) {
            mPaginate.unbind();
        }
    }

    @Override
    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return mYouTubePlayerView;
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                        YouTubePlayer youTubePlayer, boolean wasRestored) {
        LogUtils.v("onInitializationSuccess", "mPlaylistID " + mPlaylistID + " mVideoID " + mVideoID);
        mPlayer = youTubePlayer;
        if (!wasRestored) {
            if (!StringUtils.isEmpty(mPlaylistID)) {
                mPlayer.cuePlaylist(mPlaylistID);
            }else if (!StringUtils.isEmpty(mVideoID)) {
                mPlayer.cueVideo(mVideoID);
            }
        }
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
