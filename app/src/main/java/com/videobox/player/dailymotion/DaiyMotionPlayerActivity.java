package com.videobox.player.dailymotion;

import android.content.Intent;
import android.os.Build;

import com.commonlibs.base.BaseFragment;
import com.commonlibs.themvp.presenter.ActivityPresenter;
import com.dailymotion.websdk.DMWebVideoView;
import com.videobox.AppAplication;
import com.videobox.R;
import com.videobox.model.dailymotion.entity.DMVideoBean;
import com.videobox.view.delegate.Contract;
import com.videobox.view.delegate.DMPlayerDelegate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liyanju on 2017/5/1.
 */

public class DaiyMotionPlayerActivity extends ActivityPresenter<DMPlayerDelegate> implements Contract.DMPlayerHost{

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
        mVideoView.load();
        mVideoView.play();
    }

    public DMVideoBean getCurrentVideoBean() {
        return mDMVideoBean;
    }

    public String getCurrentVid(){
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
