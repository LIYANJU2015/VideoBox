package com.videobox.player.dailymotion;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.commonlibs.base.BaseFragment;
import com.commonlibs.themvp.presenter.ActivityPresenter;
import com.commonlibs.util.LogUtils;
import com.dailymotion.websdk.DMWebVideoView;
import com.videobox.AppAplication;
import com.videobox.R;
import com.videobox.model.bean.PlayRecordBean;
import com.videobox.model.dailymotion.entity.DMVideoBean;
import com.videobox.model.db.VideoBoxContract;
import com.videobox.view.delegate.Contract;
import com.videobox.view.delegate.DMPlayerDelegate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liyanju on 2017/5/1.
 */

public class DaiyMotionPlayerActivity extends ActivityPresenter<DMPlayerDelegate> implements Contract.DMPlayerHost, DMWebViewEvent {

    private DMWebVideoView mVideoView;

    private static final String VIDEO_ID = "video_id";
    private static final String DMVIDEOBEAN = "DMVideoBean";

    private IntroductionFragment mIntroductionFragment;
    private RelatedFragment mRelatedFragment;

    private DMVideoBean mDMVideoBean;
    private String videoId;

    @Override
    protected Class<DMPlayerDelegate> getDelegateClass() {
        return DMPlayerDelegate.class;
    }

    @Override
    protected void initAndBindEven() {
        videoId = getIntent().getStringExtra(VIDEO_ID);
        mDMVideoBean = getIntent().getParcelableExtra(DMVIDEOBEAN);

        mVideoView = viewDelegate.get(R.id.dmWebVideoView);
        mVideoView.setVideoId(videoId);
        mVideoView.setAutoPlay(true);
        mVideoView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.material_black));
        mVideoView.load();
        mVideoView.setDMEventListener(this);

        viewDelegate.setMaxProgress(mDMVideoBean.duration);
        viewDelegate.setProgress(0);
    }

    @Override
    public void setCurrentVideoBean(DMVideoBean dmVideoBean) {
        videoId = dmVideoBean.id;
        mDMVideoBean = dmVideoBean;
        viewDelegate.setMaxProgress(dmVideoBean.duration);
    }

    @Override
    public void onStartVideo() {
        viewDelegate.get(R.id.dm_progressbar).setVisibility(View.GONE);
    }

    @Override
    public void onLoadedmetadata() {

    }

    @Override
    public void onProgress(double bufferedTime) {
        LogUtils.v("onProgress", " bufferedTime " + bufferedTime);
        viewDelegate.setProgress((int)bufferedTime);
    }

    @Override
    public void onDurationchange(double duration) {

    }

    @Override
    public void onRebuffer(boolean rebuffer) {

    }

    @Override
    public void qualitiesavailable() {

    }

    @Override
    public void onError(String error) {
        ((AppAplication)getApplication()).showShortSnackbar(error);
    }

    @Override
    public void onSeeking(double currentTime) {
        viewDelegate.setProgress((int)currentTime);
    }

    @Override
    public void onPause(boolean paused) {

    }

    @Override
    public void onFullscreenchange(boolean fullscreen) {
        LogUtils.v("onFullscreenchange", " fullscreen " + fullscreen);
        if (fullscreen) {
            viewDelegate.get(R.id.dm_player_viewpager).setVisibility(View.GONE);
            viewDelegate.get(R.id.dm_tabLayout).setVisibility(View.GONE);

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            viewDelegate.get(R.id.dm_player_viewpager).setVisibility(View.VISIBLE);
            viewDelegate.get(R.id.dm_tabLayout).setVisibility(View.VISIBLE);

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void onEnd(boolean end) {
    }

    public DMVideoBean getCurrentVideoBean() {
        return mDMVideoBean;
    }

    public String getCurrentVid() {
        return videoId;
    }

    @Override
    public DMWebVideoView getCurrentPlayer() {
        return mVideoView;
    }

    public List<BaseFragment> getAdapterFragment() {
        List<BaseFragment> fragments = new ArrayList<>();
        mIntroductionFragment = new IntroductionFragment();
        mRelatedFragment = new RelatedFragment();
        fragments.add(mIntroductionFragment);
        fragments.add(mRelatedFragment);
        return fragments;
    }

    @Override
    public void onBackPressed() {
        mVideoView.handleBackPress(this);
//        LogUtils.v("onBackPressed", " duration ::" + mVideoView.duration
//                + " getDuration: " + mDMVideoBean.getDuration());
//        if (!mVideoView.isFullscreen()) {
//            PlayRecordBean recordBean = new PlayRecordBean();
//            recordBean.playTime = (long)mVideoView.duration;
//            recordBean.title = mDMVideoBean.title;
//            recordBean.type = PlayRecordBean.DAILYMOTION_TYPE;
//            recordBean.thumbnailUrl = mDMVideoBean.thumbnail_url;
//            recordBean.vid = mDMVideoBean.id;
//            recordBean.totalTime = mDMVideoBean.getDuration();
//
//            VideoBoxContract.PlayRecord.addPlayRecord(mContext, recordBean);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mVideoView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mVideoView.onPause();
        }
    }

    public static void launch(DMVideoBean dmVideoBean) {
        Intent intent = new Intent(AppAplication.getContext(), DaiyMotionPlayerActivity.class);
        intent.putExtra(VIDEO_ID, dmVideoBean.id);
        intent.putExtra(DMVIDEOBEAN, dmVideoBean);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        AppAplication.getContext().startActivity(intent);
    }
}
