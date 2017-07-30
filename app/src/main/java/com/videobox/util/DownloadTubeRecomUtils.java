package com.videobox.util;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.videobox.R;

/**
 * Created by liyanju on 2017/7/30.
 */

public class DownloadTubeRecomUtils {

    private final static String APPPACKAGENAME = "com.download.videodownload";


    public static boolean isInstallDownloadTube(Context context) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(APPPACKAGENAME, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo == null) {
            return false;
        } else {
            return true;
        }
    }

    public static void showDownloadTubeRecomDialog(final Activity activity) {
        try {
            View view = LayoutInflater.from(activity).inflate(R.layout.download_tube_recom, null);
            final MaterialDialog dialog = new MaterialDialog.Builder(activity).customView(view, false).show();
            view.findViewById(R.id.downloadtube_download_tv).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    gotoGP(activity.getApplicationContext());
                    FirebaseAnalyticsUtil.of().logEventClickDownloadTube();
                }
            });
            FirebaseAnalyticsUtil.of().logEventShowDownloadTube();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private static void gotoGP(Context context) {
        try {
            Intent launchIntent = new Intent();
            launchIntent.setPackage("com.android.vending");
            launchIntent.setData(Uri.parse("market://details?id=" + APPPACKAGENAME));
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(launchIntent);
        } catch (android.content.ActivityNotFoundException anfe) {
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + APPPACKAGENAME)));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static ObjectAnimator leftRightShake(View view) {
        int delta = view.getResources().getDimensionPixelOffset(R.dimen.spacing_medium);

        PropertyValuesHolder pvhTranslateX = PropertyValuesHolder.ofKeyframe(View.TRANSLATION_X,
                Keyframe.ofFloat(0f, 0),
                Keyframe.ofFloat(.10f, -delta),
                Keyframe.ofFloat(.26f, delta),
                Keyframe.ofFloat(.42f, -delta),
                Keyframe.ofFloat(.58f, delta),
                Keyframe.ofFloat(.74f, -delta),
                Keyframe.ofFloat(.90f, delta),
                Keyframe.ofFloat(1f, 0f)
        );

        return ObjectAnimator.ofPropertyValuesHolder(view, pvhTranslateX).
                setDuration(500);
    }
}
