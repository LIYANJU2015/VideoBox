package com.videobox.view.delegate;

import com.dailymotion.websdk.DMWebVideoView;
import com.videobox.model.dailymotion.entity.DMVideoBean;

/**
 * Created by liyanju on 2017/5/1.
 */

public class Contract {

    public interface IVideoListFragment {

        void showChannelVideoByID(String id);
    }

    public interface DMPlayerHost {

        DMVideoBean getCurrentVideoBean();

        void setCurrentVideoBean(DMVideoBean dmVideoBean);

        String getCurrentVid();

        DMWebVideoView getCurrentPlayer();
    }

    public interface CommonHost {

        void showLoading();

        void hideLoading();
    }
}
