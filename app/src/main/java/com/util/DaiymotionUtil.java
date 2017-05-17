package com.util;

import android.content.Context;

import com.videobox.AppAplication;
import com.videobox.R;

import java.text.SimpleDateFormat;

/**
 * Created by liyanju on 2017/5/16.
 */

public class DaiymotionUtil {

    static SimpleDateFormat sdfyyyyMMddHHmmss = new SimpleDateFormat("yyyy-MM-dd");

    public static String formatTime(long startDate) {
        if (startDate == 0) {
            return null;
        }
        Context context = AppAplication.getContext();
        long timeLong = startDate;

        if (timeLong < 60 * 1000) {
            timeLong = timeLong / 1000;
            return timeLong == 1 ? String.format(context.getString(R.string.second), timeLong) : String.format(
                    context.getString(R.string.seconds), timeLong);
        } else if (timeLong < 60 * 60 * 1000) {
            timeLong = timeLong / 1000 / 60;
            return timeLong == 1 ? String.format(context.getString(R.string.minute), timeLong) : String.format(
                    context.getString(R.string.minutes), timeLong);
        } else if (timeLong < 60 * 60 * 24 * 1000) {
            timeLong = timeLong / 60 / 60 / 1000;
            return timeLong == 1 ? String.format(context.getString(R.string.hour), timeLong) : String.format(
                    context.getString(R.string.hours), timeLong);
        } else if (timeLong < 60 * 60 * 24 * 1000 * 7) {
            timeLong = timeLong / 1000 / 60 / 60 / 24;
            return timeLong == 1 ? String.format(context.getString(R.string.day), timeLong) : String.format(
                    context.getString(R.string.days), timeLong);
        } else if (timeLong < 60 * 60 * 24 * 1000 * 7 * 4) {
            timeLong = timeLong / 1000 / 60 / 60 / 24 / 7;
            return timeLong == 1 ? String.format(context.getString(R.string.week), timeLong) : String.format(
                    context.getString(R.string.weeks), timeLong);
        }  else if (timeLong < 60 * 60 * 24 * 1000 * 7 * 4 * 12) {
            timeLong = timeLong / 1000 / 60 / 60 / 24 / 7/12;
            return timeLong == 1 ? String.format(context.getString(R.string.month), timeLong) : String.format(
                    context.getString(R.string.weeks), timeLong);
        }else {
            return sdfyyyyMMddHHmmss.format(startDate);
        }
    }
}
