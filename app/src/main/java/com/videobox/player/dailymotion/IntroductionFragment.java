package com.videobox.player.dailymotion;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.commonlibs.base.BaseFragment;
import com.commonlibs.util.ScreenUtils;
import com.commonlibs.util.SizeUtils;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.VideoOptions;
import com.videobox.util.AdViewManager;
import com.videobox.R;
import com.videobox.view.delegate.Contract;

import jaydenxiao.com.expandabletextview.ExpandableTextView;

import static com.videobox.R.id.introduce_linear;

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
        describeTV.setText(Html.fromHtml(mHost.getCurrentVideoBean().description));

        LinearLayout linearLayout = (LinearLayout)view.findViewById(R.id.introduce_linear);

        NativeExpressAdView adView = new NativeExpressAdView(mActivity);
        int adWidth = SizeUtils.px2dp(ScreenUtils.getScreenWidth() - SizeUtils.dp2px(40));
        adView.setAdSize(new AdSize(adWidth, 320));
        adView.setAdUnitId(mActivity.getString(R.string.player_ad2));
        adView.setVideoOptions(new VideoOptions.Builder()
                .setStartMuted(true)
                .build());
        linearLayout.addView(adView);
        adView.loadAd(AdViewManager.createAdRequest());


        return view;
    }
}
