package com.videobox.player.youtube;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.commonlibs.util.LogUtils;
import com.commonlibs.util.StatusBarColorCompat;
import com.commonlibs.util.StringUtils;
import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
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
import com.videobox.util.FirebaseAnalyticsUtil;
import com.videobox.view.adapter.YouTubePlayerRecyclerAdapter;
import com.videobox.view.widget.LoadingFrameLayout;

import java.util.ArrayList;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by liyanju on 2017/5/10.
 */

public class YouTubePlayerActivity extends YouTubeFailureRecoveryActivity implements IPlayItemUpdate,
        YouTubePlayer.OnFullscreenListener{

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

    private boolean fullscreen;

    private LinearLayout baseLayout;

    private YouTubePlayer youTubePlayer;

    private MaterialProgressBar playProgressBar;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        StatusBarColorCompat.setColorNoTranslucent(YouTubePlayerActivity.this,
                ContextCompat.getColor(getApplicationContext(), R.color.material_black));

        mContext = getApplicationContext();
        mAppApplication = (AppAplication)getApplication();

        setContentView(R.layout.youtube_player_layout);

        mYouTuBeModel = new YouTuBeModel(mAppApplication.getAppComponent().repositoryManager());

        mYouTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        try {
            mYouTubePlayerView.initialize(APIConstant.YouTube.DEVELOPER_KEY, this);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        parseIntent();

        initView();

        checkYouTube();

        FirebaseAnalyticsUtil.of().logEventYouTubePlayer();
    }

    private void checkYouTube() {
        YouTubeInitializationResult result = YouTubeApiServiceUtil
                .isYouTubeApiServiceAvailable(mContext);
        LogUtils.v("onCreate", "YouTubeInitializationResult "+
                result + " isUserRecoverableError " + result.isUserRecoverableError());
        if (result != YouTubeInitializationResult.SUCCESS && result.isUserRecoverableError()){
            Dialog dialog = result.getErrorDialog(this, 1);
            if (dialog != null) {
                dialog.show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.v("onActivityResult", " requestCode " + requestCode + " resultCode " + resultCode);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            finish();
            if (!StringUtils.isEmpty(mPlaylistID)) {
                YouTubePlayerActivity.launchVideoID(this, mPlaylistID);
            } else {
                YouTubePlayerActivity.launchVideoID(this, mVideoID);
            }
        }
    }

    private void initView() {
        baseLayout = (LinearLayout)findViewById(R.id.base_layout);

        mPlayerRecyclerView = (RecyclerView)findViewById(R.id.player_recyclerview);
        mPlayerRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mPlayerRecyclerView.setHasFixedSize(true);
        mPlayerAdapter = new YouTubePlayerRecyclerAdapter(this, mPlayerItems);
        mPlayerRecyclerView.setAdapter(mPlayerAdapter);

        mLoadingFrameLayout = (LoadingFrameLayout)findViewById(R.id.loading_frame);
        mLoadingFrameLayout.smoothToshow();

        playProgressBar = (MaterialProgressBar)findViewById(R.id.play_progress);
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

    @Override
    public void onFullscreen(boolean isFullscreen) {
        fullscreen = isFullscreen;
        doLayout();
    }

    private int defaultUiVisibility = -1;

    private void doLayout() {
        LinearLayout.LayoutParams playerParams =
                (LinearLayout.LayoutParams) mYouTubePlayerView.getLayoutParams();
        if (fullscreen) {
            playerParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            playerParams.height = LinearLayout.LayoutParams.MATCH_PARENT;
            mPlayerRecyclerView.setVisibility(View.GONE);

            defaultUiVisibility = getWindow().getDecorView().getSystemUiVisibility();

            setSystemUiVisibilityImmerse();

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            mPlayerRecyclerView.setVisibility(View.VISIBLE);
            playerParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            playerParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;

            getWindow().getDecorView().setSystemUiVisibility(defaultUiVisibility);

            final WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attrs);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    private void setSystemUiVisibilityImmerse() {
        if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
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
        LogUtils.v("onUpateAll", "updateCount " + updateCount);
        if (updateCount >= 2) {
            mLoadingFrameLayout.smoothToHide();
        }
    }

    @Override
    public void onUpdateError() {
        mLoadingFrameLayout.showError();
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
        this.youTubePlayer = youTubePlayer;
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

            mPlayerManager = new YouTubePlayerManager(youTubePlayer, playListHandler,
                    relateVideoHandler, playProgressBar);

            youTubePlayer.setOnFullscreenListener(this);
        }
    }

    private void savePlayRecord() {
        if (youTubePlayer == null) {
            return;
        }
        boolean isSave = mPlayerManager.getDurationMillis() - mPlayerManager.getCurrentTimeMillis()
                >= 1000 * 60;
        LogUtils.v("savePlayRecord", "isSave " + isSave);
        if (!isSave) {
            return;
        }
        YTBVideoPageBean.YouTubeVideo video = mPlayerManager.getCurPlayVideo();
        if (video != null) {
            PlayRecordBean recordBean = new PlayRecordBean();
            if (video.snippet.resourceId != null) {
                recordBean.vid = video.snippet.resourceId.videoId;
            } else {
                recordBean.vid = video.getVideoID();
            }
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
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayerManager != null) {
            mPlayerManager.onDestory();
        }
    }

    @Override
    public void onBackPressed() {
            if (fullscreen) {
                if (youTubePlayer != null) {
                    youTubePlayer.setFullscreen(false);
                }
            } else {
                super.onBackPressed();
                Observable.just(0).observeOn(Schedulers.io()).subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        try {
                            savePlayRecord();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_bottom_out);
    }

    @Override
    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return mYouTubePlayerView;
    }

    public static void launchVideoID(Activity activity, String videoID) {
        Intent intent = new Intent(activity, YouTubePlayerActivity.class);
        intent.putExtra(VIDEO_ID, videoID);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_bottom_in, 0);
    }

    public static void launchPlayListID(Activity activity, String playlistid) {
        Intent intent = new Intent(activity, YouTubePlayerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(PLAY_LIST, playlistid);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_bottom_in, 0);
    }
}
