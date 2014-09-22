package com.slider.DateSlider.labeler;

import java.util.Calendar;

import android.util.Log;

import com.slider.DateSlider.TimeObject;

/**
 * A Labeler that displays times in increments of {@value #minuteInterval} minutes.
 */
public class TimeLabeler extends Labeler {

    private final String mFormatString;

    public TimeLabeler(String formatString) {
        super(80, 60);
        mFormatString = formatString;
    }

    @Override
    public TimeObject add(long time, int val) {
        return timeObjectFromCalendar(Util.addMinutes(time, val * minuteInterval));
    }

    /**
     * override this method to set the inital TimeObject to a multiple of MINUTEINTERVAL
     */
    @Override
    public TimeObject getElem(long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        c.set(Calendar.MINUTE, c.get(Calendar.MINUTE)/minuteInterval*minuteInterval);
        Log.v("GETELEM","getelem: "+c.get(Calendar.MINUTE));
        return timeObjectFromCalendar(c);
    }

    @Override
    protected TimeObject timeObjectFromCalendar(Calendar c) {
        return Util.getTime(c, mFormatString, minuteInterval);
    }
}