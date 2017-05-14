/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package com.commonlibs.commonloader;

import android.view.View;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * Created by fanlitao on 16/8/1.
 */
public class ViewAware {

    protected Reference<View> viewRef;

    public ViewAware(View textView) {
        if (textView == null) throw new IllegalArgumentException("view must not be null");

        this.viewRef = new WeakReference<View>(textView);
    }

    public int getId() {
        View view = viewRef.get();
        return view == null ? super.hashCode() : view.hashCode();
    }

    public boolean isCollected() {
        return viewRef.get() == null;
    }

    public View getWrappedView() {
        return viewRef.get();
    }


}
