package com.videobox.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.commonlibs.base.BaseFragment;
import com.videobox.R;

/**
 * Created by liyanju on 2017/5/6.
 */

public class YoutubeSearchFragment extends BaseFragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recyclerview_layout, null);
        return view;
    }
}
