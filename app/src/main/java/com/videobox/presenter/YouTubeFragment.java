package com.videobox.presenter;

import com.commonlibs.themvp.presenter.FragmentPresenter;
import com.videobox.view.delegate.Contract;
import com.videobox.view.delegate.YouTubeDelegate;

/**
 * Created by liyanju on 2017/5/1.
 */

public class YouTubeFragment extends FragmentPresenter<YouTubeDelegate> implements Contract.IVideoListFragment{

    @Override
    protected Class<YouTubeDelegate> getDelegateClass() {
        return YouTubeDelegate.class;
    }

    @Override
    public void showChannelVideoByID(String id) {

    }
}
