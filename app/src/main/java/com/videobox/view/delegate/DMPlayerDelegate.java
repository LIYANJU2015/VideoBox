package com.videobox.view.delegate;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.commonlibs.base.AdapterViewPager;
import com.commonlibs.base.BaseFragment;
import com.commonlibs.themvp.view.AppDelegate;
import com.videobox.AppAplication;
import com.videobox.R;
import com.videobox.presenter.DaiyMotionPlayerActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liyanju on 2017/5/1.
 */

public class DMPlayerDelegate extends AppDelegate{

    private CharSequence mTitle[] = {
        AppAplication.getContext().getString(R.string.player_introduction),
                AppAplication.getContext().getString(R.string.player_related)};

    private DaiyMotionPlayerActivity playerActivity;


    @Override
    public int getRootLayoutId() {
        return R.layout.dm_player_layout;
    }

    @Override
    public void initWidget() {
        playerActivity = getActivity();
        AdapterViewPager adapterViewPager = new AdapterViewPager(playerActivity.getSupportFragmentManager());
        adapterViewPager.bindData(playerActivity.getAdapterFragment(), mTitle);

        ViewPager viewPager = get(R.id.dm_player_viewpager);
        viewPager.setAdapter(adapterViewPager);
        TabLayout tabLayout = get(R.id.dm_tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }
}
