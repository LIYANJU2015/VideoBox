package com.videobox.util;

import android.content.res.Resources;

import com.videobox.AppAplication;
import com.videobox.R;

/**
 * Created by liyanju on 2017/5/16.
 */

public class DaiymotionUtil {

    public static final int MAIN_DM_AD_VIEWTYPE = 111;

    public static String getRelativeTimeString(long secondsSinceEpoch) {
        return getRelativeTimeString(secondsSinceEpoch, 0);
    }

    public static String getRelativeTimeString(long secondsSinceEpoch, int startIndex) {
        Resources res = AppAplication.getContext().getResources();
        String timeString = null;
        long delta = (1000 * secondsSinceEpoch) - System.currentTimeMillis();
        long[] jArr = new long[6];
        jArr = new long[]{31536000000L, 2592000000L, 604800000, 86400000, 3600000, 60000};
        int[] iArr = new int[6];
        iArr = new int[]{R.string.relativeTimeInYearOne, R.string.relativeTimeInMonthOne, R.string.relativeTimeInWeekOne, R.string.relativeTimeInDayOne, R.string.relativeTimeInHourOne, R.string.relativeTimeInMinuteOne};
        int[] iArr2 = new int[6];
        iArr2 = new int[]{R.string.relativeTimeInYearOther, R.string.relativeTimeInMonthOther, R.string.relativeTimeInWeekOther, R.string.relativeTimeInDayOther, R.string.relativeTimeInHourOther, R.string.relativeTimeInMinuteOther};
        int[] iArr3 = new int[6];
        iArr3 = new int[]{R.string.relativeTimeFutureYearOne, R.string.relativeTimeFutureMonthOne, R.string.relativeTimeFutureWeekOne, R.string.relativeTimeFutureDayOne, R.string.relativeTimeFutureHourOne, R.string.relativeTimeFutureMinuteOne};
        int[] iArr4 = new int[6];
        iArr4 = new int[]{R.string.relativeTimeFutureYearOther, R.string.relativeTimeFutureMonthOther, R.string.relativeTimeFutureWeekOther, R.string.relativeTimeFutureDayOther, R.string.relativeTimeFutureHourOther, R.string.relativeTimeFutureMinuteOther};
        int i = startIndex;
        while (i < jArr.length && delta > (-jArr[i])) {
            i++;
        }
        if (i != jArr.length) {
            if ((-delta) / jArr[i] > 1) {
                timeString = res.getString(iArr2[i], new Object[]{Long.valueOf(Math.abs(delta)/jArr[i])});
            } else {
                timeString = res.getString(iArr[i], new Object[]{Long.valueOf(Math.abs(delta)/jArr[i])});
            }
        }
        i = startIndex;
        while (i < jArr.length && delta < jArr[i]) {
            i++;
        }
        if (i != jArr.length) {
            if (delta / jArr[i] > 1) {
                timeString = res.getString(iArr4[i], new Object[]{Long.valueOf(delta)/jArr[i]});
            } else {
                timeString = res.getString(iArr3[i], new Object[]{Long.valueOf(delta)/jArr[i]});
            }
        }
        if (timeString != null) {
            return timeString;
        }
        if (delta < 0) {
            return res.getString(R.string.relativeTimeJustNow);
        }
        return res.getString(R.string.relativeTimeInAFewSeconds);
    }
}
