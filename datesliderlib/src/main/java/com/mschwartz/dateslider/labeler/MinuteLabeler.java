package com.mschwartz.dateslider.labeler;

import android.content.Context;

import com.mschwartz.dateslider.TimeBoundaries;
import com.mschwartz.dateslider.TimeObject;
import com.mschwartz.dateslider.timeview.TimeTextView;
import com.mschwartz.dateslider.timeview.TimeView;

import java.util.Calendar;
import java.util.TimeZone;

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

    @Override
    public TimeObject getElem(long time) {

        Calendar c = Calendar.getInstance();
        c.setTimeZone(timeBoundaries.timezone);
        c.setTimeInMillis(time);

        TimeObject result =  Util.getMinute(c, mFormatString, timeBoundaries);
        //Log.i(TAG, "getElem " + result.toString());
        return result;
    }

}
