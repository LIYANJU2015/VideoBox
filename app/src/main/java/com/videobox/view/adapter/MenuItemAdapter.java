package com.videobox.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.videobox.R;
import com.videobox.model.dailymotion.entity.DMChannelsBean;
import com.videobox.model.youtube.entity.YTBCategoriesBean;

import java.util.ArrayList;

import static android.media.CamcorderProfile.get;

/**
 * Created by liyanju on 2017/4/30.
 */

public class MenuItemAdapter extends BaseAdapter{

    private ArrayList<DMChannelsBean.Channel> mChannels = new ArrayList<>();
    private ArrayList<YTBCategoriesBean.Categories> mCategories = new ArrayList<>();
    private int mCurChannelType;

    private static final int YOUTUBE_TYPE = 1;
    private static final int DM_TYPE = 2;

    private Context mContext;

    public MenuItemAdapter(Context context) {
        mContext = context;
    }


    public void updateDMChannel(ArrayList<DMChannelsBean.Channel> channels) {
        mChannels.addAll(channels);
        mCurChannelType = DM_TYPE;
        notifyDataSetChanged();
    }

    public void updateYouTubeCategories(ArrayList<YTBCategoriesBean.Categories> channels) {
        mCategories.addAll(channels);
        mCurChannelType = YOUTUBE_TYPE;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mCurChannelType == DM_TYPE) {
            return mChannels.size();
        } else {
            return mCategories.size();
        }
    }

    @Override
    public Object getItem(int i) {
        if (mCurChannelType == DM_TYPE) {
            return mChannels.get(i);
        } else {
            return mCategories.get(i);
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int postion, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.channel_item, null);
        }

        TextView channelTV = (TextView)view.findViewById(R.id.channel_tv);

        if (mCurChannelType == DM_TYPE) {
            DMChannelsBean.Channel channel = mChannels.get(postion);
            channelTV.setText(channel.name);
        } else {
            YTBCategoriesBean.Categories categories = mCategories.get(postion);
            channelTV.setText(categories.snippet.title);
        }

        return view;
    }
}
