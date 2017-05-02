package com.videobox.presenter;

import android.content.Intent;
import android.os.Build;

import com.commonlibs.themvp.presenter.ActivityPresenter;
import com.dailymotion.websdk.DMWebVideoView;
import com.videobox.AppAplication;
import com.videobox.R;
import com.videobox.view.delegate.DMPlayerDelegate;

/**
 * Created by liyanju on 2017/5/1.
 */

public class DaiyMotionPlayerActivity extends ActivityPresenter<DMPlayerDelegate> {

    private DMWebVideoView mVideoView;

    private static final String VIDEO_ID = "video_id";

    @Override
    protected Class<DMPlayerDelegate> getDelegateClass() {
        return DMPlayerDelegate.class;
    }

    @Override
    protected void initAndBindEven() {
        String videoId = getIntent().getStringExtra(VIDEO_ID);

        mVideoView = viewDelegate.get(R.id.dmWebVideoView);
        mVideoView.setVideoId(videoId);
        mVideoView.setAutoPlay(true);
        mVideoView.load();
    }

    @Override
    public void onBackPressed() {
        mVideoView.handleBackPress(this);
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

    public static void launch(String videoId) {
        Intent intent = new Intent(AppAplication.getContext(), DaiyMotionPlayerActivity.class);
        intent.putExtra(VIDEO_ID, videoId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        AppAplication.getContext().startActivity(intent);
    }

}
