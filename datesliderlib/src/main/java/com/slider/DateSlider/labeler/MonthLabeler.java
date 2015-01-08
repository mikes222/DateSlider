package com.slider.DateSlider.labeler;

import android.content.Context;

import com.slider.DateSlider.TimeBoundaries;
import com.slider.DateSlider.TimeObject;
import com.slider.DateSlider.timeview.TimeTextView;
import com.slider.DateSlider.timeview.TimeView;

import java.util.Calendar;

/**
 * A Labeler that displays months
 */
public class MonthLabeler extends Labeler {

    private static String TAG = "MonthLabeler";

    public MonthLabeler(String formatString, TimeBoundaries timeBoundaries) {
        super(formatString, timeBoundaries);
    }

    @Override
    public TimeObject add(long time, int val) {
        TimeObject result = Util.addMonths(time, val, mFormatString, timeBoundaries);
        //Log.i(TAG, "add " + val + ", " + result.toString());
        return result;
    }

    @Override
    public TimeView createView(Context context, boolean isCenterView) {
        return new TimeTextView(context, isCenterView, 30);
    }

    public TimeObject getElem(long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        TimeObject result = Util.getMonth(c, mFormatString, timeBoundaries);
        //Log.i(TAG, "getElem " + result.toString());
        return result;
    }

}