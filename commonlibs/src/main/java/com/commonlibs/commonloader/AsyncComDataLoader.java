package com.commonlibs.commonloader;

import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;

import com.commonlibs.util.LogUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by liyanju on 16/8/17.
 */
public class AsyncComDataLoader {

    public static final String TAG = "CommonNumberLoader";

    private ExecutorService mEngine;

    private static AsyncComDataLoader instance;

    protected static volatile boolean sIsCancel = false;

    private AsyncComDataLoader() {
        mEngine = Executors.newCachedThreadPool();
    }

    public static AsyncComDataLoader getInstance() {
        if (instance == null) {
            synchronized (AsyncComDataLoader.class) {
                if (instance == null) {
                    instance = new AsyncComDataLoader();
                }
            }
        }
        return instance;
    }

    public static void cancelAllTask() {
        sIsCancel = true;
    }

    private boolean mIsLoad = true;

    public void attachAbsListView(AbsListView absListView, final Runnable canLoadRunnable) {
        absListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    mIsLoad = true;
                    LogUtils.v(TAG, "attachAbsListView SCROLL_STATE_IDLE");
                    if (canLoadRunnable != null) {
                        canLoadRunnable.run();
                    }
                } else {
                    mIsLoad = false;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

            }
        });
    }

    /**
     * 异步加载数据
     *
     * @param listener       监听加载过程
     * @param iComDataLoader 关心加载数据的对象需要实现这个接口，实现里面的方法
     *                       已实现AbsComNumberInfoLoader可以继承，也可以自己实现接口自定义
     * @param views          监听返回时 刷新具体view
     */
    public void display(IComDataLoaderListener listener,
                        IComDataLoader iComDataLoader,
                        View... views) {
        if (iComDataLoader == null
                || TextUtils.isEmpty(iComDataLoader.getId())
                || listener == null || views[0] == null) {
            throw new IllegalArgumentException(" display arg is null");
        }

        LogUtils.v(TAG, "display getId " + iComDataLoader.getId() + " isLoadSuccess " + iComDataLoader.isLoadSuccess());

        for (int i = 0; i < views.length; i++) {
            View view = views[i];
            view.setTag(iComDataLoader.getId());
        }

        if (iComDataLoader.isLoadSuccess()) {
            try {
                listener.onLoadingComplete(iComDataLoader, views);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        if (!mIsLoad){
            LogUtils.v(TAG, "stop loader mIsLoad false>>>");
            return;
        }

        ViewAware viewAwares[] = new ViewAware[views.length];
        for (int i = 0; i < views.length; i++) {
            View view = views[i];
            viewAwares[i] = new ViewAware(view);
        }

        sIsCancel = false;

        ComDataLoadTask commonNumberInfoTask = new ComDataLoadTask(mEngine, iComDataLoader,
                listener, viewAwares);
        mEngine.submit(commonNumberInfoTask);

    }
}
