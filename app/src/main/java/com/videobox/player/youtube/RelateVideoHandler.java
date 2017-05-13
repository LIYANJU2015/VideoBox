package com.videobox.player.youtube;

import android.content.Context;
import android.view.View;

import com.commonlibs.base.BaseRecyclerViewAdapter;
import com.commonlibs.rxerrorhandler.handler.ErrorHandleSubscriber;
import com.commonlibs.rxerrorhandler.handler.RetryWithDelay;
import com.commonlibs.util.LogUtils;
import com.commonlibs.util.StringUtils;
import com.google.android.youtube.player.YouTubePlayer;
import com.videobox.AppAplication;
import com.videobox.model.APIConstant;
import com.videobox.model.bean.PlayRecordBean;
import com.videobox.model.bean.YouTubePlayerItem;
import com.videobox.model.db.VideoBoxContract;
import com.videobox.model.youtube.YouTuBeModel;
import com.videobox.model.youtube.entity.YTBVideoPageBean;

import java.util.ArrayList;

import rx.Emitter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static android.media.CamcorderProfile.get;
import static com.videobox.view.adapter.YouTubePlayerRecyclerAdapter.RELATED_VIDEO;
import static rx.Emitter.BackpressureMode.NONE;

/**
 * Created by liyanju on 2017/5/13.
 */

public class RelateVideoHandler implements BaseRecyclerViewAdapter.OnRecyclerViewItemClickListener<YouTubePlayerItem>, YouTubePlayer.PlayerStateChangeListener{

    private ArrayList<YouTubePlayerItem> items;
    private Context context;
    private final String videoId;
    private YouTuBeModel youTuBeModel;
    private IPlayItemUpdate itemUpdate;
    private IPlayCallBack iPlayCallBack;
    private YouTubePlayerItem.IntroduceVideo curPlayVideo;

    private int curIndex = 0;

    private boolean hasPlayList;

    public RelateVideoHandler(Context context, String videoId, YouTuBeModel youTuBeModel,
                              ArrayList<YouTubePlayerItem> items, IPlayItemUpdate itemUpdate,
                              YouTubePlayerItem.IntroduceVideo curPlayVideo) {
        this.context = context;
        this.items = items;
        this.videoId = videoId;
        this.youTuBeModel = youTuBeModel;
        this.itemUpdate = itemUpdate;
        this.curPlayVideo = curPlayVideo;
    }

    public String getNextVideoId() {
        int index = 0;
        String vid = null;
        for (int i = 0; i < items.size(); i++) {
            YouTubePlayerItem item = items.get(i);
            if (item.type == RELATED_VIDEO) {
                if (index == curIndex) {
                    vid = item.relateVideo.getVideoID();
                    item.relateVideo.isPlaying = true;
                    itemUpdate.onUpdateItems(i);

                    curPlayVideo.description = item.relateVideo.snippet.description;
                    curPlayVideo.title = item.relateVideo.snippet.title;
                    if (hasPlayList) {
                        itemUpdate.onUpdateItems(1);
                    } else {
                        itemUpdate.onUpdateItems(0);
                    }
                } else if (item.relateVideo != null && item.relateVideo.isPlaying) {
                    item.relateVideo.isPlaying = false;
                    itemUpdate.onUpdateItems(i);
                }
                index++;
            }
       }

        if (!StringUtils.isEmpty(vid)) {
            curIndex++;
        }

        return vid;
    }

    public void cancelPlayingStatus() {
        curIndex = 0;
        for (int i = 0; i < items.size(); i++) {
            YouTubePlayerItem item = items.get(i);
            if (item.relateVideo != null && item.relateVideo.isPlaying) {
                item.relateVideo.isPlaying = false;
                itemUpdate.onUpdateItems(i);
            }
        }

    }

    public void start(IPlayCallBack iPlayCallBack, boolean hasPlayList){
        this.iPlayCallBack = iPlayCallBack;
        this.hasPlayList = hasPlayList;

        if (!hasPlayList) {
            requestVideoInfo();
            long time = VideoBoxContract.PlayRecord.getPlayRecordTimeByVid(AppAplication.getContext(),
                    videoId, PlayRecordBean.YOUTUBE_TYPE);
            iPlayCallBack.onCanPlayVideo(videoId, (int)time);
        }

        requestRelatedVideo();
    }

