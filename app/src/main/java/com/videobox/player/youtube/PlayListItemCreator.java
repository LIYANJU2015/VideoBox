package com.videobox.player.youtube;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.paginate.recycler.LoadingListItemCreator;
import com.videobox.R;

/**
 * Created by liyanju on 2017/5/10.
 */

public class PlayListItemCreator implements LoadingListItemCreator {

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_loading_row, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }
}
