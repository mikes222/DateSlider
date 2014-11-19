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
public class YearLabeler extends Labeler {

    public YearLabeler(String formatString, TimeBoundaries timeBoundaries) {
        super(formatString, timeBoundaries);
    }

    @Override
    public TimeObject add(long time, int val) {
        return Util.addYears(time, val, mFormatString, timeBoundaries);
    }

    public TimeObject getElem(long time) {

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        return Util.getYear(c, mFormatString, timeBoundaries);
    }

    @Override
    public TimeView createView(Context context, boolean isCenterView) {
        return new TimeTextView(context, isCenterView, 40);
    }


}