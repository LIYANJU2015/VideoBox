package com.commonlibs.commonloader;

import android.view.View;

/**
 * Created by liyanju on 16/8/17.
 */
public interface IComDataLoaderListener<T> {

    public void onLoadingComplete(IComDataLoader<T> infoLoader, View... views);

    public void onCancelLoading(View... views);

    public void onStartLoading(View... views);
}
