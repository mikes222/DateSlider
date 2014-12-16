package com.slider.DateSlider.labeler;

import android.content.Context;

import com.slider.DateSlider.TimeBoundaries;
import com.slider.DateSlider.TimeObject;
import com.slider.DateSlider.timeview.TimeTextView;
import com.slider.DateSlider.timeview.TimeView;

import java.util.Calendar;

/**
 * A Labeler that displays minutes
 */
public class MinuteLabeler extends Labeler {

    private static String TAG = "MinuteLabeler";


    public MinuteLabeler(String formatString, TimeBoundaries timeBoundaries) {
        super(formatString, timeBoundaries);
    }

    @Override
    public TimeView createView(Context context, boolean isCenterView) {
        return new TimeTextView(context, isCenterView, 18);
    }

    @Override
    public TimeObject add(long time, int val) {
        TimeObject result = Util.addMinutes(time, val, mFormatString, timeBoundaries);
        //Log.i(TAG, "add " + val + ", " + result.toString());
        return result;
    }

    public TimeObject getElem(long time) {

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);

        TimeObject result =  Util.getMinute(c, mFormatString, timeBoundaries);
        //Log.i(TAG, "getElem " + result.toString());
        return result;
    }

}
