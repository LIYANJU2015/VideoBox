package com.example.myapplication;

import android.content.Context;
import android.content.UriMatcher;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.videobox.bean.DMChannelsBean;
import com.videobox.data.db.VideoBoxContract;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        DMChannelsBean.Channel dmChannelsBean = new DMChannelsBean.Channel();
        dmChannelsBean.id = "sss";
        dmChannelsBean.name = "222";
       Uri uri = appContext.getContentResolver()
               .insert(VideoBoxContract.DMChannels.CONTENT_URI,
                       VideoBoxContract.DMChannels.createContentValues(dmChannelsBean));
        Log.v("xx", " uri " + uri);
    }
}
