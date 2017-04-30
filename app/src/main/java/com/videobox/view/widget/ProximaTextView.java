package com.videobox.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.videobox.AppAplication;


/**
 * Created by Dmytro Denysenko on 5/6/15.
 */
public class ProximaTextView extends TextView {

    public ProximaTextView(Context context) {
        this(context, null);
    }

    public ProximaTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProximaTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(AppAplication.sProximaRegular);
    }

}