    private void requestVideoInfo() {
        youTuBeModel.getVideoInfoByVid(APIConstant.YouTube.sVideoList, videoId, true)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ErrorHandleSubscriber<YTBVideoPageBean>(AppAplication.sRxErrorHandler) {
                    @Override
                    public void onNext(YTBVideoPageBean dmVideosPageBean) {
                        if (dmVideosPageBean.items.size() > 0) {
                            curPlayVideo.title = dmVideosPageBean.items.get(0).snippet.title;
                            curPlayVideo.title = dmVideosPageBean.items.get(0).snippet.description;
                        }
                        itemUpdate.onUpateAll();
                    }
                });
    }

    private void requestRelatedVideo() {
        youTuBeModel.getRelatedVideo(APIConstant.YouTube.sSearchRelatedMap, videoId, true)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ErrorHandleSubscriber<YTBVideoPageBean>(AppAplication.sRxErrorHandler) {
                    @Override
                    public void onNext(YTBVideoPageBean dmVideosPageBean) {
                        if (dmVideosPageBean.items.size() > 0) {
                            int index = 0;
                            for (YTBVideoPageBean.YouTubeVideo video : dmVideosPageBean.items) {
                                YouTubePlayerItem playerItem = new YouTubePlayerItem();
                                playerItem.type = RELATED_VIDEO;
                                playerItem.relateVideo = video;
                                playerItem.position = index;
                                items.add(playerItem);
                                index++;
                            }
                            itemUpdate.onUpateAll();
                        }
                    }
                });
    }

    private void updatePlayingStatus(int newPostion) {
        int index = 0;
        for (YouTubePlayerItem item : items) {
            if (item.relateVideo != null && item.relateVideo.isPlaying) {
                item.relateVideo.isPlaying = false;
                itemUpdate.onUpdateItems(index);
            }

            if (item.relateVideo != null && index == newPostion) {
                item.relateVideo.isPlaying = true;
                itemUpdate.onUpdateItems(index);
            }

            index++;
        }
    }

    @Override
    public void onItemClick(View view, int viewType, final YouTubePlayerItem data, int position) {
        curIndex = data.position;
        LogUtils.v("onItemClick", " curIndex "+ curIndex);
        updatePlayingStatus(position);

        curPlayVideo.title = data.relateVideo.snippet.title;
        curPlayVideo.description = data.relateVideo.snippet.description;
        if (hasPlayList) {
            itemUpdate.onUpdateItems(1);
        } else {
            itemUpdate.onUpdateItems(0);
        }

        Observable.create(new Action1<Emitter<Long>>() {
            @Override
            public void call(Emitter<Long> emitter) {
                emitter.onNext(VideoBoxContract.PlayRecord
                        .getPlayRecordTimeByVid(context, data.relateVideo.getVideoID(), PlayRecordBean.YOUTUBE_TYPE));
            }
        }, NONE).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long time) {
                        iPlayCallBack.onCanPlayVideo(data.relateVideo.getVideoID(), (int)time.longValue());
                    }
                });

    }

    @Override
    public void onLoading() {

    }

    @Override
    public void onLoaded(String s) {

    }

    @Override
    public void onAdStarted() {

    }

    @Override
    public void onVideoStarted() {

    }

    @Override
    public void onVideoEnded() {
        String vid = getNextVideoId();
        LogUtils.v("onVideoEnded", " vid " + vid);
        if (!StringUtils.isEmpty(vid)) {
            long time = VideoBoxContract.PlayRecord.getPlayRecordTimeByVid(AppAplication.getContext(), vid, PlayRecordBean.YOUTUBE_TYPE);
            iPlayCallBack.onCanPlayVideo(vid, (int)time);
        } else {
            iPlayCallBack.onPlayRelateVideoEnd();
        }
    }



    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {

    }
}
