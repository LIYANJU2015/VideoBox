package com.videobox.player.youtube;

import android.content.Context;
import android.view.View;

import com.commonlibs.rxerrorhandler.handler.ErrorHandleSubscriber;
import com.commonlibs.rxerrorhandler.handler.RetryWithDelay;
import com.commonlibs.util.LogUtils;
import com.commonlibs.util.StringUtils;
import com.videobox.AppAplication;
import com.videobox.model.APIConstant;
import com.videobox.model.bean.PlayRecordBean;
import com.videobox.model.bean.YouTubePlayerItem;
import com.videobox.model.db.VideoBoxContract;
import com.videobox.model.youtube.YouTuBeModel;
import com.videobox.model.youtube.entity.YTBVideoPageBean;
import com.videobox.util.FirebaseAnalyticsUtil;
import com.videobox.view.adapter.BaseRecyclerViewAdapter;

import java.util.ArrayList;

import rx.Emitter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.videobox.view.adapter.YouTubePlayerRecyclerAdapter.RELATED_VIDEO;
import static rx.Emitter.BackpressureMode.NONE;

/**
 * Created by liyanju on 2017/5/13.
 */

public class RelateVideoHandler implements BaseRecyclerViewAdapter.OnRecyclerViewItemClickListener<YouTubePlayerItem>{

    private ArrayList<YouTubePlayerItem> items;
    private Context context;
    private final String videoId;
    private YouTuBeModel youTuBeModel;
    private IPlayItemUpdate itemUpdate;
    private IPlayCallBack iPlayCallBack;
    private YouTubePlayerItem.IntroduceVideo curPlayVideo;

    private int nextVideoIndex = 0;

    private boolean hasPlayList;

    private YTBVideoPageBean.YouTubeVideo currYouTubeVideo;

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

    public YTBVideoPageBean.YouTubeVideo getCurPlayVideo() {
        return currYouTubeVideo;
    }

    public String getNextVideoId() {
        int index = 0;
        String vid = null;

        LogUtils.v("getNextVideoId nextVideoIndex " + nextVideoIndex);

        for (int i = 0; i < items.size(); i++) {
            YouTubePlayerItem item = items.get(i);
            if (item.type == RELATED_VIDEO) {
                if (index == nextVideoIndex) {
                    vid = item.relateVideo.getVideoID();
                    item.relateVideo.isPlaying = true;
                    itemUpdate.onUpdateItems(i);

                    currYouTubeVideo = item.relateVideo;

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
            nextVideoIndex++;
        }

        return vid;
    }

    public void cancelPlayingStatus() {
        nextVideoIndex = 0;
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
            requestRelatedVideo(videoId);
        }
    }

    private void requestVideoInfo() {
        youTuBeModel.getVideoInfoByVid(APIConstant.YouTube.sVideoList, videoId)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ErrorHandleSubscriber<YTBVideoPageBean>(AppAplication.sRxErrorHandler) {
                    @Override
                    public void onNext(YTBVideoPageBean dmVideosPageBean) {
                        if (dmVideosPageBean.items.size() > 0) {
                            curPlayVideo.title = dmVideosPageBean.items.get(0).snippet.title;
                            curPlayVideo.description = dmVideosPageBean.items.get(0).snippet.description;
                            currYouTubeVideo = dmVideosPageBean.items.get(0);
                        }
                        itemUpdate.onUpateAll();
                    }
                });
    }

    private ArrayList<YouTubePlayerItem> relateItemsTemp = new ArrayList<>();

    public void requestRelatedVideo(String videoId) {
        LogUtils.v("requestRelatedVideo", "videoId " + videoId);
        youTuBeModel.getRelatedVideo(APIConstant.YouTube.sSearchRelatedMap, videoId, true)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ErrorHandleSubscriber<YTBVideoPageBean>(AppAplication.sRxErrorHandler) {
                    @Override
                    public void onNext(YTBVideoPageBean dmVideosPageBean) {
                        if (dmVideosPageBean.items.size() > 0) {
                            items.removeAll(relateItemsTemp);
                            relateItemsTemp.clear();

                            requestVideoContentDetails(dmVideosPageBean.items);

                            int index = 0;
                            for (YTBVideoPageBean.YouTubeVideo video : dmVideosPageBean.items) {
                                YouTubePlayerItem playerItem = new YouTubePlayerItem();
                                playerItem.type = RELATED_VIDEO;
                                playerItem.relateVideo = video;
                                playerItem.position = index;
                                relateItemsTemp.add(playerItem);
                                index++;
                            }

                            items.addAll(relateItemsTemp);

                            itemUpdate.onUpateAll();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        itemUpdate.onUpdateError();
                    }
                });
    }

    private void requestVideoContentDetails(final ArrayList<YTBVideoPageBean.YouTubeVideo> videos) {
        StringBuilder vidsBuilder = new StringBuilder();
        for (YTBVideoPageBean.YouTubeVideo video : videos) {
            if (video.contentDetails == null) {
                vidsBuilder.append(video.getVideoID()).append(",");
            }
        }
        if (StringUtils.isEmpty(vidsBuilder.toString())) {
            return;
        }
        youTuBeModel.getVideoInfoByVid(APIConstant.YouTube.sVideoContentDetails, vidsBuilder.toString())
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ErrorHandleSubscriber<YTBVideoPageBean>(AppAplication.sRxErrorHandler) {
                    @Override
                    public void onNext(YTBVideoPageBean dmVideosPageBean) {
                        if (dmVideosPageBean.items.size() > 0) {
                            for (YTBVideoPageBean.YouTubeVideo newVideo : dmVideosPageBean.items) {
                                String vid = newVideo.getVideoID();
                                for (YTBVideoPageBean.YouTubeVideo oldVideo : videos) {
                                    if (oldVideo.getVideoID().equals(vid)) {
                                        oldVideo.contentDetails = newVideo.contentDetails;
                                    }
                                }
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
        nextVideoIndex = data.position + 1;
        LogUtils.v("onItemClick", " nextVideoIndex "+ nextVideoIndex);
        updatePlayingStatus(position);

        currYouTubeVideo = data.relateVideo;

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

        FirebaseAnalyticsUtil.of().logEventYouTubePlayerClick("relateVideo click title "
                + currYouTubeVideo.snippet.title);

    }
}
