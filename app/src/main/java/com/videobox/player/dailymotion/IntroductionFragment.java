package com.videobox.player.dailymotion;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.commonlibs.base.BaseFragment;
import com.videobox.R;
import com.videobox.view.delegate.Contract;

import jaydenxiao.com.expandabletextview.ExpandableTextView;

/**
 * Created by liyanju on 2017/5/5.
 */

public class IntroductionFragment extends BaseFragment<Contract.DMPlayerHost> {

    private Activity mActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.introduction_fragment, null);
        TextView titleTV = (TextView)view.findViewById(R.id.title);
        titleTV.setText(mHost.getCurrentVideoBean().title);

        ExpandableTextView describeTV = (ExpandableTextView)view.findViewById(R.id.describe);
        describeTV.setText(mHost.getCurrentVideoBean().description);
        return view;
    }
}
