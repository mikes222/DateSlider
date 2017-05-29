package com.mschwartz.dateslider.labeler;

import android.content.Context;

import com.mschwartz.dateslider.TimeBoundaries;
import com.mschwartz.dateslider.TimeObject;
import com.mschwartz.dateslider.timeview.TimeTextView;
import com.mschwartz.dateslider.timeview.TimeView;

import java.util.Calendar;

/**
 * A Labeler that displays days
 */
public class DayLabeler extends Labeler {

    private static String TAG = "DayLabeler";

    public DayLabeler(String formatString, TimeBoundaries timeBoundaries) {
        super(formatString, timeBoundaries);
    }

    @Override
    public TimeView createView(Context context, boolean isCenterView) {
        return new TimeTextView(context, isCenterView, 20);
    }

    @Override
    public TimeObject add(long time, int val) {
        TimeObject result = Util.addDays(time, val, mFormatString, timeBoundaries);
        //Log.i(TAG, "add " + val + ", " + result.toString());
        return result;
    }

    @Override
    public TimeObject getElem(long time) {

        Calendar c = Calendar.getInstance();
        c.setTimeZone(timeBoundaries.timezone);
        c.setTimeInMillis(time);

        TimeObject result = Util.getDay(c, mFormatString, timeBoundaries);
        //Log.i(TAG, "getElem " + result.toString());
        return result;
    }


}