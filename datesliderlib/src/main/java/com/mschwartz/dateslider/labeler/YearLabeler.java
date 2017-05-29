package com.mschwartz.dateslider.labeler;

import android.content.Context;

import com.mschwartz.dateslider.TimeBoundaries;
import com.mschwartz.dateslider.TimeObject;
import com.mschwartz.dateslider.timeview.TimeTextView;
import com.mschwartz.dateslider.timeview.TimeView;

import java.util.Calendar;
import java.util.TimeZone;

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

    @Override
    public TimeObject getElem(long time) {

        Calendar c = Calendar.getInstance();
        c.setTimeZone(timeBoundaries.timezone);
        c.setTimeInMillis(time);
        return Util.getYear(c, mFormatString, timeBoundaries);
    }

    @Override
    public TimeView createView(Context context, boolean isCenterView) {
        return new TimeTextView(context, isCenterView, 40);
    }


}