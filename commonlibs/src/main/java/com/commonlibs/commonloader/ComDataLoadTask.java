package com.commonlibs.commonloader;

import android.text.TextUtils;
import android.view.View;

import com.commonlibs.util.LogUtils;
import com.commonlibs.util.UIThreadHelper;

import java.util.concurrent.ExecutorService;

/**
 * Created by liyanju on 16/8/17.
 */
public class ComDataLoadTask implements Runnable {

    public static final String TAG = AsyncComDataLoader.TAG;

    private ViewAware mViewAwares[];

    private IComDataLoaderListener mListener;

    private IComDataLoader mIComDataLoader;

    private ExecutorService mEngine;

    private String mId;

    public ComDataLoadTask(ExecutorService numberInfoLoaderEngine,
                           IComDataLoader iComDataLoader,
                           IComDataLoaderListener listener,
                           ViewAware... viewAwares) {
        mViewAwares = viewAwares;
        mListener = listener;
        mIComDataLoader = iComDataLoader;
        mEngine = numberInfoLoaderEngine;
        mId = mIComDataLoader.getId();
    }

    private boolean isViewsCollected() {
        if (mViewAwares != null) {
            for (int i = 0; i < mViewAwares.length; i++) {
                if (mViewAwares[i].isCollected()) {
                    return true;
                }
            }
        }
        return false;
    }

    private View[] viewAwareToView() {
        final View views[] = new View[mViewAwares.length];
        for (int i = 0; i < mViewAwares.length; i++) {
            views[i] = mViewAwares[i].getWrappedView();
        }
        return views;
    }

    @Override
    public void run() {
        if (isViewsCollected()) {
            mListener.onCancelLoading(viewAwareToView());
            return;
        }

        if (AsyncComDataLoader.sIsCancel) {
            return;
        }

        if (mIComDataLoader.isLoadSuccess()) {
            LogUtils.v(TAG, "ComDataLoadTask isLoadSuccess >>");
            return;
        }

        final Object dataObj = mIComDataLoader.onHandleSelfData(new AsyncHandleListener() {
            @Override
            public void onStartHandle() {
                UIThreadHelper.getInstance().runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isViewTagMatch() && !AsyncComDataLoader.sIsCancel) {
                            mListener.onStartLoading(viewAwareToView());
                        }
                    }
                });
            }

            @Override
            public void onHandleSuccess(final Object obj) {
                UIThreadHelper.getInstance().runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isViewTagMatch() && !AsyncComDataLoader.sIsCancel) {
                            mIComDataLoader.setLoadDataObj(obj);
                            mIComDataLoader.setLoadSuccess();
                            try {
                                mListener.onLoadingComplete(mIComDataLoader, viewAwareToView());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            if (isViewTagMatch()) {
                                mListener.onCancelLoading(viewAwareToView());
                            }

                        }
                    }
                });
            }

            @Override
            public void onHandleError() {
                UIThreadHelper.getInstance().runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!AsyncComDataLoader.sIsCancel && isViewTagMatch()) {
                            mListener.onCancelLoading(viewAwareToView());
                        }
                    }
                });
            }
        });

        UIThreadHelper.getInstance().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (!AsyncComDataLoader.sIsCancel && isViewTagMatch() && dataObj != null) {
                    mIComDataLoader.setLoadDataObj(dataObj);
                    mIComDataLoader.setLoadSuccess();
                    try {
                        mListener.onLoadingComplete(mIComDataLoader, viewAwareToView());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    if (!AsyncComDataLoader.sIsCancel && isViewTagMatch()) {
                        mListener.onCancelLoading(viewAwareToView());
                    }
                }
            }
        });
    }

    private boolean isViewTagMatch() {
        for (int i = 0; i < mViewAwares.length; i++) {
            ViewAware viewAware = mViewAwares[i];
            if (viewAware.isCollected()) {
                LogUtils.v(TAG, "isViewTagMatch isCollected " + mIComDataLoader.getId());
                return false;
            }
        }

        if (mViewAwares != null && mViewAwares.length > 0 && !mViewAwares[0].isCollected()) {
            View view = mViewAwares[0].getWrappedView();
            if (view == null) {
                return false;
            }
            String tag = (String) view.getTag();
            LogUtils.v(TAG, " isViewTagMatch run tag: " + tag + " getId: " + mId);
            if (TextUtils.isEmpty(mId)
                    || !mIComDataLoader.getId().equals(tag)) {
                LogUtils.v(TAG, " isViewTagMatch run false ");
                return false;
            }
        }
        return true;
    }

    public interface AsyncHandleListener {

        void onStartHandle();

        void onHandleSuccess(Object obj);

        void onHandleError();
    }
}
